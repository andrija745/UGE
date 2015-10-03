import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TCPPeriodicTask extends TimerTask {
	static Logger log = Logger.getLogger(TCPPeriodicTask.class.getName());

	private static boolean run = true;
	private static ExecutorService executor;
	private static Queue<Future<String>> queue;

	public TCPPeriodicTask() {
		queue = new LinkedList<Future<String>>();
	}

	@Override
	public void run() {

		if (run) {
			executor = Executors.newSingleThreadExecutor();
			// try {
			String second = String.valueOf(Calendar.getInstance()
					.getTimeInMillis() / 1000);
			Future<String> future = executor.submit(new TCPExecutor(second));
			queue.add(future);

			log.info("Started..");
			Iterator<Future<String>> it = queue.iterator();
			while (it.hasNext()) {
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
			// log.info("Waiting one minute...");
			// Thread.sleep(60000);
			// } catch (InterruptedException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
		}

		executor.shutdownNow();
	}

	public void interrupt() {
		run = false;
	}

}
