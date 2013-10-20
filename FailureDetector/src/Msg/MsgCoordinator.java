
public class MsgCoordinator extends MsgBase {
    public MsgCoordinator () {
        this.type = Type.Coordinator;
    }

    public void Handle() {
        // Pass this off to the state machine
        iLead.getElectionState().Handle(new EventCoordinatorMsg(uuid));
    }
}