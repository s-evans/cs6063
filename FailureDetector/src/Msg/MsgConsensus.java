import java.nio.ByteBuffer;
import java.util.Random;

public class MsgConsensus extends MsgBase {
    protected int consensusValue;

    public MsgConsensus () {
        this.type = Type.Consensus;
        this.consensusValue = iTolerate.getConsensusValue();
    }

    public void Handle() {
        // Ignore ourselves for now
        if ( iTolerate.isSelf(uuid, runId) ) {
            return;
        }

        // Update the consensus value stated by the process
        iTolerate.updateConsensusValue(uuid, consensusValue);

        // If the consensus message is from the leader
        if ( iTolerate.isLeader(uuid) ) {

            if ( iTolerate.isByzantineFailure ) {
                // lie
                iTolerate.setConsensusValue(new Random().nextInt());
            } else {
                // Take the leader's value
                iTolerate.setConsensusValue(consensusValue);
            }

            // Handle the event
            iTolerate.getConsensusState().Handle(new EventConsensusRoundStart());
        }
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