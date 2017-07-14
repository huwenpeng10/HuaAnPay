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

import javax.xml.parsers.ParserConfigurationException;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

public class ResetPayPasswordActivity extends BaseActivity {

    @BindView(R.id.title_bar_text)
    TextView titleBar;
    @BindView(R.id.left_Imagemage)
    ImageView leftImagemage;
    @BindView(R.id.resetpay_pass_et)
    EditText resetpayPassEt;
    @BindView(R.id.resetpay_pass_et1)
    EditText resetpayPassEt1;
    @BindView(R.id.reset_paypass_but)
    Button resetPaypassBut;
    @BindView(R.id.reset_paypass_pro)
    ProgressBar resetPaypassPro;

    private EventBus eventBus;

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
        return R.layout.activity_reset_pay_password;
    }

    @Override
    public void initView(View view) {

        ButterKnife.bind(this);


        Circle circle = new Circle();
        circle.setColor(0xFF3798f4);
        resetPaypassPro.setIndeterminateDrawable(circle);

        titleBar.setText("输入验证码");
        leftImagemage.setOnClickListener(this);
        resetPaypassBut.setOnClickListener(this);
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
            case R.id.reset_paypass_but:

                String password = resetpayPassEt.getText().toString();
                String password1 = resetpayPassEt1.getText().toString();
                if (TextUtils.isEmpty(password)) {

                    MainUtils.showToast(this, "请输入支付密码");
                    return;
                }

                if (TextUtils.isEmpty(password1)) {

                    MainUtils.showToast(this, "请再次输入支付密码");
                    return;
                }

                if (!password.equals(password1)) {
                    MainUtils.showToast(this, "两次密码输入不一致");
                    return;
                }

                resetPayPassword("ss");

                break;


            default:
                break;

        }
    }


    //校验验证码
    private void resetPayPassword(String password) {

        resetPaypassPro.setVisibility(View.VISIBLE);
        try {
            new TcpRequest(Construction.HOST, Construction.PORT)
                    .request(new SetPayPassword(this, SetPayPassword.RESETPAYPASS, SetPayPassword.SETPASSMINTYPE3, null,
                            null, null, null, null, null, null,
                            password).toString());

        } catch (Exception e) {

            resetPaypassPro.setVisibility(View.VISIBLE);
            MainUtils.showToast(this, "异常");
            e.printStackTrace();
        }
    }

    public void onEvent(TcpRequest.TcpRequestMessage message) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                resetPaypassPro.setVisibility(View.GONE);
            }
        });

        if (message.getCode() == 1) {
            Log.d("Get_Message", new String(message.getTcpRequestMessage()));
            TagUtils.Field011Add(this);
            try {
                final HashMap<String, String> data = new XmlParser()
                        .parse(new String(message.getTcpRequestMessage()));
                if (data.get(XmlBuilder.fieldName(39)).equalsIgnoreCase("00")) {

                    runOnUiThread(new Runnable() {
                        public void run() {
                            MainUtils.showToast(getApplicationContext(),
                                    "重置密码成功");
                            finish();
                        }
                    });

                } else {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            MainUtils.showToast(getApplicationContext(),
                                    data.get(XmlBuilder.fieldName(124)));
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
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        eventBus.unregister(this);

    }
}
