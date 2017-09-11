package com.example.mrxu.assemble_xml;

import android.content.Context;
import android.util.Log;

import com.example.mrxu.allinfo.UserInfo;
import com.example.mrxu.mutils.Mosaic_Base;
import com.example.mrxu.mutils.TagUtils;
import com.example.mrxu.mutils.XmlBuilder;

//支付密码取消

public class AccountWithdrawTn extends Mosaic_Base {
	public AccountWithdrawTn(Context context,String money/*,String password*/)
			throws Exception {
		super(context, 20019, new int[] { 1, 3, 4, 11,48,52,125,128 });

		Log.d("xu", "AccountWithdrawTn: "+money);
		xml = XmlBuilder.inject(context, TxCode, xml, TagUtils.Field001(bits),
				"900000",money, TagUtils.Field011(context), UserInfo
						.getPhoneNumber(),"",UserInfo.getSessionCode()
						.toString(), TagUtils.MAC(TagUtils.TxCode(TxCode),
						TagUtils.TxDate(), TagUtils.TxTime(),
						TagUtils.Field011(context),
						UserInfo.getPhoneNumber()+""));
	}
}
