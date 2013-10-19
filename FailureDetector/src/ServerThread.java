import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.MulticastSocket;
import java.nio.ByteBuffer;

public class ServerThread extends Thread {
    protected DatagramSocket socket;

    public ServerThread(int port) throws Exception {
        socket = new MulticastSocket(port);

        main.debugPrint("\nServer using port " + port);
    }

    public void run () {
        // Run forever
        while ( true ) {
            main();
        }
    }

    protected void main () {
        // Create packet
        ByteBuffer bb = ByteBuffer.allocate(main.datagramSize);
        DatagramPacket p = new DatagramPacket(bb.array(), bb.array().length);

        // Debug
        main.debugPrint("\nBlocking read...");

        // Blocking read on the socket
        try {
            socket.receive(p);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Debug
        main.debugPrint("\nGot packet");

        // Create an object based on the message
        MsgBase msg = MsgBase.Factory(bb);

        // Debug
        main.debugPrint("\n\tRecvd UUID = " + msg.getUuid().toString());
        main.debugPrint("\n\tRecvd type = " + msg.getType().ordinal());

        // Lock the mutex
        try {
            main.listMutex.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Get this process's entry in the process list
        DeathTask dt = main.processList.get(msg.getUuid());

        // Check if it exists
        if ( dt != null ) {
            // Cancel the current death timeout event
            dt.cancel();
        }

        // Create a new death timeout task
        dt = new DeathTask(msg.getUuid());

        // Add/replace entry
        main.processList.put(msg.getUuid(), dt);

        // Schedule the new death timeout event
        main.setProcessDeathTimeout(dt);

        // Let go of the mutex
        main.listMutex.release();

        // Handle the message
        msg.Handle();
    }
}
