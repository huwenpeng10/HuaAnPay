package com.example.mrxu.allinfo;

/**
 * Created by MrXu on 2017/5/12.
 */

public class TradeInfo {


    //记录选择的Pos
    public static  String POS_NAME = "蓝牙Qpos";
    //输入金额
    public static String inputMoney="0";
    //虚拟账户余额
    public static String userMoney="0";
    //pos机设备号
    public static String deviceSn;
    //手机充值码
    public static String replenishingCode;
    //需要充值的手机号码
    public static String replenishingPhoneNumber;
    //pagetates
    public static PageState pagetates = PageState.SHUAKAZHIFU;

    //刷卡页面传递磁道数据key
    public static String CARDNUM = "cardNum";
    public static String TRACKCOUNT = "trackCount";
    public static String DATA55 = "data55";
    public static String EXPIRYDATE= "expiryDate";
    public static String ICSERIALNUM= "icSerialNum";
    public static String ENPINKEY= "enpinkey";
    //卡类型
    public static String cardFlag= "IC";

    //刷卡有密码
    public static String SWIPINH = "021";
    //插卡有密码
    public static String ICPINH = "051";

    //    页面进入枚举
    public enum PageState {
    SHUAKAZHIFU, QIANDAIBAO, ZHUANGZHAN, YUECHAXUN, SHOUJICHONGZHI,RECORD
    }

    public static void setPageState(PageState authState) {
        TradeInfo.pagetates = authState;
    }


    public static String getPosName() {
        return POS_NAME;
    }

    public static void setPosName(String posName) {
        POS_NAME = posName;
    }

    public static void setDeviceSn(String str) {
        deviceSn = str;
    }

    public static void setUserMoney(String userMoney) {
        TradeInfo.userMoney = userMoney;
    }

    public static String getDeviceSn() {
        return deviceSn;
    }

    public static String getInputMoney() {
        return inputMoney;
    }

    public static void setInputMoney(String inputMoney) {
        TradeInfo.inputMoney = inputMoney;
    }

    public static String getCardFlag() {
        return cardFlag;
    }

    public static void setCardFlag(String cardFlag) {
        TradeInfo.cardFlag = cardFlag;
    }

    public static PageState getPagetates() {
        return pagetates;
    }

    public static String getUserMoney() {
        return userMoney;
    }

    public static String getReplenishingCode() {
        return replenishingCode;
    }

    public static void setReplenishingCode(String replenishingCode) {
        TradeInfo.replenishingCode = replenishingCode;
    }

    public static String getReplenishingPhoneNumber() {
        return replenishingPhoneNumber;
    }

    public static void setReplenishingPhoneNumber(String replenishingPhoneNumber) {
        TradeInfo.replenishingPhoneNumber = replenishingPhoneNumber;
    }
}
