import java.util.UUID;

public class ElectionStateHaveLeader extends ElectionStateBase {
    protected UUID uuid;

    public ElectionStateHaveLeader (UUID uuid) {
        this.uuid = uuid;
    }

    public void Handle ( EventInit evt ) {
        // Set the new leader
        main.setLeader(uuid);
    }

    public void Handle ( EventLeaderDeath evt ) {
        // Set state
        main.setElectionState(new ElectionStateNoLeader());
    }

    public void Handle ( EventElectionMsgRecvd evt ) {
        // Set state
        main.setElectionState(new ElectionStateNoLeader());
    }
}
