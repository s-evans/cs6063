import java.io.*;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


public class iLead {
    // State
    private static ElectionStateBase electionState = new ElectionStateNoLeader();
    private static ConsensusStateBase consensusState = new ConsensusStateUndecided();

    // Server thread
    private static ServerThread server;

    // Multicast client socket
    private static LossyDatagramSocket socket;

    // Duplicate parameter detection
    private static boolean bPeriod = false;
    private static boolean bTimeout = false;
    private static boolean bDestPort = false;
    private static boolean bServPort = false;
    private static boolean bLossy = false;
    public static boolean bDebug = false;
    private static boolean idOverride = false;
    public static boolean hasJoinedGroup = false;

    // Program parameters
    public static int processCount = 4;    // TODO: Make configurable   // TODO: should be 12
    public static int period = 500;
    public static int timeout = 1250;
    public static int destPort = 9000;
    public static int servPort = 9000;
    public static int lossPct = 0;
    public static int groupJoinTimeout = 1000;
    public static int leaderElectionWait = 1500;

    // Startup config file name
    protected static final String STARTUP_FILE_NAME = "startup";

    // UUID of this process
    protected static UUID uuid = UUID.randomUUID();
    protected static int instanceNum = 0;
    protected static UUID leader = uuid;
    protected static int consensusValue = new Random().nextInt();

    // Timer objects
    protected static Timer timer = new Timer();
    protected static TimerTask electionTimeoutTask = new ElectionTimeoutTask();
    protected static TimerTask consensusTimeoutTask = new ConsensusTimeoutTask();
    protected static TimerTask heartBeatTask = new HeartBeatTask();

    // Process list
    public static HashMap<UUID, Record> processList = new HashMap<UUID, Record>();


    public static int getConsensusValue() {
        return consensusValue;
    }

    public static void setConsensusValue( int val ) {
        consensusValue = val;
    }

    // Returns whether or not the current process is the leader
    public static boolean isLeader () {
        return isLeader(uuid);
    }

    // Returns whether or not the current process is the leader
    public static boolean isLeader ( UUID uuid ) {
        // Validate that there is a leader
        if ( electionState.getClass() != ElectionStateHaveLeader.class ) {
            return false;
        }

        // Check if the current process is the leader
        if ( uuid.compareTo(leader) == 0 ) {
            return true;
        }

        return false;
    }

    // Returns the number of processes required to establish a byzantine tolerant quorum
    public static int getQuorumRequirement () {
        return processCount * 2 / 3 + 1;
    }

    // Returns the number of non failed processes
    public static int getNonFailedProcessCount() {
        // Iterate over the list and count those processes that are not failed
        int count = 0;
        Iterator<Map.Entry<UUID, Record>> it = iLead.processList.entrySet().iterator();
        while ( it.hasNext() ) {
            // Get entry
            Map.Entry<UUID, Record> entry = it.next();

            // Check is the process is alive
            if ( !entry.getValue().alive ) {
                continue;
            }

            count++;
        }

        iLead.debugPrint("\nNumber of non-failed process: " + count);

        return count;
    }

    // Return whether or not a byzantine fault tolerant quorum exists
    public static boolean quorumExists () {
        // Compare current number of processes with those required
        if ( getNonFailedProcessCount() >= getQuorumRequirement() ) {
            return true;
        }

        return false;
    }

    public static Integer getMajority() {
        int [] array = new int [iLead.processList.size()];

        // Iterate over the list
        Iterator<Map.Entry<UUID, Record>> it = iLead.processList.entrySet().iterator();
        for ( int i = 0 ; it.hasNext() ; i++ ) {
            // Get entry
            Map.Entry<UUID, Record> entry = it.next();

            // Add to the array
            array[i] = entry.getValue().consensusValue;
        }

        // Calculate the majority value
        Integer maj = Majority.calc(array);

        // Debug
        iLead.debugPrint("\nMajority value: " + maj);

        // Return
        return maj;
    }

    // Print provided string to the console (only while in debug mode)
    public static void debugPrint (String str) {
        if ( bDebug ) {
            System.out.print(str);
        }
    }

