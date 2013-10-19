public class ElectionStateOkRecvd extends ElectionStateBase {
    public void Handle ( EventInit evt ) {
        main.debugPrint("\nState = ElectionStateOkRecvd");

        // Set up a timeout event to occur
        main.setElectionMsgTimeout(2);
    }

    public void Handle ( EventNoMsgTimeout evt ) {
        main.debugPrint("\nHandling EventNoMsgTimeout");

        // Set state
        main.setElectionState(new ElectionStateNoLeader());
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
