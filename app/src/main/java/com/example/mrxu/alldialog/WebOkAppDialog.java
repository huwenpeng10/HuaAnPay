package com.example.mrxu.alldialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.merxu.mypractice.R;
import com.zhy.autolayout.AutoLinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by MrXu on 2017/5/13.
 */


public class WebOkAppDialog extends Dialog {

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.web_ok_dialog_layout)
    AutoLinearLayout webOkDialogLayout;
    @BindView(R.id.yes)
    Button yes;

    private onYesOnclickListener yesOnclickListener;//确定按钮被点击了的监听器 
    private String yesStr;

    private View layoutView;
    private String titleStr;



    public WebOkAppDialog(@NonNull Context context) {
        super(context,R.style.myDialog);

    }




    public void setYesOnclickListener(String str, onYesOnclickListener onYesOnclickListener) {

        if(str!=null){
            yesStr=str;
        }
        this.yesOnclickListener = onYesOnclickListener;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_ok_dialog_layout);
        ButterKnife.bind(this);

        initData();
        initEvent();
    }

    public void setLayoutView(View view){
        layoutView = view;
    }

    public void setTitleStr(String str){
            titleStr = str;
    }



    public void initData(){

        if (layoutView!= null){
            webOkDialogLayout.addView(layoutView);
        }
        if (titleStr!= null){
            title.setText(titleStr);
        }
        if (yesStr!= null){
            yes.setText(yesStr);
        }

    }



        private void initEvent(){
            yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (yesOnclickListener != null) {
                        yesOnclickListener.onYesClick();
                    }
                }
            });
        }




    public interface onYesOnclickListener {
        public void onYesClick();
    }
}


