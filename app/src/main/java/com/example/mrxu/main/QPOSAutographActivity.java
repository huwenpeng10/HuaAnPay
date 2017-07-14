package com.example.mrxu.main;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.merxu.mypractice.R;
import com.example.mrxu.allthread.ThreadDoSign;
import com.example.mrxu.allthread.ThreadGetTerminalIDTest;
import com.example.mrxu.allthread.ThreadGiveup;
import com.example.mrxu.allthread.ThreadSwiCard;
import com.example.mrxu.allinfo.Construction;
import com.example.mrxu.allinfo.TradeInfo;
import com.example.mrxu.allinfo.TradeInfo.PageState;
import com.example.mrxu.assemble_xml.QPosSignIn;
import com.example.mrxu.base.BaseActivity;
import com.example.mrxu.mutils.MainUtils;
import com.example.mrxu.mutils.TagUtils;
import com.example.mrxu.mutils.TcpRequest;
import com.example.mrxu.mutils.TcpRequest.TcpRequestMessage;
import com.example.mrxu.mutils.XmlBuilder;
import com.example.mrxu.mutils.XmlParser;
import com.example.mrxu.myviews.ScanHuaDongView;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.ParserConfigurationException;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import fncat.qpos.Controller.POSManage;

import static com.example.merxu.mypractice.R.id.left_Imagemage;


/**
 *
 * @author Mrxu
 *
 * QPOS刷卡页面
 * */
public class QPOSAutographActivity extends BaseActivity {


    @BindView(left_Imagemage)
    ImageView leftImagemage;
    @BindView(R.id.title_bar_text)
    TextView titlebartext;

    @BindView(R.id.scanhuadongview)
    ScanHuaDongView scanhuadongview;
    @BindView(R.id.bt_again)
    Button btagain;
    @BindView(R.id.shuaka_tishi_tv1)
    TextView shuakaTishiTv1;
    @BindView(R.id.shuaka_tishi_tv2)
    TextView shuakaTishiTv2;
    @BindView(R.id.shuaka_tishi_tv3)
    TextView shuakaTishiTv3;
    @BindView(R.id.shuaka_tishi_tv4)
    TextView shuakaTishiTv4;
    @BindView(R.id.countDown_tv)
    TextView countDownTv;


    private EventBus eventBus;
    private boolean isfristbluetooth;
    private POSManage manager;
    private BluetoothDevice bluetoothDevice;
    //设备号
    private String deviceSn;
    private String pinKey_check;
    private String pinKey;
    private String macKey_check;
    private String macKey;
    private String m_check;
    private String checkvalue_Sum;
    private String p_check;
    private Timer timer = new Timer();


