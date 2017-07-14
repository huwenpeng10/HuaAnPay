package com.example.mrxu.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.merxu.mypractice.R;
import com.example.mrxu.allinfo.TradeInfo;
import com.example.mrxu.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.mrxu.main.PhoneReplenishingActivity.ACTUALMONEY;
import static com.example.mrxu.main.PhoneReplenishingActivity.INPUTPHONENUMBER;
import static com.example.mrxu.main.PhoneReplenishingActivity.REPLENISHINGCODE;
import static com.example.mrxu.main.PhoneReplenishingActivity.SURFACEMONEY;

/**
 * 手机充值详情页面
 *
 * @author MrXu
 */
public class PhoneReplenishDetailActivity extends BaseActivity {


    @BindView(R.id.left_Imagemage)
    ImageView leftImagemage;
    @BindView(R.id.title_bar_text)
    TextView titleBarText;
    @BindView(R.id.phone_number_tv)
    TextView phoneNumberTv;
    @BindView(R.id.surfaceMoney_tv)
    TextView surfaceMoneyTv;
    @BindView(R.id.actualMoney_tv)
    TextView actualMoneyTv;
    @BindView(R.id.pay_tpye_tv)
    TextView payTpyeTv;
    @BindView(R.id.detail_but)
    Button detailBut;
    private String actualMoney;
    private String surfaceMoney;
    private String replenishingCode;
    private String inputPhoneNumber;

    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        return R.layout.activity_phone_replenish_detail;
    }

    @Override
    public void initView(View view) {
        ButterKnife.bind(this);
        titleBarText.setText("充值详情");
        leftImagemage.setOnClickListener(this);
        detailBut.setOnClickListener(this);
        Intent intent = getIntent();

        actualMoney=intent.getStringExtra(ACTUALMONEY);
        surfaceMoney=intent.getStringExtra(SURFACEMONEY);
        replenishingCode=intent.getStringExtra(REPLENISHINGCODE);
        inputPhoneNumber=intent.getStringExtra(INPUTPHONENUMBER);

        phoneNumberTv.setText(inputPhoneNumber);
        surfaceMoneyTv.setText(surfaceMoney);
        actualMoneyTv.setText(actualMoney);

    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

        switch (v.getId()){
            case R.id.left_Imagemage :
                finish();

                overridePendingTransition(R.animator.push_right_out,
                        R.animator.push_right_in);
                break;
            case R.id.detail_but :

                Intent intent=null;

                if (TradeInfo.getPosName().equals(getString(R.string.Qpos))) {
                    intent=new Intent(PhoneReplenishDetailActivity.this,BlueToothConnectActivity.class);
                }else{
                    intent=new Intent(PhoneReplenishDetailActivity.this,MPOSConnectActivity.class);
                }

                TradeInfo.setInputMoney(actualMoney);
                TradeInfo.setReplenishingCode(replenishingCode);
                TradeInfo.setReplenishingPhoneNumber(inputPhoneNumber);

                TradeInfo.setPageState(TradeInfo.PageState.SHOUJICHONGZHI);
                startActivity(intent);

                overridePendingTransition(R.animator.push_left_in,
                        R.animator.push_left_out);

                break;


                default:
                break;

        }
    }

}
