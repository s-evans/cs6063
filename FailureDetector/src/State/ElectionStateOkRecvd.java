public class ElectionStateOkRecvd extends ElectionStateBase {
    public void Handle ( EventInit evt ) {
        main.debugPrint("\nState = ElectionStateOkRecvd");

        // Set up a timeout event to occur
        main.setElectionMsgTimeout();
    }

    public void Handle ( EventNoMsgTimeout evt ) {
        main.debugPrint("\nHandling EventNoMsgTimeout");

        // Set state
        main.setElectionState(new ElectionStateNoLeader());
    }
}
