package com.example.mrxu.alldialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.merxu.mypractice.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by MrXu on 2017/5/13.
 */


public class OkAppDialog extends Dialog {

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.message)
    TextView message;
    @BindView(R.id.yes)
    Button yes;

    private onYesOnclickListener yesOnclickListener;//确定按钮被点击了的监听器 
    private String yesStr;

    private String msgStr;
    private String titleStr;



    public OkAppDialog(@NonNull Context context) {
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
        setContentView(R.layout.ok_dialog_layout);
        ButterKnife.bind(this);

        initData();
        initEvent();
    }

    public void setmsgStr(String str){
            msgStr = str;
    }

    public void setTitleStr(String str){
            titleStr = str;
    }



    public void initData(){

        if (msgStr!= null){
            message.setText(msgStr);
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


