import java.util.UUID;

public class MsgDuplicate extends MsgBase {
    public int runId;

    public MsgDuplicate () {
        this.type = Type.Duplicate;
    }

    public void Handle() {
        if ( uuid == iLead.getSelf() && runId == iLead.getInstanceNum() )  {
            // TODO: Print something
            System.out.println("Cannot join group, process with uuid: " + uuid.toString() + " already exists");
            iLead.stopRunning();
            // TODO: Kill self. May be complicated by multi threading?
        }
    }
}