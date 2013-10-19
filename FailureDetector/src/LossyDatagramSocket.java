import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Random;

public class LossyDatagramSocket extends DatagramSocket {
	protected int lossPct;
	
	public LossyDatagramSocket(int lossPct) throws SocketException {
		super();
		this.lossPct = lossPct;
		
        main.debugPrint("\nUsing loss PCT: " + lossPct);
	}

	public void send(DatagramPacket p) throws IOException {
		// Create random number
		Random rand = new Random();
		int num = rand.nextInt(100);

        // Debug
        main.debugPrint("\nRolled: " + num);

		// Drop the packet randomly
		if ( num < lossPct ) {
		    main.debugPrint("\nSuprise! Dropped packet.");
			return;
		}
		
		// Actually do the send
		super.send(p);
	}
}
