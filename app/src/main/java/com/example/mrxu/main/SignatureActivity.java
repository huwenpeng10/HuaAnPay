package com.example.mrxu.main;

import android.content.Context;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.example.merxu.mypractice.R;
import com.example.mrxu.base.BaseActivity;
import com.example.mrxu.mutils.CacheFile;
import com.example.mrxu.myviews.SignatureView;

import java.io.FileNotFoundException;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 签名页面
 *
 * @author  MrXu
 *
 * */

public class SignatureActivity extends BaseActivity {

    @BindView(R.id.sv_sign)
    SignatureView svSign;
    @BindView(R.id.bt_cancel)
    Button btCancel;
    @BindView(R.id.bt_confirm)
    Button btConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        return R.layout.activity_signature;
    }

    @Override
    public void initView(View view) {

        ButterKnife.bind(this);
        btCancel.setOnClickListener(this);
        btConfirm.setOnClickListener(this);
        svSign.clear();
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

        if (v == btConfirm) {
            setResult(RESULT_OK);
            try {
                svSign.getPreviewImage().compress(CompressFormat.PNG, 90,
                        openFileOutput(CacheFile.SIGN, MODE_PRIVATE));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            finish();
        } else if (v == btCancel) {
            svSign.clear();
        }

    }

    @Override
    public void setRequestedOrientation(int requestedOrientation) {
        return;
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResult(RESULT_OK);
            try {
                svSign.getPreviewImage().compress(CompressFormat.PNG, 90,
                        openFileOutput(CacheFile.SIGN, MODE_PRIVATE));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            finish();

            return false;
        }
        return super.onKeyDown(keyCode, event);

    }
}
