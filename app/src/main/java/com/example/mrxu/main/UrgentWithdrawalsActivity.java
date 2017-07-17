package com.example.mrxu.main;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.merxu.mypractice.R;
import com.example.mrxu.alldialog.OkAppDialog;
import com.example.mrxu.allinfo.Construction;
import com.example.mrxu.allinfo.UserInfo;
import com.example.mrxu.assemble_xml.UrgentWithdrawals;
import com.example.mrxu.base.BaseActivity;
import com.example.mrxu.mutils.MainUtils;
import com.example.mrxu.mutils.TagUtils;
import com.example.mrxu.mutils.TcpRequest;
import com.example.mrxu.mutils.TcpRequest.TcpRequestMessage;
import com.example.mrxu.mutils.XmlBuilder;
import com.example.mrxu.mutils.XmlParser;
import com.github.ybq.android.spinkit.style.Circle;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 *
 * 加急提现页面
 *
 * @author MrXu
 * */

public class UrgentWithdrawalsActivity extends BaseActivity {

    @BindView(R.id.left_Imagemage)
    ImageView leftImagemage;
    @BindView(R.id.title_bar_text)
    TextView titlebartext;

    @BindView(R.id.repl_username_tv)
    TextView replUsernameTv;
    @BindView(R.id.repl_bankName_tv)
    TextView replBankNameTv;
    @BindView(R.id.repl_bankNum_tv)
    TextView replBankNumTv;
    @BindView(R.id.authState_iv)
    ImageView authStateIv;
    @BindView(R.id.ungent_withdrawals_but)
    Button ungentWithdrawalsBut;
    @BindView(R.id.ungent_withdraw_pro)
    ProgressBar ungentWithdrawPro;
    private EventBus eventBus;
    private String withdrawAmount;
    private String txdate;
    private String txTime;
    private String txserialNumber;
    private String txphoneNum;
    private String txcheckCode;
    private String txphoneCheckCode;
    private OkAppDialog successOkAppDialog;
    private OkAppDialog errorOkAppDialog;

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
        return R.layout.activity_urgent_withdrawals;
    }

    @Override
    public void initView(View view) {
        ButterKnife.bind(this);


        Circle circle =new Circle();
        circle.setColor(0xFF58a5ee);
        ungentWithdrawPro.setIndeterminateDrawable(circle);

        titlebartext.setText("加急提现");
        leftImagemage.setOnClickListener(this);
        ungentWithdrawalsBut.setOnClickListener(this);

    }

    @Override
    public void doBusiness(Context mContext) {

        initUserInfo();
    }

    @Override
    public void widgetClick(View v) {

        switch (v.getId()){
            case R.id.left_Imagemage :

                finish();
                overridePendingTransition(R.animator.push_right_out,
                        R.animator.push_right_in);

                break;
            case R.id.ungent_withdrawals_but :

                withdrawals();

                break;


            default:
                break;

        }
    }

    private void withdrawals(){//加急提现
        ungentWithdrawPro.setVisibility(View.VISIBLE);
        try {
            new TcpRequest(Construction.HOST, Construction.PORT)
                    .request(new UrgentWithdrawals(UrgentWithdrawalsActivity.this).toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initUserInfo(){

        replUsernameTv.setText(UserInfo.getUserName());
        replBankNameTv.setText(UserInfo.getBankName());

        String cardNo = UserInfo.getBindingCardNo()+"";
        String startNo = cardNo.substring(0, 4);
        String endNo = cardNo.substring(cardNo.length() - 4, cardNo.length());
        replBankNumTv.setText(startNo + "******" + endNo);

        if (UserInfo.getAuthTipsNumber()==1){
            authStateIv.setImageDrawable(getResources().getDrawable(R.mipmap.new_zhanghuxinxi_renzheng_img));
        }else{
            authStateIv.setImageDrawable(getResources().getDrawable(R.mipmap.new_zhanghuxinxi_weirenzheng_img));
        }
    }

    public void onEvent(TcpRequestMessage message){

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ungentWithdrawPro.setVisibility(View.GONE);
            }
        });

        if (message.getCode() == 1) {
            TagUtils.Field011Add(this);
            Log.d("Get_Message", new String(message.getTcpRequestMessage()));
            try {
                HashMap<String, String> data = new XmlParser()
                        .parse(new String(message.getTcpRequestMessage()));


                if((data.get(XmlBuilder.fieldName(39))).equals("00")){


                    withdrawAmount = data.get(XmlBuilder.fieldName(54));
                    txdate=data.get("TxDate");
                    txTime=data.get("TxTime");
                    txserialNumber=data.get(XmlBuilder.fieldName(11));
                    txphoneNum=UserInfo.getPhoneNumber()+"";
                    txcheckCode=data.get(XmlBuilder.fieldName(128));


                    try {
                        txphoneCheckCode=TagUtils.MAC(
                                TagUtils.TxCode(10007), txdate,
                                txTime, txserialNumber,
                                txphoneNum);
                    } catch (NoSuchAlgorithmException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    if(txcheckCode.equals(txphoneCheckCode))
                    {
                        new Thread() {
                            public void run() {
                                success();
                            }
                        }.start();
                    }else
                    {
                        MainUtils.showToast(getApplicationContext(), "校验失败");
                    }
                }else{

                    error(data.get(XmlBuilder.fieldName(124)));
                }
            } catch (SAXException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

                error("");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                error("");
            } catch (ParserConfigurationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                error("");
            }
        }else{
            if(message.getCode()==-2){
                runOnUiThread(new Runnable() {
                    public void run() {
                        MainUtils.showToast(getApplicationContext(),"网络连接超时...");
                    }
                });
            }
        }
    }

//    失败
    private void error(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                errorOkAppDialog =new OkAppDialog(UrgentWithdrawalsActivity.this);
                errorOkAppDialog.setTitleStr("提示");
                if(!TextUtils.isEmpty(msg)){
                    errorOkAppDialog.setmsgStr(msg);
                }else{
                    errorOkAppDialog.setmsgStr("异常");
                }
                errorOkAppDialog.setYesOnclickListener("好的",new OkAppDialog.onYesOnclickListener() {
                    @Override
                    public void onYesClick() {

                        errorOkAppDialog.dismiss();

                    }
                });
                errorOkAppDialog.show();
            }
        });
    }

//    成功
    private void success() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                successOkAppDialog=new OkAppDialog(UrgentWithdrawalsActivity.this);
                errorOkAppDialog.setTitleStr("提示");
                errorOkAppDialog.setmsgStr("已提现全部金额："+new BigDecimal(withdrawAmount)
                        .divide(new BigDecimal(100)).doubleValue());
                errorOkAppDialog.setYesOnclickListener("好的",new OkAppDialog.onYesOnclickListener() {
                    @Override
                    public void onYesClick() {

                        errorOkAppDialog.dismiss();

                    }
                });
                successOkAppDialog.show();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (!eventBus.isRegistered(this)){
            eventBus.register(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        eventBus.unregister(this);
    }
}