    //设备回调
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {


            switch (msg.what) {
                case -1:
                    // 提示错误的土司
                    String result = (String) msg.obj;
                    String subResult = (result.substring(0, 4));
                    Log.d("result", result);
                    if (subResult.equals("通讯测试")) {
                        if (isfristbluetooth) {
                            controllerBlueTooth();
                        } else {
                            btagain.setVisibility(View.VISIBLE);
                        }
                    } else if (subResult.equals("签到测试")) {
                        MainUtils.showToast(QPOSAutographActivity.this, "签到失败");
                        Log.d("result", result);
                        QPOSAutographActivity.this.finish();
                        overridePendingTransition(R.animator.push_right_out,
                                R.animator.push_right_in);
                    } else if (subResult.equals("刷卡取消")) {
                        MainUtils.showToast(QPOSAutographActivity.this, "取消刷卡");
                    } else if (subResult.equals("刷卡测试")) {
                        QPOSAutographActivity.this.finish();
                        MainUtils.showToast(QPOSAutographActivity.this, "刷卡失败");
                        overridePendingTransition(R.animator.push_right_out,
                                R.animator.push_right_in);
                    } else if (subResult.equals("IC测试")) {
                        QPOSAutographActivity.this.finish();

                        MainUtils.showToast(QPOSAutographActivity.this, "刷卡失败");
                        overridePendingTransition(R.animator.push_right_out,
                                R.animator.push_right_in);
                    }
                    break;

                case 0:
                    // 拿到KSN进行签到
                    String[] resultdata = (String[]) msg.obj;
                    deviceSn = resultdata[2];

                    if (deviceSn != null && !deviceSn.equals("")) {

                        TradeInfo.setDeviceSn(deviceSn);
                        signIn(deviceSn);

                    } else {
                        MainUtils.showToast(getApplicationContext(), "ksn为空");
                    }
                    break;
                case 1:
                    //签到成功请刷卡

                    initScanhuadongview(1);
                    MainUtils.showToast(QPOSAutographActivity.this, "请刷卡");
                    new ThreadSwiCard(handler, manager, TradeInfo.getInputMoney()).start();
                    CountdownStart();

                    break;
                case 2:
                    //签到成功请刷卡

                    String[] encTrackResultData = (String[]) msg.obj;

                    String allData = encTrackResultData[0];
                    Log.d(TAG, "handleMessage: ==" + allData);

                    //组装磁道数据
                    assembleTracData(encTrackResultData);


                    break;

                default:
                    break;

            }
        }
    };

    //组装提交磁道数据
    private void assembleTracData(String[] encTrackResultData) {

        String isCard = encTrackResultData[1];

        String data55 = encTrackResultData[2];
        String cardNum = encTrackResultData[3];
        String expiryDate = encTrackResultData[4];
        String icSerialNum = encTrackResultData[5];
        String enpinkey = encTrackResultData[6];

        if(enpinkey.equals("FFFFFFFFFFFFFFFF")){
            MainUtils.showToast(QPOSAutographActivity.this,"不支持无密码");
            QPOSAutographActivity.this.finish();

            overridePendingTransition(R.animator.push_right_out,
                    R.animator.push_right_in);
            return;
        }
        String encTrack2 = encTrackResultData[7];
        String encTrack3 = encTrackResultData[8];

        //去掉数据F
        String trackCount =removeStrF(encTrack2,encTrack3);


        if (isCard.equals("0")) {
                        /*ic卡*/
                startToActivity(cardNum, trackCount, data55, expiryDate,icSerialNum,enpinkey);
        } else {
                        /*磁条卡*/
                startToActivity(cardNum,trackCount,enpinkey);
        }


//刷卡

//                    二磁:6226900710283421D491012C7A3DF5B7C1EA0F
//                    三磁:996226900710283421D1561560000000000000003000000510000049122000000000300D98ABA63A272E0F
//                    55域:null
//                    卡号:6226900710283421
//                    IC卡序列号:null
//                    有效时间:null
//                    pin:C40222DF3DE2472E


//                    ic卡

//                    二磁:6217230200000058596D9B00A09158E37D589F
//                    三磁:null
//                    55域:9F260817ADFA7391847B9D9F2701809F101307010103A0A000010A010000000000058A1BF09F3704B54723629F3602007A950580800400009A031705269C01009F02060000000006005F2A02015682027C009F1A0201569F03060000000000009F3303E0F1C89F34030203009F3501229F1E0831323334353637388408A0000003330101019F0902008C9F410400000002
//                    卡号:6217230200000058596
//                    IC卡序列号:001
//                    有效时间:2404
//                    pin:6078BBF3F2449CC6


    }

