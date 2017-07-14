package com.example.mrxu.mutils;


public class Pin {

	// 将两数异�?
	public static String PinPan(String pin, String pan) {
		String w, e, sd = "";
		for (int i = 0; i < pin.length() / 4; i++) {
			w = pin.substring(i * 4, i * 4 + 4);
			e = pan.substring(i * 4, i * 4 + 4);
			try {
				System.out.println("去掉0x前的是："+w+"第二个数是："+e);
				int in = OxStringtoInt(w) ^ OxStringtoInt(e);
				System.out.println("============"+in);
				String kk = Integer.toHexString(in);
				if(kk.length() == 1){
					kk = "0"+kk;
				}
				System.out.println("十六进制的是�?+kk");
				sd = sd + kk;

			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}
		return sd;
	}

	// 专化为十进制为pin加密服务
	private static int OxStringtoInt(String ox) throws Exception {
		// 转化为小写子�?
		ox = ox.toLowerCase();
		if (ox.startsWith("0x")) {
			ox = ox.substring(2, ox.length());
		}
		int ri = 0;
		int oxlen = ox.length();
		if (oxlen > 8)
			throw (new Exception("too lang"));
		for (int i = 0; i < oxlen; i++) {
			char c = ox.charAt(i);
			int h;
			if (('0' <= c && c <= '9')) {
				h = c - 48;
			} else if (('a' <= c && c <= 'f')) {
				h = c - 87;

			} else if ('A' <= c && c <= 'F') {
				h = c - 55;
			} else {
				throw (new Exception("not a integer "));
			}
			byte left = (byte) ((oxlen - i - 1) * 4);
			ri |= (h << left);
		}
		return ri;
	}

	// �?78901234567转化�?x000x000x670x890x010x230x450x67
	public static String jiami(String str) {
		str = str.replaceAll(" ", "");
		System.out.println(str);
		String string = "0x000x00";
		for (int i = 0; i < str.length() / 2; i++) {
			string = string + "0x" + str.substring(i * 2, i * 2 + 2);
			System.out.println(string);
		}
		return string;

	}

	// �?23456转化�?x06 0x12 0x34 0x56 0xFF 0xFF 0xFF 0xFF
	public static String mima(String str) {
		String s1 = "0x06";
		String s2 = "0xff0xff0xff0xff";
		String ss = "";
		for (int i = 0; i < str.length() / 2; i++) {
			s1 = s1 + "0x" + str.substring(i * 2, i * 2 + 2);
		}
		//
		return s1 + s2;
	}
	//调用3Des加密
	public static String passMethod(String key,String data){
		byte[] s = Three3Des.encryptMode(Three3Des.hexStringToByte(key),Three3Des.hexStringToByte(data));
		return Three3Des.encodeHex(s);
	}
	//调用解密方法
	public static String decodeMethod(String key,String data){
		byte[] ss = Three3Des.decryptMode(Three3Des.hexStringToByte(key),Three3Des.hexStringToByte(data));
		return Three3Des.encodeHex(ss);
	}
}