    public static void setConsensusRoundTimeout () {
        // Cancel the currently scheduled timeout task
        consensusTimeoutTask.cancel();

        // Create a new task
        consensusTimeoutTask = new ConsensusTimeoutTask();

        // Schedule the task
        timer.schedule(consensusTimeoutTask, timeout);
    }

    // Set a timeout event to occur
    public static void setElectionMsgTimeout (int multi) {
        // Cancel the currently scheduled timeout task
        electionTimeoutTask.cancel();

        // Create a new task
        electionTimeoutTask = new ElectionTimeoutTask();

        // Schedule the task
        timer.schedule(electionTimeoutTask, multi * timeout);
    }

    // Set a process death task to occur
    public static void setProcessDeathTimeout (DeathTask dt) {
        // Schedule the task
        timer.schedule(dt, period + timeout);
    }

    // Schedules a msg task to occur immediately
    public static void msgRecvd (MsgTask mt) {
        // Schedule the task
        try {
            timer.schedule(mt, 0);
        } catch (IllegalStateException e) {
            iLead.debugPrint("IllegalStateException...");
        }
    }

    // Whether or not the current process is highest in the process list
    public static boolean isHighest() {
        // Iterate over the list searching for a higher uuid
        boolean highest = true;
        Iterator<Map.Entry<UUID, Record>> it = iLead.processList.entrySet().iterator();
        while ( it.hasNext() ) {
            // Get UUID from the list iterator
            Map.Entry<UUID, Record> entry = it.next();
            UUID curListUuid = entry.getKey();

            // check is the process is alive
            if ( !entry.getValue().alive ) {
                continue;
            }

            // Do comparison
            int val = curListUuid.compareTo(iLead.getSelf());

            // If list uuid > our uuid
            if ( val == 1 ) {
                // We're not the highest
                highest = false;
                break;
            }
        }

        iLead.debugPrint("\nAm I Highest? " + highest);

        return highest;
    }

    public static void updateAliveStatus (UUID uuid, boolean alive) {
        Record currRcd = iLead.processList.get(uuid);
        Record newRcd = new Record(currRcd.runId, currRcd.deathTask, alive, currRcd.consensusValue);
        iLead.processList.put(uuid, newRcd);
    }

    public static void updateConsensusValue(UUID uuid, int consensusValue) {
        Record currRcd = iLead.processList.get(uuid);
        Record newRcd = new Record(currRcd.runId, currRcd.deathTask, currRcd.alive, consensusValue);
        iLead.processList.put(uuid, newRcd);
    }

    // Accessor
    public static UUID getLeader() {
        return leader;
    }

    // Accessor
    public static void setLeader(UUID newLeader) {
        System.out.printf("\nNew leader established: %s", newLeader.toString());
        leader = newLeader;
    }

    // Accessor
    public static UUID getSelf () {
        return uuid;
    }

    // Compare a msg against ourselves
    public static boolean isSelf(UUID uuid, int runId) {
        if (getSelf().compareTo(uuid) == 0 && runId == getInstanceNum() ) {
            return true;
        } else {
            return false;
        }
    }

    // Retrieve this iLead process instance number
    public static int getInstanceNum() {
        return instanceNum;
    }

    //Attempt to read the startup file and throw exception if it does not exist
    private static void readStartupFile() throws IOException {
        Path initPath = Paths.get(STARTUP_FILE_NAME);
        Scanner fileParser = new Scanner(initPath);
        String firstLine = fileParser.nextLine();
        String[] initInfo = firstLine.split(",");
        iLead.uuid = UUID.fromString(initInfo[0]);
        iLead.instanceNum = Integer.parseInt(initInfo[1]) + 1;
        fileParser.close();
    }

    //Attempt to recreate the startup file with a known uuid and instance number
    private static void writeStartupFile(UUID id, int instanceNum) throws IOException {
        File initFile = new File(STARTUP_FILE_NAME);
        initFile.delete();
        FileWriter startupFile = new FileWriter(STARTUP_FILE_NAME);
        startupFile.write(id.toString());
        startupFile.write(",");
        startupFile.write(Integer.toString(instanceNum));
        startupFile.close();
    }

