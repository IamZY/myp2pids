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
	
	
	// 设置窗口的长宽
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

			// 初始化界面
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
				//服务器关闭后，清空数据库
				KeyDao.deleteKey();
				try {
					socket.close();
					//关闭进程
					System.exit(0);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			
			}
			
		});
		
	}

	/**
	 * 生成keyGen
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
	 * 收消息的线程
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
					
				System.out.println("服务器接受消息线程....");
				
				try {

					ois = new ObjectInputStream(socket.getInputStream());
					
					
					msg = (Message) ois.readObject();
					// 获得每个客户端的端口号
					InetSocketAddress address = (InetSocketAddress) socket.getRemoteSocketAddress();
					int local_port = address.getPort();
					
					this_port = local_port;
					
					//所有的消息之前都用了KeyInquire加密的 没有加密的消息一律视为异常数据包
					//可以解密的为合法数据包
					if(msg.getKeyWords().equals("KeyInquire")){
						//得到本机端口号
						System.out.println("..port->" + local_port);
						System.out.println("..flag->" + msg.getFlag());
						
						//解密传来的消息
						//String dk_str = Key.decryptKey(msg.getMessage(), msg.getKeyWords());
						//msg.setMessage(dk_str);
						int flag = msg.getFlag();

						if(flag == 1){
							//用户上线提醒
							//接受客户端传来的名字
							String dk_str = Key.decryptKey(msg.getMessage(), msg.getKeyWords());
							msg.setMessage(dk_str);
							String[] msgs = msg.getMessage().split(":");
							String name = msgs[0];
							System.out.println(name + "<-->" + local_port);
							
							//name 和 port 键值对加入数据库
							KeyDao.insertPort(name, String.valueOf(local_port));
							//向服务器接受框写值
							receiver.append(msg.getMessage() + "\n");
							
							// 再一次刷新好友列表
							// 遍历socket 将好友列表发给每个客户端
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
							System.out.println("======================服务器接收群聊消息============================");
							// 群聊
							//解密传来的消息
							String dk_str = Key.decryptKey(msg.getMessage(), msg.getKeyWords());
							msg.setMessage(dk_str);
							// 通过port遍历socket  
							Set set = socketMap.keySet();
							Iterator ite = set.iterator();
							
							while (ite.hasNext()) {
								Integer port = (Integer) ite.next();
								//找到所有的socket
								Socket socket = socketMap.get(port);
								Message msg_2 = new Message();
								//用KeyServer加密
								String ek_str_2 = Key.encryptKey(msg.getMessage(), "KeyServer");
								msg_2.setMessage(ek_str_2);
								msg_2.setAim_port(String.valueOf(local_port));
								msg_2.setFlag(2);
								ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
								oos.writeObject(msg_2);
								oos.flush();
							}
							System.out.println("======================服务器接收群聊消息完毕！============================");
						} else if (flag == 3) {
							// 私聊
							System.out.println("===============服务器接收私聊信息==================");
							
							// 用KeyInquire解密传来的端口号
							String dk_AimPort = Key.decryptKey(msg.getAim_port(), msg.getKeyWords());
							System.out.println("目标端口－＞" + dk_AimPort);
							String dk_MyPort = Key.decryptKey(msg.getMy_port(), msg.getKeyWords());
							System.out.println("本机端口－＞" + dk_MyPort);
							
							// 找对应的端口号 socket
							Socket socket = socketMap.get(Integer.valueOf(dk_AimPort));
							
							System.out.println(socket);
							
							Message msg3 = new Message();
							//消息不动 再次传递
							msg3.setMessage(msg.getMessage());
							//用KeyServer加密 端口
							String ek_Aimport = Key.encryptKey(dk_AimPort, "KeyServer");
							String ek_Myport = Key.encryptKey(dk_MyPort, "KeyServer");
							
							/** 端口转换的问题 原来的本机端口=>现在的目的端口  原来的目的端口=>现在的本机端口*/
							//本机的端口号
							msg3.setMy_port(ek_Aimport);
							//目标端口号
							msg3.setAim_port(ek_Myport);

							msg3.setFlag(3);
							ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
							oos.writeObject(msg3);
							oos.flush();
							System.out.println("===============服务器接收私聊信息完毕！================");
						}else if(flag == 4){
							System.out.println("=================刷新好友列表======================");
							String dk_str = Key.decryptKey(msg.getMessage(), msg.getKeyWords());
							msg.setMessage(dk_str);
							
							receiver.append(msg.getMessage() + '\n');
							local_port = address.getPort();
							System.out.println(local_port);
							//删除关闭客户端的socket
							socketMap.remove(local_port);
							arrayList.remove((Integer)local_port);
							System.out.println("arrayList->" + arrayList.toString());
							
							if(arrayList.size() != 0) {
								//遍历好友列表
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
							
							//将已经关闭的客户端的读取进程关闭
							ois.close();
							
							System.out.println("=================刷新好友列表完毕！======================");
							
						}

					}else {
						System.err.println("error!!");
					}
					
				} catch (IOException e) {
					e.printStackTrace();
					//当客户端断开连接的时候跳出循环
					//跳出循环
					break;
					
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NullPointerException e) {
					System.err.println("catch error!!");
					//检测到入侵信息
					//...
					// 遍历好友列表
					// 通过port遍历socket  
					Set set = socketMap.keySet();
					Iterator ite = set.iterator();
					while (ite.hasNext()) {
						Integer port = (Integer) ite.next();
						//找到所有的socket
						Socket socket = socketMap.get(port);
						Warning warnMsg = new Warning();
						//用KeyServer加密
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
	 * 接受连接的线程
	 * 
	 * @author Administrator
	 * 
	 */

	class AcceptConnectionThread extends Thread {
		
		public void run() {

			while (true) {

				try {
					
					System.out.println("接受链接消息....");
					
					socket = server.accept();

					// 获得每个客户端的端口号
					InetSocketAddress address = (InetSocketAddress) socket.getRemoteSocketAddress();
					int port = address.getPort();
			
					arrayList.add(port);
					System.out.println("arr=>" + arrayList.toString());

					// 将每个客户端的端口号和socket存如map中
					socketMap.put(port, socket);

					//客户端显示端口号
					ObjectOutputStream oos1 = new ObjectOutputStream(socket.getOutputStream());
					Message user = new Message();
					user.setFlag(0);
					user.setAim_port(String.valueOf(port));
					oos1.writeObject(user);
					oos1.flush();
					
					// 刷新好友列表
					// 遍历socket 将好友列表发给每个客户端
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
