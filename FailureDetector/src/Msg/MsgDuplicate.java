public class MsgDuplicate extends MsgBase {

    public MsgDuplicate () {
        this.type = Type.Duplicate;
    }

    public void Handle() {
        if ( iTolerate.getSelf().compareTo(uuid) == 0 )  {
            iTolerate.logToGui("Cannot join group, process with uuid: " + uuid.toString() + " already exists");
            iTolerate.stopRunning();
        }
    }
    }