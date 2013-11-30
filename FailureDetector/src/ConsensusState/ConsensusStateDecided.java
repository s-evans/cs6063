
public class ConsensusStateDecided extends ConsensusStateBase {

    public ConsensusStateDecided() {
        // Nothing to do here
    }

    public void Handle ( EventInit evt ) {
        System.out.print("\nConsensus reached! Value = " + iLead.getConsensusValue());
    }

    // TODO: Periodically send out consensus messages?

}