import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.*;
public class ChatClient extends Frame{
	
	Socket s;
	TextField tfTxt = new TextField();
	TextArea taContent = new TextArea();
	DataOutputStream dos = null;
	DataInputStream dis = null;
	private boolean bConnected = false;
	
	Thread recv = new Thread(new Received());

	public static void main(String[] args) {

		new ChatClient().launchFrame();
	}
	
	public void launchFrame() {
		this.setTitle("Chat");
		setLocation(400, 300);
		this.setSize(300, 300);
		
		add(tfTxt, BorderLayout.SOUTH);
		add(taContent, BorderLayout.NORTH);
		pack();
		
		this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
//				System.out.println("I'll exit");
				
				disconnect();
				System.exit(0);
			}
			
		});
		
		/*添加监听器*/
		tfTxt.addActionListener(new TFListener());
		/*显示出来*/
		setVisible(true);
		connectTo();
		recv.start();
	}
	
	public void connectTo() {
		try {
			s = new Socket("127.0.0.1", 8888) ;
			/*连接的时候直接把输入输出流初始化，然后直接用*/
			dos = new DataOutputStream(s.getOutputStream());
			dis = new DataInputStream(s.getInputStream());
			bConnected = true;
		} catch(UnknownHostException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("connected");
	}
	
	public void disconnect() {
		try {
			/*bConnected = false;
			recv.join(5000);
			recv.stop();*/
			dos.close();
			dis.close();
			s.close();
		} /*catch (InterruptedException e) {
			e.printStackTrace();
		}*/catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	
	private class TFListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			String str = tfTxt.getText().trim();//把输入的字符串提取出来
//			taContent.setText(str);//把提取的内容放到area里面去
			tfTxt.setText(" ");//发送之后把刚刚输入的字符串清除
			/*把字符串发送到服务器*/
			try {
				dos.writeUTF(str);
				dos.flush();
//				dos.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
	}

	private class Received implements Runnable{

		@Override
		public void run() {
			try {
				while(bConnected) {
					String str = dis.readUTF();
					/*显示在TextArea区域*/
					taContent.setText(taContent.getText() + str + '\n');
				}
			}catch(SocketException e) {
				System.out.println("退出了");
			}catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
}
