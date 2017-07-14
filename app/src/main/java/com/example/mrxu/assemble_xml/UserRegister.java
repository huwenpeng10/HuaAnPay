package com.example.mrxu.assemble_xml;

import android.content.Context;

import com.example.mrxu.mutils.Mosaic_Base;
import com.example.mrxu.mutils.TagUtils;
import com.example.mrxu.mutils.XmlBuilder;

/**
 * <li>用户注册报文组装</li>
 *
 * @author Angleline
 *
 */
public class UserRegister extends Mosaic_Base {

	public UserRegister(Context context, String phoneNumber, String password,
			String vCode) throws Exception {
		super(context, 1, new int[] { 1, 3, 11, 48, 52, 62, 128 });
		xml = XmlBuilder.inject(context, TxCode, xml, TagUtils.Field001(bits),
				TagUtils.Field003(), TagUtils.Field011(context), phoneNumber,
				TagUtils.md5Encode(password), vCode, TagUtils.MAC(
						TagUtils.TxCode(TxCode), TagUtils.TxDate(),
						TagUtils.TxTime(), TagUtils.Field011(context),
						phoneNumber));
	}
}
