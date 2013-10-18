import java.util.UUID;

public class EventCoordinatorMsg {
    protected UUID uuid;

    public EventCoordinatorMsg(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }
}
