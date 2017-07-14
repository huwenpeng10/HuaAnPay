package com.example.mrxu.assemble_xml;

import android.content.Context;

import com.example.mrxu.allinfo.UserInfo;
import com.example.mrxu.mutils.Mosaic_Base;
import com.example.mrxu.mutils.TagUtils;
import com.example.mrxu.mutils.XmlBuilder;

public class PhoneReplenishType extends Mosaic_Base {

	public PhoneReplenishType(Context context)
			throws Exception {
		super(context, 20010, new int[] { 1, 3, 11, 48, 125, 128 });
		xml = XmlBuilder.inject(context, TxCode, xml, TagUtils.Field001(bits),
				"900000", TagUtils.Field011(context), UserInfo
						.getPhoneNumber(), UserInfo.getSessionCode()
						.toString(), TagUtils.MAC(TagUtils.TxCode(TxCode),
						TagUtils.TxDate(), TagUtils.TxTime(),
						TagUtils.Field011(context),
						UserInfo.getPhoneNumber()+""));
	}
}
