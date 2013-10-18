
public class ElectionStateNoLeader extends ElectionStateBase {
    public void Handle ( EventInit evt ) {
        // Change state
        main.setElectionState(new ElectionStateElectionSent());
    }
}