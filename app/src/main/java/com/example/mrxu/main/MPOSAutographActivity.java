package com.example.mrxu.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.merxu.mypractice.R;
import com.example.mrxu.allinfo.TradeInfo;
import com.example.mrxu.mipdevicecontroller.DeviceControllerImpl;
import com.example.mrxu.mipdevicecontroller.TransferListener;
import com.example.mrxu.mutils.ByteUtils;
import com.example.mrxu.mutils.Const.DataEncryptWKIndexConst;
import com.example.mrxu.mutils.MainUtils;
import com.example.mrxu.myviews.ScanHuaDongView;
import com.newland.mtype.ModuleType;
import com.newland.mtype.common.MESeriesConst.TrackEncryptAlgorithm;
import com.newland.mtype.module.common.emv.EmvTransController;
import com.newland.mtype.module.common.emv.EmvTransInfo;
import com.newland.mtype.module.common.swiper.SwipResult;
import com.newland.mtype.module.common.swiper.SwipResultType;
import com.newland.mtype.module.common.swiper.Swiper;
import com.newland.mtype.module.external.me11.ME11SwipResult;
import com.newland.mtype.tlv.TLVPackage;
import com.newland.mtype.util.Dump;
import com.newland.mtype.util.ISOUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.mrxu.main.MPOSConnectActivity.mposConnectActivity;

public class MPOSAutographActivity extends DeviceControllerImpl {

    @BindView(R.id.mops_huadongview)
    ScanHuaDongView mopsHuadongview;
    @BindView(R.id.title_bar_text)
    TextView titleBar;
    @BindView(R.id.left_Imagemage)
    ImageView leftImagemage;
    @BindView(R.id.McountDown_tv)
    TextView countDownTv;
    @BindView(R.id.bt_again)
    Button btAgain;
    private Timer timer=new Timer();
    private String strDate;
    private String cardNumber;
    private String secondTrack;
    private String thirdTrack;
    private String TrackCount;

    private String cardExpirationDate;
    private String sequenceNumber;
    private String data_55;

    //控制器实例
    public static EmvTransController controller;

    private SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
    private boolean cardtepy=true;

    private static List<Integer> L_55TAGS = new ArrayList<Integer>();
    static {
        L_55TAGS.add(0x9f26);
        L_55TAGS.add(0x9F27);
        L_55TAGS.add(0x9F10);
        L_55TAGS.add(0x9F37);
        L_55TAGS.add(0x9F36);
        L_55TAGS.add(0x95);
        L_55TAGS.add(0x9A);
        L_55TAGS.add(0x9C);
        L_55TAGS.add(0x9F02);
        L_55TAGS.add(0x5F2A);
        L_55TAGS.add(0x82);
        L_55TAGS.add(0x9F1A);
        L_55TAGS.add(0x9F03);
        L_55TAGS.add(0x9F33);
        L_55TAGS.add(0x9F34);
        L_55TAGS.add(0x9F35);
        L_55TAGS.add(0x9F1E);
        L_55TAGS.add(0x84);
        L_55TAGS.add(0x9F09);
        L_55TAGS.add(0x9F41);
        L_55TAGS.add(0x9F63);
    }


    private static final int DetectionIC = 277;
    private static final int SwipeFail = 278;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DetectionIC:

                    mopsHuadongview.setMessge("检测到IC卡插入,请稍等...");
                    break;
                case SwipeFail:

                    MainUtils.showToast(MPOSAutographActivity.this,"刷卡失败，请重新刷卡");

