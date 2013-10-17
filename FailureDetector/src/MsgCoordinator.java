
public class MsgCoordinator extends MsgBase {
    public MsgCoordinator () {
        this.type = Type.Coordinator;
    }

    public void Handle() {
        // Set the leader to the specified process id
        main.setLeader(uuid);
    }
}