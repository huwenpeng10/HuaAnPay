package com.example.mrxu.assemble_xml;

import android.content.Context;
import com.example.mrxu.allinfo.UserInfo;
import com.example.mrxu.mutils.Mosaic_Base;
import com.example.mrxu.mutils.TagUtils;
import com.example.mrxu.mutils.XmlBuilder;


public class UrgentWithdrawals extends Mosaic_Base {

	public UrgentWithdrawals(Context context)
			throws Exception {
		super(context, 10007, new int[] { 1, 3, 11, 48, 62, 125, 128 });
		// TODO Auto-generated constructor stub
		xml = XmlBuilder.inject(context, TxCode, xml, TagUtils.Field001(bits),
				"000000", TagUtils.Field011(context), UserInfo
						.getPhoneNumber(), "0", UserInfo.getSessionCode(),
				TagUtils.MAC(TagUtils.TxCode(TxCode), TagUtils.TxDate(),
						TagUtils.TxTime(), TagUtils.Field011(context),
						UserInfo.getPhoneNumber()+""));
	}

}
