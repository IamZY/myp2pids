package indra.Key;

import indra.dao.KeyDao;

public class Key {

	/**
	 * ����
	 * @param clearStr ���ܵ�����
	 * @param keyname  ������������
	 * @return
	 */
	public static String encryptKey(String clearStr,String keyname){
		return EncryptKey.encrypt(clearStr, KeyDao.findKey(keyname).getOutput()[0], KeyDao.findKey(keyname).getOutput()[1]);
	}
	
	/**
	 * ����
	 * @param secretStr �����ܵ�����
	 * @param keyname   ������������
	 * @return
	 */
	public static String decryptKey(String secretStr,String keyname){
		return DecryptKey.decrypt(secretStr, KeyDao.findKey(keyname).getOutput()[2], KeyDao.findKey(keyname).getOutput()[1]);
	}
	
}
