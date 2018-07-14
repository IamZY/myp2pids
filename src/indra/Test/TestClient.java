package indra.Test;

import indra.domain.Message;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


public class TestClient {

	private JFrame f = new JFrame("Client");

	private JPanel p = new JPanel();

	private JTextArea receiver = new JTextArea(5, 16);
	private JTextArea sender = new JTextArea(5, 16);

	private JScrollPane jPane = new JScrollPane(receiver);
	
	private JButton btn = new JButton("发送");

	// 设置窗口的长宽
	Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
	private final int WIDTH = 200;
	private final int HEIGHT = 250;

	final int newWidth = (int) ((screen.getWidth() - WIDTH) / 2);
	final int newHeight = (int) ((screen.getHeight() - HEIGHT) / 2);
	
	private Socket socket;
	

	public TestClient() {

		try {
			socket = new Socket("127.0.0.1", 9999);
		
			init();
			addactionlisteners();
			addWindowListener();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

	}

	public void init() {

		p.add(jPane);
		p.add(sender);
		p.add(btn);

		f.add(p);

		f.setLocation(newWidth, newHeight);
		f.setSize(WIDTH, HEIGHT);
		f.setResizable(false);
		f.setVisible(true);
		
	}
	
	public void addWindowListener(){
		f.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub
				f.dispose();
				try {
					socket.close();
					System.exit(0);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
			
		});
	}

	/**
	 * 发送消息
	 */
	public void addactionlisteners() {
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String line = sender.getText();
				try {
					ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
					Message msg = new Message();
					msg.setMessage(line);
					oos.writeObject(msg);
					oos.flush();
					sender.setText("");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}

		});

	}

	public static void main(String[] args) {
		TestClient c = new TestClient();

	}

}
