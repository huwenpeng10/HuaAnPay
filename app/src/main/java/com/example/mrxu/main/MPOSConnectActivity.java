package com.example.mrxu.main;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.merxu.mypractice.R;
import com.example.mrxu.allinfo.Construction;
import com.example.mrxu.allinfo.TradeInfo;
import com.example.mrxu.assemble_xml.QPosSignIn;
import com.example.mrxu.mipdevicecontroller.DeviceControllerImpl;
import com.example.mrxu.mutils.AudioUtil;
import com.example.mrxu.mutils.MainUtils;
import com.example.mrxu.mutils.TagUtils;
import com.example.mrxu.mutils.TcpRequest;
import com.example.mrxu.mutils.TcpRequest.TcpRequestMessage;
import com.example.mrxu.mutils.XmlBuilder;
import com.example.mrxu.mutils.XmlParser;
import com.github.ybq.android.spinkit.style.ThreeBounce;
import com.icardpay.zxbbluetooth.api.ZxbListener;
import com.newland.mtype.BatteryInfoResult;
import com.newland.mtype.DeviceInfo;
import com.newland.mtype.module.common.pin.WorkingKeyType;
import com.newland.mtype.util.ISOUtils;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

import static com.example.merxu.mypractice.R.id.connect_bt;

/**
 * Mpos连接页面
 * @author MrXu
 * */

public class MPOSConnectActivity extends DeviceControllerImpl implements ZxbListener {

    @BindView(R.id.left_Imagemage)
    ImageView leftImagemage;
    @BindView(R.id.title_bar_text)
    TextView titlebartext;
    @BindView(R.id.support_card_tv)
    TextView supportCardTv;
    @BindView(R.id.connect_tv)
    TextView connectTv;
    @BindView(R.id.connect_pro)
    ProgressBar connectPro;
    @BindView(R.id.connect_bt)
    Button connectBt;
    private boolean hasCheck =false;
    private String pinKey_check;
    private String pinKey;
    private String macKey_check;
    private String macKey;
    private String m_check;
    private String checkvalue_Sum;
    private String p_check;
    private EventBus eventBus;
    private Boolean updataState=false;
    public static MPOSConnectActivity mposConnectActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        mposConnectActivity=this;
        eventBus=EventBus.getDefault();
        eventBus.register(this);
        if(!isHaveNet(this,"请检查网络连接")){
            finish();
            return;
        }

