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


public class BluetoothAlreadyDialog extends Dialog {    @BindView(R.id.title)
TextView title;
    @BindView(R.id.message)
    TextView message;
    @BindView(R.id.no)
    Button no;
    @BindView(R.id.yes)
    Button yes;
    @BindView(R.id.well)
    Button well;

    private onNoOnclickListener noOnclickListener;//取消按钮被点击了的监听器  
    private onYesOnclickListener yesOnclickListener;//确定按钮被点击了的监听器 
    private onWellOnclickListener wellOnclickListener;//未保存按钮被点击了的监听器 
    private String yesStr;
    private String noStr;
    private String wellStr;

    private String msgStr;
    private String titleStr;



    public BluetoothAlreadyDialog(@NonNull Context context) {
        super(context,R.style.myDialog);

    }


    public void setNoOnclickListener(String str, onNoOnclickListener onNoOnclickListener) {
        if(str!=null){
            noStr=str;
        }
        this.noOnclickListener = onNoOnclickListener;
    };

    public void setWellOnclickListener(String str, onWellOnclickListener onWellOnclickListener) {

        if(str!=null){
            wellStr=str;
        }
        this.wellOnclickListener = onWellOnclickListener;
    };

    public void setYesOnclickListener(String str, onYesOnclickListener onYesOnclickListener) {

        if(str!=null){
            yesStr=str;
        }
        this.yesOnclickListener = onYesOnclickListener;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_already_dialog_layout);
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
        if (well!= null){
            well.setText(wellStr);
        }
        if (noStr!= null){
            no.setText(noStr);
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
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (noOnclickListener != null) {
                    noOnclickListener.onNoClick();
                }
            }
        });
        well.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wellOnclickListener != null) {
                    wellOnclickListener.onWellClick();
                }
            }
        });
    }




    public interface onYesOnclickListener {
        public void onYesClick();
    }
    public interface onNoOnclickListener {
        public void onNoClick();
    }
    public interface onWellOnclickListener {
        public void onWellClick();
    }
}


