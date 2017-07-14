package com.example.mrxu.assemble_xml;

import android.content.Context;

import com.example.mrxu.mutils.Mosaic_Base;
import com.example.mrxu.mutils.TagUtils;
import com.example.mrxu.mutils.XmlBuilder;

/**
 * 获取验证码
 * 
 * @author MrXu
 * 
 */
public class GetValidateCode extends Mosaic_Base {
	public static final int TYPE_REGISTER = 100000;
	public static final int TYPE_LOGINPASSWORD = 200000;
	public static final int TYPE_PAYPASSWORD = 300000;
	public static final int TYPE_MERRE = 2000;
	public static final int TYPE_USERRE = 1000;

	public GetValidateCode(Context context, int type, String phoneNumber)
			throws Exception {
		super(context, 5, new int[] { 1, 3, 11, 48, 125, 128 });

		xml = XmlBuilder.inject(context, TxCode, xml, TagUtils.Field001(bits),
				String.valueOf(type), TagUtils.Field011(context), phoneNumber,
				"", TagUtils.MAC(TagUtils.TxCode(TxCode), TagUtils.TxDate(),
						TagUtils.TxTime(), TagUtils.Field011(context),
						phoneNumber));
	}

}
