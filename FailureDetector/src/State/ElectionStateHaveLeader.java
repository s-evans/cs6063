import java.util.UUID;

public class ElectionStateHaveLeader extends ElectionStateBase {
    protected UUID uuid;

    public ElectionStateHaveLeader (UUID uuid) {
        this.uuid = uuid;
    }

    public void Handle ( EventInit evt ) {
        main.debugPrint("\nState = ElectionStateHaveLeader");

        // Set the new leader
        main.setLeader(uuid);
    }

    public void Handle ( EventLeaderDeath evt ) {
        main.debugPrint("\nHandling EventLeaderDeath");

        // Set state
        main.setElectionState(new ElectionStateNoLeader());
    }

    public void Handle ( EventElectionMsgRecvd evt ) {
        main.debugPrint("\nHandling EventElectionMsgRecvd");

        // Validate UUID of the message
        if ( evt.getUuid().compareTo(main.getSelf()) == -1 ) {
            // Set state
            main.setElectionState(new ElectionStateOkSent());
        }
    }
}
