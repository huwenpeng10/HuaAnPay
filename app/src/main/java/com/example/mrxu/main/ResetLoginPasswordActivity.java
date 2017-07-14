package com.example.mrxu.main;

import android.content.Context;
import android.content.Intent;
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
import com.example.mrxu.alldialog.OkAppDialog;
import com.example.mrxu.allinfo.Construction;
import com.example.mrxu.allinfo.UserInfo;
import com.example.mrxu.assemble_xml.GetValidateCode;
import com.example.mrxu.assemble_xml.ResetPassword;
import com.example.mrxu.base.BaseActivity;
import com.example.mrxu.mutils.MainUtils;
import com.example.mrxu.mutils.PhoneCheckout;
import com.example.mrxu.mutils.PrefManager;
import com.example.mrxu.mutils.TagUtils;
import com.example.mrxu.mutils.TcpRequest;
import com.example.mrxu.mutils.TcpRequest.TcpRequestMessage;
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

public class ResetLoginPasswordActivity extends BaseActivity {

    @BindView(R.id.title_bar_text)
    TextView titleBarText;
    @BindView(R.id.left_Imagemage)
    ImageView leftImagemage;
    @BindView(R.id.et_loginphone)
    EditText etLoginphone;
    @BindView(R.id.et_logincheckkey)
    EditText etLogincheckkey;
    @BindView(R.id.check_logintv)
    TextView checkLogintv;
    @BindView(R.id.et_newloginPassword)
    EditText etNewloginPassword;
    @BindView(R.id.reset_loginpass_but)
    Button resetLoginpassBut;
    @BindView(R.id.progressbar_passreset)
    ProgressBar progressbarPassreset;

    private Timer timer;
    private EventBus eventBus;
    private String checkCode = "";

