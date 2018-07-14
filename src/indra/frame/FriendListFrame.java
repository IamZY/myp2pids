package indra.frame;

import indra.Key.CreateKey;
import indra.Key.Key;
import indra.dao.KeyDao;
import indra.domain.Message;
import indra.domain.Warning;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;


public class FriendListFrame {

	// ����
	private JFrame frame = new JFrame("Client");
	// �󲼾�
	private JPanel panel = new JPanel();

	// �����ϲ�
	private JPanel panel2 = new JPanel();

	// �ϲ��м�
	private JPanel panel3 = new JPanel();

	// �����²�
	private JPanel panel4 = new JPanel();

	// �����б������
	private JPanel panel5 = new JPanel();

	// �����б�
	private JPanel panel6 = new JPanel();

	// ��Ӻ���
	private JPanel panel7 = new JPanel();


	// �û�����ʾ��
	private JLabel userLabel = new JLabel();
	private JLabel userNameLabel = new JLabel();
	private JLabel portLabel = new JLabel();
	private JLabel portNameLabel = new JLabel();
	private JLabel friendLabel = new JLabel();

	// ���������б�
	private JList<String> list = new JList<String>();

	// ��ť
	JButton exitButton = new JButton("����");
	JButton moreButton = new JButton("Ⱥ��");

	private final int F_WIDTH = 280;
	private final int F_HEIGHT = 600;

	private Socket socket;

	private ChatFramePrivate client; // ˽��
	private ChatFrameMore c; // Ⱥ��
	
	private Map<Integer, ChatFramePrivate> friendMap = new HashMap<Integer, ChatFramePrivate>();
	
