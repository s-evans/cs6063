
public class ConsensusStateDecided extends ConsensusStateBase {

    public ConsensusStateDecided() {
        // Nothing to do here
    }

    public void Handle ( EventInit evt ) {
        System.out.print("\nConsensus reached! Value = " + iTolerate.getConsensusValue());
    }

    public void Handle ( EventConsensusRoundStart evt ) {
        // Set state
        iTolerate.setConsensusState(new ConsensusStateUndecided());

        // Propagate event
        iTolerate.getConsensusState().Handle(evt);
    }

    @Override
    public String toString() {
        return "Decided";
    }

    // TODO: Periodically send out consensus messages?

}