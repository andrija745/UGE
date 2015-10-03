import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TCPClient {

	static Logger log = Logger.getLogger(TCPClient.class.getName());

	public static void main(String[] args) {
		
		
		ExecutorService executor = Executors.newSingleThreadExecutor();
		Future<String> future = executor.submit(new TCPExecutor("0045"));

		try {
			log.info("Started..");
			log.info(future.get(20, TimeUnit.SECONDS));
			log.info("Finished!");
		} catch (TimeoutException e) {
			future.cancel(true);
			log.log(Level.SEVERE, "Terminated - Time Out!", e);
		} catch (InterruptedException e) {
			future.cancel(true);
			log.log(Level.SEVERE, "Terminated - Interupt!", e);
		} catch (ExecutionException e) {
			future.cancel(true);
			log.log(Level.SEVERE, "Terminated - Execution!", e);
		}

		executor.shutdownNow();
	}
}
