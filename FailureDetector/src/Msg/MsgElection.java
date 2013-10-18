import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class MsgElection extends MsgBase {
    public MsgElection () {
        this.type = Type.Election;
    }

    public void Handle() {
        // Pass this off to the state machine
        main.getElectionState().Handle(new EventElectionMsgRecvd(uuid));
    }
}