package com.example.mrxu.mipdevicecontroller;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.example.mrxu.base.BaseActivity;
import com.example.mrxu.mutils.AppExCode;
import com.example.mrxu.mutils.Const;
import com.example.mrxu.mutils.Const.DataEncryptWKIndexConst;
import com.example.mrxu.mutils.Const.KeyIndexConst;
import com.example.mrxu.mutils.Const.MacWKIndexConst;
import com.example.mrxu.mutils.Const.PinWKIndexConst;
import com.example.mrxu.mutils.MainUtils;
import com.newland.me.ConnUtils;
import com.newland.me.DeviceManager;
import com.newland.me.DeviceManager.DeviceConnState;
import com.newland.mtype.BatteryInfoResult;
import com.newland.mtype.ConnectionCloseEvent;
import com.newland.mtype.Device;
import com.newland.mtype.DeviceInfo;
import com.newland.mtype.DeviceOutofLineException;
import com.newland.mtype.DeviceRTException;
import com.newland.mtype.ModuleType;
import com.newland.mtype.common.ExCode;
import com.newland.mtype.common.MESeriesConst.TrackEncryptAlgorithm;
import com.newland.mtype.conn.DeviceConnParams;
import com.newland.mtype.event.AbstractProcessDeviceEvent;
import com.newland.mtype.event.DeviceEvent;
import com.newland.mtype.event.DeviceEventListener;
import com.newland.mtype.log.DeviceLogger;
import com.newland.mtype.log.DeviceLoggerFactory;
import com.newland.mtype.module.common.cardreader.CardReader;
import com.newland.mtype.module.common.cardreader.OpenCardReaderEvent;
import com.newland.mtype.module.common.emv.EmvModule;
import com.newland.mtype.module.common.emv.EmvTransController;
import com.newland.mtype.module.common.emv.QPBOCModule;
import com.newland.mtype.module.common.keyboard.KeyBoard;
import com.newland.mtype.module.common.keyboard.KeyBoardReadingEvent;
import com.newland.mtype.module.common.lcd.DispType;
import com.newland.mtype.module.common.lcd.LCD;
import com.newland.mtype.module.common.pin.AccountInputType;
import com.newland.mtype.module.common.pin.EncryptType;
import com.newland.mtype.module.common.pin.KekUsingType;
import com.newland.mtype.module.common.pin.MacAlgorithm;
import com.newland.mtype.module.common.pin.PinInput;
import com.newland.mtype.module.common.pin.PinInputEvent;
import com.newland.mtype.module.common.pin.PinManageType;
import com.newland.mtype.module.common.pin.WorkingKey;
import com.newland.mtype.module.common.pin.WorkingKeyType;
import com.newland.mtype.module.common.printer.Printer;
import com.newland.mtype.module.common.swiper.SwipResult;
import com.newland.mtype.module.common.swiper.SwipResultType;
import com.newland.mtype.module.common.swiper.Swiper;
import com.newland.mtype.module.common.swiper.SwiperReadModel;
import com.newland.mtype.module.common.swiper.TrackSecurityPaddingType;
import com.newland.mtype.module.external.me11.ME11External;
import com.newland.mtype.module.external.me11.ME11SwipResult;
import com.newland.mtype.tlv.TLVPackage;
import com.newland.mtype.util.Dump;
import com.newland.mtype.util.ISOUtils;
import com.newland.mtypex.audioport.AudioPortV100ConnParams;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;


/**
 * 具体实现�?
 * 
 * 
 */
