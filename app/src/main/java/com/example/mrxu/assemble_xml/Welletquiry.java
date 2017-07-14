package com.example.mrxu.assemble_xml;

import android.content.Context;

import com.example.mrxu.allinfo.UserInfo;
import com.example.mrxu.mutils.Mosaic_Base;
import com.example.mrxu.mutils.TagUtils;
import com.example.mrxu.mutils.XmlBuilder;

/**
 * Created by MrXu on 2017/5/14.
 */

//T+N 列表
public class Welletquiry extends Mosaic_Base {


    public Welletquiry(Context context, int page)
            throws Exception {
        super(context, 20017, new int[] { 1, 3, 11, 18, 48,62, 125, 128 });

            xml = XmlBuilder.inject(context, TxCode, xml, TagUtils.Field001(bits),
                    "900000", TagUtils.Field011(context), 1, UserInfo
                            .getPhoneNumber(),page,UserInfo.getSessionCode()
                            .toString(), TagUtils.MAC(TagUtils.TxCode(TxCode),
                            TagUtils.TxDate(), TagUtils.TxTime(),
                            TagUtils.Field011(context),
                            String.valueOf(UserInfo.getPhoneNumber())));

    }
}