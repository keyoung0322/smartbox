import java.io.IOException;
import java.net.*;

public class Server extends RPI_cont{
	static int port = 8888;
	static ServerSocket server;

    	public static void main(String[] args) throws IOException {
		try {
                        server = new ServerSocket(port);

                        while(true){
								Socket client = server.accept();
                                InetAddress inet = client.getInetAddress();
                                String ip = inet.getHostAddress();
                                System.out.println(ip+"-�젒�냽�옄 諛쒓껄");

                                //���솕�슜 �벐�젅�뱶 �깮�꽦 諛� �냼耳�
                                Thread thread = new Thread(new LedThread(client));
                                System.out.println("start");
                                thread.start();
                        }
                } catch(IOException e) {
                        e.printStackTrace();
                }
	}
}