public class DeviceControllerImpl extends BaseActivity implements
		DeviceController {

	private DeviceLogger logger = DeviceLoggerFactory
			.getLogger(DeviceControllerImpl.class);
	// 获取设备管理对象
	public static DeviceManager deviceManager = ConnUtils.getDeviceManager();
	// 音频设备
	private static final String ME11_DRIVER_NAME = "com.newland.me.ME11Driver";

	private DeviceConnParams connParams;
	private String driverName;
	// 获取设备控制器实例
	public static DeviceController controller = DeviceControllerImpl
			.getInstance();

	public static Boolean processing = false;

	public DeviceControllerImpl() {

	}

	public void init(Context context, String driverName,
			DeviceConnParams params,
			DeviceEventListener<ConnectionCloseEvent> listener) {
		deviceManager.init(context, driverName, params, listener);
		this.connParams = params;
		this.driverName = driverName;
	}

	/**
	 * 初始化设备
	 */
	protected void initMe11Controller() {

		processing = true;
		DeviceControllerImpl.this
				.initMe11DeviceController(new AudioPortV100ConnParams());
		processing = false;
	}
	/**
	 * 初始化ME11设备
	 * 
	 * @since ver1.0
	 * @param params
	 *            设备连接参数
	 */
	private void initMe11DeviceController(DeviceConnParams params) {
		DeviceControllerImpl.controller.init(DeviceControllerImpl.this,
				ME11_DRIVER_NAME, params,
				new DeviceEventListener<ConnectionCloseEvent>() {
					@Override
					public void onEvent(ConnectionCloseEvent event,
							Handler handler) {
						if (event.isSuccess()) {
							System.out.println("设备被客户主动断开！");
						}
						if (event.isFailed()) {

							runOnUiThread(new Runnable() {
								public void run() {
									MainUtils.showToast(getApplicationContext(),"设备链接异常断开！");
								}
							});
						}
					}
					@Override
					public Handler getUIHandler() {
						return null;
					}
				});
		System.out.println("驱动版本号："
				+ DeviceControllerImpl.controller.getCurrentDriverVersion());
	}
	public DeviceConnParams getDeviceConnParams() {
		Device device = deviceManager.getDevice();
		if (device == null)
			return null;

		return (DeviceConnParams) device.getBundle();
	}

	public static DeviceController getInstance() {
		return new DeviceControllerImpl();
	}

	@Override
	public void connect() throws Exception {
		deviceManager.connect();
		deviceManager.getDevice().setBundle(connParams);
	}

	@Override
	public void disConnect() {
		deviceManager.disconnect();
	}
	
	@Override
	public void updateWorkingKey(WorkingKeyType workingKeyType, byte[] encryData, byte[] checkValue) {
		PinInput pinInput = (PinInput) deviceManager.getDevice().getStandardModule(ModuleType.COMMON_PININPUT);
		int mkIndex = Const.MKIndexConst.DEFAULT_MK_INDEX;
		WorkingKeyType wkType = WorkingKeyType.MAC;
		byte[] rslt = null;
		switch (workingKeyType) {
		case PININPUT:
			rslt = pinInput.loadWorkingKey(wkType, mkIndex, PinWKIndexConst.DEFAULT_PIN_WK_INDEX, encryData);
			break;
		case DATAENCRYPT:
			rslt = pinInput.loadWorkingKey(wkType, mkIndex, DataEncryptWKIndexConst.DEFAULT_TRACK_WK_INDEX, encryData);
			break;
		case MAC:
			rslt = pinInput.loadWorkingKey(wkType, mkIndex, MacWKIndexConst.DEFAULT_MAC_WK_INDEX, encryData);
			break;
		default:
			throw new DeviceRTException(AppExCode.LOAD_WORKINGKEY_FAILED, "unknown key type!" + workingKeyType);
		}
		byte[] expectedKcv = new byte[4];
		System.arraycopy(rslt, 0, expectedKcv, 0, expectedKcv.length);
		if (!Arrays.equals(expectedKcv, checkValue)) {
			throw new RuntimeException("failed to check kcv!:[" + Dump.getHexDump(expectedKcv) + "," + Dump.getHexDump(checkValue) + "]");
		}
	}

	@Override
	public PinInputEvent startPininput(AccountInputType acctInputType,
			String acctHash, int inputMaxLen, boolean isEnterEnabled,
			String msg, long timeout) throws InterruptedException {
		isConnected();
		PinInput pinInput = (PinInput) deviceManager.getDevice()
				.getStandardModule(ModuleType.COMMON_PININPUT);
		EventHolder<PinInputEvent> listener = new EventHolder<PinInputEvent>();
		pinInput.startStandardPinInput(new WorkingKey(
				KeyIndexConst.KSN_INITKEY_INDEX), PinManageType.DUKPT,
				acctInputType, acctHash, inputMaxLen, new byte[] { 'F', 'F',
						'F', 'F', 'F', 'F', 'F', 'F', 'F', 'F' },
				isEnterEnabled, msg, (int) timeout, TimeUnit.MILLISECONDS,
				listener);
		try {
			listener.startWait();
		} catch (InterruptedException e) {
			pinInput.cancelPinInput();
			throw e;
		} finally {
		}
		PinInputEvent event = listener.event;
		event = preEvent(event, AppExCode.GET_PININPUT_FAILED);
		if (event == null) {
			logger.info("start getChipherText,but return is none!may user canceled?");
			return null;
		}
		return event;
	}

	@Override
	public void showMessage(String msg) {
		LCD lcd = (LCD) deviceManager.getDevice().getStandardModule(
				ModuleType.COMMON_LCD);
		if (lcd != null) {
			lcd.draw(msg);
		}
	}

	@Override
	public void clearScreen() {
		LCD lcd = (LCD) deviceManager.getDevice().getStandardModule(
				ModuleType.COMMON_LCD);
		if (lcd != null) {
			lcd.clearScreen();
		}
	}

	@Override
	public ME11SwipResult swipCard(String msg, long timeout, TimeUnit timeUnit) {


		ME11External me11Model = (ME11External) deviceManager.getDevice()
				.getExModule(ME11External.MODULE_NAME);
		ME11SwipResult swipeResult = me11Model.openCardReader(new ModuleType[] {
				ModuleType.COMMON_SWIPER, ModuleType.COMMON_ICCARD }, timeout,
				timeUnit, new SwiperReadModel[] {
						SwiperReadModel.READ_SECOND_TRACK,
						SwiperReadModel.READ_THIRD_TRACK }, (byte) 0xFF,
				TrackEncryptAlgorithm.BY_M10_MODEL, new WorkingKey(
						DataEncryptWKIndexConst.DEFAULT_TRACK_WK_INDEX),
				new byte[] { 0x14, 0x09, 0x25, 0x18, 0x46, 0x00 }, new byte[0],
				new byte[0]);
		return swipeResult;
	}


	protected SwipResult getSwipResult(Swiper swiper, int trackKey, String encryptType) {
		SwipResult swipRslt = swiper.readEncryptResult(new SwiperReadModel[] { SwiperReadModel.READ_IC_SECOND_TRACK }, TrackSecurityPaddingType.NONE, new WorkingKey(trackKey), encryptType, null, null);
		return swipRslt;
	}
	@Override
	public DeviceInfo getDeviceInfo() {
		return deviceManager.getDevice().getDeviceInfo();
	}

	@Override
	public BatteryInfoResult getPowerLevel() {
		return deviceManager.getDevice().getBatteryInfo();
	}

	@Override
	public void reset() {
		deviceManager.getDevice().reset();
	}

	@Override
	public void destroy() {
		deviceManager.destroy();
	}

	public DeviceConnState getDeviceConnState() {
		return deviceManager.getDeviceConnState();
	}

	@Override
	public void setParam(int tag, byte[] value) {
		TLVPackage tlvpackage = ISOUtils.newTlvPackage();
		tlvpackage.append(tag, value);
		deviceManager.getDevice().setDeviceParams(tlvpackage);
	}

	@Override
	public byte[] getParam(int tag) {
		TLVPackage pack = deviceManager.getDevice().getDeviceParams(tag);
		return pack.getValue(getOrginTag(tag));

	}

	private int getOrginTag(int tag) {
		if ((tag & 0xFF0000) == 0xFF0000) {
			return tag & 0xFFFF;
		} else if ((tag & 0xFF00) == 0xFF00) {
			return tag & 0xFF;
		}
		return tag;
	}

	@Override
	public void printBitMap(int position, Bitmap bitmap) {
		Printer printer = (Printer) deviceManager.getDevice()
				.getStandardModule(ModuleType.COMMON_PRINTER);
		printer.init();
		printer.print(position, bitmap, 30, TimeUnit.SECONDS);
	}

	@Override
	public void printString(String data) {
		Printer printer = (Printer) deviceManager.getDevice()
				.getStandardModule(ModuleType.COMMON_PRINTER);
		printer.init();
		printer.print(data, 30, TimeUnit.SECONDS);
	}

	@Override
	public byte[] encrypt(WorkingKey wk, byte[] input) {
		PinInput pinInput = (PinInput) deviceManager.getDevice()
				.getStandardModule(ModuleType.COMMON_PININPUT);
		return pinInput.encrypt(wk, EncryptType.ECB, input, new byte[] { 0x00,
				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 });
	}

	@Override
	public byte[] caculateMac(byte[] input) {
		PinInput pinInput = (PinInput) deviceManager.getDevice()
				.getStandardModule(ModuleType.COMMON_PININPUT);
		return pinInput.calcMac(MacAlgorithm.MAC_ECB, new WorkingKey(
				MacWKIndexConst.DEFAULT_MAC_WK_INDEX), input);
	}

	@Override
	public String inputPlainPwd(String title, String content, int minLength,
			int maxLength, long timeout) throws InterruptedException {
		KeyBoard keyboard = (KeyBoard) deviceManager.getDevice()
				.getStandardModule(ModuleType.COMMON_KEYBOARD);
		EventHolder<KeyBoardReadingEvent<String>> listener = new EventHolder<KeyBoardReadingEvent<String>>();
		keyboard.readPwd(DispType.NORMAL, title, content, minLength, maxLength,
				(int) timeout, TimeUnit.SECONDS, listener);
		try {
			listener.startWait();
		} catch (InterruptedException e) {
			keyboard.cancelLastReading();
			throw e;
		} finally {
			// clearScreen();
		}
		KeyBoardReadingEvent<String> event = listener.event;
		if (event == null)
			return null;

		return event.getRslt();
	}

	@Override
	public void initParms(Bundle parms) {

	}

	@Override
	public int bindLayout() {
		return 0;
	}

	@Override
	public void initView(View view) {

	}

	@Override
	public void doBusiness(Context mContext) {

	}

	@Override
	public void widgetClick(View v) {

	}

	/**
	 * 事件线程阻塞控制监听�?
	 * 
	 * @author lance
	 * 
	 * @param <T>
	 */
	private class EventHolder<T extends DeviceEvent> implements
			DeviceEventListener<T> {

		private T event;

		private final Object syncObj = new Object();

		private boolean isClosed = false;

		public void onEvent(T event, Handler handler) {
			this.event = event;
			synchronized (syncObj) {
				isClosed = true;
				syncObj.notify();
			}
		}

		public Handler getUIHandler() {
			return null;
		}

		void startWait() throws InterruptedException {
			synchronized (syncObj) {
				if (!isClosed)
					syncObj.wait();
			}
		}

	}

	@Override
	public void showMessageWithinTime(String msg, int showtime) {
		LCD lcd = (LCD) deviceManager.getDevice().getStandardModule(
				ModuleType.COMMON_LCD);
		if (lcd != null) {
			lcd.drawWithinTime(msg, showtime);
		}

	}

	@Override
	public SwipResult swipCardForPlain(String msg, long timeout,
			TimeUnit timeUnit) {
		CardReader cardReader = (CardReader) deviceManager.getDevice()
				.getStandardModule(ModuleType.COMMON_CARDREADER);
		if (cardReader == null) {

			throw new DeviceRTException(AppExCode.GET_TRACKTEXT_FAILED,
					"not support read card!");
		}
		try {
			EventHolder<OpenCardReaderEvent> listener = new EventHolder<OpenCardReaderEvent>();
			cardReader.openCardReader(msg,
					new ModuleType[] { ModuleType.COMMON_SWIPER }, timeout,
					timeUnit, listener);
			try {
				listener.startWait();
			} catch (InterruptedException e) {
				cardReader.cancelCardRead();
			} finally {
				// clearScreen();
			}
			OpenCardReaderEvent event = listener.event;
			event = preEvent(event, AppExCode.GET_TRACKTEXT_FAILED);
			if (event == null) {
				return null;
			}
			ModuleType[] openedModuleTypes = event.getOpenedCardReaders();
			if (openedModuleTypes == null || openedModuleTypes.length <= 0) {
				logger.info("start cardreader,but return is none!may user canceled?");
				return null;
			}
			if (openedModuleTypes.length > 1) {
				logger.warn("should return only one type of cardread action!but is "
						+ openedModuleTypes.length);
				throw new DeviceRTException(AppExCode.GET_TRACKTEXT_FAILED,
						"should return only one type of cardread action!but is "
								+ openedModuleTypes.length);
			}
			switch (openedModuleTypes[0]) {
			case COMMON_SWIPER:
				Swiper swiper = (Swiper) deviceManager.getDevice()
						.getStandardModule(ModuleType.COMMON_SWIPER);
				SwipResult swipRslt = swiper
						.readPlainResult(new SwiperReadModel[] {
								SwiperReadModel.READ_SECOND_TRACK,
								SwiperReadModel.READ_THIRD_TRACK });
				if (swipRslt.getRsltType() == SwipResultType.SUCCESS) {
					return swipRslt;
				}
				throw new DeviceRTException(AppExCode.GET_TRACKTEXT_FAILED,
						"swip failed:" + swipRslt.getRsltType());
			default: {
				throw new DeviceRTException(AppExCode.GET_TRACKTEXT_FAILED,
						"not support cardreader module:" + openedModuleTypes[0]);
			}
			}
		} finally {
			logger.info("closeCardReader2");
			// cardReader.closeCardReader(); // for me11
		}
	}

	/**
	 * 发起一个刷卡流程(银联加密方式)
	 * <p>
	 * 
	 * @param time
	 *            当前交易时间
	 * @param timeout
	 *            刷卡超时时间
	 * @param timeUnit
	 *            超时时间单位
	 * @return
	 */
	@Override
	public ME11SwipResult swipCard_me11(byte[] time, long timeout,
			TimeUnit timeUnit) {
		ME11External me11Model = (ME11External) deviceManager.getDevice()
				.getExModule(ME11External.MODULE_NAME);
		ME11SwipResult swipeResult = me11Model.openCardReader(new ModuleType[] {
				ModuleType.COMMON_SWIPER, ModuleType.COMMON_ICCARD }, timeout,
				timeUnit, new SwiperReadModel[] {
						SwiperReadModel.READ_SECOND_TRACK,
						SwiperReadModel.READ_THIRD_TRACK }, (byte) 0xFF,
				TrackEncryptAlgorithm.BY_UNIONPAY_MODEL, new WorkingKey(
						DataEncryptWKIndexConst.DEFAULT_TRACK_WK_INDEX), time,
				null, null);
		return swipeResult;
	}

	/**
	 * 发起一个明文刷卡流程
	 * <p>
	 * 
	 * @param time
	 *            当前交易时间
	 * @param timeout
	 *            刷卡超时时间
	 * @param timeUnit
	 *            超时时间单位
	 * @return
	 */
	@Override
	public ME11SwipResult swipCardForPlain_me11(byte[] time, long timeout,
			TimeUnit timeUnit) {

		ME11External me11Model = (ME11External) deviceManager.getDevice()
				.getExModule(ME11External.MODULE_NAME);

		ME11SwipResult swipeResult = me11Model.openCardReader(new ModuleType[] {
				ModuleType.COMMON_SWIPER, ModuleType.COMMON_ICCARD }, timeout,
				timeUnit, new SwiperReadModel[] {
						SwiperReadModel.READ_SECOND_TRACK,
						SwiperReadModel.READ_THIRD_TRACK }, (byte) 0xFF,
				TrackEncryptAlgorithm.BY_PLAIN_MODEL, new WorkingKey(
						DataEncryptWKIndexConst.DEFAULT_TRACK_WK_INDEX), time,
				null, null);
		return swipeResult;
	}

	private <T extends AbstractProcessDeviceEvent> T preEvent(T event,
			int defaultExCode) {
		if (!event.isSuccess()) {
			if (event.isUserCanceled()) {
				return null;
			}
			if (event.getException() != null) {
				if (event.getException() instanceof RuntimeException) {// 运行时异常直接抛�?
					throw (RuntimeException) event.getException();
				}
				throw new DeviceRTException(AppExCode.GET_TRACKTEXT_FAILED,
						"open card reader meet error!", event.getException());
			}
			throw new DeviceRTException(ExCode.UNKNOWN,
					"unknown exception!defaultExCode:" + defaultExCode);
		}
		return event;
	}

	@Override
	public void loadMainKey(KekUsingType kekUsingType, int mkIndex,
			byte[] keyData, byte[] checkValue, int transportKeyIndex) {
		PinInput pinInput = (PinInput) deviceManager.getDevice()
				.getStandardModule(ModuleType.COMMON_PININPUT);
		byte[] rslt = pinInput.loadMainKey(kekUsingType, mkIndex, keyData,
				transportKeyIndex);
		byte[] expectedKcv = new byte[4];
		System.arraycopy(rslt, 0, expectedKcv, 0, expectedKcv.length);

		if (!Arrays.equals(expectedKcv, checkValue)) {

			System.out.println("注入失败！");
			throw new RuntimeException("failed to check kcv!:["
					+ Dump.getHexDump(expectedKcv) + ","
					+ Dump.getHexDump(checkValue) + "]");
		}

	}

	@Override
	public String getCurrentDriverVersion() {
		if (deviceManager != null)
			return deviceManager.getDriverMajorVersion() + "."
					+ deviceManager.getDriverMinorVersion();

		return "n/a";
	}

	private void isConnected() {
		synchronized (this.driverName) {
			if (null == deviceManager || deviceManager.getDevice() == null) {
				throw new DeviceOutofLineException("device not connect!");
			}
		}
	}

	@Override
	public EmvModule getEmvModule() {
		isConnected();
		return (EmvModule) deviceManager.getDevice().getStandardModule(
				ModuleType.COMMON_EMV);
	}

	@Override
	public void startEmv(BigDecimal amt, TransferListener transferListener) {
		isConnected();
		try {
			EmvModule module = getEMVModule();

			EmvTransController controller = module
					.getEmvTransController(transferListener);
			controller.startEmv(amt, new BigDecimal("0"), true);
		} finally {
			logger.info("closeCardReader3");
			// cardReader.closeCardReader();
		}
	}

	private EmvModule getEMVModule() {
		return (EmvModule) deviceManager.getDevice().getStandardModule(
				ModuleType.COMMON_ME11EMV);
	}

	private QPBOCModule getQPBOCModule() {
		return (QPBOCModule) deviceManager.getDevice().getStandardModule(
				ModuleType.COMMON_QPBOC);
	}
}
