import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.UUID;


public class main {
    // State
    private static ElectionStateBase electionState = new ElectionStateNoLeader();
	private static boolean threadsRunning = false;

    // Failure detector objects
	private static failureServer server;
	private static failureClient client;
	private static failureDetector detector;

    // Duplicate parameter detection
	private static boolean bPeriod = false;
	private static boolean bTimeout = false;
	private static boolean bDestPort = false;
	private static boolean bServPort = false;
	private static boolean bLossy = false;
	public static boolean bDebug = false;

    // TODO: Timeout and period parameters need to be in subseconds (perhaps 500ms and 1000ms?).
    // TODO: the current values lead to ~3 sec avg detection time. Meaning ~3 sec avg leader election time will be impossible.

	// Parameters
    public static int period = 2;
	public static int timeout = 1;
	public static int destPort = 9000;
	public static int servPort = 9000;
	public static int lossPct = 0;

    // This is the UUID of this process
    protected static final UUID uuid = UUID.randomUUID();

    // Currently elected leader
    protected static UUID leader = uuid;

    // Constant values
    public static final int datagramSize = 16 + 4;

    // Process list
	public static Semaphore listMutex = new Semaphore(1);
    public static HashMap<UUID, Record> processList = new HashMap<UUID, Record>();

    // Whether or not the current process is highest in the process list
    public static boolean isHighest() {
        // Lock the mutex
        try {
            main.listMutex.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Iterate over the list searching for a higher uuid
        boolean highest = true;
        Iterator<Map.Entry<UUID, Record>> it = main.processList.entrySet().iterator();
        while ( it.hasNext() ) {
            // Get UUID from the list iterator
            Map.Entry<UUID, Record> entry = it.next();
            UUID curListUuid = entry.getKey();

            // If list uuid > our uuid
            if ( curListUuid.compareTo(main.getSelf()) == 1 ) {
                // We're not the highest
                highest = false;
                break;
            }
        }

        // Let go of the mutex
        main.listMutex.release();

        return highest;
    }

    public static UUID getLeader() {
        return leader;
    }

    public static void setLeader(UUID newLeader) {
        System.out.printf("\nNew leader established: %s", newLeader.toString());
        leader = newLeader;
    }

    public static UUID getSelf () {
        return uuid;
    }

	private static void stopRunning () {
		threadsRunning = false;
	}

	private static void startRunning () throws Exception {
		server = new failureServer(servPort);
		client = new failureClient(destPort, period);
		detector = new failureDetector(period, timeout);
		
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
				System.out.print("\n\t-p = period (seconds);");
				System.out.print("\n\t-t = timeout (seconds);");
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

    public static void sendMsg (MsgBase.Type msgType) {
        try {
            // Create a socket
            LossyDatagramSocket socket = new LossyDatagramSocket(main.lossPct);
            socket.setBroadcast(true);

            // Create a message
            MsgBase msg = MsgBase.Factory(msgType);

            // Convert to array
            byte[] b = msg.toByteBuffer().array();

            // Make a packet
            DatagramPacket p = new DatagramPacket(b, b.length);

            // Send
            try {
                p.setAddress(InetAddress.getByName("255.255.255.255"));
                p.setPort(main.destPort);
                socket.send(p);
            } catch (UnknownHostException e1) {
                System.out.print("\nFailed to resolve host");
                e1.printStackTrace();
            } catch (IOException e) {
                System.out.print("\nFailed to send");
                e.printStackTrace();
            }

        } catch ( Exception e ) {
            System.out.printf("\nFailed to send message");
        }
    }

    public static ElectionStateBase getElectionState () {
        return electionState;
    }

    public static void setElectionState ( ElectionStateBase state ) {
        // Set the state
        main.electionState = state;

        // Let the state run its thing
        main.electionState.Handle(new EventInit());
    }

	public static void main(String[] args) throws Exception {
		System.out.print("iLead V1.0 (c) 2013");
		System.out.printf("\nUUID = %s", main.getSelf());

		try {
			parseArgs(args);
		} catch ( Exception e ) {
			return;
		}
		
		startRunning();

        setElectionState(new ElectionStateNoLeader());

		waitForThreads();
	}

}
