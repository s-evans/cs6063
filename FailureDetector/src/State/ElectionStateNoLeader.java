
public class ElectionStateNoLeader extends ElectionStateBase {
    public void Handle ( EventInit evt ) {
        main.debugPrint("\nState = ElectionStateNoLeader");

        // Change state
        main.setElectionState(new ElectionStateElectionSent());
    }
}