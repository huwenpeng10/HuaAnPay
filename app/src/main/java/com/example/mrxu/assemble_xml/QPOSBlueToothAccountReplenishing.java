package com.example.mrxu.assemble_xml;

import android.content.Context;

import com.example.mrxu.allinfo.TradeInfo;
import com.example.mrxu.allinfo.UserInfo;
import com.example.mrxu.mutils.Mosaic_Base;
import com.example.mrxu.mutils.TagUtils;
import com.example.mrxu.mutils.XmlBuilder;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * Created by MrXu on 2017/5/31.
 */

public class QPOSBlueToothAccountReplenishing extends Mosaic_Base{


    public QPOSBlueToothAccountReplenishing(Context context, double amount, String sn,
                                            String trackData) throws Exception {
        //新添加的62域,数据获得在选择通道界面
        super(context, 10002, new int[] { 1, 3, 4, 11, 22, 25, 46, 48, 62, 65,125,
                128 });
            xml = XmlBuilder.inject(context, TxCode, xml, TagUtils.Field001(bits),
                    "000000", TagUtils.fill0AtLeft(new BigDecimal(amount * 100,
                            MathContext.DECIMAL32).intValue(), 12), TagUtils
                            .Field011(context), TradeInfo.SWIPINH, "00", sn, UserInfo
                            .getPhoneNumber(), "0", trackData,UserInfo
                            .getSessionCode().toString(), TagUtils.MAC(
                            TagUtils.TxCode(TxCode), TagUtils.TxDate(),
                            TagUtils.TxTime(), TagUtils.Field011(context),
                            UserInfo.getPhoneNumber()+""));

    }


}
