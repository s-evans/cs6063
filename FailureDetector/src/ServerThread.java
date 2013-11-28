import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.MulticastSocket;
import java.nio.ByteBuffer;

public class ServerThread extends Thread {
    protected DatagramSocket socket;
    protected volatile boolean stopped = false;

    public ServerThread(int port) throws Exception {
        socket = new MulticastSocket(port);

        iLead.debugPrint("\nServer using port " + port);
    }

    public void run () {
        // Run forever
        while ( !stopped ) {
            main();
        }
    }

    public void setStopped() {
        this.stopped = true;
    }

    protected void main () {
        // Create packet
        ByteBuffer bb = ByteBuffer.allocate(MsgBase.maxDatagramSize);
        DatagramPacket p = new DatagramPacket(bb.array(), bb.array().length);

        // Debug
        iLead.debugPrint("\nBlocking read...");

        // Blocking read on the socket
        try {
            socket.receive(p);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Debug
        iLead.debugPrint("\nGot packet");

        // Create an object based on the message
        MsgBase msg = MsgBase.Factory(bb, p.getLength());

        // Create a timer task
        MsgTask mt = new MsgTask(msg);

        // Schedule the task to run immediately
        iLead.msgRecvd(mt);
    }
}
