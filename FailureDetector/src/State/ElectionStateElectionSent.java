public class ElectionStateElectionSent extends ElectionStateBase {
    public void Handle ( EventInit evt ) {
        main.debugPrint("\nState = ElectionStateElectionSent");

        // Send election message
        main.sendMsg(MsgBase.Type.Election);

        // Set up a timeout event to occur
        main.setElectionMsgTimeout();
    }

    public void Handle ( EventNoMsgTimeout evt ) {
        main.debugPrint("\nHandling EventNoMsgTimeout");

        if ( main.isHighest() ) {
            // Send coordinator message
            main.sendMsg(MsgBase.Type.Coordinator);
        } else {
            // Send election message
            main.setElectionState(new ElectionStateElectionSent());
        }
    }

    public void Handle ( EventOkMsgRecvd evt ) {
        main.debugPrint("\nHandling EventOkMsgRecvd");

        // Validate uuid
        if ( evt.getUuid().compareTo(main.getSelf()) != 1 ) {
             // Ignore OK msgs UUID's from those below us
            main.debugPrint("\nIgnoring EventOkMsgRecvd from not higher UUID");
            return;
        }

        // Change state
        main.setElectionState(new ElectionStateOkRecvd());
    }
}