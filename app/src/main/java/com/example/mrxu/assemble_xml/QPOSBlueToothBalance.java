package com.example.mrxu.assemble_xml;

import android.content.Context;

import com.example.mrxu.allinfo.TradeInfo;
import com.example.mrxu.allinfo.UserInfo;
import com.example.mrxu.mutils.Mosaic_Base;
import com.example.mrxu.mutils.TagUtils;
import com.example.mrxu.mutils.XmlBuilder;


public class QPOSBlueToothBalance extends Mosaic_Base {

	public QPOSBlueToothBalance(Context context, String sn, String trackData)
			throws Exception {
		super(context, 10000, new int[] { 1, 3, 11, 22, 25, 46, 48, 65,125,
				128 });

			xml = XmlBuilder.inject(context, TxCode, xml, TagUtils.Field001(bits),
					"310000", TagUtils.Field011(context), TradeInfo.SWIPINH, "00", sn,
					UserInfo.getPhoneNumber().toString(), trackData,
					UserInfo.getSessionCode(), TagUtils.MAC(
							TagUtils.TxCode(TxCode), TagUtils.TxDate(),
							TagUtils.TxTime(), TagUtils.Field011(context),
							UserInfo.getPhoneNumber()+""));

	}
	

}
