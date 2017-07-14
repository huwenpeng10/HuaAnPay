package com.example.mrxu.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.merxu.mypractice.R;
import com.example.mrxu.allinfo.UserInfo;
import com.example.mrxu.base.BaseActivity;
import com.example.mrxu.mutils.MainUtils;
import com.zhy.autolayout.AutoRelativeLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.mrxu.allinfo.UserInfo.authTips;

public class PassWordManageActivity extends BaseActivity {

    @BindView(R.id.title_bar_text)
    TextView titleBarText;
    @BindView(R.id.left_Imagemage)
    ImageView leftImagemage;
    @BindView(R.id.set_pay_pass_layout)
    AutoRelativeLayout setPayPassLayout;
    @BindView(R.id.set_login_pass_layout)
    AutoRelativeLayout setLoginPassLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        return R.layout.activity_pass_word_manage;
    }

    @Override
    public void initView(View view) {
        ButterKnife.bind(this);
        titleBarText.setText("密码管理");
        leftImagemage.setOnClickListener(this);
        setPayPassLayout.setOnClickListener(this);
        setLoginPassLayout.setOnClickListener(this);
    }

    @Override
    public void doBusiness(Context mContext) {

        if (UserInfo.getPayPasswordState().equals("00")){
            setPayPassLayout.setVisibility(View.GONE);
        }else{
            setPayPassLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void widgetClick(View v) {

        switch (v.getId()){

            case R.id.left_Imagemage :

                PassWordManageActivity.this.finish();
                overridePendingTransition(R.animator.push_right_out,
                        R.animator.push_right_in);

                break;
            case R.id.set_pay_pass_layout :

                if (authTips.equals("认证成功")) {
                    Intent resetPayIntent=new Intent(this , CheckUserInfoActivity.class);
                    startActivity(resetPayIntent);
                    overridePendingTransition(R.animator.push_left_in,
                            R.animator.push_left_out);
                    this.finish();
                } else {

                    MainUtils.showToast(this, "请先进行实名认证");
                }



                break;
            case R.id.set_login_pass_layout :

                Intent resetloginintent=new Intent(this , ResetLoginPasswordActivity.class);
                startActivity(resetloginintent);
                overridePendingTransition(R.animator.push_left_in,
                        R.animator.push_left_out);
                this.finish();
                break;


            default:
                break;

        }
    }
}
