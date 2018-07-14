package indra.Key;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLDecoder;

public class DecryptKey {

	/**
	 * 解密
	 * 
	 * @param secretText
	 *            密文
	 * @param dStr
	 *            私钥
	 * @param nStr
	 * @return
	 */
	public static String decrypt(String secretText, String dStr, String nStr) {
		StringBuffer clearTextBuffer = new StringBuffer();
		BigInteger d = new BigInteger(dStr);// 获取私钥的参数d,n
		BigInteger n = new BigInteger(nStr);
		BigInteger c = new BigInteger(secretText);
		BigInteger m = c.modPow(d, n);// 解密明文
		byte[] mt = m.toByteArray();// 计算明文对应的字符串并输出
		for (int i = 0; i < mt.length; i++) {
			clearTextBuffer.append((char) mt[i]);
		}
		String temp = clearTextBuffer.toString();// temp为明文的字符串形式
		BigInteger b = new BigInteger(temp);// b为明文的BigInteger类型
		byte[] mt1 = b.toByteArray();

		try {
			String clearText = (new String(mt1, "GBK"));
			clearText = URLDecoder.decode(clearText, "GBK");
			return clearText;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}
}
