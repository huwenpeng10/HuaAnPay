package com.example.mrxu.assemble_xml;

import android.content.Context;

import com.example.mrxu.allinfo.UserInfo;
import com.example.mrxu.mutils.Mosaic_Base;
import com.example.mrxu.mutils.TagUtils;
import com.example.mrxu.mutils.XmlBuilder;

public class SetPayPassword extends Mosaic_Base {

	public static int SETPAYPASS = 111;
	public static int RESETPAYPASS = 223;

	public static int  SETPASSMINTYPE_NO= -1;
	public static int SETPASSMINTYPE1 = 001;
	public static int SETPASSMINTYPE2 = 002;
	public static int SETPASSMINTYPE3 = 003;


	public SetPayPassword(Context context, int setPassTapy, int setPassMintapy,
                          String paypass, String loginpass, String user_name_text,
                          String check_usercard, String check_bankcard, String check_phonenum, String check_code, String payPassWord)
			throws Exception {
		super(context, 9,
				new int[] { 1, 2, 3, 11, 18, 41, 42, 48, 52, 55, 125 });

		switch (setPassTapy) {
		case 111:
			xml = XmlBuilder.inject(context, TxCode, xml,
					TagUtils.Field001(bits), "", setPassTapy,
					TagUtils.Field011(context), "", "", "", UserInfo.getPhoneNumber(),
					TagUtils.md5Encode(paypass), TagUtils.md5Encode(loginpass),
					UserInfo.getSessionCode());
			break;
		case 223:
			switch (setPassMintapy) {
			case 001:
				xml = XmlBuilder.inject(context, TxCode, xml,
						TagUtils.Field001(bits), check_bankcard, setPassTapy,
						TagUtils.Field011(context), "001", check_phonenum,
						check_usercard, UserInfo.getPhoneNumber(), "","",
						UserInfo.getSessionCode());
				break;
			case 002:
				xml = XmlBuilder.inject(context, TxCode, xml,
						TagUtils.Field001(bits), check_bankcard, setPassTapy,
						TagUtils.Field011(context), "002", check_phonenum,
						check_usercard, UserInfo.getPhoneNumber(), "",check_code,
						UserInfo.getSessionCode());
				break;
			case 003:
				xml = XmlBuilder.inject(context, TxCode, xml,
						TagUtils.Field001(bits), check_bankcard, setPassTapy,
						TagUtils.Field011(context), "003", check_phonenum,
						check_usercard, UserInfo.getPhoneNumber(), TagUtils.md5Encode(payPassWord),"",
						UserInfo.getSessionCode());
				break;

			default:
				break;
			}
			break;

		default:
			break;
		}
	}
}
