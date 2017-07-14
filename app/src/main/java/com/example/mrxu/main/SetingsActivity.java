package com.example.mrxu.main;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.merxu.mypractice.R;
import com.example.mrxu.alldialog.CommonAppDialog;
import com.example.mrxu.allinfo.Construction;
import com.example.mrxu.allinfo.UserInfo;
import com.example.mrxu.assemble_xml.Logout;
import com.example.mrxu.base.BaseActivity;
import com.example.mrxu.mutils.MainUtils;
import com.example.mrxu.mutils.PrefManager;
import com.example.mrxu.mutils.TagUtils;
import com.example.mrxu.mutils.TcpRequest;
import com.example.mrxu.mutils.TcpRequest.TcpRequestMessage;
import com.example.mrxu.mutils.XmlBuilder;
import com.example.mrxu.mutils.XmlParser;
import com.github.ybq.android.spinkit.style.Circle;
import com.kyleduo.switchbutton.SwitchButton;
import com.zhy.autolayout.AutoRelativeLayout;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

import static com.example.merxu.mypractice.R.id.left_Imagemage;

public class SetingsActivity extends BaseActivity {

    @BindView(R.id.title_bar_text)
    TextView titleBarText;
    @BindView(left_Imagemage)
    ImageView leftImagemage;
    @BindView(R.id.set_pass_layout)
    AutoRelativeLayout setPassLayout;

    @BindView(R.id.set_service_layout)
    AutoRelativeLayout setServiceLayout;
    @BindView(R.id.set_version_text)
    TextView setVersionText;
    @BindView(R.id.set_userPhone_number)
    TextView setUserPhoneNumber;
    @BindView(R.id.set_guanyu_layout)
    AutoRelativeLayout setGuanyuLayout;
    @BindView(R.id.set_version_layout)
    AutoRelativeLayout setVersionLayout;
    @BindView(R.id.set_business_SB)
    SwitchButton setBusinessSB;
    @BindView(R.id.logoutlogin_but)
    Button logoutloginBut;
    @BindView(R.id.progressbar_set)
    ProgressBar progressbarSet;
    private PrefManager prefManager;
    private boolean isfirst;
    private EventBus eventBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventBus= EventBus.getDefault();
        eventBus.register(this);

    }

    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        return R.layout.activity_setings;
    }

    @Override
    public void initView(View view) {
        ButterKnife.bind(this);


        Circle circle=new Circle();
        circle.setColor(0xFF3798f4);
        progressbarSet.setIndeterminateDrawable(circle);

        titleBarText.setText("设置");
        leftImagemage.setOnClickListener(this);
        setPassLayout.setOnClickListener(this);
        setServiceLayout.setOnClickListener(this);
        setGuanyuLayout.setOnClickListener(this);
        setVersionLayout.setOnClickListener(this);
        logoutloginBut.setOnClickListener(this);

        setVersionText.setText(MainUtils.getAppVersion(this));

    }

    @Override
    public void doBusiness(Context mContext) {
        //判断消息提醒状态
        initSwitch();
    }

    //判断消息提醒状态
    private void initSwitch() {
        prefManager = new PrefManager(SetingsActivity.this);
        isfirst = prefManager.isFirstTimeLaunch();
        if (!TextUtils.isEmpty(prefManager.getMessageSwitch())) {
            if (prefManager.getMessageSwitch().equals("1")) {
                setBusinessSB.setChecked(true);
            } else {
                setBusinessSB.setChecked(false);
            }
        } else {

            setBusinessSB.setChecked(false);
        }


        setBusinessSB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    prefManager.setMessageSwitch("1");
                } else {

                    prefManager.setMessageSwitch("0");
                }
            }
        });
    }

    @Override
    public void widgetClick(View v) {

        switch (v.getId()) {
            case R.id.left_Imagemage:

                finish();
                overridePendingTransition(R.animator.push_right_out,
                        R.animator.push_right_in);

                break;
            case R.id.set_pass_layout:
                Intent passintent = new Intent(this, PassWordManageActivity.class);
                startActivity(passintent);
                overridePendingTransition(R.animator.push_left_in,
                        R.animator.push_left_out);

                finish();
                break;
            case R.id.set_service_layout:
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
                        + "4008-119-789")));
                overridePendingTransition(R.animator.push_left_in,
                        R.animator.push_left_out);

                break;
            case R.id.set_guanyu_layout:

                Intent guanyuintent = new Intent(this, AboutusActivity.class);
                startActivity(guanyuintent);
                overridePendingTransition(R.animator.push_left_in,
                        R.animator.push_left_out);
                break;
            case R.id.set_version_layout:

                int code = MainUtils.getAppCode(this);

                break;
            case R.id.logoutlogin_but:

                final CommonAppDialog commonAppDialog =new CommonAppDialog(this);
                commonAppDialog.setmsgStr("提示");
                commonAppDialog.setmsgStr("是否注销");
                commonAppDialog.setYesOnclickListener("确定", new CommonAppDialog.onYesOnclickListener() {
                    @Override
                    public void onYesClick() {
                        try {

                            progressbarSet.setVisibility(View.VISIBLE);
                            new TcpRequest(Construction.HOST, Construction.PORT)
                                    .request(new Logout(SetingsActivity.this).toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        commonAppDialog.dismiss();
                    }
                });
                commonAppDialog.setNoOnclickListener("取消", new CommonAppDialog.onNoOnclickListener() {
                    @Override
                    public void onNoClick() {
                        commonAppDialog.dismiss();
                    }
                });
                commonAppDialog.show();
                break;


            default:
                break;

        }
    }

    public void onEvent(TcpRequestMessage message){

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressbarSet.setVisibility(View.GONE);
            }
        });

        if (message.getTextCode().equals("F00003")) {
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
                                prefManager.setUserPhoneumenber("");
                                prefManager.setUserPassword("");
                                Intent intent =new Intent( SetingsActivity.this,LoginActivity.class);
                                startActivity(intent);
                                UserInfo.setPhoneNumber(null);
                                UserInfo.setSessionCode(null);
                                if(MainActivity.mainActivity!=null){

                                    MainActivity.mainActivity.finish();
                                }
                                SetingsActivity.this.finish();
                                overridePendingTransition(android.R.anim.fade_in,
                                        android.R.anim.fade_out);
                            }
                        });

                    } else {

                        this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                MainUtils.showToast(SetingsActivity.this, data.get(XmlBuilder.fieldName(124)));
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
                        MainUtils.showToast(SetingsActivity.this, "服务器连接超时..."
                        );
                    }
                });
            }


        }

    }

    private void error() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MainUtils.showToast(SetingsActivity.this, "异常"
                );
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (!eventBus.isRegistered(this)){
            eventBus.register(this);
        }

        String userPhoneNumber=UserInfo.getPhoneNumber()+"";
        setUserPhoneNumber.setText(userPhoneNumber);

    }

    @Override
    protected void onPause() {
        super.onPause();
        eventBus.unregister(this);
    }
}
