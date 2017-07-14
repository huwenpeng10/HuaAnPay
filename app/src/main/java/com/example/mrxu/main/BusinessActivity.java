package com.example.mrxu.main;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.merxu.mypractice.R;
import com.example.mrxu.alldialog.CommonAppDialog;
import com.example.mrxu.alldialog.OkAppDialog;
import com.example.mrxu.allinfo.Construction;
import com.example.mrxu.allinfo.UserInfo;
import com.example.mrxu.assemble_xml.GetSwitchStateInfoTn;
import com.example.mrxu.base.BaseActivity;
import com.example.mrxu.mutils.CustomSwitchCompat;
import com.example.mrxu.mutils.MainUtils;
import com.example.mrxu.mutils.TagUtils;
import com.example.mrxu.mutils.TcpRequest;
import com.example.mrxu.mutils.TcpRequest.TcpRequestMessage;
import com.example.mrxu.mutils.XmlBuilder;
import com.example.mrxu.mutils.XmlParser;
import com.github.ybq.android.spinkit.style.Circle;
import com.kyleduo.switchbutton.SwitchButton;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

import static com.example.merxu.mypractice.R.id.business_SB;
import static com.example.merxu.mypractice.R.id.business_t1SB;

/**
 * 开通业务页面
 *
 * @author MrXu
 */

public class BusinessActivity extends BaseActivity implements CustomSwitchCompat.OnCheckedChangeListener {

    @BindView(R.id.title_bar_text)
    TextView titleBarText;
    @BindView(R.id.left_Imagemage)
    ImageView leftImagemage;

    @BindView(R.id.web_tn)
    WebView webTn;
    @BindView(business_SB)
    SwitchButton businessSB;
    @BindView(R.id.business_state_tv)
    TextView businessStateTv;
    @BindView(R.id.business_t1state_tv)
    TextView businessT1stateTv;
    @BindView(R.id.business_t1SB)
    SwitchButton businessT1SB;
    @BindView(R.id.business_pro)
    ProgressBar businessPro;

    private String url = "http://119.254.69.98:8090/agentmgr/Phoneagreement.html";
    private EventBus eventBus;
    private int TNOFF = 0;
    private int TNON = 1;
    private int T1OFF = 2;
    private int T1ON = 3;
    private int isState;

    //TN通知dialog
    private OkAppDialog TnokAppDialogON;
    private OkAppDialog TnokAppDialogOFF;
    //Tn关闭前提示dialog
    private CommonAppDialog TncommonAppDialogOFF;


