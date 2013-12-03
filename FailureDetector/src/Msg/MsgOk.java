

public class MsgOk extends MsgBase {
    public MsgOk () {
        this.type = Type.Ok;
    }

    public void Handle() {
        // Pass this off to the state machine
        iTolerate.getElectionState().Handle(new EventOkMsgRecvd(uuid));
    }
}