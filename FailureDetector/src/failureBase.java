
public class failureBase {
	protected boolean running = false;
	protected Thread myThread;
	
	protected class innerThread extends Thread {
		protected failureBase outer;
		
		public innerThread(failureBase base) {
			outer = base;
		}
		
		protected boolean keepGoing () {
			return outer.isRunning();
		}
		
		protected void main () {
			// NOTE: Template method. To be overridden in subclass.
			System.out.print("\noverride me");
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
			}
		}

		public void run () {
			while ( keepGoing() ) {
				main();
			}
			return;
		}
	}
	
	public failureBase () {
		myThread = new innerThread(this);
	}
	
	public void startRunning () {
		myThread.start();
		running = true;
	}
	
	public boolean isRunning () {
		return running;
	}
	
	public void stopRunning () {
		running = false;
	}
}
