import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;


public class failureServer extends failureBase {
	protected int port;
	public HashMap<UUID, Record> myMap = new HashMap<UUID, Record>();
	
	public class Record {
		protected int sequenceNumber;
		protected Date time;
		
		public Record (int sequenceNumber) {
			this.sequenceNumber = sequenceNumber;
			time = new Date();  	
		}
		
		public int getSequenceNumber () {
			return sequenceNumber;
		}

		public Date getTime () {
			return time;
		}
	}
		
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
			ByteBuffer bb = ByteBuffer.allocate(main.datagramSize);
			DatagramPacket p = new DatagramPacket(bb.array(), bb.array().length);

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
			
			if ( main.bDebug ) {
				System.out.print("\nGot packet");
			}
			
			// Validate the datagram length
			if ( bb.array().length != main.datagramSize ) {
				System.out.print("\nDatagram length validation failed");
				return;
			}
			
			// Get the data from the packet
			UUID uuid = new UUID(bb.getLong(), bb.getLong());
			int sequenceNumber = bb.getInt();

			if ( main.bDebug ) {
				System.out.printf("\n\tRecvd UUID: %s", uuid.toString());
				System.out.printf("\n\tRecvd Sequence Number: %d", sequenceNumber);
			}
			
			// Update process list with current time, uuid, and sequence number
			Record r = new Record(sequenceNumber);
	
			try {
				main.mutex.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			outer.myMap.put(uuid, r);
			
			main.mutex.release();
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
