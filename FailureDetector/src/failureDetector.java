import java.util.Date;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.UUID;

public class failureDetector extends failureBase {
	protected int period;
	protected int timeout;

	public class innerThread extends failureBase.innerThread {
		protected failureDetector outer;

		public innerThread(failureDetector base) throws Exception {
			super(base);
			outer = base;
		}
	
		protected void main () {
			// Acquire the listMutex
			try {
				main.listMutex.acquire();
			} catch ( InterruptedException e ) {
				e.printStackTrace();
			}

			Date now = new Date();
			
			// Iterate over the list
			Iterator<Entry<UUID, Record>> it = main.processList.entrySet().iterator();
		    while (it.hasNext()) {
		    	Entry<UUID, Record> entry = it.next();
		    	UUID uuid = entry.getKey();
		    	Record r = entry.getValue();
		    	
		    	if ( main.bDebug ) {
			        System.out.printf("\nInspecting record");
			        System.out.printf("\n\tUUID: %s", uuid.toString());
			        System.out.printf("\n\tDate: %s", r.getTime().toString());
		    	}
		    	
		    	// Check for timed out processes
		        if ( now.getTime() - r.getTime().getTime() > (timeout + period) * 1000 ) {
		        	System.err.printf("\nFailure detected;");
		        	System.err.printf("\n\tUUID: %s; ", uuid.toString());
		        	System.err.printf("\n\tLast seen: %s; ", r.getTime().toString());
		        	System.err.printf("\n\tCurrent time: %s; ", now.toString());
		        	it.remove(); // avoids a ConcurrentModificationException

                    // Check if the failed client is the leader
                    if ( uuid == main.getLeader() ) {
                        // Initiate a leader election, likely on a new thread
                        System.err.printf("\n\tFailed process was the leader");
                        new Election();
                    }
		        }
		    }
			
			// Release the listMutex
			main.listMutex.release();
						
			// Sleep to not use a bunch of CPU, but also wake up fairly often to check
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public failureDetector (int period, int timeout) throws Exception {
		this.period = period;
		this.timeout = timeout;
		myThread = new innerThread(this);
	}

	public int getPeriod() {
		return period;
	}
	
	public int getTimeout() {
		return timeout;
	}
}
