import java.util.concurrent.Semaphore;



public class main {
	private static boolean threadsRunning = false;
	private static failureServer server;
	private static failureClient client;
	private static failureDetector detector;

	private static boolean bPeriod = false;
	private static boolean bTimeout = false;
	private static boolean bDestPort = false;
	private static boolean bServPort = false;
	private static boolean bLossy = false;
	public static boolean bDebug = false;
	
	public static int period = 5;
	public static int timeout = 2;
	public static int destPort = 9000;
	public static int servPort = 9000;
	public static int lossPct = 0;
	public static final int datagramSize = 16 + 4;
	
	public static Semaphore mutex = new Semaphore(1);
	
	private static void stopRunning () {
		threadsRunning = false;
	}
	
	private static void startRunning () throws Exception {
		server = new failureServer(servPort);
		client = new failureClient(destPort, period);
		detector = new failureDetector(server, period, timeout);
		
		client.startRunning();
		server.startRunning();
		detector.startRunning();

		threadsRunning = true;
	}
	
	private static boolean isRunning () {
		return threadsRunning;
	}
		
	private static void waitForThreads () {
		while ( isRunning() && client.isRunning() && 
				server.isRunning() && detector.isRunning() ) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
		}
	}
	
	private static void parseArgs(String[] args) throws Exception {		
		// Iterate over arguments
		for ( int i = 0 ; i < args.length ; i++ ) {
			if ( args[i].equals("-h") ) {
				System.out.print("\nThis program detects failures in like processes on the subnet");
				System.out.print("\n\t-h = print this help message;");
				System.out.print("\n\t-D = debug;");
				System.out.print("\n\t-l = lossy;");
				System.out.print("\n\t-p = period (seconds); default = 5;");
				System.out.print("\n\t-t = timeout (seconds); default = 3;");
				System.out.print("\n\t-d = destination port; default = 9000;");
				System.out.print("\n\t-s = server port; default = 9001;");
				throw new Exception();
			}
			
			// Handle period
			if ( args[i].equals("-p") ) {
				if ( bPeriod ) {
					System.err.print("\nDuplicate parameter detected");
					throw new Exception();
				}
				
				if ( i+1 > args.length ) {
					System.err.print("\nNo value found");
					throw new Exception();
				}
				
				i++;
				period = Integer.parseInt(args[i]);
				bPeriod = true;
				continue;
 			}

			// Handle destination port
			if ( args[i].equals("-d") ) {
				if ( bDestPort ) {
					System.err.print("\nDuplicate parameter detected");
					throw new Exception();
				}
				
				if ( i+1 > args.length ) {
					System.err.print("\nNo value found");
					throw new Exception();
				}
				
				i++;
				destPort = Integer.parseInt(args[i]);
				bDestPort = true;
				continue;
 			}

			// Handle server port
			if ( args[i].equals("-s") ) {
				if ( bServPort ) {
					System.err.print("\nDuplicate parameter detected");
					throw new Exception();
				}
				
				if ( i+1 > args.length ) {
					System.err.print("\nNo value found");
					throw new Exception();
				}
				
				i++;
				servPort = Integer.parseInt(args[i]);
				bServPort = true;
				continue;
 			}

			// Handle timeout
			if ( args[i].equals("-t") ) {
				if ( bTimeout ) {
					System.err.print("\nDuplicate parameter detected");
					throw new Exception();
				}
				
				if ( i+1 > args.length ) {
					System.err.print("\nNo value found");
					throw new Exception();
				}
				
				i++;
				timeout = Integer.parseInt(args[i]);
				bTimeout = true;
				continue;
 			}

			// Handle debug
			if ( args[i].equals("-D") ) {
				if ( bDebug ) {
					System.err.print("\nDuplicate parameter detected");
					throw new Exception();
				}
				
				bDebug = true;
				continue;
 			}

			// Handle lossy
			if ( args[i].equals("-l") ) {
				if ( bLossy ) {
					System.err.print("\nDuplicate parameter detected");
					throw new Exception();
				}
				
				if ( i+1 > args.length ) {
					System.err.print("\nNo value found");
					throw new Exception();
				}
				
				i++;
				lossPct = Integer.parseInt(args[i]);
				bLossy = true;
				continue;
 			}
			
			System.err.print("\nInvalid parameter detected: " + args[i]);
			throw new Exception();
		}
		
		return;
	}

	public static void main(String[] args) throws Exception {
		System.out.print("iDetect V1.0 (c) 2013");
		
		try {
			parseArgs(args);
		} catch ( Exception e ) {
			return;
		}
		
		startRunning();
		
		waitForThreads();
	}

}
