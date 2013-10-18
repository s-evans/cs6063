public class ElectionStateOkRecvd extends ElectionStateBase {
    public void Handle ( EventInit evt ) {
        // TODO: Spawn coordinator msg timeout thread
    }

    public void Handle ( EventNoMsgTimeout evt ) {
        // Set state
        main.setElectionState(new ElectionStateNoLeader());
    }
}