//    ic
    private void startToActivity( String cardNum, String trackCount, String data55, String expiryDate, String icSerialNum,String enpinkey) {
        Intent intent=null;
        switch (TradeInfo.getPagetates()){
            case YUECHAXUN:
                intent= new Intent(QPOSAutographActivity.this,
                        BalanceInquiryActivity.class);
                break;
            case SHUAKAZHIFU:
                intent= new Intent(QPOSAutographActivity.this,
                        QPOSCommitActivity.class);
                break;
            case SHOUJICHONGZHI:
                intent= new Intent(QPOSAutographActivity.this,
                        QPOSCommitActivity.class);
                break;
        }

        Log.d(TAG, "startToCommitActivity: =="+"cardNum:"+cardNum+"\ntrackCount:"+trackCount+"\ndata55:"+data55+"\nexpiryDate:"+expiryDate+"\nicSerialNum:"+icSerialNum);

        TradeInfo.setCardFlag("IC卡");
        intent.putExtra(TradeInfo.CARDNUM,cardNum);
        intent.putExtra(TradeInfo.TRACKCOUNT,trackCount);
        intent.putExtra(TradeInfo.DATA55,data55);
        intent.putExtra(TradeInfo.EXPIRYDATE,expiryDate);
        intent.putExtra(TradeInfo.ICSERIALNUM,icSerialNum);
        intent.putExtra(TradeInfo.ENPINKEY,enpinkey);
        startActivity(intent);

        overridePendingTransition(R.animator.push_left_in,
                R.animator.push_left_out);
    }
