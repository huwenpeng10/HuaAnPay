package com.example.mrxu.assemble_xml;

import android.content.Context;

import com.example.mrxu.allinfo.UserInfo;
import com.example.mrxu.mutils.Mosaic_Base;
import com.example.mrxu.mutils.TagUtils;
import com.example.mrxu.mutils.XmlBuilder;

public class CashWithdrawal extends Mosaic_Base {
	public CashWithdrawal(Context context,String date ,String password)
			throws Exception {
		super(context, 20018, new int[] { 1, 3, 11, 13, 48, 52, 125, 128 });
		xml = XmlBuilder.inject(context, TxCode, xml, TagUtils.Field001(bits),
				"900000", TagUtils.Field011(context),date, UserInfo
						.getPhoneNumber(),TagUtils.md5Encode(password), UserInfo.getSessionCode()
						.toString(), TagUtils.MAC(TagUtils.TxCode(TxCode),
						TagUtils.TxDate(), TagUtils.TxTime(),
						TagUtils.Field011(context),
						UserInfo.getPhoneNumber()+""));
	}
}
