

public class MsgHeartBeat extends MsgBase {
    public MsgHeartBeat () {
        this.type = Type.HeartBeat;
    }

    public void Handle() {
        // Nothing to do here
    }
}