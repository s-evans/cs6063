import javax.swing.*;
import java.io.*;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.Timer;


public class iTolerate {
    // State
    private static ElectionStateBase electionState = new ElectionStateNoLeader();

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
    protected static Integer consensusValue = new Integer(12);
    protected static Integer majorityValue = null;

    // Timer objects
    protected static Timer timer = new Timer();
    protected static TimerTask electionTimeoutTask = new ElectionTimeoutTask();
    protected static TimerTask heartBeatTask = new HeartBeatTask();

    //GUI
    public static UserInterface gui;

    // Process list
    public static HashMap<UUID, Record> processList = new HashMap<UUID, Record>();

    public static int getConsensusValue() {
        return consensusValue;
    }

    // Returns whether or not the current process is the leader
    public static boolean isLeader () {
        return isLeader(uuid);
    }

    // Returns whether or not the current process is the leader
    public static boolean isLeader ( UUID uuid ) {
        // Validate that there is a leader
        if ( !(electionState instanceof ElectionStateHaveLeader) ) {
            return false;
        }

        // Check if the current process is the leader
        if ( uuid.compareTo(leader) == 0 ) {
            return true;
        }

        return false;
    }


    public static Integer getLeaderConsensusValue() {
        if ( isLeader() ) {
            return consensusValue;
        }

        Record rcd = iTolerate.processList.get(iTolerate.getLeader());
        if ( rcd != null ) {
            return rcd.consensusValue;
        }

        return null;
    }

    public static Integer getMajority() {
        Integer [] array = new Integer [iTolerate.processList.size()];

        // Iterate over the list
        Iterator<Map.Entry<UUID, Record>> it = iTolerate.processList.entrySet().iterator();
        for ( int i = 0 ; it.hasNext() ; i++ ) {
            // Get entry
            Map.Entry<UUID, Record> entry = it.next();

            // Add to the array
            array[i] = entry.getValue().consensusValue;
        }

        // Calculate the majority value
        Integer maj = Majority.calc(array);

        // Debug
        iTolerate.debugPrint("\nMajority value: " + maj);

        // Return
        return maj;
    }

