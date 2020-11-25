import java.io.*;
import java.net.*;

public class LedThread extends RPI_cont implements Runnable {
	private Socket clientSock;
	BufferedReader buffer;

	public LedThread(Socket socket) {
		this.clientSock = socket;

		try {
			buffer = new BufferedReader(new InputStreamReader((clientSock.getInputStream())));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		//System.out.println("thread start!");
		while(true) {
			String msg = listen();
			if(msg.compareTo("LedOff") == 0) {
				RPI_cont.led.low();
				System.out.println("ledoff");
			}
		}
	}

	public String listen() {
		String msg="";
		try {
			msg= buffer.readLine();
			//System.out.println("msg:"+msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return msg;
	}
}
