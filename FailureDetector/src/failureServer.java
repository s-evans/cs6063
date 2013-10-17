import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.MulticastSocket;
import java.nio.ByteBuffer;
import java.util.UUID;

public class failureServer extends failureBase {
	protected int port;
		
	public class innerThread extends failureBase.innerThread {
		protected DatagramSocket socket;
		protected failureServer outer;

		public innerThread(failureServer base) throws Exception {
			super(base);
			outer = base;

			socket = new MulticastSocket(outer.getPort());
			
			if ( main.bDebug ) {
				System.out.printf("\nServer using port: %d", outer.getPort());
			}
		}
	
		protected void main () {
            // Create packet
			ByteBuffer bb = ByteBuffer.allocate(main.datagramSize);
			DatagramPacket p = new DatagramPacket(bb.array(), bb.array().length);

            // Debug
			if ( main.bDebug ) {
                System.out.print("\nBlocking read...");
			}
			
			// Blocking read on the socket
			try {
				socket.receive(p);
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}

            // Debug
			if ( main.bDebug ) {
				System.out.print("\nGot packet");
			}

            // Create an object based on the message
            MsgBase msg = MsgBase.Factory(bb);

            // Debug
            if ( main.bDebug ) {
                System.out.printf("\n\tRecvd UUID = %s", msg.getUuid().toString());
                System.out.printf("\n\tRecvd type = %d", msg.getType().ordinal());
            }

			// Update process list with current time, uuid, and sequence number
			Record r = new Record();

            // Lock the mutex
			try {
				main.listMutex.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

            // Add/replace entry in the process list
			main.processList.put(msg.getUuid(), r);

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