    private int TYPE = 0;
    private String phoneNumber;
    private String newPassword;
    private String checkkey;
    private PrefManager prefManager;
    private String ispage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        eventBus = EventBus.getDefault();
        eventBus.register(this);

    }

    @Override
    public void initParms(Bundle parms) {
        prefManager = new PrefManager(this);

    }

    @Override
    public int bindLayout() {
        return R.layout.activity_reset_login_password;
    }

    @Override
    public void initView(View view) {
        ButterKnife.bind(this);
        Intent intent = getIntent();
        ispage = "";
        if (intent != null) {

            ispage = intent.getStringExtra("Page");
        }

        if (!TextUtils.isEmpty(ispage)) {

            titleBarText.setText("忘记密码");
        } else {

            titleBarText.setText("重置密码");
        }


        leftImagemage.setOnClickListener(this);
        Circle circle = new Circle();
        circle.setColor(0xFF3798f4);
        progressbarPassreset.setIndeterminateDrawable(circle);


        checkLogintv.setOnClickListener(this);
        resetLoginpassBut.setOnClickListener(this);
    }

    @Override
    public void doBusiness(Context mContext) {
    }

    @Override
    public void widgetClick(View v) {


        switch (v.getId()) {


            case R.id.left_Imagemage:

                this.finish();

                overridePendingTransition(R.animator.push_right_out,
                        R.animator.push_right_in);
                break;
            case R.id.check_logintv:


                phoneNumber = etLoginphone.getText().toString();
                if (TextUtils.isEmpty(phoneNumber)) {

                    MainUtils.showToast(this, "请输入手机号");
                    return;
                }
                if (!PhoneCheckout.isMobileNumber(phoneNumber)) {

                    MainUtils.showToast(this, "请输入合法的手机号");
                    return;
                }

                timer = new Timer();
                TimerTask timerTask = new TimerTask() {
                    int countdown = 60;

                    @Override
                    public void run() {
                        countdown--;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                checkLogintv.setText(countdown + "s");

                                if (countdown <= 0) {

                                    checkLogintv.setText("重新获取验证码");
                                    checkLogintv.setClickable(true);
                                    timer.cancel();
                                    timer = null;
                                }
                            }
                        });
                    }
                };

                timer.schedule(timerTask, 0, 1000);

                fendCheckCode(phoneNumber);

                break;
            case R.id.reset_loginpass_but:

                phoneNumber = etLoginphone.getText().toString();
                newPassword = etNewloginPassword.getText().toString();
                checkkey = etLogincheckkey.getText().toString();

                if (TextUtils.isEmpty(phoneNumber)) {

                    MainUtils.showToast(this, "请输入手机号");
                    return;
                }

                if (!PhoneCheckout.isMobileNumber(phoneNumber)) {

                    MainUtils.showToast(this, "请输入合法的手机号");
                    return;
                }

                if (TextUtils.isEmpty(checkkey)) {

                    MainUtils.showToast(this, "请输入验证码");
                    return;
                }
                if (checkkey.length() != 6) {

                    MainUtils.showToast(this, "验证码不合法");
                    return;
                }

                if (!checkCode.equals(checkkey)) {

                    MainUtils.showToast(this, "验证码错误");
                    return;
                }

                if (newPassword.length() < 6) {

                    MainUtils.showToast(this, "密码位数不小于6位");
                    return;
                }

                commitPassword(phoneNumber, checkCode, newPassword);
                break;
        }
    }
    private void restartApplication() {
        final Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        this.finish();
    }
    private void fendCheckCode(String phoneNumber) {
        TYPE = 0;
        try {
            checkLogintv.setClickable(false);
            new TcpRequest(Construction.HOST, Construction.PORT)
                    .request(new GetValidateCode(this,
                            GetValidateCode.TYPE_LOGINPASSWORD, phoneNumber)
                            .toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void commitPassword(String phoneNumber, String checkCode, String password) {
        TYPE = 1;
        progressbarPassreset.setVisibility(View.VISIBLE);
        try {
            new TcpRequest(Construction.HOST, Construction.PORT)
                    .request(new ResetPassword(this, phoneNumber,
                            ResetPassword.FORGET_PASSWORD, checkCode, password)
                            .toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void onEvent(TcpRequestMessage message) {


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressbarPassreset.setVisibility(View.GONE);
            }
        });

        switch (TYPE) {
            case 0:
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
                                        MainUtils.showToast(ResetLoginPasswordActivity.this, data.get(XmlBuilder.fieldName(124)));
                                        checkLogintv.setText("获取验证码");
                                        checkLogintv.setClickable(true);
                                        if (timer != null) {
                                            timer.cancel();
                                            timer = null;
                                        }
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
                                MainUtils.showToast(ResetLoginPasswordActivity.this, chapshi
                                );
                            }
                        });
                    }

                }
                break;
            case 1:
                if (message.getTextCode().equals("F00008")) {
                    if (message.getCode() == 1) {
                        Log.d(Get_Message, new String(message.getTcpRequestMessage()));
                        TagUtils.Field011Add(this);

                        try {
                            final HashMap<String, String> data = new XmlParser()
                                    .parse(new String(message.getTcpRequestMessage()));

                            if (data.get(XmlBuilder.fieldName(39)).equalsIgnoreCase("00")) {

                                this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        MainUtils.showToast(ResetLoginPasswordActivity.this, "密码重置成功");

                                        final OkAppDialog okAppDialog = new OkAppDialog(ResetLoginPasswordActivity.this);
                                        okAppDialog.setCanceledOnTouchOutside(false);
                                        okAppDialog.setTitleStr("提示");
                                        okAppDialog.setmsgStr("密码重置成功，请重新登录");
                                        okAppDialog.setYesOnclickListener("好的", new OkAppDialog.onYesOnclickListener() {
                                            @Override
                                            public void onYesClick() {

                                                if (!TextUtils.isEmpty(ispage)) {

                                                    ResetLoginPasswordActivity.this.finish();
                                                    okAppDialog.dismiss();
                                                } else {
                                                    UserInfo.setPhoneNumber(null);
                                                    UserInfo.setSessionCode(null);
                                                    prefManager.setUserPhoneumenber("");
                                                    prefManager.setUserPassword("");

                                                    okAppDialog.dismiss();
                                                    restartApplication();
                                                }



                                            }
                                        });
                                        okAppDialog.show();
                                    }
                                });

                            } else {

                                this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        MainUtils.showToast(ResetLoginPasswordActivity.this, data.get(XmlBuilder.fieldName(124)));
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
                                String chaoshi = "服务器连接超时...";
                                MainUtils.showToast(ResetLoginPasswordActivity.this, chaoshi
                                );
                            }
                        });
                    }

                }
                break;
            default:
                break;

        }


    }

    private void error() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MainUtils.showToast(ResetLoginPasswordActivity.this, "异常"
                );
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        checkLogintv.setText("重新获取验证码");
        checkLogintv.setClickable(true);
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}
