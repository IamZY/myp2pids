package indra.frame;

import indra.Key.CreateKey;
import indra.dao.KeyDao;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

public class KeyGen {

	private JFrame frame;
	private JFrame keyServer;

	// ���ô��ڵĳ���
	Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
	private final int WIDTH = 300;
	private final int HEIGHT = 150;

	final int newWidth = (int) ((screen.getWidth() - WIDTH) / 2);
	final int newHeight = (int) ((screen.getHeight() - HEIGHT) / 2);


	/**
	 * Create the application.
	 */
	public KeyGen(JFrame keyServer) {
		this.keyServer = keyServer;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setLocation(newWidth, newHeight);
		frame.setSize(WIDTH, HEIGHT);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JButton btnkeygen = new JButton("����KeyGen");
		btnkeygen.setBounds(89, 64, 115, 23);
		frame.getContentPane().add(btnkeygen);
		
		JScrollBar scrollBar = new JScrollBar();
		//���õ�λ����  rsa���ܳ�ʼ��������ֻ����64�ı���
		scrollBar.setUnitIncrement(64);
		scrollBar.setMinimum(512);
		scrollBar.setMaximum(1024);
		scrollBar.setOrientation(JScrollBar.HORIZONTAL);
		scrollBar.setBounds(130, 18, 72, 23);
		
		
		frame.getContentPane().add(scrollBar);
		
		JLabel label = new JLabel("���볤��");
		label.setFont(new Font("����", Font.BOLD, 14));
		label.setBounds(35, 18, 72, 23);
		frame.getContentPane().add(label);
		
		final JLabel label_1 = new JLabel("512");
		label_1.setBounds(220, 18, 54, 23);
		frame.getContentPane().add(label_1);
		
		scrollBar.addAdjustmentListener(new AdjustmentListener() {
			
			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				// TODO Auto-generated method stub
				label_1.setText(String.valueOf(e.getValue()));
			}
		});
		
		
		btnkeygen.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				boolean bk_1 = KeyDao.insertKey(CreateKey.createKey("KeyServer", Integer.valueOf(label_1.getText())));
				boolean bk_2 = KeyDao.insertKey(CreateKey.createKey("KeyInquire", Integer.valueOf(label_1.getText())));
								
				if(bk_1 && bk_2){
					JOptionPane.showMessageDialog(keyServer, "������Կ�Գɹ�!", "OK_OPTION",JOptionPane.WARNING_MESSAGE);
				}else {
					JOptionPane.showMessageDialog(keyServer, "������Կ��ʧ��", "ERROR_MESSAGE",JOptionPane.ERROR_MESSAGE);
				}
							
				frame.setVisible(false);		
			}
			
		});
		
	}
}
