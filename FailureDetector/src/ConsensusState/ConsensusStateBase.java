abstract class ConsensusStateBase {

    public ConsensusStateBase() {
        // Nothing to do here
    }

    // Subclasses must handle there own init event
    abstract void Handle ( EventInit evt );

    final public void Handle ( EventQuorumLost evt ) {
        iLead.setConsensusState(new ConsensusStateUndecided());
    }

    final public void Handle ( EventNoLeader evt ) {
        iLead.setConsensusState(new ConsensusStateUndecided());
    }

    public void Handle ( EventQuorumReached evt ) {
        iLead.debugPrint("\nIgnoring " + evt.getClass().toString());
    }

    public void Handle ( EventConsensusRoundStart evt ) {
        iLead.debugPrint("\nIgnoring " + evt.getClass().toString());
    }

    public void Handle ( EventConsensusRoundEnd evt ) {
        iLead.debugPrint("\nIgnoring " + evt.getClass().toString());
    }

}