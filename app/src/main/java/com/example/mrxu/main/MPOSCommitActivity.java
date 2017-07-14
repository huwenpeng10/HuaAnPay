package com.example.mrxu.main;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.merxu.mypractice.R;
import com.example.mrxu.alldialog.OkAppDialog;
import com.example.mrxu.allinfo.BankInfo;
import com.example.mrxu.allinfo.Construction;
import com.example.mrxu.allinfo.TradeInfo;
import com.example.mrxu.allinfo.UserInfo;
import com.example.mrxu.assemble_xml.PhoneReplenish;
import com.example.mrxu.assemble_xml.PhoneReplenish_ck;
import com.example.mrxu.assemble_xml.QPOSBlueToothAccountReplenishing;
import com.example.mrxu.assemble_xml.QPOSBlueToothAccountReplenishing_ck;
import com.example.mrxu.base.BaseActivity;
import com.example.mrxu.mipdevicecontroller.DeviceController;
import com.example.mrxu.mipdevicecontroller.DeviceControllerImpl;
import com.example.mrxu.mutils.ByteUtils;
import com.example.mrxu.mutils.Const.PinWKIndexConst;
import com.example.mrxu.mutils.MainUtils;
import com.example.mrxu.mutils.Pin;
import com.example.mrxu.mutils.TagUtils;
import com.example.mrxu.mutils.TcpRequest;
import com.example.mrxu.mutils.TcpRequest.TcpRequestMessage;
import com.example.mrxu.mutils.XmlBuilder;
import com.example.mrxu.mutils.XmlParser;
import com.github.ybq.android.spinkit.style.Circle;
import com.newland.mtype.module.common.pin.WorkingKey;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

public class MPOSCommitActivity extends BaseActivity{

    @BindView(R.id.commit_bankName_tv)
    TextView commitBankNameTv;
    @BindView(R.id.commit_cardNum_tv)
    TextView commitCardNumTv;
    @BindView(R.id.commit_type_tv)
    TextView commitypeTtv;
    @BindView(R.id.commit_money_tv)
    TextView commitMoneyTv;
    @BindView(R.id.commit_userPhoneNum_tv)
    TextView commitUserPhoneNumTv;
    @BindView(R.id.commit_left_iv)
    ImageView commitLeftIv;
    @BindView(R.id.commit_but)
    Button commitBut;
    @BindView(R.id.progressbar_commit)
    ProgressBar progressbarCommit;
    @BindView(R.id.et_password)
    EditText etPassword;

    private Boolean isIcCard;

    private String cardNum;
    private String trackCount;
    private String data55;
    private String expiryDate;
    private String icSerialNum;
    private String isCard;
    private String enpinkey;
    private EventBus eventBus;

    private DeviceController controller = DeviceControllerImpl.getInstance();
    private OkAppDialog okAppDialog;

    @Override
    public void initParms(Bundle parms) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    @Override
    public int bindLayout() {

        return R.layout.activity_mposcommit;
    }

    @Override
    public void initView(View view) {
        ButterKnife.bind(this);
        commitBut.setOnClickListener(this);
        commitLeftIv.setOnClickListener(this);

        Circle circle=new Circle();
        circle.setColor(0xFF3798f4);
        progressbarCommit.setIndeterminateDrawable(circle);
    }

    @Override
    public void doBusiness(Context mContext) {

        isCard = TradeInfo.getCardFlag();
        Intent intent = getIntent();


        if (isCard.equals("IC卡")) {
            isIcCard = true;
            cardNum = intent.getStringExtra(TradeInfo.CARDNUM);
            trackCount = intent.getStringExtra(TradeInfo.TRACKCOUNT);
            data55 = intent.getStringExtra(TradeInfo.DATA55);
            expiryDate = intent.getStringExtra(TradeInfo.EXPIRYDATE);
            icSerialNum = intent.getStringExtra(TradeInfo.ICSERIALNUM);

        } else {
            isIcCard = false;
            cardNum = intent.getStringExtra(TradeInfo.CARDNUM);
            trackCount = intent.getStringExtra(TradeInfo.TRACKCOUNT);
        }



        //初始化银行卡数据
        initBankTapy();
        if(TradeInfo.getPagetates()== TradeInfo.PageState.SHUAKAZHIFU){
            commitypeTtv.setText("账户充值");
            commitUserPhoneNumTv.setText(UserInfo.getPhoneNumber() + "");

        }else if(TradeInfo.getPagetates()== TradeInfo.PageState.SHOUJICHONGZHI){
            commitypeTtv.setText("话费充值");
            commitUserPhoneNumTv.setText(TradeInfo.getReplenishingPhoneNumber()+"");
        }
        commitMoneyTv.setText(TradeInfo.getInputMoney());


    }

