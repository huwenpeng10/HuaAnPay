package com.example.mrxu.assemble_xml;

import android.content.Context;
import android.util.Log;

import com.example.mrxu.allinfo.UserInfo;
import com.example.mrxu.mutils.Mosaic_Base;
import com.example.mrxu.mutils.TagUtils;
import com.example.mrxu.mutils.XmlBuilder;

public class WithdrawalsRecord extends Mosaic_Base {

	public WithdrawalsRecord(Context context,String number,int page)
			throws Exception {
		super(context, 20017, new int[] { 1, 3, 11, 18, 48,62, 125, 128 });
		Log.d("page", page+"===========");
		xml = XmlBuilder.inject(context, TxCode, xml, TagUtils.Field001(bits),
				"900000", TagUtils.Field011(context), number, UserInfo
						.getPhoneNumber(),page, UserInfo.getSessionCode()
						.toString(), TagUtils.MAC(TagUtils.TxCode(TxCode),
						TagUtils.TxDate(), TagUtils.TxTime(),
						TagUtils.Field011(context),
						UserInfo.getPhoneNumber()+""));
	}
}
