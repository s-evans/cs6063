

abstract class ElectionStateBase {
    protected ElectionStateBase() {}

    // Subclasses must handle there own init event
    abstract void Handle ( EventInit evt );

    // Subclasses all must handle a new coordinator. No special handling.
    public final void Handle ( EventCoordinatorMsg evt ) {
        // Validate uuid against our own
        if ( evt.getUuid().compareTo(iTolerate.getSelf()) == 0 ) {
            // Ignore our own coordinator message
            return;
        }

        // Set state
        iTolerate.setElectionState(new ElectionStateHaveLeader(evt.getUuid()));
    }

    // All events are ignored by default unless overridden in subclasses
    public void Handle ( EventLeaderDeath evt ) {
        iTolerate.debugPrint("\nIgnoring Leader Death Event");
    }

    public void Handle ( EventByzantineLeader evt ) {
        iTolerate.debugPrint("\nIgnoring Byzantine Leader Event");
    }

    public void Handle ( EventNoMsgTimeout evt ) {
        iTolerate.debugPrint("\nIgnoring Msg Timeout Event");
    }

    public void Handle ( EventElectionMsgRecvd evt ) {
        iTolerate.debugPrint("\nIgnoring Election Msg Recvd Event");
    }

    public void Handle ( EventElectionMsgSent evt ) {
        iTolerate.debugPrint("\nIgnoring Election Msg Sent Event");
    }

    public void Handle ( EventOkMsgRecvd evt ) {
        iTolerate.debugPrint("\nIgnoring Ok Msg Recvd Event");
    }

    public void Handle ( EventOkMsgSent evt ) {
        iTolerate.debugPrint("\nIgnoring Ok Msg Sent Event");
    }

}