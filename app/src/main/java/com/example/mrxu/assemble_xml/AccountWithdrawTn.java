package com.example.mrxu.assemble_xml;

import android.content.Context;

import com.example.mrxu.allinfo.UserInfo;
import com.example.mrxu.mutils.Mosaic_Base;
import com.example.mrxu.mutils.TagUtils;
import com.example.mrxu.mutils.XmlBuilder;

public class AccountWithdrawTn extends Mosaic_Base {
	public AccountWithdrawTn(Context context,String money,String password)
			throws Exception {
		super(context, 20019, new int[] { 1, 3, 4, 11, 52, 48,125,128 });
		xml = XmlBuilder.inject(context, TxCode, xml, TagUtils.Field001(bits),
				"900000",money, TagUtils.Field011(context), UserInfo
						.getPhoneNumber(),TagUtils.md5Encode(password),UserInfo.getSessionCode()
						.toString(), TagUtils.MAC(TagUtils.TxCode(TxCode),
						TagUtils.TxDate(), TagUtils.TxTime(),
						TagUtils.Field011(context),
						UserInfo.getPhoneNumber()+""));
	}
}
