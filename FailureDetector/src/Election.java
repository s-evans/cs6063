

public class Election {
    protected ServerThread serverThread;
    protected ClientThread clientThread;

    // TODO: Perhaps keep some state variables here
    // TODO: Think about multicast/unicast for send and receive
    // TODO: Need to be constantly listening for new election requests incoming asynchronously

    public Election () {
        if ( main.bDebug ) {
            System.out.print("\nNew election beginning");
        }

        // Create thread objects
        serverThread = new ServerThread(this);
        clientThread = new ClientThread(this);

        // Start the threads running
        clientThread.start();
        serverThread.start();
    }

    public class ServerThread extends Thread {
        Election election;

        public ServerThread (Election election) {
            this.election = election;
        }

        public void run() {
            // TODO: Remove this statement
            main.setLeader(main.getSelf());

            // TODO: Receive answer messages on a timeout
            // TODO: If timeout occurs, set me as leader, send ELECTED message
            // TODO: If answer messages are received for this election, do something (?) (Wait for ELECTED message?)
            // TODO: If no ELECTED message received, set me as leader, send ELECTED message
        }
    }

    public class ClientThread extends Thread {
        Election election;

        public ClientThread (Election election) {
            this.election = election;
        }

        public void run() {
            // TODO: Send to peers with higher ID's than self (using main.uuid, main.processList, and main.listMutex)
            // TODO: Think about what happens initially, when no other processes are known (no one to send to)
            // TODO: Thread can die at this point (?)
        }
    }
}
