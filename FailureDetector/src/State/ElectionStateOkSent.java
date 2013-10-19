
public class ElectionStateOkSent extends ElectionStateBase {
    public void Handle ( EventInit evt ) {
        main.debugPrint("\nState = ElectionStateOkSent");

        // Send Ok message
        main.sendMsg(MsgBase.Type.Ok);

        // Check out our status
        if ( main.isHighest() ) {
            // TODO: Handle case where we aren't delivering to ourselves

            // Send coordinator message
            main.sendMsg(MsgBase.Type.Coordinator);
        } else {
            // Send election message
            main.setElectionState(new ElectionStateElectionSent());
        }

    }
}
