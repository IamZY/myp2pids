package indra.Key;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLDecoder;

public class DecryptKey {

	/**
	 * ����
	 * 
	 * @param secretText
	 *            ����
	 * @param dStr
	 *            ˽Կ
	 * @param nStr
	 * @return
	 */
	public static String decrypt(String secretText, String dStr, String nStr) {
		StringBuffer clearTextBuffer = new StringBuffer();
		BigInteger d = new BigInteger(dStr);// ��ȡ˽Կ�Ĳ���d,n
		BigInteger n = new BigInteger(nStr);
		BigInteger c = new BigInteger(secretText);
		BigInteger m = c.modPow(d, n);// ��������
		byte[] mt = m.toByteArray();// �������Ķ�Ӧ���ַ��������
		for (int i = 0; i < mt.length; i++) {
			clearTextBuffer.append((char) mt[i]);
		}
		String temp = clearTextBuffer.toString();// tempΪ���ĵ��ַ�����ʽ
		BigInteger b = new BigInteger(temp);// bΪ���ĵ�BigInteger����
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
