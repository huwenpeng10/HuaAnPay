package com.example.mrxu.assemble_xml;

import android.content.Context;
import android.graphics.Bitmap;

import com.example.mrxu.allinfo.UserInfo;
import com.example.mrxu.mutils.Mosaic_Base;
import com.example.mrxu.mutils.TagUtils;
import com.example.mrxu.mutils.XmlBuilder;


public class SignatureCommit extends Mosaic_Base {

	public SignatureCommit(Context context, Bitmap photo, String info)
			throws Exception {
		super(context, 4, new int[] { 1, 3, 11, 48, 61, 62, 125, 128 },
				XmlBuilder.newAttrs(new int[] { 61 },
						XmlBuilder.ATTRIBUTE.PHOTO));
		xml = XmlBuilder.inject(context, TxCode, xml, TagUtils.Field001(bits),
				TagUtils.Field003(), TagUtils.Field011(context), UserInfo
						.getPhoneNumber(), TagUtils.newPhoto(context, photo),
				info, UserInfo.getSessionCode(), TagUtils.MAC(
						TagUtils.TxCode(TxCode), TagUtils.TxDate(),
						TagUtils.TxTime(), TagUtils.Field011(context),
						UserInfo.getPhoneNumber()+""));
	}
}
