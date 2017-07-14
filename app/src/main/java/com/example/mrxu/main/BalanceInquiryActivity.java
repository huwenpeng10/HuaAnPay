package com.example.mrxu.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.example.merxu.mypractice.R;
import com.example.mrxu.alldialog.OkAppDialog;
import com.example.mrxu.allinfo.BankInfo;
import com.example.mrxu.allinfo.Construction;
import com.example.mrxu.allinfo.TradeInfo;
import com.example.mrxu.assemble_xml.QPOSBlueToothBalance;
import com.example.mrxu.assemble_xml.QPOSBlueToothBalance_ck;
import com.example.mrxu.base.BaseActivity;
import com.example.mrxu.mipdevicecontroller.DeviceController;
import com.example.mrxu.mipdevicecontroller.DeviceControllerImpl;
import com.example.mrxu.mutils.ByteUtils;
import com.example.mrxu.mutils.Const;
import com.example.mrxu.mutils.MainUtils;
import com.example.mrxu.mutils.NetUtil;
import com.example.mrxu.mutils.TagUtils;
import com.example.mrxu.mutils.TcpRequest;
import com.example.mrxu.mutils.TcpRequest.TcpRequestMessage;
import com.example.mrxu.mutils.XmlBuilder;
import com.example.mrxu.mutils.XmlParser;
import com.github.ybq.android.spinkit.style.ThreeBounce;
import com.newland.mtype.module.common.emv.SecondIssuanceRequest;
import com.newland.mtype.module.common.pin.WorkingKey;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

public class BalanceInquiryActivity extends BaseActivity implements OnEditorActionListener {


    @BindView(R.id.left_Imagemage)
    ImageView leftImagemage;
    @BindView(R.id.title_bar_text)
    TextView titlebartext;

    @BindView(R.id.balace_pro)
    ProgressBar balacePro;
    @BindView(R.id.commit_bankName_tv)
    TextView commitBankNameTv;
    @BindView(R.id.commit_cardNum_tv)
    TextView commitCardNumTv;
    @BindView(R.id.balace_money_tv)
    TextView moneyTv;
    @BindView(R.id.yuantext)
    TextView yuantext;
    @BindView(R.id.balaceinquiry_bt)
    Button balaceinquiryBt;
    @BindView(R.id.balance_pass_et)
    EditText balancePassEt;

