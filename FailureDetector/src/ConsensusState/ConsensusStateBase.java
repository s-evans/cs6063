abstract class ConsensusStateBase {

    public ConsensusStateBase() {
        // Nothing to do here
    }

    // Subclasses must handle there own init event
    abstract void Handle ( EventInit evt );

    final public void Handle ( EventQuorumLost evt ) {
        iTolerate.logToGui("\nConsensus state undecided: Quorum lost");
        iTolerate.setConsensusState(new ConsensusStateUndecided());
    }

    final public void Handle ( EventNoLeader evt ) {
        iTolerate.logToGui("\nConsensus state undecided: Leader lost");
        iTolerate.setConsensusState(new ConsensusStateUndecided());
    }

    public void Handle ( EventQuorumReached evt ) {
        iTolerate.debugPrint("\nIgnoring " + evt.getClass().toString());
    }

    public void Handle ( EventConsensusRoundStart evt ) {
        iTolerate.debugPrint("\nIgnoring " + evt.getClass().toString());
    }

    public void Handle ( EventConsensusRoundEnd evt ) {
        iTolerate.debugPrint("\nIgnoring " + evt.getClass().toString());
    }

}