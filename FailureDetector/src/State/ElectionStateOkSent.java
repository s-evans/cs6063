
public class ElectionStateOkSent extends ElectionStateBase {
    public void Handle ( EventInit evt ) {
        main.debugPrint("\nState = ElectionStateOkSent");

        // Send Ok message
        main.sendMsg(MsgBase.Type.Ok);

        // Check out our status
        if ( main.isHighest() ) {
            // Send coordinator message
            main.sendMsg(MsgBase.Type.Coordinator);

            // Set state
            main.setElectionState(new ElectionStateHaveLeader(main.getSelf()));
        } else {
            // Send election message
            main.setElectionState(new ElectionStateElectionSent());
        }

    }
}
