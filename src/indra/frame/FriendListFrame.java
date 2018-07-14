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

	// 窗口
	private JFrame frame = new JFrame("Client");
	// 大布局
	private JPanel panel = new JPanel();

	// 窗口上部
	private JPanel panel2 = new JPanel();

	// 上部中间
	private JPanel panel3 = new JPanel();

	// 窗口下部
	private JPanel panel4 = new JPanel();

	// 好友列表的名字
	private JPanel panel5 = new JPanel();

	// 好友列表
	private JPanel panel6 = new JPanel();

	// 添加好友
	private JPanel panel7 = new JPanel();


	// 用户名显示框
	private JLabel userLabel = new JLabel();
	private JLabel userNameLabel = new JLabel();
	private JLabel portLabel = new JLabel();
	private JLabel portNameLabel = new JLabel();
	private JLabel friendLabel = new JLabel();

	// 好友下拉列表
	private JList<String> list = new JList<String>();

	// 按钮
	JButton exitButton = new JButton("离线");
	JButton moreButton = new JButton("群聊");

	private final int F_WIDTH = 280;
	private final int F_HEIGHT = 600;

	private Socket socket;

	private ChatFramePrivate client; // 私聊
	private ChatFrameMore c; // 群聊
	
	private Map<Integer, ChatFramePrivate> friendMap = new HashMap<Integer, ChatFramePrivate>();
	
	private String name;
	private String port_str;
	
	
	public FriendListFrame(String name) {

		this.name = name;
		
		//私聊
		listactionlistener();
		//群聊
		morelistener();
		//离线
		exitlistener();

		try {
			socket = new Socket("127.0.0.1", 9999);

			//接受消息线程启动
			ReceiveMsg r = new ReceiveMsg();
			r.start();
			
			//上线监听器
			loginlistener();
			
		} catch (UnknownHostException e) {
			JOptionPane.showMessageDialog(frame, "未知错误！","ERROR_MESSAGE",JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(frame, "连接不上服务器!","ERROR_MESSAGE",JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}

	}

	/**
	 * 初始化窗口
	 */
	public void init() {
		panel.setLayout(new BorderLayout());
		panel.add(panel2, BorderLayout.NORTH);
		panel.add(panel4, BorderLayout.CENTER);

		panel2.setLayout(new BorderLayout());
		panel2.add(panel3,BorderLayout.WEST);
		
		panel3.add(userNameLabel);
		userNameLabel.setText(" 用户名:");
		userNameLabel.setFont(new Font("宋体",Font.BOLD,16));
		panel3.add(userLabel);
		userLabel.setText(name);
		userLabel.setFont(new Font("宋体",Font.BOLD,16));
		panel3.add(portNameLabel);
		portNameLabel.setText(" 端口号:");
		portNameLabel.setFont(new Font("宋体",Font.BOLD,16));
		panel3.add(portLabel);
		portLabel.setText(port_str);
		//portLabel.setText("1111");
		
		portLabel.setFont(new Font("宋体",Font.BOLD,16));
		
		panel4.setLayout(new BorderLayout());
		panel4.add(panel5);

		panel5.setLayout(new BorderLayout());
		friendLabel.setText(" 好友列表");
		friendLabel.setFont(new Font("宋体",Font.BOLD,16));
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
	 * 上线监听器
	 */
	public void loginlistener(){
		try {
			System.out.println("上线监听器.......");
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());			
			Message msg = new Message();
			msg.setMessage(Key.encryptKey(name + ":已上线!", "KeyInquire"));
			msg.setFlag(1);
			msg.setKeyWords("KeyInquire");
			oos.writeObject(msg);
			System.out.println("上线监听器over....");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}			
	}
	
	
	/**
	 * list私聊监听器
	 */
	public void listactionlistener() {
		list.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				//2 => 双击
				if (e.getClickCount() == 2) {
					System.out.println("list私聊监听器....");
					//目标名称
					String name = (String) list.getSelectedValue();
					//名称找对应端口号
					String portStr = KeyDao.getPortByName(name);
					
					Integer port = Integer.valueOf(portStr);
					
					System.out.println("目标端口 port->" + port);
					System.out.println("本机端口 port->" + port_str);
					
					client = new ChatFramePrivate(socket,port,Integer.valueOf(port_str));
					
					//将端口号和对应的窗口存入map中
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
	 * 群聊监听器
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
	 * 离线监听器
	 */
	public void exitlistener(){
		frame.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub
				try {
					ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
					Message msg = new Message();
					msg.setMessage(Key.encryptKey(name + ":已下线!", "KeyInquire"));
					msg.setKeyWords("KeyInquire");
					msg.setFlag(4);
					oos.writeObject(msg);
					//删除数据库中的钥密
					KeyDao.delOneKey(name);
					//删除数据中姓名端口号键值对信息
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
					msg.setMessage(Key.encryptKey(name + ":已下线!", "KeyInquire"));
					msg.setKeyWords("KeyInquire");
					msg.setFlag(4);
					oos.writeObject(msg);
					
					//删除数据库中的钥密
					KeyDao.delOneKey(name);
					//删除数据中姓名端口号键值对信息
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
	 * 接受消息线程
	 */
	class ReceiveMsg extends Thread {

		public void run() {

			try {

				while (true) {
					System.out.println("好友列表读取进程.....");

					// 读取对象
					ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
					Object obj = ois.readObject();
					int flag = 0;
					Message  msg = null;
					Warning warning = null;
					
					//ois.readObj() 只能读取一次
					if(obj instanceof Message){
						msg = (Message) obj;
						//接受服务器发来的消息 flag
						flag = msg.getFlag();
					} else {
						warning = (Warning) obj;
						flag = warning.getFlag();
					}
					
					System.out.println("flag->" + flag);						
					
					/**
					 * 刷新窗口
					 */
					if(flag == 0){
						port_str = String.valueOf(msg.getAim_port());
						init();
						//生成客户端的密钥对,并存入数据库中
						KeyDao.insertKey(CreateKey.createKey(name, KeyDao.getKeyLen("KeyServer")));
					}
					/**
					 * 接受好友列表 if(flag == 1)接受列表
					 */
					else if(flag == 1){
						System.out.println("========================接受好友列表============================");
						DefaultListModel<String> listModel = new DefaultListModel();
						for (int i = 0; i < msg.getList().size(); i++) {
							String local_name = KeyDao.getNameByPort(String.valueOf(msg.getList().get(i)));
							listModel.addElement(local_name);
						}
						list.setModel(listModel);
						System.out.println("========================接受好友列表完毕！============================");
					}
					else if (flag == 2) {
						/**
						 * 群聊 if(flag == 2)群聊
						 */

						System.out.println("========================客户端接收群聊======================");
						if(c == null){
							c = new ChatFrameMore(socket);
						}
						// 群聊方法
						System.out.println(c);
						//消息 用KeyServer解密
						String dk_str = Key.decryptKey(msg.getMessage(), "KeyServer"); 
						c.chatmore(dk_str,Integer.valueOf(msg.getAim_port()));
						System.out.println("========================客户端接收群聊完毕======================");
					} 
					else if (flag == 3) {

						/**
						 * 私聊 if(flag == 3) 端口号:消息
						 */
						System.out.println("===============客户端接收私聊消息===============");
						//用KeyServer解密
						Integer port2 = Integer.valueOf(Key.decryptKey(msg.getAim_port(), "KeyServer"));
						System.out.println("port2->" + port2);
						ChatFramePrivate client = friendMap.get(port2);
						//用传来的之前的本机端口号解密
						String dk_str = Key.decryptKey(msg.getMessage(), KeyDao.getNameByPort(String.valueOf(port2)));
						
						if(client == null){
							client = new ChatFramePrivate(socket, port2,Integer.valueOf(port_str));
							//保存窗口
							friendMap.put(port2, client);
							client.chatmore(dk_str,port2);
						}else{
							client.chatmore(dk_str,port2);
						}
						System.out.println("===============客户端接收私聊消息完毕！===============");
					} else if (flag == 5) {
						/**
						 * 检测到攻击数据包!
						 */
						String dk_warnMsg = Key.decryptKey(warning.getWarnPort(), "KeyServer");
						JOptionPane.showMessageDialog(frame, "检测到攻击数据包!异常端口为: " + dk_warnMsg, "ERROR_MESSAGE",JOptionPane.ERROR_MESSAGE);
					}

				}
			} catch (IOException e) {
				
				if(frame != null) {
					//服务器断开连接..响应错误
					JOptionPane.showMessageDialog(frame, "与服务器断开连接！", "ERROR_MESSAGE",JOptionPane.ERROR_MESSAGE);
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
				JOptionPane.showMessageDialog(frame, "未知错误！", "ERROR_MESSAGE",JOptionPane.ERROR_MESSAGE);
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
