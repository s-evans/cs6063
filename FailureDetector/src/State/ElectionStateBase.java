

abstract class ElectionStateBase {
    protected ElectionStateBase() {}

    // Subclasses must handle there own init event
    abstract void Handle ( EventInit evt );

    // Subclasses all must handle a new coordinator. No special handling.
    public final void Handle ( EventCoordinatorMsg evt ) {
        main.setElectionState(new ElectionStateHaveLeader(evt.getUuid()));
    }

    // All events are ignored by default unless overridden in subclasses
    public void Handle ( EventLeaderDeath evt ) {
        System.out.print("\nIgnoring Leader Death Event");
    }

    public void Handle ( EventNoMsgTimeout evt ) {
        System.out.print("\nIgnoring Msg Timeout Event");
    }

    public void Handle ( EventElectionMsgRecvd evt ) {
        System.out.print("\nIgnoring Election Msg Recvd Event");
    }

    public void Handle ( EventElectionMsgSent evt ) {
        System.out.print("\nIgnoring Election Msg Sent Event");
    }

    public void Handle ( EventOkMsgRecvd evt ) {
        System.out.print("\nIgnoring Ok Msg Recvd Event");
    }

    public void Handle ( EventOkMsgSent evt ) {
        System.out.print("\nIgnoring Ok Msg Sent Event");
    }

}