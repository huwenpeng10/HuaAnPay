package com.example.mrxu.assemble_xml;

import android.content.Context;

import com.example.mrxu.allinfo.UserInfo;
import com.example.mrxu.mutils.Mosaic_Base;
import com.example.mrxu.mutils.TagUtils;
import com.example.mrxu.mutils.XmlBuilder;

public class Authentication extends Mosaic_Base {
	public Authentication(Context context, String userName, String id,
			String cardNo, String account, String bankNo, String[] photoPath)
			throws Exception {
		super(context, 6, new int[] { 1, 3, 11, 48, 60, 61, 62, 63, 125, 128 },
				XmlBuilder.newAttrs(new int[] { 61, 62, 63 },
						XmlBuilder.ATTRIBUTE.PHOTO));
		xml = XmlBuilder.inject(context, TxCode, xml, TagUtils.Field001(bits),
				TagUtils.Field003(), TagUtils.Field011(context), UserInfo
						.getPhoneNumber(), TagUtils.AuthenticationInformation(
						userName, id, cardNo, account, bankNo), TagUtils
						.newPhoto(context, photoPath[0]), TagUtils.newPhoto(
						context, photoPath[1]), TagUtils.newPhoto(context,
						photoPath[2]),
				UserInfo.getSessionCode().toString(), TagUtils.MAC(
						TagUtils.TxCode(TxCode), TagUtils.TxDate(),
						TagUtils.TxTime(), TagUtils.Field011(context),
						UserInfo.getPhoneNumber()+""));
	}
}