    public String getpinkey(String key) {

        WorkingKey wk = new WorkingKey(PinWKIndexConst.DEFAULT_PIN_WK_INDEX);
        String strKey = "";

        byte[] bKey = controller
                .encrypt(wk, ByteUtils.hexString2ByteArray(key));

        for (int i = 0; i < bKey.length; i++)
          strKey = strKey + String.format("%02X", bKey[i]);
        return strKey;
    }


    //初始化银行类型
    private void initBankTapy() {

        String bankName = BankInfo.getname(cardNum.substring(0, 6));
        Log.d(TAG, "initBankTapy: " + bankName);

        String cardNo = cardNum;
        String startNo = cardNo.substring(0, 4);
        String endNo = cardNo.substring(cardNo.length() - 4, cardNo.length());
        commitCardNumTv.setText(startNo + "******" + endNo);

        if (bankName != null) {
            commitBankNameTv.setText(bankName);
        }else{
            commitBankNameTv.setText("未知银行");
        }
    }

    @Override
    public void widgetClick(View v) {
        switch (v.getId()) {

            case R.id.commit_left_iv:

                runError(TradeInfo.getPagetates(), null);

                break;

            case R.id.commit_but:

                switch (TradeInfo.getPagetates()){
                    case SHUAKAZHIFU:

                        commitCardData(1);

                        break;
                    case SHOUJICHONGZHI:

                        commitCardData(2);

                        break;


                    default:
                        break;

                }
                break;
            default:
                break;

        }
    }

    private void commitCardData(final int tapyNum) {


        //账户充值

        enpinkey=etPassword.getText().toString();

        //取到pin做机密
        if(!TextUtils.isEmpty(enpinkey)){

            progressbarCommit.setVisibility(View.VISIBLE);

            new Thread(new Runnable() {
                @Override
                public void run() {


                    String cardDataNum = cardNum.substring(
                            cardNum.length() - 1 - 12,
                            cardNum.length() - 1);// 截取后的卡号
                    String eStrDataNum = (Pin.PinPan(Pin.jiami(cardDataNum),
                            Pin.mima(enpinkey))).toUpperCase();// 要加密的字符串
                    // Pin码
                    enpinkey = getpinkey(eStrDataNum);


                    //拼接磁导数据
                    String tapytrackCount = "0" + "|" + cardNum + "|" + "|"
                            + trackCount + "|" + enpinkey;
                    String newtrackCount = tapytrackCount.replace("=", "D");

                    Log.d(TAG, "doBusiness: " + trackCount);


                    switch (tapyNum) {
                        case 1:


                            try {
                                if (isIcCard) {
//                      IC卡
                                    new TcpRequest(Construction.HOST,
                                            Construction.PORT)
                                            .request(new QPOSBlueToothAccountReplenishing_ck(
                                                    MPOSCommitActivity.this, Double.parseDouble(TradeInfo.getInputMoney()),
                                                    TradeInfo.getDeviceSn(),
                                                    newtrackCount, expiryDate, icSerialNum, data55)
                                                    .toString());

                                    enpinkey = null;

                                } else {

                                    new TcpRequest(Construction.HOST,
                                            Construction.PORT)
                                            .request(new QPOSBlueToothAccountReplenishing(
                                                    MPOSCommitActivity.this, Double.parseDouble(TradeInfo.getInputMoney()),
                                                    TradeInfo.getDeviceSn(),
                                                    newtrackCount)
                                                    .toString());

                                }
                            } catch (Exception e) {
                                progressbarCommit.setVisibility(View.GONE);
                                e.printStackTrace();
                                /**
                                 * 出错情况
                                 * */
                            }
                            break;
                        case 2:
                            //手机充值

                            try {
                                if (isIcCard) {
//                      IC卡
                                    progressbarCommit.setVisibility(View.VISIBLE);
                                    new TcpRequest(Construction.HOST,
                                            Construction.PORT)
                                            .request(new PhoneReplenish_ck(
                                                    MPOSCommitActivity.this, Double.parseDouble(TradeInfo.getInputMoney()),
                                                    TradeInfo.getDeviceSn(),
                                                    newtrackCount,TradeInfo.getReplenishingCode(),TradeInfo.getReplenishingPhoneNumber(), expiryDate, icSerialNum, data55)
                                                    .toString());

                                } else {

//                      磁条卡
                                    progressbarCommit.setVisibility(View.VISIBLE);
                                    new TcpRequest(Construction.HOST,
                                            Construction.PORT)
                                            .request(new PhoneReplenish(
                                                    MPOSCommitActivity.this, Double.parseDouble(TradeInfo.getInputMoney()),
                                                    TradeInfo.getDeviceSn(),
                                                    newtrackCount,TradeInfo.getReplenishingCode(),TradeInfo.getReplenishingPhoneNumber())
                                                    .toString());

                                }
                            } catch (Exception e) {
                                progressbarCommit.setVisibility(View.GONE);
                                e.printStackTrace();
                                /**
                                 * 出错情况
                                 * */
                            }
                            break;

                        default:
                            break;

                    }
                }
            }).start();



        }else{
            MainUtils.showToast(MPOSCommitActivity.this,"请输入密码");
            return;
        }



    }


