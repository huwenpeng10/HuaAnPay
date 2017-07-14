package com.example.mrxu.mipdevicecontroller;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.graphics.Bitmap;

import com.newland.me.DeviceManager.DeviceConnState;
import com.newland.mtype.BatteryInfoResult;
import com.newland.mtype.ConnectionCloseEvent;
import com.newland.mtype.DeviceInfo;
import com.newland.mtype.conn.DeviceConnParams;
import com.newland.mtype.event.DeviceEventListener;
import com.newland.mtype.module.common.emv.EmvModule;
import com.newland.mtype.module.common.pin.AccountInputType;
import com.newland.mtype.module.common.pin.KekUsingType;
import com.newland.mtype.module.common.pin.PinInputEvent;
import com.newland.mtype.module.common.pin.WorkingKey;
import com.newland.mtype.module.common.pin.WorkingKeyType;
import com.newland.mtype.module.common.swiper.Account;
import com.newland.mtype.module.common.swiper.SwipResult;
import com.newland.mtype.module.external.me11.ME11SwipResult;

/**
 * 设备控制�?p>
 * 
 * @author lance
 * @since ver1.0
 */
public interface DeviceController {
	
 
	/**
	 * 初始化设备控制器
	 *
	 * @since ver1.0
	 * @param context
	 * @param params
	 */
	public void init(Context context, String driverName, DeviceConnParams params, DeviceEventListener<ConnectionCloseEvent> listener);
	
	/**
	 * �?��连接控制器，释放相关资源<p>
	 * @since ver1.0
	 */
	public void destroy();

	/**
	 * 连接设备<p>
	 * 
	 * @since ver1.0
	 * @throws Exception
	 */
	public void connect() throws Exception;

	/**
	 * 连接中断<p>
	 * @since ver1.0
	 */
	public void disConnect();
	
	/**
	 * 更新工作密钥<p>
	 * 
	 * @since ver1.0
	 * @param workingKeyType 工作密钥类型
	 * @param encryData 加密数据
     * @param checkValue 校验数据
	 * @return
	 */
	public void updateWorkingKey(WorkingKeyType workingKeyType, byte[] encryData, byte[] checkValue) ;

	/**
	 * 发起�?��刷卡流程<p>
	 * 
	 * @since ver1.0
	 * @param msg 在设备界面显示信�?p>
	 * @param timeout 刷卡超时时间
	 * @param timeUnit 超时时间单位
	 * @return
	 */
	public ME11SwipResult swipCard(String msg, long timeout, TimeUnit timeUnit) ;
	/**
	 * 发起�?��明文刷卡流程<p>
	 * 
	 * @since ver1.0
	 * @param msg 在设备界面显示信�?p>
	 * @param timeout 刷卡超时时间
	 * @param timeUnit 超时时间单位
	 * @return
	 */
	public SwipResult swipCardForPlain(String msg, long timeout, TimeUnit timeUnit);

//	/**
//	 * 发起�?��pin输入过程<p>
//	 * 
//	 * @since ver1.0
//	 * @param acctHash 刷卡时返回的<tt>acctHash</tt>
//	 * @param inputMaxLen �?��密码输入长度 [0,12]
//	 * @param msg 界面显示数据
//	 * @return
//	 */
//	public PinInputEvent startPininput(String acctHash,int inputMaxLen, String msg);

	/**
	 * 显示消息
	 * 
	 * @since ver1.0
	 * @param msg
	 */
	public void showMessage(String msg);
	/**
	 * 在规定的时间内显示消�?
	 * 
	 * @since ver1.0
	 * @param msg
	 * @param showtime
	 */
	public void showMessageWithinTime(String msg, int showtime);

	/**
	 * pos清屏
	 * @since ver1.0
	 */
	public void clearScreen();

	/**
	 * 获得设备信息
	 * @since ver1.0
	 * @return
	 */
	public DeviceInfo getDeviceInfo();
	
	public BatteryInfoResult getPowerLevel();
	
	/**
	 * 撤消当前操作
	 */
	public void reset();
	
	/**
	 * 获得当前设备连接状�?
	 * @since ver1.0
	 * @return
	 */
	public DeviceConnState getDeviceConnState();

