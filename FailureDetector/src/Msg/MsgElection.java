public class MsgElection extends MsgBase {
    public MsgElection () {
        this.type = Type.Election;
    }

    public void Handle() {
        // Pass this off to the state machine
        iTolerate.getElectionState().Handle(new EventElectionMsgRecvd(uuid));
    }
}