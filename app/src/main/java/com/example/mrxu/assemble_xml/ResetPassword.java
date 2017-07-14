package com.example.mrxu.assemble_xml;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;

import com.example.mrxu.mutils.Mosaic_Base;
import com.example.mrxu.mutils.TagUtils;
import com.example.mrxu.mutils.XmlBuilder;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class ResetPassword extends Mosaic_Base {
	public static final int FORGET_PASSWORD = 100000;
	public static final int MODIFY_PASSWORD = 200000;

	/**
	 * @param context
	 * @param phoneNumber
	 * @param type
	 *            重置密码和修改密码均使用此接口，重置密码{@code type=}{@link #FORGET_PASSWORD}，
	 *            修改密码{@code type=}{@link #MODIFY_PASSWORD}
	 * @param param1
	 *            {@link #FORGET_PASSWORD}时为验证码，{@link #MODIFY_PASSWORD}时为旧密码
	 * @param param2
	 *            新密码
	 * @throws IllegalArgumentException
	 * @throws IllegalStateException
	 * @throws IOException
	 * @throws NameNotFoundException
	 * @throws NoSuchAlgorithmException
	 */
	public ResetPassword(Context context, String phoneNumber, int type,
			String key, String password) throws Exception {
		super(context, 8, new int[] { 1, 3, 11, 48, 52, 128 });
		switch (type) {
		case FORGET_PASSWORD:
			xml = XmlBuilder.inject(context, TxCode, xml, TagUtils
					.Field001(bits), String.valueOf(FORGET_PASSWORD), TagUtils
					.Field011(context), phoneNumber, TagUtils.ResetPassword(
					key, password), TagUtils.MAC(TagUtils.TxCode(TxCode),
					TagUtils.TxDate(), TagUtils.TxTime(),
					TagUtils.Field011(context), phoneNumber));
			break;
		case MODIFY_PASSWORD:
			xml = XmlBuilder.inject(context, TxCode, xml, TagUtils
					.Field001(bits), String.valueOf(MODIFY_PASSWORD), TagUtils
					.Field011(context), phoneNumber, TagUtils.ModifyPassword(
					key, password), TagUtils.MAC(TagUtils.TxCode(TxCode),
					TagUtils.TxDate(), TagUtils.TxTime(),
					TagUtils.Field011(context), phoneNumber));
			break;
		}

	}
}
