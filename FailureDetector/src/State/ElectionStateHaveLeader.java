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

        // If the current process is the leader
        if ( iLead.isLeader() && iLead.quorumExists() ) {
            System.out.print("\n\tStarting new round"); // TODO: Remove
            // Start a consensus operation
            iLead.getConsensusState().Handle(new EventConsensusRoundStart());
        }
    }

    public void Handle ( EventLeaderDeath evt ) {
        iLead.debugPrint("\nHandling " + evt.getClass());

        // Set state
        iLead.setElectionState(new ElectionStateNoLeader());

        // Handle no leader
        iLead.getConsensusState().Handle(new EventNoLeader());
    }

    public void Handle ( EventByzantineLeader evt ) {
        iLead.debugPrint("\nHandling " + evt.getClass());

        // Set state
        iLead.setElectionState(new ElectionStateNoLeader());

        // Handle no leader
        iLead.getConsensusState().Handle(new EventNoLeader());
    }

    public void Handle ( EventElectionMsgRecvd evt ) {
        iLead.debugPrint("\nHandling " + evt.getClass());

        // Validate UUID of the message
        if ( evt.getUuid().compareTo(iLead.getSelf()) == -1 ) {
            // Set state
            iLead.setElectionState(new ElectionStateOkSent());
        }
    }
}
