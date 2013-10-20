import java.util.UUID;

public class ElectionStateHaveLeader extends ElectionStateBase {
    protected UUID uuid;

    public ElectionStateHaveLeader (UUID uuid) {
        this.uuid = uuid;
    }

    public void Handle ( EventInit evt ) {
        iLead.debugPrint("\nState = ElectionStateHaveLeader");

        // Set the new leader
        iLead.setLeader(uuid);
    }

    public void Handle ( EventLeaderDeath evt ) {
        iLead.debugPrint("\nHandling EventLeaderDeath");

        // Set state
        iLead.setElectionState(new ElectionStateNoLeader());
    }

    public void Handle ( EventElectionMsgRecvd evt ) {
        iLead.debugPrint("\nHandling EventElectionMsgRecvd");

        // Validate UUID of the message
        if ( evt.getUuid().compareTo(iLead.getSelf()) == -1 ) {
            // Set state
            iLead.setElectionState(new ElectionStateOkSent());
        }
    }
}