	private String name;
	private String port_str;
	
	
	public FriendListFrame(String name) {

		this.name = name;
		
		//˽��
		listactionlistener();
		//Ⱥ��
		morelistener();
		//����
		exitlistener();

		try {
			socket = new Socket("127.0.0.1", 9999);

			//������Ϣ�߳�����
			ReceiveMsg r = new ReceiveMsg();
			r.start();
			
			//���߼�����
			loginlistener();
			
		} catch (UnknownHostException e) {
			JOptionPane.showMessageDialog(frame, "δ֪����","ERROR_MESSAGE",JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(frame, "���Ӳ��Ϸ�����!","ERROR_MESSAGE",JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}

	}

	/**
	 * ��ʼ������
	 */
	public void init() {
		panel.setLayout(new BorderLayout());
		panel.add(panel2, BorderLayout.NORTH);
		panel.add(panel4, BorderLayout.CENTER);

		panel2.setLayout(new BorderLayout());
		panel2.add(panel3,BorderLayout.WEST);
		
		panel3.add(userNameLabel);
		userNameLabel.setText(" �û���:");
		userNameLabel.setFont(new Font("����",Font.BOLD,16));
		panel3.add(userLabel);
		userLabel.setText(name);
		userLabel.setFont(new Font("����",Font.BOLD,16));
		panel3.add(portNameLabel);
		portNameLabel.setText(" �˿ں�:");
		portNameLabel.setFont(new Font("����",Font.BOLD,16));
		panel3.add(portLabel);
		portLabel.setText(port_str);
		//portLabel.setText("1111");
		
		portLabel.setFont(new Font("����",Font.BOLD,16));
		
		panel4.setLayout(new BorderLayout());
		panel4.add(panel5);

		panel5.setLayout(new BorderLayout());
		friendLabel.setText(" �����б�");
		friendLabel.setFont(new Font("����",Font.BOLD,16));
		panel5.add(friendLabel, BorderLayout.NORTH);
		panel5.add(panel6);

		panel6.setLayout(new BorderLayout());
		panel6.add(list, BorderLayout.NORTH);

		panel6.add(panel7, BorderLayout.SOUTH);
		panel7.setLayout(new GridLayout(1, 3, 2, 2));
		panel7.add(exitButton);
		panel7.add(new JLabel());
		panel7.add(moreButton);


		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		final int newWidth = (int) ((screen.getWidth() - F_WIDTH) / 2);
		final int newHeight = (int) ((screen.getHeight() - F_HEIGHT) / 2);

		frame.add(panel);
		frame.setLocation(newWidth, newHeight);
		frame.setResizable(false);
		frame.setSize(280, 400);
		frame.setVisible(true);
	}

	/**
	 * ���߼�����
	 */
	public void loginlistener(){
		try {
			System.out.println("���߼�����.......");
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());			
			Message msg = new Message();
			msg.setMessage(Key.encryptKey(name + ":������!", "KeyInquire"));
			msg.setFlag(1);
			msg.setKeyWords("KeyInquire");
			oos.writeObject(msg);
			System.out.println("���߼�����over....");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}			
	}
	
	
	/**
	 * list˽�ļ�����
	 */
	public void listactionlistener() {
		list.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				//2 => ˫��
				if (e.getClickCount() == 2) {
					System.out.println("list˽�ļ�����....");
					//Ŀ������
					String name = (String) list.getSelectedValue();
					//�����Ҷ�Ӧ�˿ں�
					String portStr = KeyDao.getPortByName(name);
					
					Integer port = Integer.valueOf(portStr);
					
					System.out.println("Ŀ��˿� port->" + port);
					System.out.println("�����˿� port->" + port_str);
					
					client = new ChatFramePrivate(socket,port,Integer.valueOf(port_str));
					
					//���˿ںźͶ�Ӧ�Ĵ��ڴ���map��
					friendMap.put(port, client);
																				
					Set set = friendMap.keySet();
					Iterator ite = set.iterator();
					while(ite.hasNext()){
						Integer port3 = (Integer) ite.next();
						System.out.println(port3);
						ChatFramePrivate chatFramePrivate = friendMap.get(port3);
						System.out.println(chatFramePrivate);
					}
					
				}
			}

		});
	}

	/**
	 * Ⱥ�ļ�����
	 */
	public void morelistener() {
		moreButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				c = new ChatFrameMore(socket);
			}
		});
	}

	
	/**
	 * ���߼�����
	 */
	public void exitlistener(){
		frame.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub
				try {
					ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
					Message msg = new Message();
					msg.setMessage(Key.encryptKey(name + ":������!", "KeyInquire"));
					msg.setKeyWords("KeyInquire");
					msg.setFlag(4);
					oos.writeObject(msg);
					//ɾ�����ݿ��е�Կ��
					KeyDao.delOneKey(name);
					//ɾ�������������˿ںż�ֵ����Ϣ
					KeyDao.delByName(name);
					frame.dispose();
					socket.close();
					oos.close();
					System.exit(0);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
		});
		
		exitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				try {
					ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
					Message msg = new Message();
					msg.setMessage(Key.encryptKey(name + ":������!", "KeyInquire"));
					msg.setKeyWords("KeyInquire");
					msg.setFlag(4);
					oos.writeObject(msg);
					
					//ɾ�����ݿ��е�Կ��
					KeyDao.delOneKey(name);
					//ɾ�������������˿ںż�ֵ����Ϣ
					KeyDao.delByName(name);
					frame.dispose();
					socket.close();
					oos.close();
					System.exit(0);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
			
		});
	}
	
	/**
	 * ������Ϣ�߳�
	 */
	class ReceiveMsg extends Thread {

		public void run() {

			try {

				while (true) {
					System.out.println("�����б��ȡ����.....");

					// ��ȡ����
					ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
					Object obj = ois.readObject();
					int flag = 0;
					Message  msg = null;
					Warning warning = null;
					
					//ois.readObj() ֻ�ܶ�ȡһ��
					if(obj instanceof Message){
						msg = (Message) obj;
						//���ܷ�������������Ϣ flag
						flag = msg.getFlag();
					} else {
						warning = (Warning) obj;
						flag = warning.getFlag();
					}
					
					System.out.println("flag->" + flag);						
					
					/**
					 * ˢ�´���
					 */
					if(flag == 0){
						port_str = String.valueOf(msg.getAim_port());
						init();
						//���ɿͻ��˵���Կ��,���������ݿ���
						KeyDao.insertKey(CreateKey.createKey(name, KeyDao.getKeyLen("KeyServer")));
					}
					/**
					 * ���ܺ����б� if(flag == 1)�����б�
					 */
					else if(flag == 1){
						System.out.println("========================���ܺ����б�============================");
						DefaultListModel<String> listModel = new DefaultListModel();
						for (int i = 0; i < msg.getList().size(); i++) {
							String local_name = KeyDao.getNameByPort(String.valueOf(msg.getList().get(i)));
							listModel.addElement(local_name);
						}
						list.setModel(listModel);
						System.out.println("========================���ܺ����б���ϣ�============================");
					}
					else if (flag == 2) {
						/**
						 * Ⱥ�� if(flag == 2)Ⱥ��
						 */

						System.out.println("========================�ͻ��˽���Ⱥ��======================");
						if(c == null){
							c = new ChatFrameMore(socket);
						}
						// Ⱥ�ķ���
						System.out.println(c);
						//��Ϣ ��KeyServer����
						String dk_str = Key.decryptKey(msg.getMessage(), "KeyServer"); 
						c.chatmore(dk_str,Integer.valueOf(msg.getAim_port()));
						System.out.println("========================�ͻ��˽���Ⱥ�����======================");
					} 
					else if (flag == 3) {

						/**
						 * ˽�� if(flag == 3) �˿ں�:��Ϣ
						 */
						System.out.println("===============�ͻ��˽���˽����Ϣ===============");
						//��KeyServer����
						Integer port2 = Integer.valueOf(Key.decryptKey(msg.getAim_port(), "KeyServer"));
						System.out.println("port2->" + port2);
						ChatFramePrivate client = friendMap.get(port2);
						//�ô�����֮ǰ�ı����˿ںŽ���
						String dk_str = Key.decryptKey(msg.getMessage(), KeyDao.getNameByPort(String.valueOf(port2)));
						
						if(client == null){
							client = new ChatFramePrivate(socket, port2,Integer.valueOf(port_str));
							//���洰��
							friendMap.put(port2, client);
							client.chatmore(dk_str,port2);
						}else{
							client.chatmore(dk_str,port2);
						}
						System.out.println("===============�ͻ��˽���˽����Ϣ��ϣ�===============");
					} else if (flag == 5) {
						/**
						 * ��⵽�������ݰ�!
						 */
						String dk_warnMsg = Key.decryptKey(warning.getWarnPort(), "KeyServer");
						JOptionPane.showMessageDialog(frame, "��⵽�������ݰ�!�쳣�˿�Ϊ: " + dk_warnMsg, "ERROR_MESSAGE",JOptionPane.ERROR_MESSAGE);
					}

				}
			} catch (IOException e) {
				
				if(frame != null) {
					//�������Ͽ�����..��Ӧ����
					JOptionPane.showMessageDialog(frame, "��������Ͽ����ӣ�", "ERROR_MESSAGE",JOptionPane.ERROR_MESSAGE);
					KeyDao.delByName(KeyDao.getNameByPort(port_str));
					try {
						socket.close();
						frame.dispose();
						System.exit(0);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				
			} catch (ClassNotFoundException e) {
				JOptionPane.showMessageDialog(frame, "δ֪����", "ERROR_MESSAGE",JOptionPane.ERROR_MESSAGE);
				KeyDao.delByName(KeyDao.getNameByPort(port_str));
				try {
					frame.dispose();
					socket.close();
					System.exit(0);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

		}

	}

	/**
	 * main
	 */
	public static void main(String args[]) {
		FriendListFrame flf = new FriendListFrame("456");
	}

}
