package com.example.mrxu.mutils;

import android.content.Context;
import android.content.SharedPreferences;

//判断是否第一次加载控制类

public class PrefManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    int PRIVATE_MODE = 0;

    //SharedPreferences 文件名
    private static final String PREF_NAME = "intro_slider";

    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    private static final String IS_FIRST_POS = "isFirstPos";
    private static final String POS_NAME = "posName";
    private static final String Message_NAME = "messageName";
    private static final String USER_PASSWORD = "user_password";
    private static final String USER_PHONEUMENBER= "user_phonenumber";

    public PrefManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME,PRIVATE_MODE);
        editor = pref.edit();
    }

//    判断第一次加载引导页
    public void setFirstTimeLaunch(boolean isFirstTime){
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch(){
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }


//      判断是否第一次选择刷卡器Dialog
    public void setFirstPos(boolean isFirstTime,String posName){
        editor.putBoolean(IS_FIRST_POS, isFirstTime);
        editor.putString(POS_NAME, posName);
        editor.commit();
    }


    //      判断是否第一次选择刷卡器Dialog
    public boolean getFirstPos(){
        return pref.getBoolean(IS_FIRST_POS, true);
    }


    public String getPosName(){
        return pref.getString(POS_NAME,"蓝牙Qpos");
    }

//消息提醒开关
    public void setMessageSwitch(String posName){
        editor.putString(Message_NAME, posName);
        editor.commit();
    }

    public String getMessageSwitch (){
        return pref.getString(Message_NAME,"");
    }

    //  记住密码
    public void setUserPassword(String password){
        editor.putString(USER_PASSWORD, password);
        editor.commit();
    }

    public String getUserPassword(){
        return pref.getString(USER_PASSWORD,"");
    }

    public void setUserPhoneumenber(String phoneumenber){
        editor.putString(USER_PHONEUMENBER, phoneumenber);
        editor.commit();
    }

    public String getUserPhoneumenber(){
        return pref.getString(USER_PHONEUMENBER,"");
    }

}
