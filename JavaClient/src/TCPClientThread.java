import java.util.Calendar;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TCPClientThread extends Thread {
	static Logger log = Logger.getLogger(TCPClientThread.class.getName());

	private static boolean run = true;

	@Override
	public void run() {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		Queue<Future<String>> queue = new LinkedList<Future<String>>();

		while (run) {
			try {
				String second = String.valueOf(Calendar.getInstance()
						.getTimeInMillis() / 1000);
				Future<String> future = executor
						.submit(new TCPExecutor(second));
				queue.add(future);

				log.info("Started..");
				while (!queue.isEmpty()) {
					future = queue.poll();
					try {
						log.info(future.get(20, TimeUnit.SECONDS));
					} catch (TimeoutException e) {
						future.cancel(true);
						queue.add(future);
						log.log(Level.SEVERE, "Terminated - Time Out!", e);
					} catch (InterruptedException e) {
						future.cancel(true);
						queue.add(future);
						log.log(Level.SEVERE, "Terminated - Interupt!", e);
					} catch (ExecutionException e) {
						future.cancel(true);
						queue.add(future);
						log.log(Level.SEVERE, "Terminated - Execution!", e);
					}
				}
				log.info("Finished!");
				log.info("Waiting one minute...");
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		executor.shutdownNow();
	}

	public void interrupt() {
		run = false;
	}
}