//    磁条
    private void startToActivity( String cardNum, String trackCount,String enpinkey) {
        Intent intent=null;
        switch (TradeInfo.getPagetates()){
            case YUECHAXUN:
                intent= new Intent(QPOSAutographActivity.this,
                        BalanceInquiryActivity.class);
                break;
            case SHUAKAZHIFU:
                intent= new Intent(QPOSAutographActivity.this,
                        QPOSCommitActivity.class);
                break;
            case SHOUJICHONGZHI:
                intent= new Intent(QPOSAutographActivity.this,
                        QPOSCommitActivity.class);
                break;
        }
        Log.d(TAG, "startToCommitActivity: =="+"cardNum:"+cardNum+"\ntrackCount:"+trackCount);

        TradeInfo.setCardFlag("磁条卡");
        intent.putExtra(TradeInfo.CARDNUM,cardNum);
        intent.putExtra(TradeInfo.TRACKCOUNT,trackCount);
        intent.putExtra(TradeInfo.ENPINKEY,enpinkey);
        startActivity(intent);

        overridePendingTransition(R.animator.push_left_in,
                R.animator.push_left_out);
    }

    private String removeStrF(String encTrack2, String encTrack3) {
        String changeEncTrack = null;
        String changeEncTrack2 = null;
        String changeEncTrack3 = null;

        if (encTrack2 != null) {
            char[] data = encTrack2.toCharArray();
            data[data.length - 1] = ' ';
            changeEncTrack2 = String.valueOf(data).trim();
        }
        if (encTrack3 != null && !encTrack3.equals("")) {
            char[] data = encTrack3.toCharArray();
            data[data.length - 1] = ' ';
            changeEncTrack3 = String.valueOf(data).trim();
        }

        Log.d(TAG, "removeStrF: "+changeEncTrack);


        if(changeEncTrack3!=null){
            changeEncTrack= changeEncTrack2 + "|" + changeEncTrack3;
        }else{
            changeEncTrack=changeEncTrack2+"|";
        }

        return changeEncTrack;
    }

    private void CountdownStart() {

        TimerTask task = new TimerTask() {
            int i = 40;

            @Override
            public void run() {
                // TODO Auto-generated method stub
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        i--;
                        countDownTv.setText(String.valueOf(i) + "s");

                        if (i <= 0) {
                            MainUtils.showToast(getApplicationContext(),
                                    "刷卡超时");
                            QPOSAutographActivity.this.finish();
                            overridePendingTransition(R.animator.push_right_out,
                                    R.animator.push_right_in);
                            timer.cancel();
                        }
                    }
                });

            }
        };
        timer.schedule(task, 0, 1000);

    }

    @Override
    public void initParms(Bundle parms) {


    }

    /**
     *
     * 绑定的layout
     * */
    @Override
    public int bindLayout() {
        return R.layout.activity_qposautograph;
    }

    @Override
    public void initView(View view) {
        ButterKnife.bind(this);
        titlebartext.setText("刷卡");
        leftImagemage.setOnClickListener(this);
        btagain.setOnClickListener(this);


        if (TradeInfo.getPagetates() == PageState.SHUAKAZHIFU) {
            scanhuadongview.setText("*支持所有银联标准卡");
        } else {
            scanhuadongview.setText("*支持所有银联借记卡");
        }

        //初始化刷卡显示控件
        initScanhuadongview(0);
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

                    updateWorkingKey(pinKey, macKey, p_check, m_check);

                } else {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            MainUtils.showToast(QPOSAutographActivity.this,
                                    data.get(XmlBuilder.fieldName(124)));
                        }
                    });
                }

            } catch (SAXException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    public void run() {
                        MainUtils.showToast(QPOSAutographActivity.this, "签到异常");
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    public void run() {
                        MainUtils.showToast(QPOSAutographActivity.this, "签到异常");
                    }
                });
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    public void run() {
                        MainUtils.showToast(QPOSAutographActivity.this, "签到异常");
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
                    MainUtils.showToast(QPOSAutographActivity.this, "签到失败");
                }
            });

        }
    }

    private void updateWorkingKey(String PinKey, String MacKey, String p_check, String m_check) {


        if (!PinKey.isEmpty() && !MacKey.isEmpty()) {
            // TODO Auto-generated method stub

            String upMackey = MacKey + m_check;

            String upPinkey = PinKey + p_check;


            new ThreadDoSign(handler, manager, upMackey, upPinkey, upMackey).start();

        } else {
            MainUtils.showToast(getApplicationContext(), "传入工作密钥数据有误");

        }


    }


    /**
     * 通讯测试
     */
    private void controllerBlueTooth() {

        if (bluetoothDevice != null) {
            final String address = bluetoothDevice.getAddress();
            manager.SetBlueToothAddress(address);
            new ThreadGetTerminalIDTest(handler, manager).start();
        } else {
            MainUtils.showToast(getApplicationContext(), "连接错误，获取蓝牙失败");
        }
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
                                    QPOSAutographActivity.this, sn).toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    e.printStackTrace();
                } finally {
                    // tv_message.setText(sn);
                }
            }
        }).start();
    }

    @Override
    public void doBusiness(Context mContext) {
        Intent intent = getIntent();
        isfristbluetooth = intent.getBooleanExtra("isfristbluetooth", true);

        manager = POSManage.getInstance(POSManage.BLUETOOTHMODE);
        if (manager != null) {
            manager.setDebugMode(true);
            bluetoothDevice = getIntent().getParcelableExtra("device");
            // 连接蓝牙设备
            controllerBlueTooth();
        }


    }

    public void initScanhuadongview(int num) {
        if (num == 0) {
            scanhuadongview.setLayoutParm(-1, -1, 1, R.mipmap.new_shuakazhifu_01_img);
            scanhuadongview.setMessge("正在连接设备");
            shuakaTishiTv1.setText(getString(R.string.con_qpos1));
            shuakaTishiTv2.setText(getString(R.string.con_qpos2));
            shuakaTishiTv3.setText(getString(R.string.con_qpos3));
            shuakaTishiTv4.setText(getString(R.string.con_qpos4));

        } else if (num == 1) {
            scanhuadongview.setLayoutParm(-1, -1, -1, R.mipmap.new_shuakazhifu_02_img);
            scanhuadongview.setMessge("请刷卡");
            scanhuadongview.startAnim();

            shuakaTishiTv1.setText(getString(R.string.swipe_qpos1));
            shuakaTishiTv2.setText(getString(R.string.swipe_qpos2));
            shuakaTishiTv3.setText(getString(R.string.swipe_qpos3));
            shuakaTishiTv4.setText(getString(R.string.swipe_qpos4));

        }
    }

    @Override
    public void widgetClick(View v) {

        switch (v.getId()) {
            case R.id.left_Imagemage:
                QPOSAutographActivity.this.finish();
                overridePendingTransition(R.animator.push_right_out,
                        R.animator.push_right_in);
                break;
            case R.id.bt_again:

                btagain.setVisibility(View.GONE);
                controllerBlueTooth();
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
        //取消刷卡记时
        timer.cancel();
        new ThreadGiveup(handler, manager).start();
        manager.Close();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation

        eventBus = EventBus.getDefault();
        eventBus.register(this);
    }


}
