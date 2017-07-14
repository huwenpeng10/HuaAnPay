package com.example.mrxu.mutils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.example.mrxu.base.BaseActivity;

/**
 * Created by MrXu on 2017/5/15.
 */

public class NetBroadcastReceiver extends BroadcastReceiver{

    public NetEvevt evevt = BaseActivity.evevt;



    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)){

            int netWorkState = NetUtil.getNetWorkState(context);
            if(evevt!=null){
                evevt.onNetChange(netWorkState);
            }

        }

    }

    public interface NetEvevt {

        public void onNetChange(int netMobiles);
    }
}
