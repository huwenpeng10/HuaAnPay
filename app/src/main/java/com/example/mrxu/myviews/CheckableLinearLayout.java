package com.example.mrxu.myviews;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Checkable;
import android.widget.TextView;

import com.example.merxu.mypractice.R;
import com.zhy.autolayout.AutoRelativeLayout;

/**
 * Created by MrXu on 2017/6/22.
 */

public class CheckableLinearLayout extends AutoRelativeLayout implements Checkable {

    private boolean mChecked;
    //标记金额
    private TextView face_surface_money;
    //真实金额
    private TextView face_actualmoney;
    private AutoRelativeLayout autoRelativeLayout;


    public CheckableLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater layoutInflater= (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        autoRelativeLayout = (AutoRelativeLayout) layoutInflater.inflate(R.layout.face_value_view,this);
        face_surface_money = (TextView) autoRelativeLayout.findViewById(R.id.face_surface_money);
        face_actualmoney = (TextView) autoRelativeLayout.findViewById(R.id.face_actualmoney);
    }

    /**
     * 表面金额*/
    public void setFaceSurfaceMoney(String str){
        face_surface_money.setText(str);
    }
    /**
     * 实际金额*/
    public void setFaceActualMoney(String str){
        face_actualmoney.setText(str);
    }

    @Override
    public void setChecked(boolean checked) {
        mChecked = checked;
         setBackgroundDrawable(checked ? new ColorDrawable(0xFF58A5EE) :  new ColorDrawable(0xFFB5E0FF));//当选中时呈现蓝色
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void toggle() {

        setChecked(!mChecked);
    }

}