                    if(!MPOSAutographActivity.this.isFinishing()){

                        SwipeCard();
                    }
                    break;
                default:
                    break;
            }
        };
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        return R.layout.activity_mposautograph;
    }

    @Override
    public void initView(View view) {
        ButterKnife.bind(this);
        titleBar.setText("刷卡");
        leftImagemage.setOnClickListener(this);
        btAgain.setOnClickListener(this);
        initmopsHuadongview();
        CountdownStart();

    }

    @Override
    public void doBusiness(Context mContext) {
        SwipeCard();
    }

    @Override
    public void widgetClick(View v) {

        switch (v.getId()){
            case R.id.left_Imagemage :
                /**
                 * 销毁设备控制器
                 */
                DeviceControllerImpl.controller.destroy();
                /** 销毁状态 */
                processing = true;
                mposConnectActivity.finish();
                finish();
                overridePendingTransition(R.animator.push_right_out,
                        R.animator.push_right_in);
                break;
            case R.id.bt_again :

                CountdownStart();
                processing = false;
                break;


                default:
                break;

        }
    }




    /** 刷卡 */
    private void SwipeCard() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                processing = true;
                try {
                    Date curDate = new Date(System.currentTimeMillis());// 获取当前时间,建议从后台获取(用于PBOC交易)
                    strDate = formatter.format(curDate);
                    ME11SwipResult swipRslt = DeviceControllerImpl.controller
                            .swipCard_me11(ByteUtils
                                    .hexString2ByteArray(strDate.substring(2)),
                            120000L, TimeUnit.MILLISECONDS);

                    ModuleType[] moduleType = swipRslt.getReadModels();
/**
*
 * 磁条卡
* */
                    if (moduleType[0] == ModuleType.COMMON_SWIPER) {
                        cardtepy = false;
                        byte[] secondTrackByte = swipRslt.getSecondTrackData();
                        byte[] thirdTrackByte = swipRslt.getThirdTrackData();
                        cardNumber = swipRslt.getAccount().getAcctNo();
                        secondTrack = new String(secondTrackByte, "ISO-8859-1");
                        byte[] thirdTrackdata = swipRslt.getThirdTrackData();
                        if ( thirdTrackdata == null) {
                            thirdTrack = "";
                        } else {
                            thirdTrack = new String(thirdTrackByte,
                                    "ISO-8859-1");
                        }
                        TrackCount = secondTrack + "|" + thirdTrack;
                        TrackCount = TrackCount.replace(" ", "");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                startToActivity(cardNumber,TrackCount);
                            }
                        });
/**
 *
 * ic卡
 * */
                    } else if (moduleType[0] == ModuleType.COMMON_ICCARD) {
                        cardtepy = true;

                        Message message = Message.obtain();
                        message.what =DetectionIC;
                        handler.sendMessage(message);

                        if(TradeInfo.getPagetates()== TradeInfo.PageState.YUECHAXUN)
                        {
                            DeviceControllerImpl.controller.startEmv(
                                    new BigDecimal("00"),
                                    new SimpleTransferListener());
                            swipRslt.getSecondTrackData();
                        }else if(TradeInfo.getPagetates()== TradeInfo.PageState.SHUAKAZHIFU){
                            DeviceControllerImpl.controller.startEmv(
                                    new BigDecimal(Double.parseDouble(TradeInfo.getInputMoney())),
                                    new SimpleTransferListener());
                            swipRslt.getSecondTrackData();
                        }else if(TradeInfo.getPagetates()== TradeInfo.PageState.SHOUJICHONGZHI){
                            DeviceControllerImpl.controller.startEmv(
                                    new BigDecimal(Double.parseDouble(TradeInfo.getInputMoney())),
                                    new SimpleTransferListener());
                            swipRslt.getSecondTrackData();
                        }


                    }
                } catch (Exception e) {
                    Message message = Message.obtain();
                    message.what =SwipeFail;
                    handler.sendMessage(message);

                    return;
                }
                processing = false;
            }
        }).start();

    }



//ic卡
    private void startToActivity( String cardNum, String trackCount, String data55, String expiryDate, String icSerialNum) {
        Intent intent=null;
        switch (TradeInfo.getPagetates()){
            case YUECHAXUN:
                intent= new Intent(MPOSAutographActivity.this,
                        BalanceInquiryActivity.class);
                finish();
                break;
            case SHUAKAZHIFU:
                intent= new Intent(MPOSAutographActivity.this,
                        MPOSCommitActivity.class);
                finish();
                break;
            case SHOUJICHONGZHI:
                intent= new Intent(MPOSAutographActivity.this,
                        MPOSCommitActivity.class);
                finish();
                break;
        }

        TradeInfo.setCardFlag("IC卡");
        intent.putExtra(TradeInfo.CARDNUM,cardNum);
        intent.putExtra(TradeInfo.TRACKCOUNT,trackCount);
        intent.putExtra(TradeInfo.DATA55,data55);
        intent.putExtra(TradeInfo.EXPIRYDATE,expiryDate);
        intent.putExtra(TradeInfo.ICSERIALNUM,icSerialNum);
        startActivity(intent);
        overridePendingTransition(R.animator.push_left_in,
                R.animator.push_left_out);
        timer.cancel();
    }
    //    磁条
    private void startToActivity( String cardNum, String trackCount) {
        Intent intent=null;
        switch (TradeInfo.getPagetates()){
            case YUECHAXUN:
                intent= new Intent(MPOSAutographActivity.this,
                        BalanceInquiryActivity.class);
                finish();
                break;
            case SHUAKAZHIFU:
                intent= new Intent(MPOSAutographActivity.this,
                        MPOSCommitActivity.class);
                finish();
                break;
            case SHOUJICHONGZHI:
                intent= new Intent(MPOSAutographActivity.this,
                        MPOSCommitActivity.class);
                finish();
                break;
        }
        Log.d(TAG, "startToCommitActivity: =="+"cardNum:"+cardNum+"\ntrackCount:"+trackCount);

        TradeInfo.setCardFlag("磁条卡");
        intent.putExtra(TradeInfo.CARDNUM,cardNum);
        intent.putExtra(TradeInfo.TRACKCOUNT,trackCount);
        startActivity(intent);
        overridePendingTransition(R.animator.push_left_in,
                R.animator.push_left_out);

        timer.cancel();

    }



    private SwipResult getSwip() {
        SwipResult swipRslt = null;
        try {
            Swiper swiper = (Swiper) deviceManager.getDevice().getStandardModule(
                    ModuleType.COMMON_SWIPER);

            swipRslt = getSwipResult(swiper,
                    DataEncryptWKIndexConst.DEFAULT_TRACK_WK_INDEX,
                    TrackEncryptAlgorithm.BY_UNIONPAY_MODEL);
            if (swipRslt.getRsltType() == SwipResultType.SUCCESS) {
                return swipRslt;
            }
        } catch (Exception e) {
            MainUtils.showToast(MPOSAutographActivity.this,"请重新插入IC卡");
        }
        return swipRslt;
    }

