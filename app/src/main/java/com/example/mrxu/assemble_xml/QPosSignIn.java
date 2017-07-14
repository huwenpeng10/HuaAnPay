package com.example.mrxu.assemble_xml;

import android.content.Context;

import com.example.mrxu.allinfo.UserInfo;
import com.example.mrxu.mutils.Mosaic_Base;
import com.example.mrxu.mutils.TagUtils;
import com.example.mrxu.mutils.XmlBuilder;

public class QPosSignIn extends Mosaic_Base {
	public static final String POS_TYPE_OTHER = "02";

	public QPosSignIn(Context context, String posSN)
			throws Exception {

		super(context, 7, new int[] { 1, 3, 11, 46, 48, 61, 125, 128 });
		xml = XmlBuilder.inject(context, TxCode, xml, TagUtils.Field001(bits),
				TagUtils.Field003(), TagUtils.Field011(context), posSN,
				UserInfo.getPhoneNumber(), POS_TYPE_OTHER, UserInfo
						.getSessionCode().toString(), TagUtils.MAC(
						TagUtils.TxCode(TxCode), TagUtils.TxDate(),
						TagUtils.TxTime(), TagUtils.Field011(context),
						UserInfo.getPhoneNumber()+""));
	}
}
