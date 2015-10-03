import java.util.Scanner;
import java.util.logging.Logger;

public class TCPClient {

	static Logger log = Logger.getLogger(TCPClient.class.getName());

	public static void main(String[] args) {
		TCPClientThread thread = new TCPClientThread();
		thread.start();

		Scanner sc = new Scanner(System.in);
		String line = sc.nextLine();
		while (!line.equalsIgnoreCase("stop")) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			line = sc.nextLine();
		}
		thread.interrupt();
		sc.close();
	}
}