        initMe11Controller();
        readerDeviceInfo();

    }


    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        return R.layout.activity_connect;

    }

    @Override
    public void initView(View view) {
        ButterKnife.bind(this);


        titlebartext.setText("连接刷卡器");
        leftImagemage.setOnClickListener(this);
        connectBt.setVisibility(View.GONE);
        connectBt.setOnClickListener(this);
        ThreeBounce threeBounce=new ThreeBounce();
        connectPro.setIndeterminateDrawable(threeBounce);
        threeBounce.setColor(0xFFFFFFFF);

        AudioUtil audioUtil =AudioUtil.getInstance(MPOSConnectActivity.this);
        int maxVolume = audioUtil.getSystemMaxVolume();
        audioUtil.setMediaVolume(maxVolume);

    }


    @Override
    public void widgetClick(View v) {
        switch (v.getId()){
            case R.id.left_Imagemage :

                /**
                 * 销毁设备控制器
                 */

                DeviceControllerImpl.controller.disConnect();
                DeviceControllerImpl.controller.destroy();
                finish();
                overridePendingTransition(R.animator.push_right_out,
                        R.animator.push_right_in);
                break;
            case connect_bt:
                processing = true;
                connectPro.setVisibility(View.VISIBLE);
                connectBt.setVisibility(View.GONE);
                connectTv.setText("正在连接");
                initMe11Controller();
                readerDeviceInfo();
                break;
                default:
                break;

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!eventBus.isRegistered(this)) {
            eventBus.register(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        eventBus.unregister(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void readerDeviceInfo() {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    processing = true;
                    try {
                        connectDevice();
                        DeviceInfo deviceInfo = null;

                        deviceInfo = DeviceControllerImpl.controller
                                .getDeviceInfo();
                        // 获取设备电量信息
                        BatteryInfoResult batteryInfo = DeviceControllerImpl.controller
                                .getPowerLevel();
                        int battery = Integer.parseInt(batteryInfo
                                .getElectricBattery());

                        if (battery <= 10) {
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    MainUtils.showToast(MPOSConnectActivity.this,"电量过低，请充电后使用！");

                                }
                            });
                        }
                        runOnUiThread(new Runnable() {
                            public void run() {
                                hasCheck = true;

                                if (hasCheck) {
                                    signIn(TradeInfo.getDeviceSn());
                                }
                            }
                        });
                    } catch (Exception e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                MainUtils.showToast(MPOSConnectActivity.this,"");

                                runErro("连接错误，请尝试重新连接");
                                connectTv.setText("连接错误");

                            }
                        });
                        return;

                    }
                    processing = false;
                }
            }).start();
        }

    /**
     * 进行安全验证，pos机签到
     */
    private void signIn(final String sn) {

        Log.d(TAG, "signIn: =" + sn);
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    new TcpRequest(Construction.HOST, Construction.PORT)
                            .request(new QPosSignIn(
                                    MPOSConnectActivity.this, sn).toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    e.printStackTrace();
                } finally {
                    // tv_message.setText(sn);
                }
            }
        }).start();
    }



    /**
     * 此处刷卡器未连接时会回掉
     *
     * */


    public void connectDevice() {

        try {
            DeviceControllerImpl.controller.connect();
            // 唯一标识
            String ksn = DeviceControllerImpl.controller.getDeviceInfo()
                    .getCSN();
            TradeInfo.setDeviceSn(ksn);


        } catch (Exception e1) {

            e1.printStackTrace();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    hasCheck = false;
                    runErro("获取设备标识异常，请尝试重新连接");
                    connectTv.setText("设备标识异常");
                }
            });

            throw new RuntimeException(e1.getMessage(), e1);
        }
    }


    public void onEvent(TcpRequestMessage message) {


        if (message.getCode() == 1) {
            Log.d(Get_Message, new String(message.getTcpRequestMessage()));
            TagUtils.Field011Add(this);


            try {
                /** 刷卡器签到 */
                final HashMap<String, String> data = new XmlParser()
                        .parse(new String(message.getTcpRequestMessage()));
                if (data.get(XmlBuilder.fieldName(39)).equalsIgnoreCase("00")) {
                    String key = data.get(XmlBuilder.fieldName(62));
                    pinKey_check = key.substring(0, 40);
                    pinKey = pinKey_check.substring(0, 32);
                    macKey_check = key.substring(40, 80);
                    macKey = macKey_check.substring(0, 32);
                    m_check = macKey_check.substring(32, 40);
                    checkvalue_Sum = data.get(XmlBuilder.fieldName(108));
                    p_check = checkvalue_Sum.substring(0, 8);

                    Log.d(TAG, "onEvent: pinkey==" + pinKey);
                    Log.d(TAG, "onEvent: macKey==" + macKey);
                    Log.d(TAG, "onEvent: m_check==" + m_check);
                    Log.d(TAG, "onEvent: p_check==" + p_check);

                    updateWorkingKey();

                } else {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            MainUtils.showToast(MPOSConnectActivity.this,
                                    data.get(XmlBuilder.fieldName(124)));
                        }
                    });
                }

            } catch (SAXException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    public void run() {
                        MainUtils.showToast(MPOSConnectActivity.this, "签到异常");
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    public void run() {
                        MainUtils.showToast(MPOSConnectActivity.this, "签到异常");
                    }
                });
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    public void run() {
                        MainUtils.showToast(MPOSConnectActivity.this, "签到异常");
                    }
                });
            }


        } else if (message.getCode() == -2) {

            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    MainUtils.showToast(getApplicationContext(), "服务器连接超时...");
                }
            });

        } else {

            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    MainUtils.showToast(MPOSConnectActivity.this, "签到失败");
                }
            });

        }
    }



    /** 更新工作密钥 */
    private void updateWorkingKey() {
        processing = true;
        try {
            DeviceControllerImpl.controller.updateWorkingKey(
                    WorkingKeyType.PININPUT, ISOUtils.hex2byte(pinKey),
                    ISOUtils.hex2byte(p_check));
            DeviceControllerImpl.controller.updateWorkingKey(
                    WorkingKeyType.DATAENCRYPT, ISOUtils.hex2byte(macKey),
                    ISOUtils.hex2byte(m_check));
            updataState=true;
            runOnUiThread(new Runnable() {
                public void run() {
                    MainUtils.showToast(MPOSConnectActivity.this,"更新工作密钥成功");
                   connectTv.setText("已连接");
                   connectPro.setVisibility(View.INVISIBLE);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });

            /**
             *
             * 装载密钥成功
             *
             * */
            startNextActivity();


        } catch (Exception ex) {

            updataState=false;
            connectTv.setText("工作密钥异常");
            runErro("更新工作密钥异常，请尝试重新连接");

        }
        processing = false;
    }


    private void startNextActivity(){

        Intent intent=new Intent(this,MPOSAutographActivity.class);
        startActivity(intent);
        overridePendingTransition(R.animator.push_left_in,
                R.animator.push_left_out);

    }


    private void runErro(final String msg){

        if (msg!=null){
            runOnUiThread(new Runnable() {
            public void run() {
                MainUtils.showToast(MPOSConnectActivity.this,msg);
                connectBt.setVisibility(View.VISIBLE);
                connectPro.setVisibility(View.GONE);
            }
        });

        }
    }

    @Override
    public void onGetDeviceInfo(String s, String s1, String s2, int i) {

    }

    @Override
    public void onUpgradeProgress(int i) {

    }

    @Override
    public void onUpgradeFirmware(boolean b, String s) {

    }

    @Override
    public void onUpdateKey() {

    }

    @Override
    public void onGetCheckcode(String s) {

    }

    @Override
    public void onSetFactor() {

    }

    @Override
    public void onOpenCardReader(String s) {

    }

    @Override
    public void onCloseCardReader() {

    }

    @Override
    public void onReadCardNumber(String s, String s1) {

    }

    @Override
    public void onReadTrackData(String s, String s1, String s2, String s3, String s4, String s5) {

    }

    @Override
    public void onGetPbocTradeData(String s, String s1, String s2, String s3, String s4, String s5, String s6, String s7) {

    }

    @Override
    public void onExecutePbocSecondAuth(String s, String s1, String s2, String s3, String s4) {

    }

    @Override
    public void onEncryptPin(String s, String s1) {

    }

    @Override
    public void onCalculateMac(String s, String s1) {

    }

    @Override
    public void onChangeToUpgradeMode() {

    }

    @Override
    public void onWriteDeviceId() {

    }

    @Override
    public void onWriteMainKey() {

    }

    @Override
    public void onTestCommunicate() {

    }

    @Override
    public void onStateChanged(int i) {

    }

    @Override
    public void onDeviceFound(BluetoothDevice bluetoothDevice, short i) {

    }

    @Override
    public void onDiscoveryStarted() {

    }

    @Override
    public void onDiscoveryFinished() {

    }

    @Override
    public void onBondStateChanged(BluetoothDevice bluetoothDevice, int i) {

    }

    @Override
    public void onConnectStateChanged(BluetoothDevice bluetoothDevice, int i) {

    }

    @Override
    public void onError(int i) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            /**
             * 销毁设备控制器
             */

            DeviceControllerImpl.controller.disConnect();
            DeviceControllerImpl.controller.destroy();
            finish();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

}
