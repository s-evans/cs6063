
abstract class failureBase {
	protected boolean running = false;
	protected Thread myThread;
	
	abstract class innerThread extends Thread {
		protected failureBase outer;
		
		public innerThread(failureBase base) {
			outer = base;
		}
		
		protected boolean keepGoing () {
			return outer.isRunning();
		}
		
		abstract void main ();

		public void run () {
			while ( keepGoing() ) {
				main();
			}
		}
	}
	
	protected failureBase () {
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
