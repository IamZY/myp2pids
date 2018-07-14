package indra.Test;

import indra.Key.CreateKey;
import indra.Key.DecryptKey;
import indra.Key.EncryptKey;
import indra.dao.KeyDao;
import indra.domain.Proxy;


public class Test {
	public static void main(String[] args) {
		//������Կ��
		Proxy proxy = CreateKey.createKey("KeyServer", 1024);
		
		KeyDao.insertKey(proxy);
		
		String clearText = "��ã�";
		
		proxy = KeyDao.findKey("KeyServer");
		
		//����
		String secretText = EncryptKey.encrypt(clearText, proxy.getOutput()[0], proxy.getOutput()[1]);
		System.out.println("�����ǣ�" + clearText);
		System.out.println("�����ǣ�" + secretText);
		//����
		System.out.println("���ܺ�������ǣ�" + DecryptKey.decrypt(secretText, proxy.getOutput()[2], proxy.getOutput()[1]));
	}
}