    private static void startRunning () throws Exception {
        // Create a socket
        socket = new LossyDatagramSocket(iLead.lossPct);
        socket.setBroadcast(true);

        // Create thread object
        server = new ServerThread(servPort);

        // Start a recurring heartbeat task
        timer.scheduleAtFixedRate(heartBeatTask, 0, period);

        // Grace period for looking for duplicate message
        timer.schedule(new GroupJoinTask(), groupJoinTimeout);

        // Reset the state machine
        timer.schedule(new InitTask(), leaderElectionWait);

        // Start the server thread
        server.start();
    }

    private static void parseArgs(String[] args) throws Exception {
        // Iterate over arguments
        for ( int i = 0 ; i < args.length ; i++ ) {
            if ( args[i].equals("-h") ) {
                System.out.print("\nThis program detects failures in like processes on the subnet");
                System.out.print("\n\t-h = print this help message;");
                System.out.print("\n\t-D = debug;");
                System.out.print("\n\t-l = lossy;");
                System.out.print("\n\t-p = period (milliseconds);");
                System.out.print("\n\t-t = timeout (milliseconds);");
                System.out.print("\n\t-d = destination port; default = 9000;");
                System.out.print("\n\t-s = server port; default = 9001;");
                System.out.print("\n\t-i = identifier (16bytes); default = random");
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

            if ( args[i].equals("-i") ) {
                i++;
                try {
                    uuid = UUID.fromString(args[i]);
                    idOverride = true;
                } catch (IllegalArgumentException e) {
                    System.err.println("Please enter a valid UUID");
                }
                continue;
            }
            
            System.err.print("\nInvalid parameter detected: " + args[i]);
            throw new Exception();
        }
        
        return;
    }

    public static void stopRunning() {
        // Stop the server
        server.setStopped();

        // Stop the timer tasks
        timer.cancel();

        // Close the socket
        socket.close();

    }

    public static void sendMsg (MsgBase msg) {
        // Convert to array
        byte[] b = msg.toByteBuffer().array();

        // Make a packet
        DatagramPacket p = new DatagramPacket(b, b.length);

        // Send
        try {
            p.setAddress(InetAddress.getByName("255.255.255.255"));
            p.setPort(iLead.destPort);
            socket.send(p);
        } catch (UnknownHostException e1) {
            System.out.print("\nFailed to resolve host");
            e1.printStackTrace();
        } catch (IOException e) {
            System.out.print("\nFailed to send");
            e.printStackTrace();
        }
    }

    public static void sendMsg (MsgBase.Type msgType) {
        iLead.debugPrint("\nSending Message Type " + msgType.ordinal());

        try {
            // Create a message
            MsgBase msg = MsgBase.Factory(msgType);

            // Send it
            sendMsg(msg);
        } catch ( Exception e ) {
            System.out.printf("\nFailed to send message");
        }
    }

    // Accessor
    public static ElectionStateBase getElectionState () {
        return electionState;
    }

    // Accessor
    public static ConsensusStateBase getConsensusState () {
        return consensusState;
    }

    // Set the consensus state variable
    public static void setConsensusState ( ConsensusStateBase state ) {
        // Set the state
        iLead.consensusState = state;

        // Let the state run its thing
        iLead.consensusState.Handle(new EventInit());
    }

    // Set the election state variable
    public static void setElectionState ( ElectionStateBase state ) {
        // Set the state
        iLead.electionState = state;

        // Let the state run its thing
        iLead.electionState.Handle(new EventInit());
    }

    // Application entry point
    public static void main(String[] args) throws Exception {
        System.out.print("iLead V1.0 (c) 2013");

        parseArgs(args);

        //Attempt to read the UUID and instance number from the startup file in the current directory
        if ( !idOverride ) {
            try {
                readStartupFile();
            } catch (IOException e) {
                debugPrint("Unable to locate " + STARTUP_FILE_NAME);
            }
        }

        writeStartupFile(iLead.uuid, iLead.instanceNum);

        System.out.printf("\nUUID = %s", iLead.getSelf());
        System.out.println(" Instance Number = " + iLead.instanceNum);

        startRunning();
    }



}
