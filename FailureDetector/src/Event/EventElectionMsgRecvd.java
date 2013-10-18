import java.util.UUID;

public class EventElectionMsgRecvd {
    protected UUID uuid;

    public EventElectionMsgRecvd(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }
}
