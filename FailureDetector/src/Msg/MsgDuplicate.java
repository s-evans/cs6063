import java.util.UUID;

public class MsgDuplicate extends MsgBase {
    public int runId;

    public MsgDuplicate () {
        this.type = Type.Duplicate;
    }

    public void Handle() {
        if ( uuid == iLead.getSelf() && runId == iLead.getInstanceNum() )  {
            // TODO: Print something
            // TODO: Kill self. May be complicated by multi threading?
        }
    }
}