package indra.Test;

import indra.Key.CreateKey;
import indra.Key.DecryptKey;
import indra.Key.EncryptKey;
import indra.dao.KeyDao;
import indra.domain.Proxy;


public class Test {
	public static void main(String[] args) {
		//生成密钥对
		Proxy proxy = CreateKey.createKey("KeyServer", 1024);
		
		KeyDao.insertKey(proxy);
		
		String clearText = "你好！";
		
		proxy = KeyDao.findKey("KeyServer");
		
		//加密
		String secretText = EncryptKey.encrypt(clearText, proxy.getOutput()[0], proxy.getOutput()[1]);
		System.out.println("明文是：" + clearText);
		System.out.println("密文是：" + secretText);
		//解密
		System.out.println("解密后的明文是：" + DecryptKey.decrypt(secretText, proxy.getOutput()[2], proxy.getOutput()[1]));
	}
}
