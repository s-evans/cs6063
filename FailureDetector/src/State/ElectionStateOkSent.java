
public class ElectionStateOkSent extends ElectionStateBase {
    public void Handle ( EventInit evt ) {
        main.debugPrint("\nState = ElectionStateOkSent");

        // Send Ok message
        main.sendMsg(MsgBase.Type.Ok);

        // Send election message
        main.setElectionState(new ElectionStateElectionSent());
    }
}
