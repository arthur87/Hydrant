import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;


public class Server extends Thread{
	private Hydrant hydrant;
	private int port;
	private Vector clist;
	private ServerSocket serverSocket;
	private Socket socket;
	public Server(Hydrant hydrant, String port){
		this.hydrant = hydrant;
		this.port = Integer.valueOf(port);
		this.clist = new Vector();
	}
	public void run() {
		try{
			serverSocket = new ServerSocket(this.port);
			while(true){
				socket = serverSocket.accept();
				SClient c = new SClient(this, socket);
				clist.add(c);
				c.start();
			}
		}catch(IOException e){
			e.printStackTrace();
			stopServer();
		}
	}
	private void stopServer(){
		try {
			serverSocket.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	public void deleteClient(SClient c){
		clist.remove(c);
	}
	public void dispose(){
		if(clist != null){
			Enumeration e = clist.elements();
			while(e.hasMoreElements()){
				SClient c = (SClient)(e.nextElement());
				c.dispose();
			}
		}
	}
	public void sendToAllClient(String s){
		if(clist != null){
			Enumeration e = clist.elements();
			while(e.hasMoreElements()){
				SClient c = (SClient)(e.nextElement());
				c.sendToClient(s + '\0');
			}			
		}
	}
	public void reserveMessage(String s){
		hydrant.printLogMessage(s);
	}
}
