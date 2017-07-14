package com.example.mrxu.allthread;

import android.os.Handler;

import fncat.qpos.Controller.POSManage;
import fncat.qpos.Controller.StatusCode;
import fncat.qpos.Controller.util;

/**
 * Created by MrXu on 2017/5/24.
 */

public class ThreadGetTerminalIDTest extends Thread{
    private Handler handler;
    private POSManage manager;

    public ThreadGetTerminalIDTest(Handler handler, POSManage manager) {
        this.handler = handler;
        this.manager = manager;

    }

    @Override
    public void run() {
        try {
            //尝试去和读卡器通信，获取terminalID和PSAMID，易势App目前使用的是PSAMid
            int r = manager.doGetTerminalID();
            if (r == StatusCode.SUCCESS) {
                String TerminalID = manager.getTerminalIDTid();
                String PSAMid = manager.getTerminalIDPid();

                String [] data=new String []{"通讯测试","通讯测试<font color='green'>成功！</font>\n终端号：" + TerminalID + "\nPSAMID：" + PSAMid ,TerminalID,PSAMid};

                util.HandData(handler,data , 0);
            } else {
                util.HandData(handler,"通讯测试<font color='red'>失败！</font>\n失败原因：" + ErrorHint.errorMap.get(r) + "~错误码：" + Integer.toHexString(r), -1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            util.HandData(handler, "通讯测试<font color='red'>失败！</font>\n失败原因：" + "发送异常！", -1);
        }finally{
            if(manager.isConnected()){
                manager.Close();
            }
        }
    }
}
