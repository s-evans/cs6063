import java.util.UUID;

public class MsgDuplicate extends MsgBase {
    private int runId;

    public MsgDuplicate (UUID uuid, int runId) {
        this.type = Type.Duplicate;
        this.runId = runId;
        this.uuid = uuid;
    }

    public int getRunId() {
        return runId;
    }

    public void Handle() {
        // if ( uuid == iLead.getSelf() && runId == iLead.getInstanceNum() )  {
            // TODO: Print something
            // TODO: Kill self. May be complicated by multi threading?

        // }
    }
}