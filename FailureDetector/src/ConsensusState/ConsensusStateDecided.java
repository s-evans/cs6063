
public class ConsensusStateDecided extends ConsensusStateBase {

    public ConsensusStateDecided() {
        // Nothing to do here
    }

    public void Handle ( EventInit evt ) {
        System.out.print("\nConsensus reached! Value = " + iLead.getConsensusValue());
    }

    public void Handle ( EventConsensusRoundStart evt ) {
        // Set state
        iLead.setConsensusState(new ConsensusStateUndecided());

        // Propagate event
        iLead.getConsensusState().Handle(evt);
    }

    // TODO: Periodically send out consensus messages?

}