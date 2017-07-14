package com.example.mrxu.myviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.merxu.mypractice.R;
import com.github.ybq.android.spinkit.style.Wave;
import com.zhy.autolayout.AutoRelativeLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.merxu.mypractice.R.id.messge_tv;
import static com.example.merxu.mypractice.R.id.tv_tips;

/**
 * Created by MrXu on 2017/5/24.
 */

public class ScanHuaDongView extends AutoRelativeLayout {


    private final Context context;
    @BindView(tv_tips)
    TextView tvTips;
    @BindView(messge_tv)
    TextView messgeTv;
    @BindView(R.id.iv_bootm)
    AutoRelativeLayout ivBootm;
    @BindView(R.id.iv_san_left)
    ImageView ivSanLeft;
    @BindView(R.id.iv_san_right)
    ImageView ivSanRight;
    @BindView(R.id.huadong_pro)
    ProgressBar huadongPro;

    public ScanHuaDongView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initLayout();
    }


    private void initLayout() {
        // TODO Auto-generated method stub
        inflate(context, R.layout.layout_scan_huadong, this);
        ButterKnife.bind(this);
        Wave wave=new Wave();
        huadongPro.setIndeterminateDrawable(wave);

    }

    public void startAnim() {
        runToLeft();
        runToRight();
    }

    public void setLayoutParm(int leftResId, int RightResId, int huadongpro, int BootmResId) {
        // TODO Auto-generated method stub
        if (leftResId == -1) {
            ivSanLeft.setVisibility(View.INVISIBLE);
        } else {
            ivSanLeft.setVisibility(View.VISIBLE);
        }

        if (RightResId == -1) {
            ivSanRight.setVisibility(View.INVISIBLE);
        } else {
            ivSanRight.setVisibility(View.VISIBLE);
        }
        if (huadongpro == -1) {
            huadongPro.setVisibility(View.INVISIBLE);
        } else {
            huadongPro.setVisibility(View.VISIBLE);
        }
        if (BootmResId == -1) {
            ivBootm.setVisibility(View.INVISIBLE);
        } else {
            ivBootm.setBackgroundResource(BootmResId);
        }

    }

    public void setText(String str) {
        tvTips.setText(str);
    }

    public void setMessge(String str) {
        // TODO Auto-generated method stub
        messgeTv.setText(str);
    }


    /**
     * 由左向右运动
     */
    private void runToLeft() {
        // TODO Auto-generated method stub
        Animation anim = AnimationUtils.loadAnimation(context, R.anim.qpos_anim_toright);
        ivSanLeft.startAnimation(anim);
    }


    /**
     * 由由向左运动
     */
    private void runToRight() {
        // TODO Auto-generated method stub
        Animation anim = AnimationUtils.loadAnimation(context, R.anim.qpos_anim_toleft);
        ivSanRight.startAnimation(anim);
    }

}
