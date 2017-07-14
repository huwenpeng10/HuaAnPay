package com.example.mrxu.assemble_xml;

import android.content.Context;

import com.example.mrxu.allinfo.UserInfo;
import com.example.mrxu.mutils.Mosaic_Base;
import com.example.mrxu.mutils.TagUtils;
import com.example.mrxu.mutils.XmlBuilder;

import java.math.BigDecimal;
import java.math.MathContext;

public class PhoneReplenish extends Mosaic_Base {

	public PhoneReplenish(Context context, double amount, String sn,
			String trackData, String code, String phone)
			throws Exception {
		super(context, 20011, new int[] { 1, 3, 4, 11, 22, 25, 46, 48, 62, 65,
				125, 128 });
		xml = XmlBuilder.inject(context, TxCode, xml, TagUtils.Field001(bits),
				"900000", TagUtils.fill0AtLeft(new BigDecimal(amount * 100,
						MathContext.DECIMAL32).intValue(), 12), TagUtils
						.Field011(context), "021", "00", sn, UserInfo.getPhoneNumber(), code + phone, trackData,
				UserInfo.getSessionCode(), TagUtils.MAC(
						TagUtils.TxCode(TxCode), TagUtils.TxDate(),
						TagUtils.TxTime(), TagUtils.Field011(context),
						UserInfo.getPhoneNumber()+""));
	}
}