    // Print provided string to the console (only while in debug mode)
    public static void debugPrint (String str) {
        if ( bDebug ) {
            iTolerate.logToGui(str);
        }
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
            iTolerate.debugPrint("IllegalStateException...");
        }
    }

    // Whether or not the current process is highest in the process list
    public static boolean isHighest() {
        // Iterate over the list searching for a higher uuid
        boolean highest = true;
        Iterator<Map.Entry<UUID, Record>> it = iTolerate.processList.entrySet().iterator();
        while ( it.hasNext() ) {
            // Get UUID from the list iterator
            Map.Entry<UUID, Record> entry = it.next();
            UUID curListUuid = entry.getKey();

            // check is the process is alive
            if ( !entry.getValue().alive ) {
                continue;
            }

            // check if the process is byzantine
            if ( iTolerate.majorityValue != null &&
                    entry.getValue().consensusValue != null &&
                    entry.getValue().consensusValue.compareTo(iTolerate.majorityValue) != 0 ) {
                continue;
            }

            // Do comparison
            int val = curListUuid.compareTo(iTolerate.getSelf());

            // If list uuid > our uuid
            if ( val == 1 ) {
                // We're not the highest
                highest = false;
                break;
            }
        }

        iTolerate.debugPrint("\nAm I Highest? " + highest);

        return highest;
    }

    public static void updateProcessRestart (UUID uuid) {
        Record currRcd = iTolerate.processList.get(uuid);
        Record newRcd = new Record(currRcd.runId, currRcd.deathTask, true, null);
        iTolerate.processList.put(uuid, newRcd);
    }

    public static void updateProcessDeath (UUID uuid) {
        Record currRcd = iTolerate.processList.get(uuid);
        Record newRcd = new Record(currRcd.runId, currRcd.deathTask, false, null);
        iTolerate.processList.put(uuid, newRcd);
    }

    // Accessor
    public static UUID getLeader() {
        return leader;
    }

    // Accessor
    public static void setLeader(UUID newLeader) {
        iTolerate.logToGui("\nNew leader established: " + newLeader.toString());
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

    // Retrieve this iTolerate process instance number
    public static int getInstanceNum() {
        return instanceNum;
    }

    //Attempt to read the startup file and throw exception if it does not exist
    private static void readStartupFile() throws IOException {
        Path initPath = Paths.get(STARTUP_FILE_NAME);
        Scanner fileParser = new Scanner(initPath);
        String firstLine = fileParser.nextLine();
        String[] initInfo = firstLine.split(",");
        iTolerate.uuid = UUID.fromString(initInfo[0]);
        iTolerate.instanceNum = Integer.parseInt(initInfo[1]) + 1;
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

        //TODO: Move GUI creation to seperate function
        //TODO: Refactor this class where needed to support GUI
        JFrame frame = new JFrame("UserInterface");
        gui = new UserInterface();
        frame.setContentPane(gui.uiForm);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        // Create a socket
        socket = new LossyDatagramSocket(iTolerate.lossPct);
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
                iTolerate.logToGui("\nThis program detects failures in like processes on the subnet");
                iTolerate.logToGui("\n\t-h = print this help message;");
                iTolerate.logToGui("\n\t-D = debug;");
                iTolerate.logToGui("\n\t-l = lossy;");
                iTolerate.logToGui("\n\t-p = period (milliseconds);");
                iTolerate.logToGui("\n\t-t = timeout (milliseconds);");
                iTolerate.logToGui("\n\t-d = destination port; default = 9000;");
                iTolerate.logToGui("\n\t-s = server port; default = 9001;");
                iTolerate.logToGui("\n\t-i = identifier (16bytes); default = random");
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
            p.setPort(iTolerate.destPort);
            socket.send(p);
        } catch (UnknownHostException e1) {
            iTolerate.logToGui("\nFailed to resolve host");
            e1.printStackTrace();
        } catch (IOException e) {
            iTolerate.logToGui("\nFailed to send");
            e.printStackTrace();
        }
    }

    public static void sendMsg (MsgBase.Type msgType) {
        iTolerate.debugPrint("\nSending Message Type " + msgType.ordinal());

        try {
            // Create a message
            MsgBase msg = MsgBase.Factory(msgType);

            // Send it
            sendMsg(msg);
        } catch ( Exception e ) {
            iTolerate.logToGui("\nFailed to send message");
        }
    }

    // Accessor
    public static ElectionStateBase getElectionState () {
        return electionState;
    }

    // Set the election state variable
    public static void setElectionState ( ElectionStateBase state ) {
        // Set the state
        iTolerate.electionState = state;

        // Let the state run its thing
        iTolerate.electionState.Handle(new EventInit());
    }

    // Cause this node to exhibit a byzantine failure
    public static void startByzantineFailure() {
        iTolerate.logToGui("\nByzantine Failure " +  iTolerate.getSelf());
        // Cancel the currently scheduled timeout task
        consensusValue = new Random().nextInt();
    }

    // Repair this node from a byzantine failure
    public static void repairNode() {
        iTolerate.logToGui("\nRepair Byzantine Failure " + iTolerate.getSelf());
        consensusValue = 12;
    }

    public static void logToGui(String message) {
        gui.updateLogPanel(message);
    }

    // Application entry point
    public static void main(String[] args) throws Exception {
        System.out.println("iTolerate V1.0 (c) 2013");

        parseArgs(args);

        //Attempt to read the UUID and instance number from the startup file in the current directory
        if ( !idOverride ) {
            try {
                readStartupFile();
            } catch (IOException e) {
                debugPrint("Unable to locate " + STARTUP_FILE_NAME);
            }
        }

        writeStartupFile(iTolerate.uuid, iTolerate.instanceNum);

        System.out.print("\nUUID = " + iTolerate.getSelf());
        System.out.print(" Instance Number = " + iTolerate.instanceNum);

        startRunning();
    }



}