//    Ic卡数据回调
    private class SimpleTransferListener implements TransferListener {
        @Override
        public void onEmvFinished(boolean arg0, EmvTransInfo context)
                throws Exception {
        }
        @Override
        public void onError(EmvTransController arg0, Exception arg1) {
        }
        @Override
        public void onFallback(EmvTransInfo arg0) throws Exception {
        }
        @Override
        public void onRequestOnline(EmvTransController controller,
                                    EmvTransInfo context) throws Exception {
            cardExpirationDate = context.getCardExpirationDate();
            sequenceNumber = "00" + context.getCardSequenceNumber();
            cardNumber = context.getCardNo();
            String Track_2_eqv_data = context.getTrack_2_eqv_data() == null ? "无返回"
                    : Dump.getHexDump(context.getTrack_2_eqv_data());
            SwipResult result = getSwip();
            secondTrack = new String(result.getSecondTrackData(), "ISO-8859-1");
            TrackCount = secondTrack + "|";

            TLVPackage tlvPackage = context.setExternalInfoPackage(L_55TAGS);
            data_55 = ISOUtils.hexString(tlvPackage.pack());
            MPOSAutographActivity.this.controller = controller;

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                     String cardNum, String trackCount, String data55, String expiryDate, String icSerialNum
                    startToActivity(cardNumber,TrackCount,data_55,cardExpirationDate,sequenceNumber);

                }
            });
        }
        @Override
        public void onRequestPinEntry(EmvTransController arg0, EmvTransInfo arg1)
                throws Exception {
            arg0.cancelEmv();
        }
        @Override
        public void onRequestSelectApplication(EmvTransController arg0,
                                               EmvTransInfo arg1) throws Exception {
            arg0.cancelEmv();
        }

        @Override
        public void onRequestTransferConfirm(EmvTransController arg0,
                                             EmvTransInfo arg1) throws Exception {
            arg0.cancelEmv();
        }

        @Override
        public void onSwipMagneticCard(SwipResult swipRslt) {

        }

        @Override
        public void onOpenCardreaderCanceled() {

        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();


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

                            /**
                             * 销毁设备控制器
                             */
                            DeviceControllerImpl.controller.destroy();
                            /** 销毁状态 */
                            processing = true;

                            /**
                             *
                             * 刷卡超时返回充值页面
                             *
                             *
                             * */

                            mposConnectActivity.finish();
                            MPOSAutographActivity.this.finish();
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

    //初始化滑动动画和剩余时间

    public void initmopsHuadongview() {

            mopsHuadongview.setLayoutParm(-1, -1, -1, R.mipmap.new_shuakazhifu_02_img);
            mopsHuadongview.setMessge("请刷卡");
            mopsHuadongview.startAnim();

    }

    @Override
    protected void onStop() {
        super.onStop();
        timer.cancel();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            /**
             * 销毁设备控制器
             */
            DeviceControllerImpl.controller.destroy();
            /** 销毁状态 */
            processing = true;
            mposConnectActivity.finish();
            finish();
            overridePendingTransition(R.animator.push_right_out,
                    R.animator.push_right_in);
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}
