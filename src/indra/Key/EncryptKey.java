package indra.Key;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EncryptKey {
	/**
	 * ������RSA��Կ�а���������������Ϣ��e��n��������������m,�������ĵĹ�ʽ��m��e�η�����n��ģ��
	 * 
	 * @param clearText
	 *            ����
	 * @param eStr
	 *            ��Կ
	 * @param nStr
	 * @return
	 */
	public static String encrypt(String clearText, String eStr, String nStr) {
		String secretText = new String();

		try {
			clearText = URLEncoder.encode(clearText, "GBK");
			byte text[] = clearText.getBytes("GBK");// ���ַ���ת����byte�������飬ʵ���Ǹ����ַ��Ķ�������ʽ
			BigInteger mm = new BigInteger(text);// �����ƴ�ת��Ϊһ��������
			clearText = mm.toString();

			BigInteger e = new BigInteger(eStr);
			BigInteger n = new BigInteger(nStr);
			byte[] ptext = clearText.getBytes("UTF8"); // ��ȡ���ĵĴ�����
			BigInteger m = new BigInteger(ptext);
			BigInteger c = m.modPow(e, n);
			secretText = c.toString();
		} catch (UnsupportedEncodingException ex) {
			Logger.getLogger(CreateKey.class.getName()).log(Level.SEVERE, null, ex);
		}
		return secretText;
	}
}
