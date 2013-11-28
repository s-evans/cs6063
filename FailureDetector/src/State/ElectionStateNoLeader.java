
public class ElectionStateNoLeader extends ElectionStateBase {
    public void Handle ( EventInit evt ) {
        // Debug
        iLead.debugPrint("\nState = ElectionStateNoLeader");

        // Change state
        iLead.setElectionState(new ElectionStateElectionSent());
    }
}