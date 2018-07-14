package indra.frame;

import indra.Key.Key;
import indra.dao.KeyDao;
import indra.domain.Message;
import indra.domain.Warning;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

public class KeyServer {
	private JFrame frame = new JFrame("KeyServer");
	
	private JFrame getFrame(){
		return frame;
	}
	
	private JPanel panel = new JPanel();

	private JTextArea receiver = new JTextArea(12, 16);

	private JScrollPane jPane = new JScrollPane(receiver);

	private JButton keyButton = new JButton("KeyGen");
	
	
	// ���ô��ڵĳ���
	Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
	private final int WIDTH = 200;
	private final int HEIGHT = 300;

	final int newWidth = (int) ((screen.getWidth() - WIDTH) / 2);
	final int newHeight = (int) ((screen.getHeight() - HEIGHT) / 2);
	
	private ServerSocket server;
	private Socket socket;
	private Message msg;

	private ArrayList<Integer> arrayList = new ArrayList<Integer>();
	private Map<Integer, Socket> socketMap = new HashMap<Integer, Socket>();

	public KeyServer() {

		try {
			server = new ServerSocket(9999);

			AcceptConnectionThread act = new AcceptConnectionThread();
			act.start();

			// ��ʼ������
			init();
			
			genKeyGen();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void init() {

		panel.add(jPane);
		panel.add(keyButton);
		frame.add(panel);

		frame.setLocation(newWidth, newHeight);
		frame.setSize(WIDTH, HEIGHT);
		frame.setVisible(true);
		frame.setResizable(false);

		frame.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub
				super.windowClosing(e);
				//�������رպ�������ݿ�
				KeyDao.deleteKey();
				try {
					socket.close();
					//�رս���
					System.exit(0);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			
			}
			
		});
		
	}

