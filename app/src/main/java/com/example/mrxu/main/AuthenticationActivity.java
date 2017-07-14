package com.example.mrxu.main;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.merxu.mypractice.R;
import com.example.mrxu.alldialog.WebOkAppDialog;
import com.example.mrxu.base.BaseActivity;
import com.example.mrxu.mutils.CardUtils;
import com.example.mrxu.mutils.MainUtils;

import java.text.ParseException;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 实名认证完善用户信息页面
 *
 * @author MrXu
 */

public class AuthenticationActivity extends BaseActivity {


    @BindView(R.id.left_Imagemage)
    ImageView leftImageage;
    @BindView(R.id.title_bar_text)
    TextView titleBarText;


    @BindView(R.id.auth_name_et)
    EditText authNameEt;
    @BindView(R.id.auth_cardid_et)
    EditText authCardidEt;
    @BindView(R.id.auth_cardnumber_et)
    EditText authCardnumberEt;
    @BindView(R.id.auth_openingbank_name_et)
    EditText authOpeningbankNameEt;
    @BindView(R.id.auth_openingbank_number_et)
    EditText authOpeningbankNumberEt;
    @BindView(R.id.next_but)
    Button nextBut;

    @BindView(R.id.cb_deal)
    CheckBox cbDeal;
    //    条款信息
    @BindView(R.id.tv_deal)
    TextView tvDeal;
    //开户行查询
    @BindView(R.id.btn_inquire)
    TextView btnInquire;


    public static final String KEY_NAME = "name";
    public static final String KEY_USERID = "user_id";
    public static final String KEY_CARDNUMBER = "bank_cardnumber";
    public static final String KEY_OPPENNAME = "oppen_name";
    public static final String KEY_NUMBER = "oppen_number";


    @Override
    public void initParms(Bundle parms) {


    }

    @Override
    public int bindLayout() {
        return R.layout.activity_authentication;
    }

    @Override
    public void initView(View view) {
        ButterKnife.bind(this);

        titleBarText.setText("实名认证");
        leftImageage.setOnClickListener(this);
        nextBut.setOnClickListener(this);
        tvDeal.setOnClickListener(this);
        btnInquire.setOnClickListener(this);

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

            case R.id.btn_inquire:
                Intent actionintent = new Intent();
                actionintent.setData(Uri.parse("http://www.posp.cn"));
                actionintent.setAction(Intent.ACTION_VIEW);
                startActivity(actionintent); //启动浏览器
                break;
            case R.id.next_but:

//                姓名
//                身份证
//                银行卡号
//                开户人名称
//                开户行号

                String name = authNameEt.getEditableText().toString();
                String cardid = authCardidEt.getEditableText().toString();
                String cardnumber = authCardnumberEt.getEditableText().toString();
                String openingbankName = authOpeningbankNameEt.getEditableText().toString();
                String openingbankNumber = authOpeningbankNumberEt.getEditableText().toString();
                CardUtils cardUtils = new CardUtils();
                Boolean checkCard = null;
                try {
                    checkCard = cardUtils.IDCardValidate(cardid).equals("")?false:true;
                } catch (ParseException e) {
                    e.printStackTrace();
                    MainUtils.showToast(this, "身份证查询异常");
                }


                if (TextUtils.isEmpty(name)) {
                    MainUtils.showToast(this, authNameEt.getHint().toString());
                    return;
                }
                if (TextUtils.isEmpty(cardid)) {
                    MainUtils.showToast(this, authCardidEt.getHint().toString());
                    return;
                }
                if (checkCard) {
                    MainUtils.showToast(this, "请输入正确的身份证号");
                    return;
                }
                if (TextUtils.isEmpty(cardnumber)) {
                    MainUtils.showToast(this, authCardnumberEt.getHint().toString());
                    return;
                }
                if(cardnumber.length()<16||cardnumber.length()>19){
                    MainUtils.showToast(this, "请输入正确的银行卡号");
                    return;
                }
                if (TextUtils.isEmpty(openingbankName)) {
                    MainUtils.showToast(this, authOpeningbankNameEt.getHint().toString());
                    return;
                }
                if (TextUtils.isEmpty(openingbankNumber)) {
                    MainUtils.showToast(this, authOpeningbankNumberEt.getHint().toString());
                    return;
                }
                if(openingbankNumber.length()!=12){

                    MainUtils.showToast(this, "请输入正确的开户行号");
                    return;
                }
                if (!cbDeal.isChecked()) {
                    MainUtils.showToast(this, "请阅读并勾选用户协议");
                    return;
                }

                Intent intent = new Intent(this, AuthenticationPhotoActivity.class);

                intent.putExtra(KEY_NAME, name);
                intent.putExtra(KEY_USERID, cardid);
                intent.putExtra(KEY_CARDNUMBER, cardnumber);
                intent.putExtra(KEY_OPPENNAME, openingbankName);
                intent.putExtra(KEY_NUMBER, openingbankNumber);
                startActivity(intent);
                overridePendingTransition(R.animator.push_left_in, R.animator.push_left_out);

                break;


            default:
                break;

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
    }
}
