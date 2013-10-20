
public class ElectionStateOkSent extends ElectionStateBase {
    public void Handle ( EventInit evt ) {
        iLead.debugPrint("\nState = ElectionStateOkSent");

        // Send Ok message
        iLead.sendMsg(MsgBase.Type.Ok);

        // Send election message
        iLead.setElectionState(new ElectionStateElectionSent());
    }
}
