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
            Handle(new EventConsensusRoundStart());
        }
    }

    public void Handle ( EventConsensusRoundStart evt ) {
        // Dream up a new consensus value
        if ( iLead.isLeader() ) {
            iLead.setConsensusValue(new Random().nextInt());
        }

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

        // Check majority operation output
        if ( maj == null ) {
            // Announce
            System.out.print("\nFailed to find majority value at consensus timeout");

            // Handle the event
            Handle(new EventQuorumReached());

            return;
        }

        // Check majority function output against the leader's consensus value to check for byzantine leader
        Integer leaderVal = iLead.getLeaderConsensusValue();
        if ( leaderVal != null && maj.compareTo(leaderVal) != 0 ) {
            // Announce
            System.out.print("\nByzantine leader detected!");
            System.out.print("\n\tmajority = " + maj + "; leader = " + iLead.getLeaderConsensusValue());   //TOdo: REMOVE

            // Handle the event
            iLead.getElectionState().Handle(new EventByzantineLeader());

            return;
        }

        // Majority value found, set the state
        iLead.setConsensusState(new ConsensusStateDecided());
    }

}