package com.example.mrxu.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.example.merxu.mypractice.R;
import com.example.mrxu.allinfo.TradeInfo;
import com.example.mrxu.allinfo.TradeInfo.PageState;
import com.example.mrxu.allinfo.UserInfo;
import com.example.mrxu.base.BaseActivity;
import com.example.mrxu.mutils.MainUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReplenishingActivity extends BaseActivity implements OnEditorActionListener {


    @BindView(R.id.left_Imagemage)
    ImageView leftImagemage;
    @BindView(R.id.title_bar_text)
    TextView titlebartext;


    @BindView(R.id.repl_username_tv)
    TextView replUsernameTv;
    @BindView(R.id.repl_bankName_tv)
    TextView replBankNameTv;
    @BindView(R.id.repl_bankNum_tv)
    TextView replBankNumTv;
    @BindView(R.id.authState_iv)
    ImageView authStateIv;
    @BindView(R.id.repl_money_dt)
    EditText replMoneydt;
    @BindView(R.id.next_but)
    Button nextBut;
    @BindView(R.id.urgent_withdrawals_but)
    Button urgentWithdrawalsBut;
    private String money;


    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        return R.layout.activity_replenishing;
    }


    @Override
    public void initView(View view) {
        ButterKnife.bind(this);




        titlebartext.setText("账户充值");
        leftImagemage.setOnClickListener(this);
        nextBut.setOnClickListener(this);
        urgentWithdrawalsBut.setOnClickListener(this);
        replMoneydt.setOnEditorActionListener(this);


    }

    @Override
    public void doBusiness(Context mContext) {
        initUserInfo();
    }

    public void initUserInfo(){

        replUsernameTv.setText(UserInfo.getUserName());
        replBankNameTv.setText(UserInfo.getBankName());

        String cardNo = UserInfo.getBindingCardNo()+"";//银行卡号
        String startNo = cardNo.substring(0, 4);
        String endNo = cardNo.substring(cardNo.length() - 4, cardNo.length());
        replBankNumTv.setText(startNo + "******" + endNo);

        if (UserInfo.getAuthTipsNumber()==1){
            authStateIv.setImageDrawable(getResources().getDrawable(R.mipmap.new_zhanghuxinxi_renzheng_img));
        }else{
            authStateIv.setImageDrawable(getResources().getDrawable(R.mipmap.new_zhanghuxinxi_weirenzheng_img));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

//        Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//
//            public void run() {
//                InputMethodManager inputManager = (InputMethodManager) replMoneydt.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//                inputManager.showSoftInput(replMoneydt, 0);
//            }
//
//        }, 500);


    }

    @Override
    public void widgetClick(View v) {

        switch (v.getId()) {
            case R.id.left_Imagemage:

                this.finish();

                overridePendingTransition(R.animator.push_right_out,
                        R.animator.push_right_in);
                break;
            case R.id.next_but:

                TradeInfo.setPageState(PageState.SHUAKAZHIFU);
                startNextActivity();
                break;
            case R.id.urgent_withdrawals_but://加急体现

                Intent urgentWIntent =new Intent(this,UrgentWithdrawalsActivity.class);
                startActivity(urgentWIntent);
                overridePendingTransition(R.animator.push_left_in,
                        R.animator.push_left_out);
                break;


            default:
                break;

        }

    }



    private void startNextActivity() {
        money = replMoneydt.getText().toString();
        if (!TextUtils.isEmpty(money)) {
            if (!money.substring(0, 1).equals(".")) {//substring(0, 1) 截取字符串 从0位开始 到1位结束
                if (Double.parseDouble(money) > 0) {

                    if (TradeInfo.getPosName().equals(getString(R.string.Qpos))) {
                        TradeInfo.setInputMoney(money);
                        Intent intent = new Intent(this, BlueToothConnectActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.animator.push_left_in,
                                R.animator.push_left_out);
                    } else {
                        TradeInfo.setInputMoney(money);
                        Intent intent = new Intent(this, MPOSConnectActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.animator.push_left_in,
                                R.animator.push_left_out);
                    }
                } else {
                    MainUtils.showToast(this, "您输入的金额不对");
                }
            } else {
                MainUtils.showToast(this, "您输入的金额不对");
            }
        } else {
            MainUtils.showToast(this, "请您输入金额");
        }
    }


    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        {
            if (v == replMoneydt) {

                startNextActivity();

            }

            return false;
        }
    }
}
