import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.UUID;


public class failureClient extends failureBase {
	protected int port;
	protected int period;
		
	public class innerThread extends failureBase.innerThread {
		protected DatagramSocket socket;
		protected int sequenceNumber = 0;
		protected failureClient outer;
		protected UUID uuid = UUID.randomUUID();

		public innerThread(failureClient base) throws Exception {
			super(base);
			outer = base;
			socket = new LossyDatagramSocket(main.lossPct);
			socket.setBroadcast(true);
		}
		
		public void main () {
			ByteBuffer bb = ByteBuffer.allocate(main.datagramSize);
			bb.putLong(uuid.getMostSignificantBits());
			bb.putLong(uuid.getLeastSignificantBits());
			bb.putInt(sequenceNumber);

			byte[] b = bb.array();
			DatagramPacket p = new DatagramPacket(b, b.length);

	        try {
	        	p.setAddress(InetAddress.getByName("255.255.255.255"));
		        p.setPort(outer.getPort());
				socket.send(p);
			} catch (UnknownHostException e1) {
				System.out.print("\nFailed to resolve host");
				e1.printStackTrace();
			} catch (IOException e) {
				System.out.print("\nFailed to send");
				e.printStackTrace();
			}
			
			if ( main.bDebug ) {
				System.out.print("\nHeart Beat Packet Sent");
		        System.out.printf("\n\tSent UUID: %s", uuid.toString());
		        System.out.printf("\n\tSent Sequence Number: %d", sequenceNumber);
			}
	        
			// Sleep
			try {
				Thread.sleep(outer.getPeriod() * 1000);
			} catch (Exception e) {
			}
			
			// Increment counter
			sequenceNumber++;
		}
	}

	public failureClient (int port, int period) throws Exception {
		myThread = new innerThread(this);
		this.port = port;
		this.period = period;
	}
	
	public int getPort() {
		return port;
	}
	
	public int getPeriod() {
		return period;
	}
	
}
