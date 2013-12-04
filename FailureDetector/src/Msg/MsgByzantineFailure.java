public class MsgByzantineFailure extends MsgBase {
    protected int consensusValue;

    public MsgByzantineFailure () {
        this.type = Type.ByzantineFailure;
        this.consensusValue = iTolerate.getConsensusValue();
    }

    public void Handle() {
        // TODO lie about the consensus value
        if ( iTolerate.isSelf(uuid, runId) ) {

            // Update the consensus value stated by the process
            iTolerate.updateConsensusValue(uuid, consensusValue);

            // Use own consensus value
            iTolerate.setConsensusValue(consensusValue);

            // Handle the event
            iTolerate.getConsensusState().Handle(new EventConsensusRoundStart());
        }
    }
}
