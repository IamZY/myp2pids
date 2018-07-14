package indra.Key;


import indra.domain.Proxy;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;


public class CreateKey {
		
	/**
	 * ������Կ����������ָ�����ܺͽ����㷨ΪRSA
	 * 
	 * @param keylen
	 * @return
	 */
	// public static String[] createKey(int keylen) {// ������Կ����
	public static Proxy createKey(String name, int keylen) {
		String[] output = new String[5]; // �����洢��Կ��e n d p q
		Proxy proxy = null;
		try {
			KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
			kpg.initialize(keylen); // ָ����Կ�ĳ��ȣ���ʼ����Կ��������
			KeyPair kp = kpg.generateKeyPair(); // ������Կ��

			RSAPublicKey puk = (RSAPublicKey) kp.getPublic();
			RSAPrivateCrtKey prk = (RSAPrivateCrtKey) kp.getPrivate();

			BigInteger e = puk.getPublicExponent();
			BigInteger n = puk.getModulus();

			BigInteger d = prk.getPrivateExponent();
			BigInteger p = prk.getPrimeP();
			BigInteger q = prk.getPrimeQ();
			output[0] = e.toString();
			output[1] = n.toString();
			output[2] = d.toString();
			output[3] = p.toString();
			output[4] = q.toString();

			proxy = new Proxy(name, String.valueOf(keylen), output);

		} catch (NoSuchAlgorithmException ex) {
			Logger.getLogger(CreateKey.class.getName()).log(Level.SEVERE, null,
					ex);
		}
		return proxy;
	}

}