
public class ElectionStateNoLeader extends ElectionStateBase {
    public void Handle ( EventInit evt ) {
        iLead.debugPrint("\nState = ElectionStateNoLeader");

        // Change state
        iLead.setElectionState(new ElectionStateElectionSent());
    }
}