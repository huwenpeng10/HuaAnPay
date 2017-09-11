package com.example.mrxu.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.merxu.mypractice.R;
import com.example.mrxu.alldialog.CommonAppDialog;
import com.example.mrxu.alldialog.OkAppDialog;
import com.example.mrxu.allinfo.Construction;
import com.example.mrxu.allinfo.TradeInfo;
import com.example.mrxu.allinfo.UserInfo;
import com.example.mrxu.assemble_xml.AccountWithdrawTn;
import com.example.mrxu.assemble_xml.CashWithdrawal;
import com.example.mrxu.assemble_xml.GetSevenDayAmout;
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
import java.math.BigDecimal;
import java.math.MathContext;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.ParserConfigurationException;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;


/**
 * Tn提现页面
 *
 * @author MrXu
 */

public class WithdrawalsActivity extends BaseActivity implements PwdEditText.OnInputFinishListener {


    @BindView(R.id.left_Imagemage)
    ImageView leftImagemage;
    @BindView(R.id.title_bar_text)
    TextView titleBarText;

    @BindView(R.id.repl_username_tv)
    TextView replUsernameTv;
    @BindView(R.id.authState_iv)
    ImageView authStateIv;
    @BindView(R.id.user_money_tv)
    TextView userMoneyTv;
    @BindView(R.id.repl_bankNum_tv)
    TextView replBankNumTv;
    @BindView(R.id.repl_zhong_layout)
    AutoRelativeLayout repl_zhong_layout;
    @BindView(R.id.withdraw_state_tv)
    TextView withdrawStateTv;
    @BindView(R.id.withdrawals_sevenDay_LV)
    ListView withdrawals_sevenDay_LV;
    @BindView(R.id.userAmout_button)
    Button userAmoutButton;
    @BindView(R.id.withdrawals_pro)
    ProgressBar withdrawalsPro;
    @BindView(R.id.right_Tv)
    TextView rightTv;
    @BindView(R.id.withdrawals_money_dt)
    EditText withdrawalsMoneyDt;

    private PwdEditText withPasswordEd;

