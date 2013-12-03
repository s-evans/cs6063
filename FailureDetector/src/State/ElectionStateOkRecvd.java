public class ElectionStateOkRecvd extends ElectionStateBase {
    public void Handle ( EventInit evt ) {
        iTolerate.debugPrint("\nState = ElectionStateOkRecvd");

        // Set up a timeout event to occur
        iTolerate.setElectionMsgTimeout(2);
    }

    public void Handle ( EventNoMsgTimeout evt ) {
        iTolerate.debugPrint("\nHandling EventNoMsgTimeout");

        // Set state
        iTolerate.setElectionState(new ElectionStateNoLeader());
    }

    public void Handle ( EventOkMsgRecvd evt ) {
        iTolerate.debugPrint("\nHandling EventOkMsgRecvd");

        // Validate uuid
        if ( evt.getUuid().compareTo(iTolerate.getSelf()) != 1 ) {
            // Ignore OK msgs UUID's from those below us
            iTolerate.debugPrint("\nIgnoring EventOkMsgRecvd from not higher UUID");
            return;
        }

        // Change state
        iTolerate.setElectionState(new ElectionStateOkRecvd());
    }
}
