import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServerThread extends Thread {

	private static boolean run = true;

	@Override
	public void run() {
		try {
			String clientSentence;
			String response;
			ServerSocket welcomeSocket = new ServerSocket(1234);
			System.out.println("Server Socket started...");
			while (run) {
				try {
					Socket connectionSocket = welcomeSocket.accept();
					BufferedReader inFromClient = new BufferedReader(
							new InputStreamReader(
									connectionSocket.getInputStream()));
					DataOutputStream outToClient = new DataOutputStream(
							connectionSocket.getOutputStream());
					clientSentence = inFromClient.readLine();
					System.out.println("Received: " + clientSentence);
					response = clientSentence.toLowerCase() + '\n';
					System.out.println("Sending: " + response);
					outToClient.writeBytes(response);
					connectionSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			welcomeSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void interrupt() {
		run = false;
	}

}
