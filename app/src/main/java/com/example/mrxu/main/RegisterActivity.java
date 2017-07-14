package com.example.mrxu.main;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.merxu.mypractice.R;
import com.example.mrxu.alldialog.OkAppDialog;
import com.example.mrxu.alldialog.WebOkAppDialog;
import com.example.mrxu.allinfo.Construction;
import com.example.mrxu.assemble_xml.GetValidateCode;
import com.example.mrxu.assemble_xml.UserRegister;
import com.example.mrxu.base.BaseActivity;
import com.example.mrxu.mutils.MainUtils;
import com.example.mrxu.mutils.PhoneCheckout;
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

public class RegisterActivity extends BaseActivity {


    @BindView(R.id.left_Imagemage)
    ImageView leftImagemage;
    @BindView(R.id.title_bar_text)
    TextView titleBarText;
    @BindView(R.id.et_register_phone)
    EditText etRegisterPhone;
    @BindView(R.id.et_register_keycode)
    EditText etRegisterKeycode;
    @BindView(R.id.tv_register_keycode)
    TextView tvRegisterKeycode;
    @BindView(R.id.et_register_password1)
    EditText etRegisterPassword1;
    @BindView(R.id.et_register_password2)
    EditText etRegisterPassword2;
    @BindView(R.id.but_register_commit)
    Button butRegisterCommit;
    @BindView(R.id.pro_register)
    ProgressBar proRegister;
    @BindView(R.id.cb_deal)
    CheckBox cbDeal;
    @BindView(R.id.tv_deal)
    TextView tvDeal;
    private Timer timer;
    private String phoneNumber;
    private int TYPE;
    private EventBus eventBus;
    private String checkCode;
    private String registerKeycode;
    private String registerPassword1;
    private String registerPassword2;

    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        return R.layout.activity_register;
    }

    @Override
    public void initView(View view) {

        ButterKnife.bind(this);
        titleBarText.setText("注册");
        leftImagemage.setOnClickListener(this);
        tvRegisterKeycode.setOnClickListener(this);
        butRegisterCommit.setOnClickListener(this);
        tvDeal.setOnClickListener(this);

        Circle circle = new Circle();
        circle.setColor(0xFF3798f4);
        proRegister.setIndeterminateDrawable(circle);

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
            case R.id.tv_deal:

                WebView view = new WebView(this);
                view.getSettings()
                        .setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
                view.getSettings().setTextZoom(80);
                view.loadUrl("file:///android_asset/deal.html");
                final WebOkAppDialog webOkAppDialog = new WebOkAppDialog(this);
                webOkAppDialog.setTitleStr("用户协议");
                webOkAppDialog.setLayoutView(view);
                webOkAppDialog.setYesOnclickListener("知道了", new WebOkAppDialog.onYesOnclickListener() {
                    @Override
                    public void onYesClick() {
                        webOkAppDialog.dismiss();
                    }
                });
                webOkAppDialog.show();
            break;
            case R.id.tv_register_keycode:


                phoneNumber = etRegisterPhone.getText().toString();
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
                                tvRegisterKeycode.setText(countdown + "s");

                                if (countdown <= 0) {

                                    tvRegisterKeycode.setText("重新获取验证码");
                                    tvRegisterKeycode.setClickable(true);
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

            case R.id.but_register_commit:


                phoneNumber = etRegisterPhone.getText().toString();
                registerKeycode = etRegisterKeycode.getText().toString();
                registerPassword1 = etRegisterPassword1.getText().toString();
                registerPassword2 = etRegisterPassword2.getText().toString();
                if (TextUtils.isEmpty(phoneNumber)) {

                    MainUtils.showToast(this, "请输入手机号");
                    return;
                }
                if (!PhoneCheckout.isMobileNumber(phoneNumber)) {

                    MainUtils.showToast(this, "请输入合法的手机号");
                    return;
                }
                if (!registerKeycode.equals(checkCode)) {

                    MainUtils.showToast(this, "验证码错误");
                    return;
                }
                if (TextUtils.isEmpty(registerPassword1)) {

                    MainUtils.showToast(this, "请输入手机号");
                    return;
                }
                if (TextUtils.isEmpty(registerPassword2)) {

                    MainUtils.showToast(this, "请输入手机号");
                    return;
                }
                if (registerPassword1.length() < 6) {
                    MainUtils.showToast(this, "密码不合法");
                    return;
                }
                if (!registerPassword1.equals(registerPassword2)) {
                    MainUtils.showToast(this, "两次密码不一样");
                    return;
                }
                if (!cbDeal.isChecked()) {
                    MainUtils.showToast(this, "请阅读并勾选用户协议");
                    return;
                }

                register(phoneNumber, registerPassword2, registerKeycode);


                break;


            default:
                break;

        }
    }

    private void fendCheckCode(String phoneNumber) {
        TYPE = 0;
        try {
            tvRegisterKeycode.setClickable(false);
            new TcpRequest(Construction.HOST, Construction.PORT)
                    .request(new GetValidateCode(this,
                            GetValidateCode.TYPE_REGISTER, phoneNumber)
                            .toString());
        } catch (Exception e) {
            MainUtils.showToast(this, "异常");
            e.printStackTrace();
        }
    }

    private void register(String phoneNumber, String password, String code) {
        proRegister.setVisibility(View.VISIBLE);
        TYPE = 1;
        try {
            new TcpRequest(Construction.HOST, Construction.PORT)
                    .request(new UserRegister(this, phoneNumber,
                            password, code).toString());
        } catch (Exception e) {
            MainUtils.showToast(this, "异常");
            proRegister.setVisibility(View.GONE);
            e.printStackTrace();
        }
    }


    public void onEvent(TcpRequest.TcpRequestMessage message) {


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                proRegister.setVisibility(View.GONE);
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
                                        MainUtils.showToast(RegisterActivity.this, data.get(XmlBuilder.fieldName(124)));
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
                                MainUtils.showToast(RegisterActivity.this, chapshi
                                );
                            }
                        });
                    }
                }
                break;
            case 1:

                if (message.getTextCode().equals("F00001")) {
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

                                        final OkAppDialog okAppDialog = new OkAppDialog(RegisterActivity.this);
                                        okAppDialog.setTitleStr("提示");
                                        okAppDialog.setmsgStr("恭喜您注册成功");
                                        okAppDialog.setYesOnclickListener("立即登录", new OkAppDialog.onYesOnclickListener() {
                                            @Override
                                            public void onYesClick() {
                                                okAppDialog.dismiss();
                                                RegisterActivity.this.finish();
                                            }
                                        });
                                        okAppDialog.show();

                                    }
                                });

                            } else {

                                this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        MainUtils.showToast(RegisterActivity.this, data.get(XmlBuilder.fieldName(124)));
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
                                MainUtils.showToast(RegisterActivity.this, chapshi
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                timer.cancel();
                timer = null;
                MainUtils.showToast(RegisterActivity.this, "异常");
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventBus = EventBus.getDefault();
        eventBus.register(this);

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
}
