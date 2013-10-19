import java.util.TimerTask;
import java.util.UUID;

public class DeathTask extends TimerTask {
    private UUID uuid;

    public DeathTask (UUID uuid) {
        this.uuid = uuid;
    }

    public void run () {
        // Announce
        System.err.printf("\nFailure detected;");
        System.err.printf("\n\tUUID: %s; ", uuid.toString());

        // Remove process from list
        main.remove(uuid);

        // Check if the failed client is the leader
        if ( uuid.compareTo(main.getLeader()) == 0 ) {
            // Initiate a leader election, likely on a new thread
            System.err.printf("\n\tFailed process was the leader");

            // Initiate an election
            main.getElectionState().Handle(new EventLeaderDeath());
        }
    }
}