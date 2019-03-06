import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
	
	List<Client> clients = new ArrayList<Client>();//����һ��������

	public static void main(String[] args) {
		new ChatServer().startChat();
	}

	/*Main�����Ǿ�̬����������new�Ǿ�̬�߳���Ķ���
	 * ���Զ���һ���Ǿ�̬�ķ������main�����еĹ�����Ȼ����main������ʹ�ø÷Ǿ�̬����*/
	public void startChat() {
		boolean flag = false;
		ServerSocket ss = null;
		try {
			ss  = new ServerSocket(8888);
			flag = true;
		} catch(BindException e){
			System.out.println("�˿�ʹ����...");
			System.exit(0);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		try {	
		   while(flag) {
				Socket s = ss.accept();
				Client c = new Client(s);
				new Thread(c).start();
				clients.add(c);
				System.out.println("a client connected!");			
			}
		}catch(IOException e) {
			e.printStackTrace();
		}finally {
			try {
				ss.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public class Client implements Runnable{

		private Socket s;
		private DataInputStream dis = null;
		private DataOutputStream dos = null;
		private boolean flags = false;
		
		public Client (Socket s) {
			this.s = s;
			try {
				dis = new DataInputStream(s.getInputStream());
				dos = new DataOutputStream(s.getOutputStream());
				flags = true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public void send(String str) {
			try {
				dos.writeUTF(str);
			} catch (IOException e) {
				/*��һ���ͻ����˳��󣬷�����Ҫ��List��������Ƴ�*/
				clients.remove(this);
				System.out.println("��ʾ���Է��˳���");
			}
		}
		
		@Override
		public void run() {
			/*����ͻ����������֮�����Ϣ����*/
			try {
				while(flags) {
					String str = dis.readUTF();
					System.out.println(str);
					//Ⱥ���������ͻ���
					for(int i = 0; i < clients.size(); i++) {
						Client c = clients.get(i);
						c.send(str);
					}
				}
			}catch(EOFException e) {
				System.out.println("Client closed");
			}catch(IOException e){
				e.printStackTrace();	
			}finally {
				try {
					if(s != null) s.close();
					if(dis != null) dis.close();
					if(dos != null) dos.close();
				}catch(IOException e) {
					e.printStackTrace();
				}
			}
			
		}
		
	}
}