	/**
	 * ����keyGen
	 */
	public void genKeyGen(){
		keyButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				new KeyGen(frame);
			}
		});
	}
	
	
	/**
	 * ����Ϣ���߳�
	 * 
	 * @author IamZY
	 * 
	 */

	class ReceiveThread extends Thread {

		private Socket socket;

		public ReceiveThread(Socket socket) {
			this.socket = socket;
		}

		private int this_port;
		
		private ObjectInputStream ois;
		
		public void run() {

			
			while (true) {
					
				System.out.println("������������Ϣ�߳�....");
				
				try {

					ois = new ObjectInputStream(socket.getInputStream());
					
					
					msg = (Message) ois.readObject();
					// ���ÿ���ͻ��˵Ķ˿ں�
					InetSocketAddress address = (InetSocketAddress) socket.getRemoteSocketAddress();
					int local_port = address.getPort();
					
					this_port = local_port;
					
					//���е���Ϣ֮ǰ������KeyInquire���ܵ� û�м��ܵ���Ϣһ����Ϊ�쳣���ݰ�
					//���Խ��ܵ�Ϊ�Ϸ����ݰ�
					if(msg.getKeyWords().equals("KeyInquire")){
						//�õ������˿ں�
						System.out.println("..port->" + local_port);
						System.out.println("..flag->" + msg.getFlag());
						
						//���ܴ�������Ϣ
						//String dk_str = Key.decryptKey(msg.getMessage(), msg.getKeyWords());
						//msg.setMessage(dk_str);
						int flag = msg.getFlag();

						if(flag == 1){
							//�û���������
							//���ܿͻ��˴���������
							String dk_str = Key.decryptKey(msg.getMessage(), msg.getKeyWords());
							msg.setMessage(dk_str);
							String[] msgs = msg.getMessage().split(":");
							String name = msgs[0];
							System.out.println(name + "<-->" + local_port);
							
							//name �� port ��ֵ�Լ������ݿ�
							KeyDao.insertPort(name, String.valueOf(local_port));
							//����������ܿ�дֵ
							receiver.append(msg.getMessage() + "\n");
							
							// ��һ��ˢ�º����б�
							// ����socket �������б���ÿ���ͻ���
							Set set = socketMap.keySet();
							Iterator ite = set.iterator();
							while (ite.hasNext()) {
								Integer ports = (Integer) ite.next();
								Socket socket = socketMap.get(ports);
								ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
								Message msg_1 = new Message();
								msg_1.setFlag(1);
								msg_1.setList(arrayList);
								oos.writeObject(msg_1);
								oos.flush();
							}
						} else if (flag == 2) {
							System.out.println("======================����������Ⱥ����Ϣ============================");
							// Ⱥ��
							//���ܴ�������Ϣ
							String dk_str = Key.decryptKey(msg.getMessage(), msg.getKeyWords());
							msg.setMessage(dk_str);
							// ͨ��port����socket  
							Set set = socketMap.keySet();
							Iterator ite = set.iterator();
							
							while (ite.hasNext()) {
								Integer port = (Integer) ite.next();
								//�ҵ����е�socket
								Socket socket = socketMap.get(port);
								Message msg_2 = new Message();
								//��KeyServer����
								String ek_str_2 = Key.encryptKey(msg.getMessage(), "KeyServer");
								msg_2.setMessage(ek_str_2);
								msg_2.setAim_port(String.valueOf(local_port));
								msg_2.setFlag(2);
								ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
								oos.writeObject(msg_2);
								oos.flush();
							}
							System.out.println("======================����������Ⱥ����Ϣ��ϣ�============================");
						} else if (flag == 3) {
							// ˽��
							System.out.println("===============����������˽����Ϣ==================");
							
							// ��KeyInquire���ܴ����Ķ˿ں�
							String dk_AimPort = Key.decryptKey(msg.getAim_port(), msg.getKeyWords());
							System.out.println("Ŀ��˿ڣ���" + dk_AimPort);
							String dk_MyPort = Key.decryptKey(msg.getMy_port(), msg.getKeyWords());
							System.out.println("�����˿ڣ���" + dk_MyPort);
							
							// �Ҷ�Ӧ�Ķ˿ں� socket
							Socket socket = socketMap.get(Integer.valueOf(dk_AimPort));
							
							System.out.println(socket);
							
							Message msg3 = new Message();
							//��Ϣ���� �ٴδ���
							msg3.setMessage(msg.getMessage());
							//��KeyServer���� �˿�
							String ek_Aimport = Key.encryptKey(dk_AimPort, "KeyServer");
							String ek_Myport = Key.encryptKey(dk_MyPort, "KeyServer");
							
							/** �˿�ת�������� ԭ���ı����˿�=>���ڵ�Ŀ�Ķ˿�  ԭ����Ŀ�Ķ˿�=>���ڵı����˿�*/
							//�����Ķ˿ں�
							msg3.setMy_port(ek_Aimport);
							//Ŀ��˿ں�
							msg3.setAim_port(ek_Myport);

							msg3.setFlag(3);
							ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
							oos.writeObject(msg3);
							oos.flush();
							System.out.println("===============����������˽����Ϣ��ϣ�================");
						}else if(flag == 4){
							System.out.println("=================ˢ�º����б�======================");
							String dk_str = Key.decryptKey(msg.getMessage(), msg.getKeyWords());
							msg.setMessage(dk_str);
							
							receiver.append(msg.getMessage() + '\n');
							local_port = address.getPort();
							System.out.println(local_port);
							//ɾ���رտͻ��˵�socket
							socketMap.remove(local_port);
							arrayList.remove((Integer)local_port);
							System.out.println("arrayList->" + arrayList.toString());
							
							if(arrayList.size() != 0) {
								//���������б�
								Set set = socketMap.keySet();
								Iterator ite = set.iterator();
								while (ite.hasNext()) {
									Integer ports = (Integer) ite.next();
									Socket socket = socketMap.get(ports);
									ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
									Message msg4 = new Message();
									msg4.setFlag(1);
									msg4.setList(arrayList);
									oos.writeObject(msg4);
									oos.flush();
								}
							}
							
							//���Ѿ��رյĿͻ��˵Ķ�ȡ���̹ر�
							ois.close();
							
							System.out.println("=================ˢ�º����б���ϣ�======================");
							
						}

					}else {
						System.err.println("error!!");
					}
					
				} catch (IOException e) {
					e.printStackTrace();
					//���ͻ��˶Ͽ����ӵ�ʱ������ѭ��
					//����ѭ��
					break;
					
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NullPointerException e) {
					System.err.println("catch error!!");
					//��⵽������Ϣ
					//...
					// ���������б�
					// ͨ��port����socket  
					Set set = socketMap.keySet();
					Iterator ite = set.iterator();
					while (ite.hasNext()) {
						Integer port = (Integer) ite.next();
						//�ҵ����е�socket
						Socket socket = socketMap.get(port);
						Warning warnMsg = new Warning();
						//��KeyServer����
						String ek_warnPort = Key.encryptKey(String.valueOf(this_port), "KeyServer");
						warnMsg.setWarnPort(ek_warnPort);
						warnMsg.setFlag(5);
						ObjectOutputStream oos;
						try {
							oos = new ObjectOutputStream(socket.getOutputStream());
							oos.writeObject(warnMsg);
							oos.flush();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}

				}

			}

		}

	}

	
	/**
	 * �������ӵ��߳�
	 * 
	 * @author Administrator
	 * 
	 */

	class AcceptConnectionThread extends Thread {
		
		public void run() {

			while (true) {

				try {
					
					System.out.println("����������Ϣ....");
					
					socket = server.accept();

					// ���ÿ���ͻ��˵Ķ˿ں�
					InetSocketAddress address = (InetSocketAddress) socket.getRemoteSocketAddress();
					int port = address.getPort();
			
					arrayList.add(port);
					System.out.println("arr=>" + arrayList.toString());

					// ��ÿ���ͻ��˵Ķ˿ںź�socket����map��
					socketMap.put(port, socket);

					//�ͻ�����ʾ�˿ں�
					ObjectOutputStream oos1 = new ObjectOutputStream(socket.getOutputStream());
					Message user = new Message();
					user.setFlag(0);
					user.setAim_port(String.valueOf(port));
					oos1.writeObject(user);
					oos1.flush();
					
					// ˢ�º����б�
					// ����socket �������б���ÿ���ͻ���
					Set set = socketMap.keySet();
					Iterator ite = set.iterator();
					while (ite.hasNext()) {
						Integer ports = (Integer) ite.next();
						Socket socket = socketMap.get(ports);
						ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
						Message msg_1 = new Message();
						msg_1.setFlag(1);
						//msg_1.setAim_port(port);
						msg_1.setList(arrayList);
						oos.writeObject(msg_1);
						oos.flush();
					}
					
					ReceiveThread r = new ReceiveThread(socket);
					r.start();

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					KeyDao.deleteKey();
				}
				
			}
		}
	}

	public static void main(String args[]) {
		KeyServer qqServer = new KeyServer();
	}

}
