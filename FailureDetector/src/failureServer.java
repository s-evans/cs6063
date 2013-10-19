import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.MulticastSocket;
import java.nio.ByteBuffer;
import java.util.UUID;

// TODO: Refactor to thread subclass, rename, and remove parent class

public class failureServer extends failureBase {
	protected int port;
		
	public class innerThread extends failureBase.innerThread {
		protected DatagramSocket socket;
		protected failureServer outer;

		public innerThread(failureServer base) throws Exception {
			super(base);
			outer = base;

			socket = new MulticastSocket(outer.getPort());
			
            main.debugPrint("\nServer using port " + outer.getPort());
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

            // Add/replace entry in the process list
            DeathTask dt = main.processList.get(msg.getUuid());

            if ( dt != null ) {
                // Cancel the current event
                dt.cancel();
            }

            // Create a new task
            dt = new DeathTask(msg.getUuid());

            // Add/replace entry
            main.processList.put(msg.getUuid(), dt);

            // Schedule a new event
            main.setProcessDeathTimeout(dt);

            // Let go of the mutex
			main.listMutex.release();

            // Handle the message
            msg.Handle();
		}
	}

	public failureServer (int port) throws Exception {
		this.port = port;
		myThread = new innerThread(this);
	}
	
	public int getPort() {
		return port;
	}
}