	/**
	 * 设置参数
	 * 
	 * @since ver1.0
	 * @param tag 设置的标签数�?
	 * @param value 设置的数�?
	 */
	public void setParam(int tag, byte[] value);

	/**
	 * 获得对应的参�?
	 * 
	 * @since ver1.0
	 * @param tag  获得的参数数�?
	 * @return
	 */
	public byte[] getParam(int tag);
	/**
	 * 发起�?��pin读取过程,并返回pin信息
	 * <p>
	 * 该方法需要在刷卡完成后进�?因为pin输入过程�?��依赖上次刷卡的账号信�?
	 * <p>
	 * 通过{@link SwipResult#getAccount()}可以获得刷卡时的账户信息，需要将
	 * {@link Account#getIdentityHash()}传入供设备校验�?
	 * 
	 * @param acctHash上次刷卡时的账户hash
	 * @param inputMaxLen允许密码输入�?���?
	 * @param isEnterEnabled密码输入�?
	 *            ,是否通过回车返回，否则则输满即返回�?
	 * @param msg输入信息
	 * @param timeout超时时间
	 * @return 若客户设备撤�?则返回nullF
	 */
	public PinInputEvent startPininput(AccountInputType acctInputType, String acctHash, int inputMaxLen, boolean isEnterEnabled, String msg, long timeout) throws InterruptedException;
//
//	/**
//	 * �?��密码输入
//	 * @param acctHash 账号hash
//	 * @param inputMaxLen �?��输入长度
//	 * @param msg 显示信息
//	 * @param listener 响应监听
//	 */
//	public void startPininput(String acctHash, int inputMaxLen, String msg,
//			DeviceEventListener<PinInputEvent> listener);
	
	/**
	 * 获取设备连接参数
	 * @return
	 */
	public DeviceConnParams getDeviceConnParams();
	/**
	 * 打印图片
	 * @param position 偏移�?
	 * @param bitmap 位图
	 */
	public void printBitMap(int position, Bitmap bitmap);
	
	/**
	 * 打印字符
	 * @param data 待打印数�?
	 */
	public void printString(String data);
	
	/**
	 * 加密接口(ECB方式)
	 * @param wk工作密钥
	 * @param input待加密数�?
	 * @return
	 */
	public byte[] encrypt(WorkingKey wk, byte[] input);
	
	/**
	 * 计算mac方法
	 * @param input
	 * @return
	 */
	public byte[] caculateMac(byte[] input);
	
	/**
	 * �?���?��密码明文输入过程输入过程(pos显示*�?<p>
	 * 
	 * @param title 标题
	 * @param content 内容
	 * @param minLength �?��长度
	 * @param maxLength �?��长度
	 * @param timeout 超时时间
	 * @return
	 * 		若客户设备撤�?则返回null
	 * @throws InterruptedException 当线程被中断时，抛出该异�?
	 */
	public String inputPlainPwd(String title, String content, int minLength, int maxLength, long timeout) throws InterruptedException;
	
	
	/**
	 * 装载主秘�?
	 * @param kekUsingType主密钥传输使用的方式
	 * @param mkIndex主秘钥索�?
	 * @param keyData主密�?
	 * @param checkValue校验�?
	 * @param transportKeyIndex传输密钥索引(仅在KekUsingType.MAIN_KEY)时使�?
	 */
	public void loadMainKey(KekUsingType kekUsingType, int mkIndex, byte[] keyData, byte[] checkValue, int transportKeyIndex);

	public String getCurrentDriverVersion();
	
	/**
	 * 
	 * 
	 * @param cardReaders
	 * @param amt
	 * @param timeout
	 * @param timeunit
	 */
	public void startEmv(BigDecimal amt, TransferListener transferListener);
	
	/**
	 * 获取emv模块
	 * @param filePath
	 * @return
	 */
	public EmvModule getEmvModule();

	/**
	 * 
	 * @param time
	 * @param timeout
	 * @param timeUnit
	 * @return
	 */
	ME11SwipResult swipCardForPlain_me11(byte[] time, long timeout,
                                         TimeUnit timeUnit);
	/**
	 * 
	 * @param time
	 * @param timeout
	 * @param timeUnit
	 * @return
	 */
	ME11SwipResult swipCard_me11(byte[] time, long timeout, TimeUnit timeUnit);
}