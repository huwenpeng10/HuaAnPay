package com.example.mrxu.assemble_xml;

import android.content.Context;

import com.example.mrxu.allinfo.TradeInfo;
import com.example.mrxu.allinfo.UserInfo;
import com.example.mrxu.mutils.Mosaic_Base;
import com.example.mrxu.mutils.TagUtils;
import com.example.mrxu.mutils.XmlBuilder;

import java.math.BigDecimal;
import java.math.MathContext;

public class QPOSBlueToothAccountReplenishing_ck extends Mosaic_Base {


    public QPOSBlueToothAccountReplenishing_ck(Context context, double amount, String sn,
                                               String trackData,String expiryDate,String icSerialNum,String data55) throws Exception {
        super(context, 10002, new int[]{1, 3, 4, 11, 14, 22, 23, 25, 46, 48,
                55, 62, 65, 125, 128});

            xml = XmlBuilder.inject(context, TxCode, xml, TagUtils.Field001(bits),
                    "000000", TagUtils.fill0AtLeft(new BigDecimal(amount * 100,
                            MathContext.DECIMAL32).intValue(), 12), TagUtils
                            .Field011(context),
                    expiryDate, TradeInfo.ICPINH,
                    icSerialNum, "00", sn, UserInfo
                            .getPhoneNumber(), data55, "0",
                    trackData, UserInfo.getSessionCode().toString(),
                    TagUtils.MAC(TagUtils.TxCode(TxCode), TagUtils.TxDate(),
                            TagUtils.TxTime(), TagUtils.Field011(context),
                            UserInfo.getPhoneNumber()+""));

    }

}
