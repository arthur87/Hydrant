import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;


public class Client extends Thread{
	private Hydrant hydrant;
	private Socket socket;
	private BufferedReader in;
	private OutputStream out;

	public Client(Hydrant hydrant, String address, String port){
		try {
			this.hydrant = hydrant;
			socket = new Socket(InetAddress.getByName(address), Integer.valueOf(port));
			in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF8"));
			out = socket.getOutputStream();
			this.start();
		} catch (UnknownHostException e) {
			this.hydrant.printLogMessage(e.toString());
        } catch (IOException e) {
			this.hydrant.printLogMessage(e.toString());
        } catch(Exception e) {
        	this.hydrant.printLogMessage(e.toString());
        }
	}

	public void run() {
		try{
			char c[] = new char[1];
			while(in.read(c, 0, 1) != -1) {
				StringBuffer sb = new StringBuffer();
				while(c[0] != '\0') {
					sb.append(c[0]);
					in.read(c, 0, 1);
				}
				this.hydrant.printLogMessage(sb.toString());
			}
		}catch(IOException e){}
	}
	public void sendToServer(String s){
		try {
			out.write(s.getBytes("UTF8"));
			out.flush();
		} catch (UnsupportedEncodingException e) {
			// TODO 自動生成された catch ブロック
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
		}
	}

	public void close(){
		try {
			in.close();
			out.close();
			socket.close();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
		}
	}
}
