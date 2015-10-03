import java.util.Scanner;

public class TCPServer {

	public static void main(String argv[]) throws Exception {
		TCPServerThread thread = new TCPServerThread();
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
