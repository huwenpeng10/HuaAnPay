package com.example.mrxu.mutils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CompoundButton;

/**
 * Created by MrXu on 2017/6/16.
 */

public class CustomSwitchCompat extends CompoundButton {

    private OnCheckedChangeListener mOnCheckedChangeListener;

    public CustomSwitchCompat(Context context) {
        super(context);
    }

    public CustomSwitchCompat(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomSwitchCompat(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setChecked(boolean checked) {
        setChecked(checked, true);
    }

    public void setChecked(boolean checked, boolean isCallBySelf) {
        if (isCallBySelf)
            setOnCheckedChangeListener(null);
        else
            setOnCheckedChangeListener(mOnCheckedChangeListener);
        super.setChecked(checked);
    }

    @Override
    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        mOnCheckedChangeListener = onCheckedChangeListener;
        super.setOnCheckedChangeListener(onCheckedChangeListener);
    }

    @Override
    public void toggle() {
        setChecked(!isChecked(), false);
    }
}  