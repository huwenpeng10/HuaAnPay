package com.example.mrxu.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.example.merxu.mypractice.R;
import com.example.mrxu.allinfo.BankInfo;
import com.example.mrxu.allinfo.Construction;
import com.example.mrxu.allinfo.UserInfo;
import com.example.mrxu.allinfo.UserInfo.AuthState;
import com.example.mrxu.assemble_xml.Login;
import com.example.mrxu.base.BaseActivity;
import com.example.mrxu.mutils.MainUtils;
import com.example.mrxu.mutils.NetUtil;
import com.example.mrxu.mutils.PhoneCheckout;
import com.example.mrxu.mutils.PrefManager;
import com.example.mrxu.mutils.TagUtils;
import com.example.mrxu.mutils.TcpRequest;
import com.example.mrxu.mutils.TcpRequest.TcpRequestMessage;
import com.example.mrxu.mutils.XmlBuilder;
import com.example.mrxu.mutils.XmlParser;
import com.github.ybq.android.spinkit.style.Wave;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

import static com.example.merxu.mypractice.R.id.et_password;
import static com.example.mrxu.main.MainActivity.mainActivity;

public class LoginActivity extends BaseActivity implements OnEditorActionListener {


    @BindView(R.id.et_phoneNumber)
    EditText etPhoneNumber;
    @BindView(et_password)
    EditText etPassword;
    @BindView(R.id.register_text)
    TextView registerText;
    @BindView(R.id.forgotpass_text)
    TextView forgotpassText;
    @BindView(R.id.login_but)
    Button loginBut;
    @BindView(R.id.progressbar_login)
    ProgressBar progressbarLogin;
    private long exitTime = 0;
    private String userPhoneNumber;
    private String userPassword;
    private PrefManager prefManager;
    private EventBus eventbus;


    @Override
    public void initParms(Bundle parms) {
        prefManager = new PrefManager(this);
    }

    @Override
    public int bindLayout() {

        return R.layout.activity_login;
    }

    @Override
    public void initView(View view) {
        ButterKnife.bind(this);
        loginBut.setOnClickListener(this);
        forgotpassText.setOnClickListener(this);
        registerText.setOnClickListener(this);

        registerText.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        forgotpassText.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        registerText.getPaint().setAntiAlias(true);//抗锯齿
        forgotpassText.getPaint().setAntiAlias(true);//抗锯齿

        Wave chasingDots = new Wave();
        chasingDots.setColor(0xFFefefef);
        progressbarLogin.setIndeterminateDrawable(chasingDots);

        etPassword.setOnEditorActionListener(this);

        userPhoneNumber = prefManager.getUserPhoneumenber();

        if (!TextUtils.isEmpty(userPhoneNumber)) {
            etPhoneNumber.setText(userPhoneNumber);
        }

    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

        switch (v.getId()) {
            case R.id.login_but:

                if(NetUtil.getNetWorkState(this) !=-1){

                    startLogin();
                }else{
                    MainUtils.showToast(this,"请检查网络连接...");
                }

                break;
         case R.id.register_text:

             Intent registerintent =new Intent(this,RegisterActivity.class);
             startActivity(registerintent);
             overridePendingTransition(R.animator.push_left_in,
                     R.animator.push_left_out);

                break;
            case R.id.forgotpass_text:

                Intent passintent =new Intent(this,ResetLoginPasswordActivity.class);
                passintent.putExtra("Page","login");
                startActivity(passintent);
                overridePendingTransition(R.animator.push_left_in,
                        R.animator.push_left_out);
                break;


            default:
                break;

        }
    }

