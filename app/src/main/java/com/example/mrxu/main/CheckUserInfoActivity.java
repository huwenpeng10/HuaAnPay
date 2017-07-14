package com.example.mrxu.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
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
import com.example.mrxu.mutils.CardUtils;
import com.example.mrxu.mutils.MainUtils;
import com.example.mrxu.mutils.TagUtils;
import com.example.mrxu.mutils.TcpRequest;
import com.example.mrxu.mutils.XmlBuilder;
import com.example.mrxu.mutils.XmlParser;
import com.github.ybq.android.spinkit.style.Circle;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

public class CheckUserInfoActivity extends BaseActivity implements View.OnTouchListener {

    @BindView(R.id.title_bar_text)
    TextView titleBar;
    @BindView(R.id.left_Imagemage)
    ImageView leftImagemage;
    @BindView(R.id.checkuser_phonenumber_tv)
    TextView checkuserPhonenumberTv;
    @BindView(R.id.checkuser_cardid_et)
    EditText checkuserCardidEt;
    @BindView(R.id.checkuser_bankcardnumber_et)
    EditText checkuserBankcardnumberEt;
    @BindView(R.id.checkuser_nextbut)
    Button checkuserNextbut;
    @BindView(R.id.checkuser_pro)
    ProgressBar checkuserPro;
    @BindView(R.id.erro1_icon)
    ImageView erro1Icon;
    @BindView(R.id.erro2_icon)
    ImageView erro2Icon;
    @BindView(R.id.check_userName_tv)
    TextView checkUserNameTv;

    private EventBus eventBus;
    private String checkResult;

    /**
     * 明天任务
     */

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
        return R.layout.activity_check_user_info;
    }

    @Override
    public void initView(View view) {
        ButterKnife.bind(this);

        Circle circle = new Circle();
        circle.setColor(0xFF3798f4);
        checkuserPro.setIndeterminateDrawable(circle);


        titleBar.setText("校验用户");
        leftImagemage.setOnClickListener(this);
        checkuserNextbut.setOnClickListener(this);
        checkuserCardidEt.setOnTouchListener(this);
        checkuserBankcardnumberEt.setOnTouchListener(this);

        checkuserPhonenumberTv.setText(UserInfo.getPhoneNumber() + "");
        checkUserNameTv.setText(UserInfo.getUserName() + "");

    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

        switch (v.getId()) {
            case R.id.left_Imagemage:

                finish();

                overridePendingTransition(R.animator.push_right_out,
                        R.animator.push_right_in);
                break;
            case R.id.checkuser_nextbut:

                String userName = checkUserNameTv.getText().toString();
                String userCardId = checkuserCardidEt.getEditableText().toString();
                String bankCardNumber = checkuserBankcardnumberEt.getEditableText().toString();
                String phoneNumber = checkuserPhonenumberTv.getText().toString();

                CardUtils cardUtils = new CardUtils();
                Boolean checkCard = null;
                try {
                    checkCard = cardUtils.IDCardValidate(userCardId).equals("") ? false : true;
                } catch (ParseException e) {
                    e.printStackTrace();
                    MainUtils.showToast(this, "身份证查询异常");
                }

                if(checkCard){
                    MainUtils.showToast(this, "身份证号不合法");
                    return;
                }

                if (TextUtils.isEmpty(userName)) {
                    MainUtils.showToast(this, "请输入真实姓名");
                    return;
                }
                if (TextUtils.isEmpty(bankCardNumber)) {
                    MainUtils.showToast(this, "请输入银行卡号");
                    return;
                }
                if (bankCardNumber.length() < 16 || bankCardNumber.length() > 19) {
                    MainUtils.showToast(this, "请输入正确的银行卡号");
                    return;
                }

                checkUser(userName, userCardId, bankCardNumber, phoneNumber);


                break;

            default:
                break;

        }

    }


    private void checkUser(String userName, String userCardId, String bankCardNumber, String phoneNumber) {

        checkuserPro.setVisibility(View.VISIBLE);
        try {
            new TcpRequest(Construction.HOST, Construction.PORT)
                    .request(new SetPayPassword(CheckUserInfoActivity.this,
                            SetPayPassword.RESETPAYPASS, SetPayPassword.SETPASSMINTYPE1, null, null, userName,
                            userCardId, bankCardNumber, phoneNumber,
                            null, null).toString());
        } catch (Exception e) {
            checkuserPro.setVisibility(View.GONE);
            e.printStackTrace();
        }
    }


    public void onEvent(TcpRequest.TcpRequestMessage message) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                checkuserPro.setVisibility(View.GONE);
            }
        });

        if (message.getCode() == 1) {
            Log.d("Get_Message", new String(message.getTcpRequestMessage()));
            TagUtils.Field011Add(this);
            try {
                final HashMap<String, String> data = new XmlParser()
                        .parse(new String(message.getTcpRequestMessage()));
                if (data.get(XmlBuilder.fieldName(39)).equalsIgnoreCase("00")) {
                    int code = -1;

                    final String[] _data = data.get(XmlBuilder.fieldName(65))
                            .split("##");

                    for (int i = 0; i < _data.length; i++) {

                        final String[] _dataMassage = _data[i].split("\\|");

                        code = Integer.parseInt(_dataMassage[0]);
                        checkResult = _dataMassage[1];
                        switch (code) {
                            case 00:
                                runOnUiThread(new Runnable() {
                                    public void run() {

                                        Intent intent = new Intent(
                                                CheckUserInfoActivity.this,
                                                PhoneCheckUserActivity.class);

                                        startActivity(intent);
                                        overridePendingTransition(R.animator.push_left_in,
                                                R.animator.push_left_out);
                                    }
                                });

                                break;
                            case 01:
                                runOnUiThread(new Runnable() {
                                    public void run() {
//                                        银行卡

                                        MainUtils.showToast(
                                                getApplicationContext(),
                                                "验证失败，请检查错误信息");
                                        erro2Icon.setVisibility(View.VISIBLE);
                                    }
                                });

                                break;
                            case 02:
                                runOnUiThread(new Runnable() {
                                    public void run() {

                                        MainUtils.showToast(
                                                getApplicationContext(),
                                                "验证失败，请检查错误信息");
                                    }
                                });
                                break;
                            case 03:
                                runOnUiThread(new Runnable() {
                                    public void run() {

//                                        身份证
                                        MainUtils.showToast(
                                                getApplicationContext(),
                                                "验证失败，请检查错误信息");
                                        erro1Icon.setVisibility(View.VISIBLE);
                                    }
                                });
                                break;

                            default:
                                break;
                        }

                    }

                } else {
                    runOnUiThread(new Runnable() {
                        public void run() {

                            MainUtils.showToast(CheckUserInfoActivity.this, data.get(XmlBuilder.fieldName(124)));
                        }
                    });
                }

            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            }
        } else if (message.getCode() == -2) {
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    MainUtils.showToast(getApplicationContext(), "服务器连接超时...");
                }
            });
        } else {
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    MainUtils.showToast(getApplicationContext(), "更改失败");
                }
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        eventBus.unregister(this);

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // TODO Auto-generated method stub

        if (v == checkuserCardidEt) {
            erro1Icon.setVisibility(View.GONE);

        } else if (v == checkuserBankcardnumberEt) {
            erro2Icon.setVisibility(View.GONE);

        }
        return false;
    }
}
