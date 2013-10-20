import java.util.TimerTask;
import java.util.UUID;

public class DeathTask extends TimerTask {
    private UUID uuid;

    public DeathTask (UUID uuid) {
        this.uuid = uuid;
    }

    public void run () {
        // Check edge case
        if ( uuid.compareTo(iLead.getSelf()) == 0 ) {
            // Ignore
            return;
        }

        // Announce
        System.err.printf("\nFailure detected;");
        System.err.printf("\n\tUUID: %s; ", uuid.toString());

        // Remove process from list
        iLead.remove(uuid);

        // Check if the failed client is the leader
        if ( uuid.compareTo(iLead.getLeader()) == 0 ) {
            // Initiate a leader election, likely on a new thread
            System.err.printf("\n\tFailed process was the leader");

            // Initiate an election
            iLead.getElectionState().Handle(new EventLeaderDeath());
        }
    }
}