    private DeviceController controller = DeviceControllerImpl.getInstance();
    private String trackCount;
    private String enpinkey;
    private String isCard;
    private boolean isIcCard;
    private String cardNum;
    private String data55;
    private String expiryDate;
    private String icSerialNum;
    private EventBus eventBus;
    private double balace;
    private String card_no;
    private String edPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        eventBus = EventBus.getDefault();
        eventBus.register(this);

    }

    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        return R.layout.activity_balance_inquiry;

    }

    @Override
    public void initView(View view) {
        ButterKnife.bind(this);
        balancePassEt.setOnEditorActionListener(this);
        ThreeBounce chasingDots = new ThreeBounce();
        chasingDots.setColor(0xFF0097f8);
        balacePro.setIndeterminateDrawable(chasingDots);

    }

    @Override
    public void doBusiness(Context mContext) {


        leftImagemage.setOnClickListener(this);
        balaceinquiryBt.setOnClickListener(this);
        titlebartext.setText("余额查询");

        isCard = TradeInfo.getCardFlag();
        Intent intent = getIntent();


        cardNum = intent.getStringExtra(TradeInfo.CARDNUM);
        trackCount = intent.getStringExtra(TradeInfo.TRACKCOUNT);
        enpinkey = intent.getStringExtra(TradeInfo.ENPINKEY);

        if (isCard.equals("IC卡")) {
            isIcCard = true;
            data55 = intent.getStringExtra(TradeInfo.DATA55);
            expiryDate = intent.getStringExtra(TradeInfo.EXPIRYDATE);
            icSerialNum = intent.getStringExtra(TradeInfo.ICSERIALNUM);

        } else {
            isIcCard = false;
        }


        //查询银行类型
        initBankTapy();


        //判断蓝牙QPOS的时候自动查询
        if (TradeInfo.getPosName().equals(getString(R.string.Qpos))) {
            balancePassEt.setVisibility(View.GONE);
            balaceinquiryBt.setVisibility(View.GONE);
            balancelnQuery(enpinkey);
        } else {
            balaceinquiryBt.setVisibility(View.VISIBLE);
            balancePassEt.setVisibility(View.VISIBLE);
        }
    }


    //查询余额
    private void balancelnQuery(String password) {

        balacePro.setVisibility(View.VISIBLE);

        //拼接磁导数据
        trackCount = "0" + "|" + cardNum + "|" + "|"
                + trackCount + "|" + password;
        trackCount = trackCount.replace("=", "D");

        Log.d(TAG, "doBusiness: " + trackCount);
        enpinkey = null;

        try {
            if (isCard.equals("IC卡")) {

                isIcCard = true;
                new TcpRequest(Construction.HOST,
                        Construction.PORT).request(new QPOSBlueToothBalance_ck(
                        this,
                        TradeInfo.getDeviceSn(),
                        trackCount, expiryDate, data55, icSerialNum)
                        .toString());
            } else if (isCard.equals("磁条卡")) {


                isIcCard = false;
                new TcpRequest(Construction.HOST,
                        Construction.PORT)
                        .request(new QPOSBlueToothBalance(
                                this,
                                TradeInfo.getDeviceSn(), trackCount)
                                .toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
            runError(null);
        }
    }


    public void onEvent(TcpRequestMessage message) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                balacePro.setVisibility(View.GONE);
            }
        });
        if (message.getCode() == 1) {
            TagUtils.Field011Add(this);
            Log.d("Get_Message", new String(message.getTcpRequestMessage()));
            try {
                final HashMap<String, String> data = new XmlParser()
                        .parse(new String(message.getTcpRequestMessage()));



                if(isIcCard){
                    SecondIssuanceRequest request = new SecondIssuanceRequest();
                    request.setAuthorisationResponseCode(data.get(XmlBuilder
                            .fieldName(39)));
                    MPOSAutographActivity.controller.secondIssuance(request);
                }

                if (data.get(XmlBuilder.fieldName(39)).equalsIgnoreCase("00")) {

                    runFunction(data);

                } else {

                    runError(data.get(XmlBuilder.fieldName(124)));

                }
            } catch (SAXException e) {
                e.printStackTrace();
                runError(null);
            } catch (IOException e) {
                e.printStackTrace();
                runError(null);
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
                runError(null);
            }
        } else {
            if (message.getCode() == -2) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        MainUtils.showToast(getApplicationContext(),
                                "网络连接超时...");

                    }
                });
                runError(null);
            }
        }

    }  //初始化银行类型

    private void initBankTapy() {

        String bankName = BankInfo.getname(cardNum.substring(0, 6));
        Log.d(TAG, "initBankTapy: " + bankName);

        String cardNo = cardNum;
        String startNo = cardNo.substring(0, 4);
        String endNo = cardNo.substring(cardNo.length() - 4, cardNo.length());
        commitCardNumTv.setText(startNo + "******" + endNo);

        if (bankName != null) {
            commitBankNameTv.setText(bankName);
        } else {
            commitBankNameTv.setText("未知银行");
        }
    }

    private void runFunction(final HashMap<String, String> data) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                balaceinquiryBt.setVisibility(View.GONE);
                balace = new BigDecimal(data.get(XmlBuilder.fieldName(54))).divide(new BigDecimal(100)).doubleValue();
                moneyTv.setText(balace + "");
                yuantext.setVisibility(View.VISIBLE);

            }
        });

    }

    private void runError(final String msg) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                MainUtils.showToast(BalanceInquiryActivity.this, "查询失败");
                yuantext.setVisibility(View.INVISIBLE);
                {

                    final OkAppDialog okAppDialog = new OkAppDialog(BalanceInquiryActivity.this);
                    okAppDialog.setCanceledOnTouchOutside(false);
                    okAppDialog.setTitleStr("失败原因");
                    okAppDialog.setmsgStr(msg);
                    okAppDialog.setYesOnclickListener("好的", new OkAppDialog.onYesOnclickListener() {

                        @Override
                        public void onYesClick() {
                            okAppDialog.dismiss();
                            Intent intent=new Intent(BalanceInquiryActivity.this,MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
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
    public void widgetClick(View v) {

        switch (v.getId()) {
            case R.id.left_Imagemage:

                Intent intent = new Intent(BalanceInquiryActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.animator.push_right_out,
                        R.animator.push_right_in);
                break;
            case R.id.balaceinquiry_bt:


                edPassword = balancePassEt.getText().toString();
                    if (!TextUtils.isEmpty(edPassword)) {
                        if (edPassword.length()==6) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            balacePro.setVisibility(View.VISIBLE);
                                        }
                                    });
                                    balancelnQuery(getpinkey(edPassword));
                                }
                            }).start();

                            balancePassEt.setText("");
                        }else{
                            balancePassEt.setText("");
                            MainUtils.showToast(this, "密码输入错误，请重新输入");
                        }

                    } else {
                        MainUtils.showToast(this, "请输入密码");
                    }

                break;


            default:
                break;

        }
    }

    public String getpinkey(String key) {
        WorkingKey wk = new WorkingKey(Const.PinWKIndexConst.DEFAULT_PIN_WK_INDEX);
        String strKey = "";

        byte[] bKey = controller
                .encrypt(wk, ByteUtils.hexString2ByteArray(key));

        for (int i = 0; i < bKey.length; i++)
            strKey = strKey + String.format("%02X", bKey[i]);
        return strKey;
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
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (v == balancePassEt) {

            if (NetUtil.getNetWorkState(this) != -1) {

                edPassword = balancePassEt.getText().toString();
                if (!TextUtils.isEmpty(edPassword)) {

                    if (edPassword.length()==6) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        balacePro.setVisibility(View.VISIBLE);
                                    }
                                });
                                balancelnQuery(getpinkey(edPassword));
                            }
                        }).start();

                        balancePassEt.setText("");
                    }else{
                        balancePassEt.setText("");
                        MainUtils.showToast(this, "密码输入错误，请重新输入");
                    }
                }
            } else {
                MainUtils.showToast(this, "请检查网络连接...");
            }
        }

        return false;
    }

}


