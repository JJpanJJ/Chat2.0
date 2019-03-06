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
		
		/*��Ӽ�����*/
		tfTxt.addActionListener(new TFListener());
		/*��ʾ����*/
		setVisible(true);
		connectTo();
		recv.start();
	}
	
	public void connectTo() {
		try {
			s = new Socket("127.0.0.1", 8888) ;
			/*���ӵ�ʱ��ֱ�Ӱ������������ʼ����Ȼ��ֱ����*/
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
			String str = tfTxt.getText().trim();//��������ַ�����ȡ����
//			taContent.setText(str);//����ȡ�����ݷŵ�area����ȥ
			tfTxt.setText(" ");//����֮��Ѹո�������ַ������
			/*���ַ������͵�������*/
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
					/*��ʾ��TextArea����*/
					taContent.setText(taContent.getText() + str + '\n');
				}
			}catch(SocketException e) {
				System.out.println("�˳���");
			}catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
}
