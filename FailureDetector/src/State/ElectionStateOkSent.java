
public class ElectionStateOkSent extends ElectionStateBase {
    public void Handle ( EventInit evt ) {
        iTolerate.debugPrint("\nState = ElectionStateOkSent");

        // Send Ok message
        iTolerate.sendMsg(MsgBase.Type.Ok);

        // Send election message
        iTolerate.setElectionState(new ElectionStateElectionSent());
    }
}
