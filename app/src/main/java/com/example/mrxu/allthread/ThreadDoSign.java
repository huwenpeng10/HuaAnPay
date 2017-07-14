package com.example.mrxu.allthread;

import android.os.Handler;

import fncat.qpos.Controller.POSManage;
import fncat.qpos.Controller.StatusCode;
import fncat.qpos.Controller.util;

/**
 * ǩ��
 *
 * @author laogong
 */
public class ThreadDoSign extends Thread {

    private Handler mHandler;
    private POSManage pm;
    private String tck;
    private String pinkey;
    private String mackey;

    public ThreadDoSign(Handler mHandler, POSManage pm, String tck, String pinkey, String mackey) {
        this.mHandler = mHandler;
        this.pm = pm;
        this.tck = tck;
        this.pinkey = pinkey;
        this.mackey = mackey;
    }

    @Override
    public void run() {
        try {
            int r = pm.doSignIn(tck + pinkey + mackey);
            if (r == StatusCode.SUCCESS) {
                String[] data = new String[]{"签到成功，请查看终端！"};
                util.HandData(mHandler, data, 1);
            } else {
                util.HandData(mHandler, "签到测试失败~错误码：" + ErrorHint.errorMap.get(r) + "---" + Integer.toHexString(r), -1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            util.HandData(mHandler, "签到测试异常！", -1);
        }
    }


}
