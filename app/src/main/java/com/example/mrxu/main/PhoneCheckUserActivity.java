package com.example.mrxu.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.merxu.mypractice.R;
import com.example.mrxu.allinfo.Construction;
import com.example.mrxu.allinfo.UserInfo;
import com.example.mrxu.assemble_xml.GetValidateCode;
import com.example.mrxu.assemble_xml.SetPayPassword;
import com.example.mrxu.base.BaseActivity;
import com.example.mrxu.mutils.MainUtils;
import com.example.mrxu.mutils.TagUtils;
import com.example.mrxu.mutils.TcpRequest;
import com.example.mrxu.mutils.XmlBuilder;
import com.example.mrxu.mutils.XmlParser;
import com.github.ybq.android.spinkit.style.Circle;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.ParserConfigurationException;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

public class PhoneCheckUserActivity extends BaseActivity implements TextWatcher {

    @BindView(R.id.title_bar_text)
    TextView titleBar;
    @BindView(R.id.left_Imagemage)
    ImageView leftImagemage;
    @BindView(R.id.checkuser_phonecode)
    EditText checkuserPhonecode;
    @BindView(R.id.conuntdown_tv)
    TextView conuntdownTv;
    @BindView(R.id.phone_check_pro)
    ProgressBar phoneCheckPro;

    private EventBus eventBus;
    //访问数据类型
    private int dateType = 1;

    private Timer timer;
    private String checkCode="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventBus = EventBus.getDefault();
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (!eventBus.isRegistered(this)) {
            eventBus.register(this);
        }

    }

    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        return R.layout.activity_phone_check_user;
    }

    @Override
    public void initView(View view) {
        ButterKnife.bind(this);

        timer = new Timer();



        Circle circle = new Circle();
        circle.setColor(0xFF3798f4);
        phoneCheckPro.setIndeterminateDrawable(circle);

        titleBar.setText("输入验证码");
        leftImagemage.setOnClickListener(this);
        conuntdownTv.setOnClickListener(this);
        checkuserPhonecode.addTextChangedListener(this);
    }

    @Override
    public void doBusiness(Context mContext) {


        countDown();
        getMeassgeCode(UserInfo.getPhoneNumber()+"");
    }

