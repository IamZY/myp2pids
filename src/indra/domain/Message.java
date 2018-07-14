package indra.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Message implements Serializable{

	//��ǰ�˿ں�
	private String my_port;
	//Ŀ��˿ں�
	private String aim_port; 
	//����˿ںż���
	private ArrayList<Integer> list;
	private String name;
	private String message;
	//���ܵ���ʽ KeyServer ���� KeyInquire ��������ʽ
	private String keyWords;
	
	//������־λ
	private int flag;

	public String getKeyWords() {
		return keyWords;
	}

	public void setKeyWords(String keyWords) {
		this.keyWords = keyWords;
	}

	public String getName() {
		return name;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<Integer> getList() {
		return list;
	}

	public void setList(ArrayList<Integer> list) {
		this.list = list;
	}


	public String getMy_port() {
		return my_port;
	}

	public void setMy_port(String my_port) {
		this.my_port = my_port;
	}

	public String getAim_port() {
		return aim_port;
	}

	public void setAim_port(String aim_port) {
		this.aim_port = aim_port;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}
	
	
}
