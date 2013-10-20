public class ElectionStateOkRecvd extends ElectionStateBase {
    public void Handle ( EventInit evt ) {
        iLead.debugPrint("\nState = ElectionStateOkRecvd");

        // Set up a timeout event to occur
        iLead.setElectionMsgTimeout(2);
    }

    public void Handle ( EventNoMsgTimeout evt ) {
        iLead.debugPrint("\nHandling EventNoMsgTimeout");

        // Set state
        iLead.setElectionState(new ElectionStateNoLeader());
    }

    public void Handle ( EventOkMsgRecvd evt ) {
        iLead.debugPrint("\nHandling EventOkMsgRecvd");

        // Validate uuid
        if ( evt.getUuid().compareTo(iLead.getSelf()) != 1 ) {
            // Ignore OK msgs UUID's from those below us
            iLead.debugPrint("\nIgnoring EventOkMsgRecvd from not higher UUID");
            return;
        }

        // Change state
        iLead.setElectionState(new ElectionStateOkRecvd());
    }
}