//获取验证码
    private void getMeassgeCode(String phoneNumber){
        dateType = 1;
        try {
            new TcpRequest(Construction.HOST, Construction.PORT)
                    .request(new GetValidateCode(this,
                            GetValidateCode.TYPE_PAYPASSWORD, phoneNumber)
                            .toString());
        } catch (Exception e) {
            MainUtils.showToast(this, "发送请求失败");
            error();
            e.printStackTrace();
        }
    }
    //校验验证码
    private void checkUserPhone(String code){

        phoneCheckPro.setVisibility(View.VISIBLE);
        dateType = 2;
        try {
            new TcpRequest(Construction.HOST, Construction.PORT)
                    .request(new SetPayPassword(this, SetPayPassword.RESETPAYPASS, SetPayPassword.SETPASSMINTYPE2, null,
                            null, null, null, null, null, code, null)
                            .toString());

        } catch (Exception e) {
           MainUtils.showToast(this,"发送请求失败");
            e.printStackTrace();
        }
    }

    private void countDown(){

        conuntdownTv.setClickable(false);
        if(timer==null){
            timer=new Timer();
        }
        TimerTask timerTask =new TimerTask() {

            int timenum = 60;
            @Override
            public void run() {

                timenum--;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        conuntdownTv.setText(timenum+"s");
                        if(timenum<=0){

                            conuntdownTv.setClickable(true);
                            conuntdownTv.setText("重新获取验证码");
                            timer.cancel();
                            timer = null;
                        }
                    }
                });



            }
        };
        timer.schedule(timerTask,0 ,1000);
    }




    @Override
    public void widgetClick(View v) {

        switch (v.getId()) {
            case R.id.left_Imagemage:

                finish();

                overridePendingTransition(R.animator.push_right_out,
                        R.animator.push_right_in);
                break;
            case R.id.conuntdown_tv:

                countDown();
                getMeassgeCode(UserInfo.getPhoneNumber()+"");

                break;


            default:
                break;

        }
    }


    public void onEvent(TcpRequest.TcpRequestMessage message) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                phoneCheckPro.setVisibility(View.GONE);
            }
        });

        //获取验证码
        if(dateType == 1){

            if (message.getTextCode().equals("F00005")) {
                if (message.getCode() == 1) {
                    Log.d(Get_Message, new String(message.getTcpRequestMessage()));
                    TagUtils.Field011Add(this);

                    try {
                        final HashMap<String, String> data = new XmlParser()
                                .parse(new String(message.getTcpRequestMessage()));

                        if (data.get(XmlBuilder.fieldName(39)).equalsIgnoreCase("00")) {

                            checkCode = data.get(XmlBuilder.fieldName(52));

                        } else {

                            this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    MainUtils.showToast(PhoneCheckUserActivity.this, data.get(XmlBuilder.fieldName(124)));
                                }
                            });
                        }


                    } catch (SAXException e) {
                        e.printStackTrace();
                        error();
                    } catch (IOException e) {
                        e.printStackTrace();
                        error();
                    } catch (ParserConfigurationException e) {
                        e.printStackTrace();
                        error();
                    } catch (Exception e) {
                        e.printStackTrace();
                        error();
                    }


                } else if (message.getCode() == -2) {
                    this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String chapshi = "服务器连接超时...";
                            MainUtils.showToast(PhoneCheckUserActivity.this, chapshi
                            );
                        }
                    });
                }
            }


        //校验手机
        }else{


            if (message.getTextCode().equals("F00009")) {
                if (message.getCode() == 1) {
                    Log.d(Get_Message, new String(message.getTcpRequestMessage()));
                    TagUtils.Field011Add(this);

                    try {
                        final HashMap<String, String> data = new XmlParser()
                                .parse(new String(message.getTcpRequestMessage()));

                        if (data.get(XmlBuilder.fieldName(39)).equalsIgnoreCase("00")) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(PhoneCheckUserActivity.this,
                                            ResetPayPasswordActivity.class);
                                    startActivity(intent);
                                    overridePendingTransition(R.animator.push_left_in,
                                            R.animator.push_left_out);
                                    PhoneCheckUserActivity.this.finish();
                                }
                            });


                        } else {

                            this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    MainUtils.showToast(PhoneCheckUserActivity.this, data.get(XmlBuilder.fieldName(124)));
                                }
                            });
                        }


                    } catch (SAXException e) {
                        e.printStackTrace();
                        error();
                    } catch (IOException e) {
                        e.printStackTrace();
                        error();
                    } catch (ParserConfigurationException e) {
                        e.printStackTrace();
                        error();
                    } catch (Exception e) {
                        e.printStackTrace();
                        error();
                    }


                } else if (message.getCode() == -2) {
                    this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String chapshi = "服务器连接超时...";
                            MainUtils.showToast(PhoneCheckUserActivity.this, chapshi
                            );
                        }
                    });
                }
            }

        }

    }

    private void error() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                timer.cancel();
                timer=null;
                conuntdownTv.setClickable(true);
                conuntdownTv.setText("重新发送");
                MainUtils.showToast(PhoneCheckUserActivity.this , "获取验证码异常,请重新获取");
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        eventBus.unregister(this);

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(timer != null){
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

        if (s.length() == 6) {//限制长度
            String inputCheckCode = checkuserPhonecode.getText().toString();
            if(inputCheckCode.equals(checkCode)){
                checkUserPhone(checkCode);
            }else{
                checkuserPhonecode.setText("");
                MainUtils.showToast(this,"验证码错误，请重新输入");
            }
        }

    }
}
