public class MsgByzantineFailure extends MsgBase {

    public MsgByzantineFailure () {
        this.type = Type.ByzantineFailure;
    }

    public void Handle() {
        if ( iTolerate.getSelf().compareTo(uuid) == 0 )  {
            iTolerate.logToGui("Set New byzantine Leader: " + uuid.toString());
            iTolerate.getElectionState().Handle(new EventElectionMsgRecvd(uuid));
        }
    }
}
