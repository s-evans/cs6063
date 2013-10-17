

public class MsgOk extends MsgBase {
    public MsgOk () {
        this.type = Type.Ok;
    }

    public void Handle() {
        // TODO: Spawn a thread to wait for coordinator message on a timeout. If timeout expires, send another election message.
    }
}