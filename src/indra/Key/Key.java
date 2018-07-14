package indra.Key;

import indra.dao.KeyDao;

public class Key {

	/**
	 * 加密
	 * @param clearStr 加密的内容
	 * @param keyname  加密器的名称
	 * @return
	 */
	public static String encryptKey(String clearStr,String keyname){
		return EncryptKey.encrypt(clearStr, KeyDao.findKey(keyname).getOutput()[0], KeyDao.findKey(keyname).getOutput()[1]);
	}
	
	/**
	 * 解密
	 * @param secretStr 被加密的内容
	 * @param keyname   加密器的名称
	 * @return
	 */
	public static String decryptKey(String secretStr,String keyname){
		return DecryptKey.decrypt(secretStr, KeyDao.findKey(keyname).getOutput()[2], KeyDao.findKey(keyname).getOutput()[1]);
	}
	
}
