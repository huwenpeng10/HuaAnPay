package com.example.mrxu.allthread;

import android.os.Handler;

import fncat.qpos.Controller.POSManage;
import fncat.qpos.Controller.StatusCode;
import fncat.qpos.Controller.util;

/**
 * Created by MrXu on 2017/5/26.
 */

public class ThreadGiveup extends Thread {

    private Handler mHandler;
    private POSManage pm;

    public ThreadGiveup(Handler mHandler, POSManage pm) {
        this.mHandler = mHandler;
        this.pm = pm;
    }

    @Override
    public void run() {
        int r = pm.giveup();
        if (r == StatusCode.SUCCESS) {
            if (mHandler != null)
                util.HandData(mHandler, "刷卡取消，请等待终端返回数据！", -1);
        } else {
            if (mHandler != null)
                util.HandData(mHandler, ErrorHint.errorMap.get(r) + "~错误码：" + Integer.toHexString(r), -1);
        }
    }
}
