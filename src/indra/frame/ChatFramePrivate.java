package indra.frame;

import indra.Key.Key;
import indra.dao.KeyDao;
import indra.domain.Message;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;


public class ChatFramePrivate {
	/**
	 * ��������
	 */
	private JFrame c_frame = new JFrame("ChatFramePrivate");

	private JPanel c_panel = new JPanel();
	// �û���������
	private JPanel c_panel2 = new JPanel();
	// ������ϲ�
	private JPanel c_panel3 = new JPanel();
	// ������²�
	private JPanel c_panel4 = new JPanel();

	// �������
	private JPanel c_panel5 = new JPanel(); 
	// �رպͷ���
	private JPanel c_panel6 = new JPanel();

	private JTextArea area = new JTextArea(15, 49);
	private JTextArea area2 = new JTextArea(8, 49);

	private JLabel msgLabel = new JLabel();

	private JLabel nullLabel = new JLabel();
	private JLabel nullLabel3 = new JLabel();

	private JButton nullButton = new JButton();
	private JButton closeButton = new JButton("�ر�");
	private JButton privateButton = new JButton("����");
	private JButton sendButton = new JButton();

	private JScrollPane jp = new JScrollPane(area);

	private Socket socket;
	// private ServerSocket server;

	private final int c_width = 700;
	private final int c_height = 560;
	
	private ObjectOutputStream oos;
	private Integer aim_port;
	private Integer my_port;
	
	public ChatFramePrivate(Socket socket,Integer aim_port,Integer my_port) {

		this.aim_port = aim_port;
		this.my_port = my_port;
		this.socket = socket;		
		init();
		addactionlistener();
	}

	public void init() {

		c_panel2.setLayout(new GridLayout(2, 1, 2, 2));
		String p = KeyDao.getNameByPort(String.valueOf(aim_port));
		msgLabel.setText("��"+ p +"������");
		c_panel2.add(msgLabel);

		c_panel3.setLayout(new GridLayout(1, 3, 2, 2));
		c_panel3.add(c_panel2, BorderLayout.WEST);
		nullButton.setVisible(false);
		c_panel3.add(nullButton, BorderLayout.CENTER);

		nullLabel
				.setText("                                                                        ");
		c_panel3.add(nullLabel, BorderLayout.EAST);

		c_panel4.setLayout(new GridLayout(1, 4));
		c_panel4.add(nullLabel3);
		nullLabel3.setText("                                 ");
		sendButton.setVisible(false);
		c_panel4.add(sendButton);
		c_panel4.add(closeButton);
		c_panel4.add(privateButton);

		c_panel5.setLayout(new BorderLayout());
		area.setEditable(false);
		c_panel5.add(jp, BorderLayout.NORTH);
		c_panel5.add(c_panel6);
		c_panel5.add(area2, BorderLayout.SOUTH);

		c_panel.add(c_panel3, BorderLayout.NORTH);
		c_panel.add(c_panel5, BorderLayout.CENTER);
		c_panel.add(c_panel4, BorderLayout.SOUTH);

		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		int newWidth = (int) (screen.getWidth() - c_width) / 2;
		int newHeight = (int) (screen.getHeight() - c_height) / 2;

		c_frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		c_frame.add(c_panel);
		c_frame.setLocation(newWidth, newHeight);
		c_frame.setSize(c_width, c_height);
		c_frame.setVisible(true);
		c_frame.setResizable(false);
	}

	/**
	 * ˽�ĺ�Ⱥ�ķ���Ϣ
	 * ��Ϣ����ʽ  ��Ϣ
	 */
	public void addactionlistener() {

		privateButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
				String line = area2.getText();
				System.out.println(line);
				try {
					oos = new ObjectOutputStream(socket.getOutputStream());
					
					if(line==""){
						line = " ";
					}
					
					
					/** ��ʾ�����ĶԻ��� */
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date date = new Date();
					String d = sdf.format(date);
					//port ������
					area.append("����" + " " + d + "\n");
					area.append(line + "\n");
					////////////////////////////////////////////////////

					Message msg = new Message();
					msg.setFlag(3);  // flag == 3 ˽��
					
					System.out.println("my_port->" + my_port);
					
					//�ñ����˿ںż�����Ϣ
					String ek_str = Key.encryptKey(line, KeyDao.getNameByPort(String.valueOf(my_port)));
					msg.setMessage(ek_str);
					//��KeyInquire����  �����˿ںź�Ŀ�Ķ˿ں�
					String ek_MyPort = Key.encryptKey(String.valueOf(my_port), "KeyInquire");
					String ek_AimPort = Key.encryptKey(String.valueOf(aim_port),"KeyInquire");
					//Ŀ��������port
					msg.setAim_port(ek_AimPort);
					msg.setMy_port(ek_MyPort);
					msg.setKeyWords("KeyInquire");
					
					oos.writeObject(msg);
					oos.flush();				
					System.out.println("��Ϣ�������....");
					area2.setText("");
					
				} catch (IOException e1) {
					//�������Ͽ�����..��Ӧ����
					JOptionPane.showMessageDialog(c_frame, "��������Ͽ����ӣ�", "ERROR_MESSAGE",JOptionPane.ERROR_MESSAGE);
					KeyDao.delByName(KeyDao.getNameByPort(String.valueOf(my_port)));
					c_frame.dispose();
				}
			
			}
			
		});
		
		
		//�رհ�ť
		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				c_frame.dispose();
			}
		});
		
	}

	
	/**
	 * ��ʾ��Ϣ�ķ���
	 */
	public void chatmore(String msg,int port){
		//�յ�����Ϣmsg	
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
		Date date = new Date();
		String d = sdf.format(date);
		String aim_name = KeyDao.getNameByPort(String.valueOf(this.aim_port));
		area.append(aim_name + " " + d + "\n");
		area.append(msg + "\n");
	}
	
	public static void main(String[] args) {
		//ChatFramePrivate c = new ChatFramePrivate();
	}


	
}
