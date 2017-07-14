package com.example.mrxu.mutils;

import java.util.regex.Pattern;

public class PhoneCheckout {
	/**
	 * 手机号码校验
	 * <p>
	 * 校验格式参照<a href=
	 * "http://baike.baidu.com/link?url=B7EB1XqsrwIz0tlyYkEgluiKPUv1StYjFd7F1XZCQDWHIyV-YPaWHw5g6vQUvGFt"
	 * >手机号码(百度百科)</a>
	 * </p>
	 * <p>
	 * 修正了手机号码验证，添加了<a href=
	 * "http://www.sznews.com/tech/content/2014-03/11/content_9205839.htm"
	 * >170</a>号段的支持
	 * </p>
	 *
	 * @param phoneNumber
	 * @return
	 */
	public static final boolean isMobileNumber(String phoneNumber) {
		return Pattern
				.compile(
						"^((13[0-9])|(14[5,7])|(15[^4,\\D])|(170)|(18[0-9]))\\d{8}$")
				.matcher(phoneNumber).matches();
	}
}
