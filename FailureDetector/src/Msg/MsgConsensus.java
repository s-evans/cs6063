import java.nio.ByteBuffer;

public class MsgConsensus extends MsgBase {
    public int consensusValue;

    public MsgConsensus () {
        this.type = Type.Consensus;
        this.consensusValue = 0;
    }

    public MsgConsensus ( int consensusValue ) {
        // default construct
        this();

        // set members
        this.consensusValue = consensusValue;
    }

    public void Handle() {
        // Update the consensus value stated by the process
        iLead.updateConsensusValue(uuid, consensusValue);

        // TODO: Check the list for consensus
    }

    // Override the default implementation
    public void fromByteBufferSub ( ByteBuffer bb ) {
        // Get the additional parameter from the buffer
        consensusValue = bb.getInt();
    }

    // Override default implementation
    public void toByteBufferSub ( ByteBuffer bb ) {
        // Populate the buffer with additional parameters
        bb.putInt(consensusValue);
    }

    public int getDatagramSize () {
        // Get the base datagram and add the additional message size to it
        return super.getDatagramSize () + 4;
    }
}