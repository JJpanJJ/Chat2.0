import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
	
	List<Client> clients = new ArrayList<Client>();//定义一个集合类

	public static void main(String[] args) {
		new ChatServer().startChat();
	}

	/*Main方法是静态方法，不能new非静态线程类的对象，
	 * 所以定义一个非静态的方法完成main方法中的工作，然后在main方法中使用该非静态方法*/
	public void startChat() {
		boolean flag = false;
		ServerSocket ss = null;
		try {
			ss  = new ServerSocket(8888);
			flag = true;
		} catch(BindException e){
			System.out.println("端口使用中...");
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
				/*当一个客户端退出后，服务器要从List里面把它移除*/
				clients.remove(this);
				System.out.println("提示：对方退出了");
			}
		}
		
		@Override
		public void run() {
			/*处理客户端与服务器之间的消息传送*/
			try {
				while(flags) {
					String str = dis.readUTF();
					System.out.println(str);
					//群发给其他客户端
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
