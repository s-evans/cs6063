
public class ElectionStateOkSent extends ElectionStateBase {
    public void Handle ( EventInit evt ) {
        // Send Ok message
        main.sendMsg(MsgBase.Type.Ok);

        // Check out our status
        if ( main.isHighest() ) {
            // Send coordinator message
            main.sendMsg(MsgBase.Type.Coordinator);
        } else {
            // Send election message
            main.setElectionState(new ElectionStateElectionSent());
        }

    }
}