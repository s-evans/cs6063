import java.util.UUID;

public class MsgDuplicate extends MsgBase {

    public MsgDuplicate () {
        this.type = Type.Duplicate;
    }

    public void Handle() {
        if ( iLead.getSelf().compareTo(uuid) == 0 )  {
            // TODO: Print something
            System.out.println("Cannot join group, process with uuid: " + uuid.toString() + " already exists");
            iLead.stopRunning();
            // TODO: Kill self. May be complicated by multi threading?
        }
    }
}