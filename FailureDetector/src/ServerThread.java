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

        // Create a timer task
        MsgTask mt = new MsgTask(msg);

        // Schedule the task to run immediately
        main.msgRecvd(mt);
    }
}
