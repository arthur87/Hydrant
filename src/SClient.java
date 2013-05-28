import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;


public class SClient extends Thread{
	private Server server;
	private Socket socket;
	private BufferedReader in;
	private OutputStream out;
	private boolean runFlag = true;
	public SClient(Server server, Socket socket){
		this.server = server;
		this.socket = socket;
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF8"));
			out = socket.getOutputStream();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	public void run() {
		try {
			char c[] = new char[1];
			while(in.read(c, 0, 1) != -1 && runFlag){
				StringBuffer sb = new StringBuffer();
				while(c[0] != '\0') {
					sb.append(c[0]);
					in.read(c, 0, 1);
				}
				server.reserveMessage(sb.toString());
			}
		}catch(IOException e){
		}finally{
			stopClient();
		}
	}
	public void sendToClient(String s){
		try{
			byte[] b = s.getBytes("UTF8");
			out.write(b);
			out.flush();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	private void stopClient(){
		server.deleteClient(this);
		try{
			in.close();
			out.close();
			socket.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	public void dispose(){
		runFlag = false;
	}
}
