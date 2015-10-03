import java.util.Scanner;
import java.util.Timer;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Logger;

public class TCPClient {

	static Logger log = Logger.getLogger(TCPClient.class.getName());
	private static TCPPeriodicTask periodicTask;
	private static ScheduledExecutorService scheduledExecutor;

	public static void main(String[] args) {

		periodicTask = new TCPPeriodicTask();
		// scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
		// scheduledExecutor.scheduleAtFixedRate(periodicTask, 0, 10,
		// TimeUnit.SECONDS);
		Timer timer = new Timer();
		timer.schedule(periodicTask, 0, 10 * 1000);

		// listen to console input for stop
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

		// stop all
		periodicTask.interrupt();
		scheduledExecutor.shutdown();
		sc.close();
	}
}
