import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Calendar;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TCPExecutor implements Callable<String> {

	static Logger log = Logger.getLogger(TCPExecutor.class.getName());

	private String cycle = "0000";

	public TCPExecutor(String cycle) {
		this.cycle = cycle;
	}

	@Override
	public String call() throws Exception {

		try {
			Socket skt = new Socket("172.24.26.13", 1234);

			log.info("Povezan na " + skt.getRemoteSocketAddress());

			BufferedReader in = new BufferedReader(new InputStreamReader(skt.getInputStream()));
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(skt.getOutputStream()));

			Thread.sleep(2000);
			log.info("Proslo 2 sekundi, prosledjujem IM poruku\n");
			out.write("IM" + cycle);
			// out.close();
			out.flush();

			long start = Calendar.getInstance().getTimeInMillis();
			log.info("Received string: \n");
			log.info(in.readLine()); // Read one line and output it on screen

			log.info("\nPrimljeno\n");
			log.info("Vreme potrebno za prijem paketa: " + (Calendar.getInstance().getTimeInMillis() - start) + "ms");
			in.close();
			skt.close();

		} catch (Exception e) {
			log.log(Level.SEVERE, "Connection Failed", e);
		}

		return "OK";
	}

}
