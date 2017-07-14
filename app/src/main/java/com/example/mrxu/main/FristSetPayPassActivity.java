package com.example.mrxu.main;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.merxu.mypractice.R;
import com.example.mrxu.allinfo.Construction;
import com.example.mrxu.allinfo.UserInfo;
import com.example.mrxu.assemble_xml.SetPayPassword;
import com.example.mrxu.base.BaseActivity;
import com.example.mrxu.mutils.MainUtils;
import com.example.mrxu.mutils.TagUtils;
import com.example.mrxu.mutils.TcpRequest;
import com.example.mrxu.mutils.TcpRequest.TcpRequestMessage;
import com.example.mrxu.mutils.XmlBuilder;
import com.example.mrxu.mutils.XmlParser;
import com.example.mrxu.myviews.PwdEditText;
import com.github.ybq.android.spinkit.style.Circle;
import com.zhy.autolayout.AutoRelativeLayout;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

public class FristSetPayPassActivity extends BaseActivity implements PwdEditText.OnInputFinishListener {


    @BindView(R.id.title_bar_text)
    TextView titleBarText;
    @BindView(R.id.left_Imagemage)
    ImageView leftImagemage;

    @BindView(R.id.frist_paypassword_et)
    PwdEditText fristPaypasswordEt;
    @BindView(R.id.frist_loginpassword_et)
    EditText fristLoginpasswordEt;
    @BindView(R.id.pay_pass_layout_bottom)
    AutoRelativeLayout payPassLayoutBottom;
    @BindView(R.id.frist_setpaypass_but)
    Button frist_setpaypassBut;
    @BindView(R.id.frist_paypassword_pro)
    ProgressBar frist_paypasswordPro;

    private String payPassword;
    private String loginPassword;
    private EventBus eventBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        eventBus= EventBus.getDefault();
        eventBus.register(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!eventBus.isRegistered(this)){

            eventBus.register(this);
        }
    }

    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        return R.layout.activity_fristsetpass;
    }

    @Override
    public void initView(View view) {
        ButterKnife.bind(this);


        Circle circle=new Circle();
        circle.setColor(0xFF3798f4);
        frist_paypasswordPro.setIndeterminateDrawable(circle);

        titleBarText.setText("设置支付密码");
        leftImagemage.setOnClickListener(this);
        frist_setpaypassBut.setOnClickListener(this);
        fristPaypasswordEt.setOnInputFinishListener(this);

    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

        switch (v.getId()){
            case R.id.left_Imagemage :

                this.finish();
                overridePendingTransition(R.animator.push_right_out,
                        R.animator.push_right_in);
                break;
            case R.id.frist_setpaypass_but :

                if(TextUtils.isEmpty(payPassword)){

                    MainUtils.showToast(this,"请输入支付密码");
                    return;
                }

                loginPassword = fristLoginpasswordEt.getText().toString();

                if(TextUtils.isEmpty(loginPassword)){

                MainUtils.showToast(this,"请输入登录密码");
                return;
                }

                if(loginPassword.length()<6){

                    MainUtils.showToast(this,"登录密码不合法");
                    return;
                }

                setFristPayPassword(payPassword,loginPassword);

                break;


                default:
                break;

        }
    }


    // 获取交易状况
    private void setFristPayPassword(String payPassword , String loginPassword) {
        frist_paypasswordPro.setVisibility(View.VISIBLE);
        try {
            new TcpRequest(Construction.HOST, Construction.PORT)
                    .request(new SetPayPassword(this, SetPayPassword.SETPAYPASS, SetPayPassword.SETPASSMINTYPE_NO, payPassword,
                            loginPassword, null, null, null, null, null, null)
                            .toString());
        } catch (Exception e) {
            MainUtils.showToast(this, "异常");
            e.printStackTrace();
        }
    }

    public void onEvent(TcpRequestMessage message) {

        if (message.getTextCode().equals("F00009")) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    frist_paypasswordPro.setVisibility(View.GONE);
                }
            });


            if (message.getCode() == 1) {
                Log.d("Get_Message", new String(message.getTcpRequestMessage()));

                TagUtils.Field011Add(this);
                try {
                    final HashMap<String, String> data = new XmlParser()
                            .parse(new String(message.getTcpRequestMessage()));
                    Log.d("TextCode", message.getTextCode());

                    if (message.getTextCode().equals("F00009")) {
                        if (data.get(XmlBuilder.fieldName(39)).equalsIgnoreCase(
                                "00")) {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    UserInfo.setPayPasswordState("01");
                                    MainUtils.showToast(getApplicationContext(),
                                            "设置成功");
                                    finish();
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    // TODO Auto-generated method stub
                                    MainUtils.showToast(
                                            FristSetPayPassActivity.this,
                                            data.get(XmlBuilder.fieldName(124)));
                                }
                            });
                        }
                    }

                } catch (ArrayIndexOutOfBoundsException e) {
                    e.printStackTrace();
                } catch (SAXException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (ParserConfigurationException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            } else if (message.getCode() == -2) {

                runOnUiThread(new Runnable() {
                    public void run() {
                        MainUtils.showToast(getApplicationContext(), "网络连接超时...");
                    }
                });

            }




        }

    }


    @Override
    public void onInputFinish(String password) {
        
        this.payPassword = password;

        payPassLayoutBottom.setVisibility(View.VISIBLE);
    }


    @Override
    protected void onPause() {
        super.onPause();

        eventBus.unregister(this);
    }
}
