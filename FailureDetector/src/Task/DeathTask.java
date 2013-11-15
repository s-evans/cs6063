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

        // TODO: Add a similar class, ProcRestartTask, that doesn't remove from list, and prints something different

        // Remove process from list
        // iLead.remove(uuid);

        // update the failed record to the failed state
        iLead.updateAliveStatus(uuid, false);

        // Check if the failed client is the leader
        if ( uuid.compareTo(iLead.getLeader()) == 0 ) {
            // Initiate a leader election, likely on a new thread
            System.err.printf("\n\tFailed process was the leader");

            // Initiate an election
            iLead.getElectionState().Handle(new EventLeaderDeath());
        }
    }
}