package com.example.mrxu.main;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.merxu.mypractice.R;
import com.example.mrxu.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 关于我们
 *
 * @author MrXu
 * */

public class AboutusActivity extends BaseActivity {



    @BindView(R.id.title_bar_text)
    TextView title_bar_text;
    @BindView(R.id.left_Imagemage)
    ImageView leftImagemage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        return R.layout.activity_aboutus;
    }

    @Override
    public void initView(View view) {
        ButterKnife.bind(this);
        title_bar_text.setText("关于我们");
        leftImagemage.setOnClickListener(this);
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


                default:
                break;

        }


    }
}
