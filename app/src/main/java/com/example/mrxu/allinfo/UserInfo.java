package com.example.mrxu.allinfo;

import android.text.TextUtils;

import java.math.BigInteger;

/**
 * Created by MrXu on 2017/5/16.
 */

public class UserInfo {


    //用户名
    private static String userName="";
    //用户ID(手机号)
    public static BigInteger phoneNumber;
    //用户Code
    public static  BigInteger sessionCode;
    //用户银行卡
    private static BigInteger bindingCardNo;
    //业务开通状态
    public static int state = 0;
    //银行名称
    public static String bankName;
    //是否设置了支付密码  01  有     00 无
    public static String payPasswordState = "00";


    public enum AuthState {
        UNAUTHERIZED, AUTHERIZED, AUTHERING, AUTHENTICATION_FAIL
    }

    public static void setAuthState(AuthState authState) {
        UserInfo.authState = authState;
    }

    //认证状态枚举
    private static AuthState authState = AuthState.UNAUTHERIZED;
    //认证成功 / 认证失败
    public static String authTips;

    public static int authTipsNumber;






/**
 *
 * get数据
 * */

    public static String getUserName() {
    return userName;
}

    public static BigInteger getPhoneNumber() {
        return phoneNumber;
    }

    public static BigInteger getSessionCode() {
        return sessionCode;
    }

    public static String getAuthTips() {
        return authTips;
    }

    public static BigInteger getBindingCardNo() {
        return bindingCardNo;
    }

    public static void setState(int state) {
        UserInfo.state = state;
    }
    public static String getBankName() {
        return bankName;
    }


    /**
     *
     * set数据
     * */
    public static void setUserName(String userName) {
        UserInfo.userName = userName;
    }

    public static void setPhoneNumber(String phoneNumber) {
        if (TextUtils.isEmpty(phoneNumber))
            UserInfo.phoneNumber = null;
        else
            UserInfo.phoneNumber = new BigInteger(phoneNumber);
    }

    public static void setSessionCode(String sessionCode) {
        if (TextUtils.isEmpty(sessionCode))
            UserInfo.sessionCode = null;
        else
            UserInfo.sessionCode = new BigInteger(sessionCode);
    }

    public static void setAuthTips(String authTips) {
        UserInfo.authTips = authTips;
    }

    public static void setBindingCardNo(String bindingCardNo) {

        UserInfo.bindingCardNo = new BigInteger(bindingCardNo);
    }

    public static int getAuthTipsNumber() {
        return authTipsNumber;
    }

    public static void setAuthTipsNumber(int authTipsNumber) {
        UserInfo.authTipsNumber = authTipsNumber;
    }

    public static int getState() {
        return state;
    }

    public static void setBankName(String bankName) {
        UserInfo.bankName = bankName;
    }

    public static String getPayPasswordState() {
        return payPasswordState;
    }

    public static void setPayPasswordState(String payPasswordState) {
        UserInfo.payPasswordState = payPasswordState;
    }
}
