package com.example.mrxu.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.example.merxu.mypractice.R;
import com.example.mrxu.allinfo.BankInfo;
import com.example.mrxu.allinfo.Construction;
import com.example.mrxu.allinfo.UserInfo;
import com.example.mrxu.allinfo.UserInfo.AuthState;
import com.example.mrxu.assemble_xml.Login;
import com.example.mrxu.base.BaseActivity;
import com.example.mrxu.mutils.MainUtils;
import com.example.mrxu.mutils.PrefManager;
import com.example.mrxu.mutils.TagUtils;
import com.example.mrxu.mutils.TcpRequest;
import com.example.mrxu.mutils.TcpRequest.TcpRequestMessage;
import com.example.mrxu.mutils.XmlBuilder;
import com.example.mrxu.mutils.XmlParser;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Timer;

import javax.xml.parsers.ParserConfigurationException;

import de.greenrobot.event.EventBus;

/**
 * 启动页面
 *
 * @author MrXu
 *
 * */
public class LaunchActivity extends BaseActivity {

    private boolean isfirst;
    private boolean islogin;
    private PrefManager prefManager;
    private int overtime = 3;
    private Timer timer = new Timer();
    private EventBus eventbus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        eventbus=EventBus.getDefault();
        eventbus.register(LaunchActivity.this);
        return R.layout.activity_launch;
    }

    @Override
    public void initView(View view) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!eventbus.isRegistered(this)){

            eventbus.register(LaunchActivity.this);
        }
    }

    @Override
    public void doBusiness(Context mContext) {
        prefManager = new PrefManager(LaunchActivity.this);
        isfirst = prefManager.isFirstTimeLaunch();


        //加载启动界面
        Integer time = 2000;    //设置等待时间，单位为毫秒
        Handler handler = new Handler();
        //当计时结束时，跳转至主界面
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                if (!isfirst) {
                    startLogin();
                } else {
                    prefManager.setFirstTimeLaunch(false);
                    startActivity(new Intent(LaunchActivity.this, WelcomeActivity.class));
                    LaunchActivity.this.finish();
                }
            }
        }, time);

        //测试不用自动登录 在方法内直接跳转
//        startActivity("测试待删除");

    }


    public void onEvent(TcpRequestMessage message) {


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
                                        LaunchActivity.this,
                                        "此用户非“摇钱树B”注册用户");
                                islogin = false;
                            }
                        });
                    } else {

                        if (!data62[2].isEmpty()) {
                            UserInfo.setBindingCardNo(data62[2]);
                            UserInfo.setBankName(BankInfo.getname(data62[2]));
                            UserInfo.setUserName(data62[1]);
                        }
                        this.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                /**
                                 *
                                 * 暂时未添加T+N
                                 *
                                 * */

                                islogin = true;
                                startActivity("登录");
                            }
                        });
                    }

                }


            } catch (SAXException e) {
                islogin = false;
                startActivity("异常");
                e.printStackTrace();
            } catch (IOException e) {
                islogin = false;
                startActivity("异常");
                e.printStackTrace();
            } catch (ParserConfigurationException e) {
                islogin = false;
                startActivity("异常");
                e.printStackTrace();
            }catch (Exception e) {
                islogin = false;
                startActivity("异常");
                e.printStackTrace();
            }


        } else if (message.getCode() == -2) {
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String chapshi = "服务器连接超时...";
                    MainUtils.showToast(getApplicationContext(), chapshi
                    );
                    islogin = false;
                    startActivity("异常");
                }
            });
        } else {
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String shibai = "登录失败";
                    MainUtils.showToast(LaunchActivity.this, shibai);
                    islogin = false;
                    startActivity("异常");
                }
            });
        }

    }


    //启动登录线程
    private void startLogin() {

        if (!TextUtils.isEmpty(prefManager.getUserPhoneumenber()) && !TextUtils.isEmpty(prefManager.getUserPassword())) {

            try {
                new TcpRequest(Construction.HOST, Construction.PORT)
                        .request(new Login(this, prefManager.getUserPhoneumenber(), prefManager.getUserPassword())
                                .toString());
            } catch (Exception e) {
                e.printStackTrace();
                MainUtils.showToast(this, "登录失败");
                startActivity("异常");
            }
        }else{
            islogin=false;
            startActivity("登录");

        }
    }

    private void startActivity(String str) {

        Log.d(TAG, "startActivity: " + str);
        timer.cancel();
        if (islogin) {
            startActivity(MainActivity.class);
            LaunchActivity.this.finish();
        } else {
            startActivity(LoginActivity.class);
            LaunchActivity.this.finish();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        eventbus.unregister(LaunchActivity.this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void widgetClick(View v) {

    }
}
