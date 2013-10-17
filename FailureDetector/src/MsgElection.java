import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class MsgElection extends MsgBase {
    public MsgElection () {
        this.type = Type.Election;
    }

    public void Handle() {
        // Compare incoming message's uuid to this process's uuid
        int comp = uuid.compareTo(main.uuid);

        // Handle comparison result
        if ( comp == -1 ) {
            // Received uuid < this process's uuid

            // Send Ok Msg
            main.sendMsg(MsgBase.Type.Ok);

            // Lock the mutex
            try {
                main.listMutex.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Iterate over the list searching for a higher uuid
            boolean highest = true;
            Iterator<Map.Entry<UUID, Record>> it = main.processList.entrySet().iterator();
            while ( it.hasNext() ) {
                // Get UUID from the list iterator
                Map.Entry<UUID, Record> entry = it.next();
                UUID curListUuid = entry.getKey();

                // If list uuid > our uuid
                if ( curListUuid.compareTo(main.uuid) == 1 ) {
                    // We're not the highest
                    highest = false;
                    break;
                }
            }

            // Let go of the mutex
            main.listMutex.release();

            // Check status
            if ( highest ) {
                // Send Coordinator
                main.sendMsg(MsgBase.Type.Coordinator);
            } else {
                // Send Election Msg
                main.sendMsg(MsgBase.Type.Election);
            }
        } else if ( comp == 1 ) {
            // Received uuid > this process's uuid

            // Ignore
        } else if ( comp == 0 ) {
            // Multicast loop back

            // TODO: I sent an election message. Spawn a thread on a timeout. If timeout expires, do something? Based on state?
        } else {
            throw new RuntimeException("UUID.compareTo() is out of spec");
        }
    }
}