    //启动登录线程
    private void startLogin() {

        userPassword = etPassword.getText().toString();
        userPhoneNumber = etPhoneNumber.getText().toString();
        if (!TextUtils.isEmpty(userPhoneNumber) && !TextUtils.isEmpty(userPassword)) {
            if(PhoneCheckout.isMobileNumber(userPhoneNumber)){

                try {

                    progressbarLogin.setVisibility(View.VISIBLE);
                    new TcpRequest(Construction.HOST, Construction.PORT)
                            .request(new Login(this, userPhoneNumber, userPassword)
                                    .toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    progressbarLogin.setVisibility(View.GONE);
                    MainUtils.showToast(this, "登录失败");
                }

            }else{

                MainUtils.showToast(this, "请输入合法的手机号");
            }

        } else {
            MainUtils.showToast(this, "用户名和密码不能为空");
        }
    }


    private void startActivity(String str) {

        Log.d(TAG, "startActivity: " + str);

        Intent intent =new Intent(this,MainActivity.class);
        startActivity(intent);
        LoginActivity.this.finish();
    }




    public void onEvent(TcpRequestMessage message) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressbarLogin.setVisibility(View.GONE);
            }
        });

        if (message.getCode() == 1) {
            Log.d(Get_Message, new String(message.getTcpRequestMessage()));
            TagUtils.Field011Add(this);

            try {
                final HashMap<String, String> data = new XmlParser()
                        .parse(new String(message.getTcpRequestMessage()));



                /*
                *
                * : 00000826F00002<?xml version="1.0" encoding="UTF-8"?>
                                                                         <root>
                                                                           <head>
                                                                             <TxCode>F00002</TxCode>
                                                                             <TxDate>20170516</TxDate>
                                                                             <TxTime>152616</TxTime>
                                                                             <TxDesc/>
                                                                             <TxAction>000</TxAction>
                                                                             <Operator/>
                                                                             <Version>1.0</Version>
                                                                           </head>
                                                                           <body>
                                                                             <Field001>10110000001110000000000000000000000000100000000100000000000001001000000000000000000000000000000000000000000000000000000000011000</Field001>
                                                                             <Field003>100000</Field003>
                                                                             <Field004>0</Field004>
                                                                             <Field011>000001</Field011>
                                                                             <Field012>152616</Field012>
                                                                             <Field013>20170516</Field013>
                                                                             <Field039>00</Field039>
                                                                             <Field048>18515332180</Field048>
                                                                             <Field062>1|许瑞|6214830119630886|认证成功</Field062>
                                                                             <Field065>1</Field065>
                                                                             <Field124>交易成功</Field124>
                                                                             <Field125>1851533218020170516152616</Field125>
                                                                           </body>
                                                                         </root>
                * */

                if (data.get(XmlBuilder.fieldName(39)).equalsIgnoreCase(
                        "00")) {

                    UserInfo.setSessionCode(data.get(XmlBuilder
                            .fieldName(125)));
                    UserInfo.setPhoneNumber(data.get(XmlBuilder
                            .fieldName(48)));
                    UserInfo.setPayPasswordState(data.get(XmlBuilder
                            .fieldName(46)));


                    String tnstate = data.get(XmlBuilder.fieldName(65));
                    Log.d(TAG, "onEvent: TN数据:" + tnstate);
                    if (tnstate.equals("0")) {
                        UserInfo.state = 0;// 老用户

                    } else if (tnstate.equals("1")) {
                        UserInfo.state = 1;// T+N

                    } else if (tnstate.equals("2")) {
                        UserInfo.state = 2;// T+1

                    }

                    /*
                    * 62域数据数组
                    * */
                    final String[] data62 = data.get(
                            XmlBuilder.fieldName(62)).split("\\|");


                    switch (Integer.parseInt(data62[0])) {
                        case 0:
//                            未实名认证
                            UserInfo.setAuthState(AuthState.UNAUTHERIZED);
                            UserInfo.setAuthTips(data62[3]);
                            UserInfo.setAuthTipsNumber(Integer.parseInt(data62[0]));
                            break;
                        case 1:
//                           已完成实名认证
                            UserInfo.setAuthState(AuthState.AUTHERIZED);
                            UserInfo.setAuthTips(data62[3]);
                            UserInfo.setAuthTipsNumber(Integer.parseInt(data62[0]));
                            break;
                        case 2:
//                           实名认证审核中……
                            UserInfo.setAuthState(AuthState.AUTHERING);
                            UserInfo.setAuthTips(data62[3]);
                            UserInfo.setAuthTipsNumber(Integer.parseInt(data62[0]));
                            break;
                        case 3:
//                           实名认证失败
                            UserInfo.setAuthState(AuthState.AUTHENTICATION_FAIL);
                            UserInfo.setAuthTips(data62[3]);
                            UserInfo.setAuthTipsNumber(Integer.parseInt(data62[0]));
                            break;
                        default:
                            break;
                    }
                    if (data62[0].equals("1") && data62[2].isEmpty()) {

                        this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                MainUtils.showToast(
                                        getApplicationContext(),
                                        "此用户非“摇钱树B”注册用户");
                            }
                        });
                    } else {
                        if (!data62[2].isEmpty()) {
                            UserInfo.setBindingCardNo(data62[2]);
                            UserInfo.setBankName(BankInfo.getname(data62[2]));
                        }
                        UserInfo.setUserName(data62[1]);
                        this.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {


                                if (!TextUtils.isEmpty(userPhoneNumber) && !TextUtils.isEmpty(userPassword)) {
                                    prefManager.setUserPhoneumenber(userPhoneNumber);
                                    prefManager.setUserPassword(userPassword);
                                }
                                startActivity("登录成功");

                            }
                        });
                    }

                }else{

                    this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            MainUtils.showToast(
                                    getApplicationContext(),data.get(XmlBuilder
                                            .fieldName(124)));
                        }
                    });

                }


            } catch (SAXException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        progressbarLogin.setVisibility(View.GONE);
                        MainUtils.showToast(getApplicationContext(), "登录异常" );
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        progressbarLogin.setVisibility(View.GONE);
                        MainUtils.showToast(getApplicationContext(), "登录异常" );
                    }
                });
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        progressbarLogin.setVisibility(View.GONE);
                        MainUtils.showToast(getApplicationContext(), "登录异常" );
                    }
                });
            }catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        progressbarLogin.setVisibility(View.GONE);
                        MainUtils.showToast(getApplicationContext(), "登录异常" );
                    }
                });
            }


        } else if (message.getCode() == -2) {
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressbarLogin.setVisibility(View.GONE);
                    String chapshi = "服务器连接超时...";
                    MainUtils.showToast(getApplicationContext(), chapshi );
                }
            });
        } else {
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressbarLogin.setVisibility(View.GONE);
                    String shibai = "登录失败";
                    MainUtils.showToast(LoginActivity.this, shibai);
                }
            });
        }

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }


    public void exit() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {

            MainUtils.showToast(this, "再按一次退出程序");
            exitTime = System.currentTimeMillis();
        } else {
            if(mainActivity!=null){
                mainActivity.finish();
            }
            finish();
            System.exit(0);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!eventbus.isRegistered(this)){
            eventbus.register(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        eventbus.unregister(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        eventbus=EventBus.getDefault();
        eventbus.register(this);

    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (v == etPassword) {
            if(NetUtil.getNetWorkState(this) !=-1){

                startLogin();
            }else{
                MainUtils.showToast(this,"请检查网络连接...");
            }
        }

/*测试使用*/
//        startActivity(new Intent(this , WalletActivity.class));

        return false;
    }
}
