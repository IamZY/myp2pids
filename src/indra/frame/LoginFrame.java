package indra.frame;

import indra.dao.KeyDao;
import indra.dao.LoginDao;
import indra.domain.User;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

import java.awt.BorderLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.Color;

public class LoginFrame {

	private JFrame frame;
	private JTextField userField;
	private JPasswordField pwdField;
	// 对话框
	private JDialog dialog = new JDialog(frame, false);
	private JLabel msglLabel = new JLabel();

	private JButton loginButton = new JButton("安全登录");
	
	// 设置窗口的长宽
	Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
	private final int WIDTH = 350;
	private final int HEIGHT = 250;

	final int newWidth = (int) ((screen.getWidth() - WIDTH) / 2);
	final int newHeight = (int) ((screen.getHeight() - HEIGHT) / 2);

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LoginFrame window = new LoginFrame();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public LoginFrame() {
		initialize();
		isempty();
		login();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setTitle("用户登录");
		frame.setLocation(newWidth, newHeight);
		frame.setSize(WIDTH, HEIGHT);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JLabel label = new JLabel("用户登录");
		label.setFont(new Font("宋体", Font.BOLD, 20));

		JLabel label_1 = new JLabel("用户名");
		label_1.setFont(new Font("宋体", Font.BOLD, 14));

		JLabel label_2 = new JLabel("密码");
		label_2.setFont(new Font("宋体", Font.BOLD, 14));

		userField = new JTextField();
		userField.setForeground(Color.LIGHT_GRAY);
		userField.setText("用户名");
		userField.setColumns(11);

		pwdField = new JPasswordField();
		pwdField.setForeground(Color.LIGHT_GRAY);
		pwdField.setText("********");
		pwdField.setColumns(20);

		//loginButton = new JButton("登录");
		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout
				.setHorizontalGroup(groupLayout
						.createParallelGroup(Alignment.LEADING)
						.addGroup(
								groupLayout
										.createSequentialGroup()
										.addGroup(
												groupLayout
														.createParallelGroup(
																Alignment.LEADING)
														.addGroup(
																groupLayout
																		.createSequentialGroup()
																		.addGap(38)
																		.addGroup(
																				groupLayout
																						.createParallelGroup(
																								Alignment.TRAILING)
																						.addComponent(
																								label_2,
																								GroupLayout.PREFERRED_SIZE,
																								36,
																								GroupLayout.PREFERRED_SIZE)
																						.addComponent(
																								label_1))
																		.addGap(26)
																		.addGroup(
																				groupLayout
																						.createParallelGroup(
																								Alignment.LEADING)
																						.addComponent(
																								pwdField,
																								GroupLayout.PREFERRED_SIZE,
																								126,
																								GroupLayout.PREFERRED_SIZE)
																						.addComponent(
																								userField,
																								GroupLayout.PREFERRED_SIZE,
																								GroupLayout.DEFAULT_SIZE,
																								GroupLayout.PREFERRED_SIZE)
																						.addComponent(
																								label,
																								GroupLayout.PREFERRED_SIZE,
																								84,
																								GroupLayout.PREFERRED_SIZE)))
														.addGroup(
																groupLayout
																		.createSequentialGroup()
																		.addGap(124)
																		.addComponent(
																				loginButton)))
										.addContainerGap(73, Short.MAX_VALUE)));
		groupLayout
				.setVerticalGroup(groupLayout
						.createParallelGroup(Alignment.LEADING)
						.addGroup(
								groupLayout
										.createSequentialGroup()
										.addGap(18)
										.addComponent(label)
										.addGap(36)
										.addGroup(
												groupLayout
														.createParallelGroup(
																Alignment.BASELINE)
														.addComponent(label_1)
														.addComponent(
																userField,
																GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE))
										.addGap(27)
										.addGroup(
												groupLayout
														.createParallelGroup(
																Alignment.BASELINE)
														.addComponent(label_2)
														.addComponent(
																pwdField,
																GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE))
										.addGap(18).addComponent(loginButton)
										.addContainerGap(26, Short.MAX_VALUE)));
		frame.getContentPane().setLayout(groupLayout);
	}

	/**
	 * 判断Textfiled是否为空
	 */
	public void isempty() {
		userField.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (userField.getText().equals("用户名")) {
					userField.setText("");
				}
			}

		});

		pwdField.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (pwdField.getText().equals("********")) {
					pwdField.setText("");
				}

			}
		});
	}

	/**
	 * 登录事件
	 */
	public void login() {
		loginButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub

				String id = userField.getText();
				String pwd = pwdField.getText();

				User user = new User(id, pwd);
				LoginDao login = new LoginDao();
				
				if(!KeyDao.isKeyGenExit()) {
					JOptionPane.showMessageDialog(null, "生成钥密失败！", "ERROR_MESSAGE", JOptionPane.ERROR_MESSAGE);
				}else {
					if (login.verify(user)) {
						new FriendListFrame(userField.getText());
						frame.setVisible(false);
					} else {
						msglLabel.setText("               用户名或密码错误请重新登录");
						dialog.getContentPane().add(msglLabel);
						dialog.setSize(250, 150);
						dialog.setLocation(((int) screen.getWidth() - WIDTH) / 2,
								((int) screen.getHeight() - HEIGHT) / 2);
						dialog.setVisible(true);
						dialog.setResizable(false);
					}
				}
				
				

				

			}
		});
	}
}
