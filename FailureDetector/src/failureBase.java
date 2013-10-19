
abstract class failureBase {
	protected Thread myThread;
	
	abstract class innerThread extends Thread {
		protected failureBase outer;
		
		public innerThread(failureBase base) {
			outer = base;
		}

		abstract void main ();

		public void run () {
			while ( true ) {
				main();
			}
		}
	}
	
	protected failureBase () {
	}
	
	public void startRunning () {
		myThread.start();
	}
}
