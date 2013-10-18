import java.util.UUID;

public class EventOkMsgRecvd {
    protected UUID uuid;

    public EventOkMsgRecvd(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }
}