    public void onEvent(TcpRequestMessage message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressbarCommit.setVisibility(View.GONE);
            }
        });
        if (message.getCode() == 1) {
            TagUtils.Field011Add(this);
            Log.d("Get_Message", new String(message.getTcpRequestMessage()));
            try {
                final HashMap<String, String> data = new XmlParser()
                        .parse(new String(message.getTcpRequestMessage()));

                if (data.get(XmlBuilder.fieldName(39)).equalsIgnoreCase("00")) {
//                    成功
                    runFunction(TradeInfo.getPagetates(), data);
                } else if(data.get(XmlBuilder.fieldName(39)).equalsIgnoreCase("55")){
//                    密码错误
                    passError(data);
                }else{
//                    失败
                    runError(TradeInfo.getPagetates(), data);
                }
            } catch (SAXException e) {
                e.printStackTrace();
                runError(TradeInfo.getPagetates(), null);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParserConfigurationException e) {
                runError(TradeInfo.getPagetates(), null);
                e.printStackTrace();
            }
        } else {
            if (message.getCode() == -2) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        MainUtils.showToast(MPOSCommitActivity.this,
                                "网络连接超时...");
                    }
                });
            }
        }


    }




//    public enum PageState {
//        SHUAKAZHIFU, QIANDAIBAO, ZHUANGZHAN, YUECHAXUN, SHOUJICHONGZHI
//    }

    //成功

    private Intent successIntent, failureIntent;

    private void runFunction(final TradeInfo.PageState function,
                             final HashMap<String, String> data) {

        runOnUiThread(new Runnable() {
            public void run() {
                successIntent = new Intent();
                switch (function) {
                    case SHUAKAZHIFU:
                        successIntent.setClass(MPOSCommitActivity.this,
                                TransactionVoucherActivity.class);
                        successIntent.putExtra(TransactionVoucherActivity.CARDNUM,
                                data.get(XmlBuilder.fieldName(2)));
                        successIntent
                                .putExtra(
                                        TransactionVoucherActivity.KEY_SERIAL_NO,
                                        TagUtils.Field011(MPOSCommitActivity.this));
                        successIntent.putExtra(
                                TransactionVoucherActivity.KEY_DATE,
                                data.get("TxDate"));
                        successIntent.putExtra(
                                TransactionVoucherActivity.KEY_TIME,
                                data.get("TxTime"));
                        successIntent.putExtra(
                                TransactionVoucherActivity.KEY_REF_NO,
                                data.get(XmlBuilder.fieldName(37)));
                        successIntent.putExtra(
                                TransactionVoucherActivity.KEY_MONER,
                                TradeInfo.getInputMoney());
                        successIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        try {

                            startActivity(successIntent);
                            overridePendingTransition(R.animator.push_left_in,
                                    R.animator.push_left_out);
                        } catch (ActivityNotFoundException e) {
                            e.printStackTrace();
                        } finally {
                            finish();
                        }
                        return;


                    case SHOUJICHONGZHI:


                        final OkAppDialog okAppDialog =new OkAppDialog(MPOSCommitActivity.this);
                        okAppDialog.setTitleStr("提示");
                        okAppDialog.setmsgStr(data.get(XmlBuilder.fieldName(124)));
                        okAppDialog.setYesOnclickListener("好的",new OkAppDialog.onYesOnclickListener() {
                            @Override
                            public void onYesClick() {
                                successIntent.setClass(MPOSCommitActivity.this,
                                PhoneReplenishingActivity.class);
                                successIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                try {

                                    startActivity(successIntent);
                                    overridePendingTransition(R.animator.push_left_in,
                                            R.animator.push_left_out);
                                } catch (ActivityNotFoundException e) {
                                    e.printStackTrace();
                                } finally {
                                    finish();
                                }
                                okAppDialog.dismiss();
                            }
                        });


                        okAppDialog.show();
                        break;
                    default:
                        break;
                }

            }
        });
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode==event.KEYCODE_BACK){

            runError(TradeInfo.getPagetates(), null);
        }
        return true;
    }