    private String Usermoney;
    private EventBus eventBus;
    private ArrayList<WithdrawalsDayInfo> arrayList;
    private WithdrawalsAdapter withdrawalsAdapter;
    private String itemDate;
    private int istype;
    private PopupWindow InputMoneywindow;
    private String copyconversionMoney;
    private String conversionMoney;
    private String inputStr;
//    private int popwindowType;
    private ImageView popwindowDisIv;
    private TextView popwindowForgotPass;


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
        return R.layout.activity_withdrawals;
    }

    @Override
    public void initView(View view) {

        ButterKnife.bind(this);
        leftImagemage.setOnClickListener(this);
        userAmoutButton.setOnClickListener(this);
        rightTv.setOnClickListener(this);
        titleBarText.setText("提现");
        arrayList = new ArrayList<WithdrawalsDayInfo>();
        withdrawalsAdapter = new WithdrawalsAdapter();
        withdrawals_sevenDay_LV.setAdapter(withdrawalsAdapter);

    }

    @Override
    public void doBusiness(Context mContext) {
        Circle circle = new Circle();
        circle.setColor(0xFFA3A3A3);
        withdrawalsPro.setIndeterminateDrawable(circle);

        Usermoney = TradeInfo.getUserMoney();
        initUserInfo();
        sevenDayWithDrawals();

    }

    //七天提现记录查询
    private void sevenDayWithDrawals() {
        withdrawalsPro.setVisibility(View.VISIBLE);
        istype = 1;
        try {
            new TcpRequest(Construction.HOST, Construction.PORT)
                    .request(new GetSevenDayAmout(
                            WithdrawalsActivity.this).toString());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            MainUtils.showToast(getApplicationContext(), "异常");
            e.printStackTrace();
        }
    }

    //日提现
    private void dayWithDrawals(String date /*, String password*/) {
//        withdrawalsPro.setVisibility(View.VISIBLE);
        istype = 2;
        try {
            new TcpRequest(Construction.HOST, Construction.PORT)
                    .request(new CashWithdrawal(
                            WithdrawalsActivity.this, date /*,password*/).toString());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            MainUtils.showToast(getApplicationContext(), "异常");
            e.printStackTrace();
        }
    }

    //总资产提现
    private void totalAssetsWithDrawals(String money/* , String password*/) {
        withdrawalsPro.setVisibility(View.VISIBLE);

        istype = 3;
        try {
            new TcpRequest(Construction.HOST, Construction.PORT)
                    .request(new AccountWithdrawTn(
                            WithdrawalsActivity.this, money/* ,password*/).toString());
        } catch (Exception e) {
            // TODO: handle exception

            Log.d(TAG, "totalAssetsWithDrawals: ");
        }
    }

    @Override
    public void widgetClick(View v) {

        switch (v.getId()) {
            case R.id.left_Imagemage:
                setResult(RESULT_OK);

                finish();
                overridePendingTransition(R.animator.push_right_out,
                        R.animator.push_right_in);
                break;
            case R.id.right_Tv:
                Intent intent = new Intent(this, WithdrawalsRecordActivity.class);
                startActivity(intent);
                overridePendingTransition(R.animator.push_left_in,
                        R.animator.push_left_out);
                break;
            case R.id.userAmout_button:
                if (Double.parseDouble(TradeInfo.getUserMoney()) < 200) {
                    MainUtils.showToast(WithdrawalsActivity.this, "总资产小于200元");
                    return;
                }
                String inputmoneystr = withdrawalsMoneyDt.getEditableText()
                        .toString();
                if(TextUtils.isEmpty(inputmoneystr)){

                    MainUtils.showToast(this,"请输入金额");
                    return;
                }
                double inputmoneyD = Double.parseDouble(inputmoneystr);
                BigDecimal bd = new BigDecimal(inputmoneyD);
                conversionMoney = bd.setScale(2,BigDecimal.ROUND_HALF_UP)+"";
                Log.d(TAG, "widgetClick: "+Double.parseDouble(conversionMoney));
                if(inputmoneyD < 200){

                    MainUtils.showToast(WithdrawalsActivity.this,"提现金额小于200元");
                    return;
                }
                if(inputmoneyD > Double.parseDouble(TradeInfo.getUserMoney())){

                    MainUtils.showToast(WithdrawalsActivity.this,"请输入有效金额");
                    return;
                }

//                popwindowType = 1;
                /*showPopwindow();*/

                copyconversionMoney = conversionMoney;
                conversionMoney = TagUtils.fill0AtLeft(new BigDecimal(Double.parseDouble(conversionMoney) * 100,
                        MathContext.DECIMAL32).intValue(), 12);

                Log.d(TAG, "onInputFinish: "+conversionMoney);
                totalAssetsWithDrawals(conversionMoney);
//                InputMoneywindow.dismiss();


                break;

            case R.id.popwindow_dis_iv:

//                InputMoneywindow.dismiss();

                break;
            case R.id.popwindow_forgot_pass:


//                InputMoneywindow.dismiss();

                Intent forgotPassintent =new Intent(this , CheckUserInfoActivity.class);
                startActivity(forgotPassintent);
                overridePendingTransition(R.animator.push_left_in,
                        R.animator.push_left_out);

                break;

            default:
                break;

        }

    }


    public void onEvent(TcpRequestMessage message) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                withdrawalsPro.setVisibility(View.GONE);
            }
        });

        switch (istype) {

            case 1:

                if (message.getCode() == 1) {
                    Log.d("Get_Message", new String(message.getTcpRequestMessage()));
                    TagUtils.Field011Add(this);
                    try {
                        final HashMap<String, String> data = new XmlParser()
                                .parse(new String(message.getTcpRequestMessage()));
                        Log.d("state", data.get(XmlBuilder.fieldName(39)) + "");
                        if (data.get(XmlBuilder.fieldName(39)).equalsIgnoreCase(
                                "00")
                                || data.get(XmlBuilder.fieldName(39))
                                .equalsIgnoreCase("TT")) {
                            runOnUiThread(new Runnable() {
                                public void run() {
//                            20170618|000000001400|000000000007|00##


                                    if (!TextUtils.isEmpty(data.get(XmlBuilder.fieldName(65)))) {

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                repl_zhong_layout.setVisibility(View.VISIBLE);
                                            }
                                        });

                                        String[] data65 = data.get(XmlBuilder.fieldName(65)).split("##");

                                        for (String item : data65) {

                                            String[] itemDada = item.split("\\|");

                                            String date = itemDada[0];

                                            String withdrawalsDayMoney = new BigDecimal(
                                                    itemDada[1])
                                                    .divide(new BigDecimal(100))
                                                    .setScale(2).toPlainString();
                                            String counterFee = new BigDecimal(
                                                    itemDada[2])
                                                    .divide(new BigDecimal(100))
                                                    .setScale(2).toPlainString();

                                            String itemState = itemDada[3];

                                            WithdrawalsDayInfo withdrawalsDayInfo = new WithdrawalsDayInfo(date, withdrawalsDayMoney, counterFee, itemState);
                                            arrayList.add(withdrawalsDayInfo);


                                        }


                                    } else {

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                repl_zhong_layout.setVisibility(View.GONE);
                                            }
                                        });

                                    }

                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    MainUtils.showToast(WithdrawalsActivity.this, data.get(XmlBuilder.fieldName(124)));
                                }
                            });

                        }
                    } catch (Exception e) {
                        // TODO: handle exception
                        e.printStackTrace();
                    } finally {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {


                                withdrawalsAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                }
                break;
            case 2:
                if (message.getCode() == 1) {
                    Log.d("Get_Message", new String(message.getTcpRequestMessage()));
                    TagUtils.Field011Add(this);
                    final HashMap<String, String> data;
                    try {
                        data = new XmlParser()
                                .parse(new String(message.getTcpRequestMessage()));
                        if (data.get(XmlBuilder.fieldName(39)).equalsIgnoreCase(
                                "00")
                                || data.get(XmlBuilder.fieldName(39))
                                .equalsIgnoreCase("TT")||data.get(XmlBuilder.fieldName(39))
                                .equalsIgnoreCase("UO")) {

                            /**
                             * 日提现成功
                             * */

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    MainUtils.showToast(WithdrawalsActivity.this, "提现成功");
                                    /*刷新提现数据*/
                                    sevenDayWithDrawals();
                                }
                            });

                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    MainUtils.showToast(WithdrawalsActivity.this, data.get(XmlBuilder.fieldName(124)));
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

                }

                break;
            case 3:
                if (message.getCode() == 1) {
                    Log.d("Get_Message", new String(message.getTcpRequestMessage()));
                    TagUtils.Field011Add(this);
                    final HashMap<String, String> data;
                    try {
                        data = new XmlParser()
                                .parse(new String(message.getTcpRequestMessage()));
                        if (data.get(XmlBuilder.fieldName(39)).equalsIgnoreCase(
                                "00")
                                || data.get(XmlBuilder.fieldName(39))
                                .equalsIgnoreCase("TT")||data.get(XmlBuilder.fieldName(39))
                                .equalsIgnoreCase("UO")) {

                            /**
                             * 总资产提现成功
                             * */

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    final OkAppDialog okAppDialog = new OkAppDialog(WithdrawalsActivity.this);
                                    okAppDialog.setTitleStr("提示");
                                    okAppDialog.setmsgStr("提现成功");
                                    okAppDialog.setYesOnclickListener("好的", new OkAppDialog.onYesOnclickListener() {
                                        @Override
                                        public void onYesClick() {
                                            double doubleUserMoney = Double.parseDouble(TradeInfo.getUserMoney());
                                            Log.d(TAG, "onYesClick: doubleUserMoney=="+doubleUserMoney);
                                            double doubleStrConversionMoney = Double.parseDouble(copyconversionMoney);
                                            Log.d(TAG, "onYesClick: doubleStrConversionMoney=="+doubleStrConversionMoney);
                                            doubleUserMoney = doubleUserMoney - doubleStrConversionMoney;
                                            TradeInfo.setUserMoney(doubleUserMoney + "");
                                            userMoneyTv.setText(doubleUserMoney + "");
                                            okAppDialog.dismiss();
                                        }
                                    });
                                    okAppDialog.show();

                                }
                            });

                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    MainUtils.showToast(WithdrawalsActivity.this, data.get(XmlBuilder.fieldName(124)));
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

                }


                break;


            default:
                break;

        }

    }


    public void initUserInfo() {

        userMoneyTv.setText("￥" + Usermoney);
        if (Double.parseDouble(Usermoney) > 10) {
            withdrawStateTv.setText("+可提现");
        } else {
            withdrawStateTv.setText("-金额不足");
        }
        replUsernameTv.setText(UserInfo.getUserName());

        String cardNo = UserInfo.getBindingCardNo() + "";
        String startNo = cardNo.substring(0, 4);
        String endNo = cardNo.substring(cardNo.length() - 4, cardNo.length());
        replBankNumTv.setText(startNo + "******" + endNo);

        if (UserInfo.getAuthTipsNumber() == 1) {
            authStateIv.setImageDrawable(getResources().getDrawable(R.mipmap.new_zhanghuxinxi_renzheng_img));
        } else {
            authStateIv.setImageDrawable(getResources().getDrawable(R.mipmap.new_zhanghuxinxi_weirenzheng_img));
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

//    提现
    @Override
    public void onInputFinish(String password) {


//        if(popwindowType==1){
//
//            conversionMoney = TagUtils.fill0AtLeft(new BigDecimal(Double.parseDouble(conversionMoney) * 100,
//                    MathContext.DECIMAL32).intValue(), 12);
//
//            totalAssetsWithDrawals(conversionMoney /*,password*/);
////            InputMoneywindow.dismiss();
//        }else if(popwindowType == 2){
//
//            dayWithDrawals(itemDate/* , password*/);
//        }

    }


    class WithdrawalsAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public WithdrawalsDayInfo getItem(int position) {
            return arrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            HoldView holdView = null;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.withdrawals_day_item, parent, false);
                holdView = new HoldView(convertView);
                convertView.setTag(holdView);
            } else {
                holdView = (HoldView) convertView.getTag();
            }
            holdView.with_counterfee_tv.setText("手续费" + arrayList.get(position).getCounterFee());
            holdView.withitem_money_tv.setText("￥" + arrayList.get(position).getWithdrawalsDayMoney());


            if (Double.parseDouble(arrayList.get(position).getWithdrawalsDayMoney()) - Double.parseDouble(arrayList.get(position).getCounterFee()) >= 200) {
                holdView.withitem_state_tv.setText("提现");
                holdView.withitem_state_tv.setFocusable(false);
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final CommonAppDialog commonAppDialog = new CommonAppDialog(WithdrawalsActivity.this);
                        commonAppDialog.setTitleStr("提示");
                        commonAppDialog.setmsgStr("确认提现？");
                        commonAppDialog.setYesOnclickListener("确定", new CommonAppDialog.onYesOnclickListener() {
                            @Override
                            public void onYesClick() {
                                itemDate = arrayList.get(position).getItemDate();
//                                popwindowType = 2;
//                                showPopwindow();


                                dayWithDrawals(itemDate/* , password*/);

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
                    }
                });


            } else {

                holdView.withitem_state_tv.setText("提现");
                holdView.withitem_state_tv.setFocusable(false);
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final OkAppDialog okAppDialog = new OkAppDialog(WithdrawalsActivity.this);
                        okAppDialog.setTitleStr("提示");
                        okAppDialog.setmsgStr("提现金额扣除手续费后需高于200元，否则不可提现");
                        okAppDialog.setYesOnclickListener("好的", new OkAppDialog.onYesOnclickListener() {
                            @Override
                            public void onYesClick() {

                                okAppDialog.dismiss();
                            }
                        });
                        okAppDialog.show();
                    }
                });

            }


            Date date = null;

            try {
                date = new SimpleDateFormat("yyyyMMdd").parse(itemDate);
                itemDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            holdView.withitem_date_tv.setText(itemDate);

            return convertView;
        }

        public class HoldView {

            TextView with_counterfee_tv;
            TextView withitem_money_tv;
            TextView withitem_state_tv;
            TextView withitem_date_tv;

            public HoldView(View view) {
                with_counterfee_tv = (TextView) view.findViewById(R.id.with_counterfee_tv);
                withitem_money_tv = (TextView) view.findViewById(R.id.withitem_money_tv);
                withitem_state_tv = (TextView) view.findViewById(R.id.withitem_state_tv);
                withitem_date_tv = (TextView) view.findViewById(R.id.withitem_date_tv);
            }

        }

    }

    private void showPopwindow() {
        // 利用layoutInflater获得View
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.inputmoney_popwindow, null);
        withPasswordEd = (PwdEditText) view.findViewById(R.id.with_password_ed);
        popwindowDisIv = (ImageView) view.findViewById(R.id.popwindow_dis_iv);
        popwindowForgotPass = (TextView) view.findViewById(R.id.popwindow_forgot_pass);
        popwindowDisIv.setOnClickListener(this);
        popwindowForgotPass.setOnClickListener(this);
        withPasswordEd.setOnInputFinishListener(this);
        InputMoneywindow = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        InputMoneywindow.setFocusable(true);
        InputMoneywindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        // 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        InputMoneywindow.setBackgroundDrawable(dw);
        // 设置popWindow的显示和消失动画
        InputMoneywindow.setAnimationStyle(R.style.mypopwindow_anim_style);
        // 在底部显示
        InputMoneywindow.showAtLocation(this.findViewById(R.id.userAmout_button),
                Gravity.BOTTOM, 0, 0);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            public void run() {
                InputMethodManager inputManager = (InputMethodManager) withPasswordEd.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(withPasswordEd, 0);
            }

        }, 500);


    }


    class WithdrawalsDayInfo {

        private String itemDate;
        private String withdrawalsDayMoney;
        private String counterFee;
        private String itemState;

        public WithdrawalsDayInfo(String itemDate, String withdrawalsDayMoney, String counterFee, String itemState) {
            this.itemDate = itemDate;
            this.withdrawalsDayMoney = withdrawalsDayMoney;
            this.counterFee = counterFee;
            this.itemState = itemState;
        }

        public String getItemDate() {
            return itemDate;
        }

        public void setItemDate(String itemDate) {
            this.itemDate = itemDate;
        }

        public String getWithdrawalsDayMoney() {
            return withdrawalsDayMoney;
        }

        public void setWithdrawalsDayMoney(String withdrawalsDayMoney) {
            this.withdrawalsDayMoney = withdrawalsDayMoney;
        }

        public String getCounterFee() {
            return counterFee;
        }

        public void setCounterFee(String counterFee) {
            this.counterFee = counterFee;
        }

        public String getItemState() {
            return itemState;
        }

        public void setItemState(String itemState) {
            this.itemState = itemState;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (InputMoneywindow != null) {

            InputMoneywindow.dismiss();
        }

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResult(RESULT_OK);

            finish();
            overridePendingTransition(R.animator.push_right_out,
                    R.animator.push_right_in);

            return false;
        }
        return super.onKeyDown(keyCode, event);

    }
}
