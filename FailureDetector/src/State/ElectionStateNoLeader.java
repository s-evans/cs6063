
public class ElectionStateNoLeader extends ElectionStateBase {
    public void Handle ( EventInit evt ) {
        // Debug
        iTolerate.debugPrint("\nState = ElectionStateNoLeader");

        // Change state
        iTolerate.setElectionState(new ElectionStateElectionSent());
    }
}