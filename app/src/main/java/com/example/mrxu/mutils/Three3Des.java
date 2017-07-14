package com.example.mrxu.mutils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
public class Three3Des {
	private static final String Algorithm = "DESede/ECB/NOPADDING"; // 定义加密算法,可用
	// DES,DESede,Blowfish，DESede/CBC/PKCS5Padding
	private static final String hexString="0123456789ABCDEF";
	/**
	 * 													 
	 * @param keybyte  加密密钥，长度为24字节
	 * @param src 	  字节数组(根据给定的字节数组构造一个密钥�? )
	 * @return
	 */
	public static byte[] encryptMode(byte[] keybyte, byte[] src) {
		try {
			// 根据给定的字节数组和算法构�?�?��密钥
			SecretKey deskey = new SecretKeySpec(keybyte, Algorithm);
			// 加密
			Cipher c1 = Cipher.getInstance(Algorithm);
			c1.init(Cipher.ENCRYPT_MODE, deskey);
			return c1.doFinal(src);
		} catch (java.security.NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		} catch (javax.crypto.NoSuchPaddingException e2) {
			e2.printStackTrace();
		} catch (Exception e3) {
			e3.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param keybyte 密钥
	 * @param src	    �?��解密的数�?
	 * @return
	 */
	public static byte[] decryptMode(byte[] keybyte, byte[] src) {
		try {
			// 生成密钥
			SecretKey deskey = new SecretKeySpec(keybyte, Algorithm);
			// 解密
			Cipher c1 = Cipher.getInstance(Algorithm);
			c1.init(Cipher.DECRYPT_MODE, deskey);
			return c1.doFinal(src);
		} catch (java.security.NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		} catch (javax.crypto.NoSuchPaddingException e2) {
			e2.printStackTrace();
		} catch (Exception e3) {
			e3.printStackTrace();
		}
		return null;
	}
	
	 /**
	   * �?6进制字符串转换成字节数组
	   * @param
	   * @return byte[]
	   */
	  public static byte[] hexStringToByte(String hex) {
	   int len = (hex.length() / 2);
	   byte[] result = new byte[len];
	   char[] achar = hex.toCharArray();
	   for (int i = 0; i < len; i++) {
	    int pos = i * 2;
	    result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
	   }
	   return result;
	  }  
	  private static int toByte(char c) {
		    byte b = (byte) "0123456789ABCDEF".indexOf(c);
		    return b;
		 }
	//byte[]�?6进制String
		
		public static final String encodeHex(byte bytes[]) {
			StringBuffer buf = new StringBuffer(bytes.length * 2);
			for (int i = 0; i < bytes.length; i++) {
				if ((bytes[i] & 0xff) < 16)
					buf.append("0");
				buf.append(Long.toString(bytes[i] & 0xff, 16));
			}
			return buf.toString();
		}
}
