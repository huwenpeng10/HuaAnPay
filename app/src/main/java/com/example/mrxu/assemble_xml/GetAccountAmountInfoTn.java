package com.example.mrxu.assemble_xml;

import android.content.Context;

import com.example.mrxu.allinfo.UserInfo;
import com.example.mrxu.mutils.Mosaic_Base;
import com.example.mrxu.mutils.TagUtils;
import com.example.mrxu.mutils.XmlBuilder;

public class GetAccountAmountInfoTn extends Mosaic_Base {
	//清算
	public GetAccountAmountInfoTn(Context context,int page)
			throws Exception {
		super(context, 20017, new int[] { 1, 3, 11, 18, 48,62, 125, 128 });
		// 使用替代的方法填充数据
		xml = XmlBuilder.inject(context, TxCode, xml, TagUtils.Field001(bits),
				"900000", TagUtils.Field011(context), 2, UserInfo
						.getPhoneNumber(),page, UserInfo.getSessionCode()
						.toString(), TagUtils.MAC(TagUtils.TxCode(TxCode),
						TagUtils.TxDate(), TagUtils.TxTime(),
						TagUtils.Field011(context),
						UserInfo.getPhoneNumber()+""));
	}
}
