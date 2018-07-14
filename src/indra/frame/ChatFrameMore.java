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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

public class ChatFrameMore {
	/**
	 * 窗口属性
	 */
	private JFrame c_frame = new JFrame("ChatFrameMore");

	private JPanel c_panel = new JPanel();
	// 用户名和在线
	private JPanel c_panel2 = new JPanel();
	// 聊天框上部
	private JPanel c_panel3 = new JPanel();
	// 聊天框下部
	private JPanel c_panel4 = new JPanel();

	// 输入输出
	private JPanel c_panel5 = new JPanel(); 
	// 关闭和发送
	private JPanel c_panel6 = new JPanel();

	private JTextArea area = new JTextArea(15, 49);
	private JTextArea area2 = new JTextArea(8, 49);

	private JLabel msgLabel2 = new JLabel();

	private JLabel nullLabel = new JLabel();
	private JLabel nullLabel3 = new JLabel();

	private JButton nullButton = new JButton();
	private JButton closeButton = new JButton("关闭");
	private JButton privateButton = new JButton();
	private JButton sendButton = new JButton("发送");

	private JScrollPane jp = new JScrollPane(area);

	private Socket socket;

	private final int c_width = 700;
	private final int c_height = 560;
	
	private ObjectOutputStream oos;
	
	
	public ChatFrameMore(Socket socket) {

		this.socket = socket;
		init();
		addactionlistener();
	}

	public void init() {
		c_panel2.setLayout(new GridLayout(2, 1, 2, 2));
		msgLabel2.setText("电脑在线");
		c_panel2.add(msgLabel2);
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
		c_panel4.add(privateButton);
		privateButton.setVisible(false);
		c_panel4.add(closeButton);
		c_panel4.add(sendButton);

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
	 * 私聊和群聊发消息
	 * 消息的形式  消息
	 */
	public void addactionlistener() {

		sendButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				System.out.println("--------------");
				String line = area2.getText();

				try {
					oos = new ObjectOutputStream(socket.getOutputStream());
					Message msg = new Message();
					//用KeyInquire加密
					String ek_str = Key.encryptKey(line, "KeyInquire"); 
					msg.setFlag(2);
					msg.setMessage(ek_str);
					msg.setKeyWords("KeyInquire");
					oos.writeObject(msg);
					oos.flush();
					area2.setText("");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		
		//关闭按钮
		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				c_frame.dispose();
			}
		});
		
	}

	/**
	 * 显示消息的方法
	 */
	public void chatmore(String msg,int port){
		//收到的消息msg	
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
		Date date = new Date();
		String d = sdf.format(date);
		String name = KeyDao.getNameByPort(String.valueOf(port));
		area.append(name + " " + d + "\n");
		area.append(msg + "\n");
	}
	
	public static void main(String[] args) {
		//ChatFrame_Client c = new ChatFrame_Client();
	}


	
}