    //T1通知dialog
    private OkAppDialog T1okAppDialogON;
    private OkAppDialog T1okAppDialogOFF;
    //T1关闭前提示dialog
    private CommonAppDialog T1commonAppDialogOFF;

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
        return R.layout.activity_business;
    }

    @Override
    public void initView(View view) {
        ButterKnife.bind(this);

        titleBarText.setText("7天业务开通");
        leftImagemage.setOnClickListener(this);

        webTn.loadUrl(url);
        TnokAppDialogON = new OkAppDialog(BusinessActivity.this);
        TnokAppDialogON.setCanceledOnTouchOutside(false);
        TnokAppDialogOFF = new OkAppDialog(BusinessActivity.this);
        TnokAppDialogOFF.setCanceledOnTouchOutside(false);
        TncommonAppDialogOFF = new CommonAppDialog(BusinessActivity.this);
        TncommonAppDialogOFF.setCanceledOnTouchOutside(false);

        T1okAppDialogON = new OkAppDialog(BusinessActivity.this);
        T1okAppDialogON.setCanceledOnTouchOutside(false);
        T1okAppDialogOFF = new OkAppDialog(BusinessActivity.this);
        T1okAppDialogOFF.setCanceledOnTouchOutside(false);
        T1commonAppDialogOFF = new CommonAppDialog(BusinessActivity.this);
        T1commonAppDialogOFF.setCanceledOnTouchOutside(false);
//      检查t+n状态


        Circle circle = new Circle();
        circle.setColor(0xFF58A6EF);
        businessPro.setIndeterminateDrawable(circle);

        initTnState();
        initT1State();


        businessSB.setOnCheckedChangeListener(this);
        businessT1SB.setOnCheckedChangeListener(this);

    }

    private Boolean initTnState() {

        boolean ischeck = true;

        businessSB.setOnCheckedChangeListener(null);

        if (UserInfo.getState() == 1) {

            ischeck = true;
            businessSB.setChecked(ischeck);
            businessStateTv.setText("您已开通T+N业务");
        } else {
            ischeck = false;
            businessSB.setChecked(ischeck);
            businessStateTv.setText("立即开通T+N业务");
        }


        businessSB.setOnCheckedChangeListener(this);
        return ischeck;

    }


    private Boolean initT1State() {

        boolean ischeck = true;

        businessT1SB.setOnCheckedChangeListener(null);

        if (UserInfo.getState() == 2) {

            ischeck = true;
            businessT1SB.setChecked(ischeck);
            businessT1stateTv.setText("您已开通T+1业务");
        } else {
            ischeck = false;
            businessT1SB.setChecked(ischeck);
            businessT1stateTv.setText("立即开通T+1业务");
        }

        businessT1SB.setOnCheckedChangeListener(this);
        return ischeck;

    }

    private void getTnStateON() {

        isState = 1;
        businessPro.setVisibility(View.VISIBLE);
        try {
            new TcpRequest(Construction.HOST, Construction.PORT)
                    .request(new GetSwitchStateInfoTn(
                            BusinessActivity.this, TNON).toString());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            MainUtils.showToast(this, "异常");
            e.printStackTrace();
        }
    }

    private void getTnStateOFF() {

        isState = 2;
        businessPro.setVisibility(View.VISIBLE);
        try {
            new TcpRequest(Construction.HOST, Construction.PORT)
                    .request(new GetSwitchStateInfoTn(
                            BusinessActivity.this, TNOFF).toString());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            MainUtils.showToast(this, "异常");
            e.printStackTrace();
        }
    }

    private void getT1StateON() {

        isState = 3;
        businessPro.setVisibility(View.VISIBLE);
        try {
            new TcpRequest(Construction.HOST, Construction.PORT)
                    .request(new GetSwitchStateInfoTn(
                            BusinessActivity.this, T1ON).toString());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            MainUtils.showToast(this, "异常");
            e.printStackTrace();
        }
    }

    private void getT1StateOFF() {

        isState = 4;
        businessPro.setVisibility(View.VISIBLE);
        try {
            new TcpRequest(Construction.HOST, Construction.PORT)
                    .request(new GetSwitchStateInfoTn(
                            BusinessActivity.this, T1OFF).toString());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            MainUtils.showToast(this, "异常");
            e.printStackTrace();
        }
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


            default:
                break;

        }

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

    public void onEvent(TcpRequestMessage message) {

        /**
         * true开
         * false 关
         *
         * */

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                businessPro.setVisibility(View.GONE);
            }
        });


        if (message.getTextCode().equals("F20020")) {


            switch (isState) {

                case 1:
                    /**
                     * tn 开
                     * */

                    if (message.getCode() == 1) {
                        Log.d("Get_Message", new String(message.getTcpRequestMessage()));
                        TagUtils.Field011Add(this);
                        try {
                            final HashMap<String, String> data = new XmlParser()
                                    .parse(new String(message.getTcpRequestMessage()));


                            if (data.get(XmlBuilder.fieldName(39)).equalsIgnoreCase(
                                    "00")) {
                                this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        TnokAppDialogON.setTitleStr("消息提示");
                                        TnokAppDialogON.setCanceledOnTouchOutside(false);
                                        TnokAppDialogON.setmsgStr("恭喜您！已成功开通T+N业务");
                                        TnokAppDialogON.setYesOnclickListener("好的", new OkAppDialog.onYesOnclickListener() {
                                            @Override
                                            public void onYesClick() {

                                                UserInfo.setState(1);
                                                initTnState();
                                                setTnstate();
                                                TnokAppDialogON.dismiss();
                                            }
                                        });

                                        TnokAppDialogON.show();
                                    }
                                });
                            } else {
                                this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        TnokAppDialogON.setTitleStr("消息提示");
                                        TnokAppDialogON.setCanceledOnTouchOutside(false);
                                        TnokAppDialogON.setmsgStr(data.get(XmlBuilder.fieldName(124)));
                                        TnokAppDialogON.setYesOnclickListener("好的", new OkAppDialog.onYesOnclickListener() {
                                            @Override
                                            public void onYesClick() {
                                                initTnState();
                                                setTnstate();
                                                TnokAppDialogON.dismiss();
                                            }
                                        });

                                        TnokAppDialogON.show();

                                    }
                                });
                            }
                        } catch (Exception e) {
                            // TODO: handle exception
                            e.printStackTrace();
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    TnokAppDialogON.setTitleStr("消息提示");
                                    TnokAppDialogON.setCanceledOnTouchOutside(false);
                                    TnokAppDialogON.setmsgStr("开通异常");
                                    TnokAppDialogON.setYesOnclickListener("好的", new OkAppDialog.onYesOnclickListener() {
                                        @Override
                                        public void onYesClick() {
                                            initTnState();
                                            setTnstate();
                                            TnokAppDialogON.dismiss();
                                        }
                                    });

                                    TnokAppDialogON.show();
                                }
                            });

                        }
                    } else {
                        if (message.getCode() == -2) {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    MainUtils.showToast(getApplicationContext(),
                                            "网络连接超时...");
                                }
                            });
                        }
                    }


                    break;

                case 2:


                    /**
                     * tn 关
                     * */

                    if (message.getCode() == 1) {
                        Log.d("Get_Message", new String(message.getTcpRequestMessage()));
                        TagUtils.Field011Add(this);
                        try {
                            final HashMap<String, String> data = new XmlParser()
                                    .parse(new String(message.getTcpRequestMessage()));

                            if (data.get(XmlBuilder.fieldName(39)).equalsIgnoreCase(
                                    "00")) {
                                this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        TnokAppDialogOFF.setTitleStr("消息提示");
                                        TnokAppDialogOFF.setCanceledOnTouchOutside(false);
                                        TnokAppDialogOFF.setmsgStr("已关闭T+N业务");
                                        TnokAppDialogOFF.setYesOnclickListener("好的", new OkAppDialog.onYesOnclickListener() {
                                            @Override
                                            public void onYesClick() {

                                                UserInfo.setState(0);
                                                initTnState();
                                                setTnstate();
                                                TnokAppDialogOFF.dismiss();
                                            }
                                        });

                                        TnokAppDialogOFF.show();
                                    }
                                });
                            } else {
                                this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        TnokAppDialogOFF.setTitleStr("消息提示");
                                        TnokAppDialogOFF.setCanceledOnTouchOutside(false);
                                        TnokAppDialogOFF.setmsgStr(data.get(XmlBuilder.fieldName(124)));
                                        TnokAppDialogOFF.setYesOnclickListener("好的", new OkAppDialog.onYesOnclickListener() {
                                            @Override
                                            public void onYesClick() {
                                                initTnState();
                                                setTnstate();
                                                TnokAppDialogOFF.dismiss();
                                            }
                                        });
                                        TnokAppDialogOFF.show();
                                    }
                                });
                            }
                        } catch (Exception e) {
                            // TODO: handle exception
                            e.printStackTrace();
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    TnokAppDialogOFF.setTitleStr("消息提示");
                                    TnokAppDialogOFF.setCanceledOnTouchOutside(false);
                                    TnokAppDialogOFF.setmsgStr("关闭T+N异常,请稍后再试");
                                    TnokAppDialogOFF.setYesOnclickListener("好的", new OkAppDialog.onYesOnclickListener() {
                                        @Override
                                        public void onYesClick() {
                                            initTnState();
                                            setTnstate();
                                            TnokAppDialogOFF.dismiss();
                                        }
                                    });

                                    TnokAppDialogOFF.show();
                                }
                            });

                        }
                    } else {
                        if (message.getCode() == -2) {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    MainUtils.showToast(getApplicationContext(),
                                            "网络连接超时...");
                                }
                            });
                        }

                    }


                    break;

                case 3:

                    /**
                     * t1 开
                     * */

                    if (message.getCode() == 1) {
                        Log.d("Get_Message", new String(message.getTcpRequestMessage()));
                        TagUtils.Field011Add(this);
                        try {
                            final HashMap<String, String> data = new XmlParser()
                                    .parse(new String(message.getTcpRequestMessage()));


                            if (data.get(XmlBuilder.fieldName(39)).equalsIgnoreCase(
                                    "00")) {
                                this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        T1okAppDialogON.setTitleStr("消息提示");
                                        T1okAppDialogON.setCanceledOnTouchOutside(false);
                                        T1okAppDialogON.setmsgStr("恭喜您！已成功开通T+1业务");
                                        T1okAppDialogON.setYesOnclickListener("好的", new OkAppDialog.onYesOnclickListener() {
                                            @Override
                                            public void onYesClick() {


                                                UserInfo.setState(2);
                                                initT1State();
                                                setTnstate();
                                                T1okAppDialogON.dismiss();
                                            }
                                        });

                                        T1okAppDialogON.show();
                                    }
                                });
                            } else {
                                this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        T1okAppDialogON.setTitleStr("消息提示");
                                        T1okAppDialogON.setCanceledOnTouchOutside(false);
                                        T1okAppDialogON.setmsgStr(data.get(XmlBuilder.fieldName(124)));
                                        T1okAppDialogON.setYesOnclickListener("好的", new OkAppDialog.onYesOnclickListener() {
                                            @Override
                                            public void onYesClick() {
                                                initT1State();
                                                setTnstate();
                                                T1okAppDialogON.dismiss();
                                            }
                                        });

                                        T1okAppDialogON.show();

                                    }
                                });
                            }
                        } catch (Exception e) {
                            // TODO: handle exception
                            e.printStackTrace();
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    T1okAppDialogON.setTitleStr("消息提示");
                                    T1okAppDialogON.setCanceledOnTouchOutside(false);
                                    T1okAppDialogON.setmsgStr("开通T+1异常");
                                    T1okAppDialogON.setYesOnclickListener("好的", new OkAppDialog.onYesOnclickListener() {
                                        @Override
                                        public void onYesClick() {
                                            initTnState();
                                            setTnstate();
                                            T1okAppDialogON.dismiss();
                                        }
                                    });

                                    T1okAppDialogON.show();
                                }
                            });

                        }
                    } else {
                        if (message.getCode() == -2) {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    MainUtils.showToast(getApplicationContext(),
                                            "网络连接超时...");
                                }
                            });
                        }
                    }

                    break;

                case 4:

                    /**
                     * t1 关
                     * */

                    if (message.getCode() == 1) {
                        Log.d("Get_Message", new String(message.getTcpRequestMessage()));
                        TagUtils.Field011Add(this);
                        try {
                            final HashMap<String, String> data = new XmlParser()
                                    .parse(new String(message.getTcpRequestMessage()));

                            if (data.get(XmlBuilder.fieldName(39)).equalsIgnoreCase(
                                    "00")) {
                                this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        T1okAppDialogOFF.setTitleStr("消息提示");
                                        T1okAppDialogOFF.setCanceledOnTouchOutside(false);
                                        T1okAppDialogOFF.setmsgStr("已关闭T+1业务");
                                        T1okAppDialogOFF.setYesOnclickListener("好的", new OkAppDialog.onYesOnclickListener() {
                                            @Override
                                            public void onYesClick() {

                                                UserInfo.setState(0);
                                                initT1State();
                                                setTnstate();
                                                T1okAppDialogOFF.dismiss();
                                            }
                                        });

                                        T1okAppDialogOFF.show();
                                    }
                                });
                            } else {
                                this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        T1okAppDialogOFF.setTitleStr("消息提示");
                                        T1okAppDialogOFF.setCanceledOnTouchOutside(false);
                                        T1okAppDialogOFF.setmsgStr(data.get(XmlBuilder.fieldName(124)));
                                        T1okAppDialogOFF.setYesOnclickListener("好的", new OkAppDialog.onYesOnclickListener() {
                                            @Override
                                            public void onYesClick() {
                                                initT1State();
                                                setTnstate();
                                                T1okAppDialogOFF.dismiss();
                                            }
                                        });
                                        T1okAppDialogOFF.show();
                                    }
                                });
                            }
                        } catch (Exception e) {
                            // TODO: handle exception
                            e.printStackTrace();
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    T1okAppDialogOFF.setTitleStr("消息提示");
                                    T1okAppDialogOFF.setCanceledOnTouchOutside(false);
                                    T1okAppDialogOFF.setmsgStr("关闭T+1异常,请稍后再试");
                                    T1okAppDialogOFF.setYesOnclickListener("好的", new OkAppDialog.onYesOnclickListener() {
                                        @Override
                                        public void onYesClick() {
                                            initTnState();
                                            setTnstate();
                                            T1okAppDialogOFF.dismiss();
                                        }
                                    });

                                    T1okAppDialogOFF.show();
                                }
                            });

                        }
                    } else {
                        if (message.getCode() == -2) {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    MainUtils.showToast(getApplicationContext(),
                                            "网络连接超时...");
                                }
                            });
                        }

                    }



                    break;

                default:

                    break;

            }


        }

    }

    public void setTnstate(){
        if(businessSB.isChecked()){

            UserInfo.setState(1);
        }else if(businessT1SB.isChecked()){
            UserInfo.setState(2);
        }else{
            UserInfo.setState(0);
        }
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        switch (buttonView.getId()) {
            case business_SB:
                if (isChecked) {

                    /**
                     * 开通
                     * */

                    if(businessT1SB.isChecked()){

                        MainUtils.showToast(this,"请先关闭T1业务");
                        initTnState();
                        return;
                    }
                    getTnStateON();

                } else {

                    TncommonAppDialogOFF.setTitleStr("提示");
                    TncommonAppDialogOFF.setmsgStr("确认关闭T+N业务？");
                    TncommonAppDialogOFF.setYesOnclickListener("确定", new CommonAppDialog.onYesOnclickListener() {
                        @Override
                        public void onYesClick() {

                            getTnStateOFF();
                            TncommonAppDialogOFF.dismiss();
                        }
                    });

                    TncommonAppDialogOFF.setNoOnclickListener("取消", new CommonAppDialog.onNoOnclickListener() {

                        @Override
                        public void onNoClick() {
                            TncommonAppDialogOFF.dismiss();
                            initTnState();
                        }
                    });
                    TncommonAppDialogOFF.show();
                }

                break;
            case business_t1SB:


                if (isChecked) {

                    /**
                     * 开通
                     * */

                    if(businessSB.isChecked()){

                        MainUtils.showToast(this,"请先关闭Tn业务");
                        initT1State();
                        return;
                    }

                    getT1StateON();

                } else {

                    T1commonAppDialogOFF.setTitleStr("提示");
                    T1commonAppDialogOFF.setmsgStr("确认关闭T+1业务？");
                    T1commonAppDialogOFF.setYesOnclickListener("确定", new CommonAppDialog.onYesOnclickListener() {
                        @Override
                        public void onYesClick() {

                            getT1StateOFF();
                            T1commonAppDialogOFF.dismiss();
                        }
                    });

                    T1commonAppDialogOFF.setNoOnclickListener("取消", new CommonAppDialog.onNoOnclickListener() {

                        @Override
                        public void onNoClick() {
                            T1commonAppDialogOFF.dismiss();
                            initT1State();
                        }
                    });
                    T1commonAppDialogOFF.show();
                }

                break;


            default:
                break;

        }

    }
}