/**
 * 密码错误
 * */
    private void passError(final HashMap<String,String> data) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                okAppDialog = new OkAppDialog(MPOSCommitActivity.this);
                String msg = data.get(XmlBuilder.fieldName(124));
                okAppDialog.setCanceledOnTouchOutside(false);
                okAppDialog.setTitleStr("失败原因");
                okAppDialog.setmsgStr(msg+",请重新输入");
                okAppDialog.setYesOnclickListener("好的", new OkAppDialog.onYesOnclickListener() {

                    @Override
                    public void onYesClick() {
                        okAppDialog.dismiss();
                        overridePendingTransition(R.animator.push_down_in,
                                R.animator.push_down_out);
                    }
                });
                okAppDialog.show();
            }
        });
    }



    //失败


    private void runError(final TradeInfo.PageState function,
                          final HashMap<String, String> data) {

        /**
         * 销毁设备控制器
         */

        runOnUiThread(new Runnable() {
            public void run() {
                failureIntent = new Intent();

                switch (function) {
                    case SHUAKAZHIFU:
                        failureIntent.setClass(MPOSCommitActivity.this,
                                ReplenishingActivity.class);
                        break;
                    case SHOUJICHONGZHI:
                        failureIntent.setClass(MPOSCommitActivity.this,
                                PhoneReplenishingActivity.class);
                        break;
                    default:
                        break;
                }
                failureIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);


                if (data == null) {

                    try {
                        startActivity(failureIntent);
                        overridePendingTransition(R.animator.push_down_in,
                                R.animator.push_down_out);
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                    } finally {
                        finish();
                    }
                } else {

                    okAppDialog = new OkAppDialog(MPOSCommitActivity.this);
                    String msg = data.get(XmlBuilder.fieldName(124));
                    okAppDialog.setCanceledOnTouchOutside(false);
                    okAppDialog.setTitleStr("失败原因");
                    okAppDialog.setmsgStr(msg);
                    okAppDialog.setYesOnclickListener("好的", new OkAppDialog.onYesOnclickListener() {

                        @Override
                        public void onYesClick() {
                            okAppDialog.dismiss();
                            startActivity(failureIntent);
                            overridePendingTransition(R.animator.push_down_in,
                                    R.animator.push_down_out);
                        }
                    });
                    okAppDialog.show();
                }
            }
        });
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

        /**
         * 销毁设备控制器
         */

        DeviceControllerImpl.controller.disConnect();

        DeviceControllerImpl.controller.destroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        eventBus = EventBus.getDefault();
    }
}
