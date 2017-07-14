package com.example.mrxu.allthread;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import fncat.qpos.Controller.POSManage;
import fncat.qpos.Controller.StatusCode;
import fncat.qpos.Controller.util;

/**
 * Created by MrXu on 2017/5/26.
 */

public class ThreadSwiCard extends Thread {

    private Handler mHandler;
    private POSManage pm;
    private String money;
    private int isCard;
    private String tosdata;

    public ThreadSwiCard(Handler mHandler, POSManage pm, String money) {
        this.mHandler = mHandler;
        this.pm = pm;
        this.money = money;
    }

    @Override
    public void run() {
        try {
//			int r = pm.doTradeUnit(money, position, getRadomString(), "1990050000000001000000010427011209323031303130303030313131", 180);
            int amount = (int) (Double.parseDouble(money)*100);
            int r = pm.doTrade(Integer.toString(amount));
            if (r == StatusCode.SUCCESS || r == StatusCode.SWIPE_JS2_CARD) {
                String encTrack2 = null;
                String encTrack3 = null;
                String cardNum = null;
                String icSerialNum = null;
                String icData55 = null;
                String expiryDate = null;

                String temp55=pm.getIcCardData55();
                String[] encTrack=pm.getCardEncTrackData();
                String pin = pm.getPinCipher();

//					encTrack 不为null是磁条卡，是null为IC卡
                if(encTrack!=null){
                    isCard=1;
                    encTrack2=encTrack[0];
                    encTrack3=encTrack[1];
                    Log.d("encTrack2", "run: "+encTrack2);
                    cardNum=encTrack2.substring(0, encTrack2.indexOf("D"));
                    tosdata="\n二磁:"+encTrack2+"\n三磁:"+encTrack3+"\n卡号:"+cardNum+"\npin:"+pin;

                }else{
                    isCard=0;
                    encTrack2=pm.getIcCardEncTrack2Data();
                    cardNum=pm.getIcCardNumber();
                    icSerialNum=pm.getIcCardSerialNum();
                    expiryDate=pm.getExpiryDate().substring(0,4);
                    icData55= TextUtils.isEmpty(temp55)?temp55:temp55.substring(0, temp55.length()-8);
                     tosdata="\n二磁:"+encTrack2+"\n三磁:"+encTrack3+"\n55域:"+icData55+"\n卡号:"+cardNum+"\nIC卡序列号:"+icSerialNum+"\n有效时间:"+expiryDate+"\npin:"+pin;

                }



                String data[]=new String[]{tosdata,isCard+"",icData55,cardNum,expiryDate,icSerialNum,pin,encTrack2,encTrack3};

                util.HandData(mHandler,data, 2);
            } else {
                if(isCard==1){
                    util.HandData(mHandler, "刷卡测试失败~错误码：" +ErrorHint.errorMap.get(r) +"------"+ Integer.toHexString(r), -1);
                }else{
                    util.HandData(mHandler, "IC测试失败~错误码：" +ErrorHint.errorMap.get(r) +"------"+ Integer.toHexString(r), -1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if(isCard==1){
                util.HandData(mHandler, "刷卡测试发送异常！", -1);
            }else{
                util.HandData(mHandler, "IC测试发送异常！", -1);
            }

        }
    }
}
