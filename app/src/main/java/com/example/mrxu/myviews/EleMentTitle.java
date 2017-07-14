package com.example.mrxu.myviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.merxu.mypractice.R;

import butterknife.BindView;

/**
 * Created by MrXu on 2017/5/19.
 */

public class EleMentTitle extends RelativeLayout {


    @BindView(R.id.left_Imagemage)
    ImageView leftImagemage;
    @BindView(R.id.title_bar_text)
    TextView titleBarText;
    @BindView(R.id.right_Imagemage)
    ImageView rightImagemage;


    public EleMentTitle(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflate = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflate.inflate(R.layout.title_bar, this);

    }

    public void setTitleBarText(String text){
        if(text!=null){
            titleBarText.setText(text);
        }
    };

    public void setLeftImagemage(int Imagemage,OnClickListener onClickListener) {
        if(Imagemage!=-1){
            leftImagemage.setImageDrawable(getResources().getDrawable(R.mipmap.new_left_icon));
            leftImagemage.setOnClickListener(onClickListener);
        }else{
            leftImagemage.setVisibility(View.GONE);
        }
    }


    public void setRightImagemage(int Imagemage,OnClickListener onClickListener) {
        if(Imagemage!=-1){
            rightImagemage.setImageDrawable(getResources().getDrawable(R.mipmap.new_left_icon));
            rightImagemage.setOnClickListener(onClickListener);
        }else{
            rightImagemage.setVisibility(View.GONE);
        }
    }
}
