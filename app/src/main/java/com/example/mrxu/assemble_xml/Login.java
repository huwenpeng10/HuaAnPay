package com.example.mrxu.assemble_xml;

import android.content.Context;

import com.example.mrxu.mutils.Mosaic_Base;
import com.example.mrxu.mutils.TagUtils;
import com.example.mrxu.mutils.XmlBuilder;

/**
 * Created by MrXu on 2017/5/14.
 */

public class Login extends Mosaic_Base {
    public Login(Context context, String phoneNumber, String password)
            throws Exception {
        super(context, 2, new int[] { 1, 3, 11, 48, 52, 128 });
        // 使用替代的方法填充数据
        xml = XmlBuilder.inject(context, TxCode, xml, TagUtils.Field001(bits),
                TagUtils.Field003(), TagUtils.Field011(context), phoneNumber,
                TagUtils.md5Encode(password), TagUtils.MAC(
                        TagUtils.TxCode(TxCode), TagUtils.TxDate(),
                        TagUtils.TxTime(), TagUtils.Field011(context),
                        phoneNumber));
    }
}
