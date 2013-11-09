import java.nio.ByteBuffer;
import java.util.UUID;

abstract class MsgBase {
    // List of supported messages types in the protocol
    enum Type {
        Unknown,
        HeartBeat,
        Election,
        Ok,
        Coordinator,
        Duplicate
    }

    // Create a generic message object based on type
    public static MsgBase Factory( Type msgType ) {
        switch ( msgType ) {
            case HeartBeat:
                return new MsgHeartBeat();

            case Election:
                return new MsgElection();

            case Ok:
                return new MsgOk();

            case Coordinator:
                return new MsgCoordinator();

            case Duplicate:
                return new MsgDuplicate();

            default:
                throw new RuntimeException("Invalid msg type");
        }

    }

    // Create a message object from a byte buffer
    public static MsgBase Factory( ByteBuffer bb ) {
        // Validate the datagram length
        if ( bb.array().length != iLead.datagramSize ) {
            System.out.print("\nDatagram length validation failed");
            throw new RuntimeException();
        }

        // Get data from the packet
        UUID uuid = new UUID(bb.getLong(), bb.getLong());
        Type msgType = Type.values()[bb.getInt()];
        int runId = bb.getInt();

        // Create / modify object
        MsgBase msg = Factory(msgType);
        msg.uuid = uuid;
        msg.runId = runId;

        // Give it away now
        return msg;
    }

    // Data fields
    protected Type type;
    protected UUID uuid;
    protected int runId;

    // Default constructor
    protected MsgBase () {
        this.type = Type.Unknown;
        this.uuid = iLead.getSelf();
        this.runId = iLead.getInstanceNum();
    }

    // Create a byte buffer from a message object
    public ByteBuffer toByteBuffer () {
        ByteBuffer bb = ByteBuffer.allocate(iLead.datagramSize);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        bb.putInt(type.ordinal());
        bb.putInt(runId);
        return bb;
    }

    // Get the UUID of the message
    public UUID getUuid() {
        return uuid;
    }

    public Type getType () {
        return type;
    }

    public int getRunId() {
        return runId;
    }

    // Handle the message
    public abstract void Handle();
}