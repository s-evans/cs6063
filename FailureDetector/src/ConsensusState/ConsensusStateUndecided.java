import java.util.Random;

public class ConsensusStateUndecided extends ConsensusStateBase {

    public ConsensusStateUndecided() {
        // Nothing to do here
    }

    public void Handle ( EventInit evt ) {
        // Nothing to do here
    }

    public void Handle ( EventQuorumReached evt ) {
        // If the leader detects that a quorum is reached
        if ( iLead.isLeader() ) {
            // Dream up a new consensus value
            iLead.setConsensusValue(new Random().nextInt());

            // Handle the event
            Handle(new EventConsensusRoundStart());
        }
    }

    public void Handle ( EventConsensusRoundStart evt ) {
        // Send the consensus value
        iLead.sendMsg(MsgBase.Type.Consensus);

        // Set the timeout event
        iLead.setConsensusRoundTimeout();
    }

    public void Handle ( EventConsensusRoundEnd evt ) {
        // Validate that quorum still exists
        if ( !iLead.quorumExists() ) {
            System.out.print("\nQuorum does not exist");
            return;
        }

        // Get the majority value from all processes
        Integer maj = iLead.getMajority();
        if ( maj == null ) {
            // Announce
            System.out.print("\nFailed to find majority value at consensus timeout");

            // Handle the event
            Handle(new EventQuorumReached());

            return;
        }

        // Majority value found, set the state
        iLead.setConsensusState(new ConsensusStateDecided());
    }

}