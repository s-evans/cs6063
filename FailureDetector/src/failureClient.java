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
		protected failureClient outer;

		public innerThread(failureClient base) throws Exception {
			super(base);
			outer = base;
			socket = new LossyDatagramSocket(main.lossPct);
			socket.setBroadcast(true);
		}
		
		public void main () {
            // Create a message
            MsgBase msg = MsgBase.Factory(MsgBase.Type.HeartBeat);

            // Convert to array
            byte[] b = msg.toByteBuffer().array();

            // Make a packet
            DatagramPacket p = new DatagramPacket(b, b.length);

            // Send
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

            // Debug
			if ( main.bDebug ) {
				System.out.print("\nHeart Beat Packet Sent");
		        System.out.printf("\n\tSent UUID: %s", main.getSelf().toString());
			}
	        
			// Sleep
			try {
				Thread.sleep(outer.getPeriod() * 1000);
			} catch (Exception e) {
                // Ignore
			}
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
