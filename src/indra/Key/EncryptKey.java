package indra.Key;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EncryptKey {
	/**
	 * 加密在RSA公钥中包含有两个整数信息：e和n。对于明文数字m,计算密文的公式是m的e次方再与n求模。
	 * 
	 * @param clearText
	 *            明文
	 * @param eStr
	 *            公钥
	 * @param nStr
	 * @return
	 */
	public static String encrypt(String clearText, String eStr, String nStr) {
		String secretText = new String();

		try {
			clearText = URLEncoder.encode(clearText, "GBK");
			byte text[] = clearText.getBytes("GBK");// 将字符串转换成byte类型数组，实质是各个字符的二进制形式
			BigInteger mm = new BigInteger(text);// 二进制串转换为一个大整数
			clearText = mm.toString();

			BigInteger e = new BigInteger(eStr);
			BigInteger n = new BigInteger(nStr);
			byte[] ptext = clearText.getBytes("UTF8"); // 获取明文的大整数
			BigInteger m = new BigInteger(ptext);
			BigInteger c = m.modPow(e, n);
			secretText = c.toString();
		} catch (UnsupportedEncodingException ex) {
			Logger.getLogger(CreateKey.class.getName()).log(Level.SEVERE, null, ex);
		}
		return secretText;
	}
}
