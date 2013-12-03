import java.util.UUID;

public class ElectionStateHaveLeader extends ElectionStateBase {
    protected UUID uuid;

    public ElectionStateHaveLeader (UUID uuid) {
        this.uuid = uuid;
    }

    public void Handle ( EventInit evt ) {
        iTolerate.debugPrint("\nState = ElectionStateHaveLeader");

        // Set the new leader
        iTolerate.setLeader(uuid);

        // If the current process is the leader
        if ( iTolerate.isLeader() && iTolerate.quorumExists() ) {
            System.out.print("\n\tStarting new round"); // TODO: Remove
            // Start a consensus operation
            iTolerate.getConsensusState().Handle(new EventConsensusRoundStart());
        }
    }

    public void Handle ( EventLeaderDeath evt ) {
        iTolerate.debugPrint("\nHandling " + evt.getClass());

        // Set state
        iTolerate.setElectionState(new ElectionStateNoLeader());

        // Handle no leader
        iTolerate.getConsensusState().Handle(new EventNoLeader());
    }

    public void Handle ( EventByzantineLeader evt ) {
        iTolerate.debugPrint("\nHandling " + evt.getClass());

        // Set state
        iTolerate.setElectionState(new ElectionStateNoLeader());

        // Handle no leader
        iTolerate.getConsensusState().Handle(new EventNoLeader());
    }

    public void Handle ( EventElectionMsgRecvd evt ) {
        iTolerate.debugPrint("\nHandling " + evt.getClass());

        // Validate UUID of the message
        if ( evt.getUuid().compareTo(iTolerate.getSelf()) == -1 ) {
            // Set state
            iTolerate.setElectionState(new ElectionStateOkSent());
        }
    }
}
