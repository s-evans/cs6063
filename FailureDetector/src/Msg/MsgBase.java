import java.nio.ByteBuffer;
import java.util.UUID;

abstract class MsgBase {
    // UUID + Type + Run ID + SSID
    public static final int minDatagramSize = 16 + 4 + 4 + 4;
    public static final int maxDatagramSize = minDatagramSize + 4;

    // List of supported messages types in the protocol
    enum Type {
        Unknown,
        HeartBeat,
        Election,
        Ok,
        Coordinator,
        Duplicate,
        Consensus
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

            case Consensus:
                return new MsgConsensus();

            default:
                throw new RuntimeException("Invalid msg type");
        }

    }

    // Create a message object from a byte buffer
    public static MsgBase Factory( ByteBuffer bb, int length ) {
        // Validate the length of the packet
        if ( length < MsgBase.minDatagramSize ) {
            System.out.print("\nDatagram length validation failed");
            throw new RuntimeException("\nDatagram length validation failed");
        }

        // Create the message object
        MsgBase msg = Factory(Type.values()[bb.getInt()]);

        // Populate the message from the stream
        msg.fromByteBuffer(bb, length);

        // Give it away now
        return msg;
    }

    // Data fields
    protected Type type;
    protected UUID uuid;
    protected int runId;
    protected int ssid;

    // Default constructor
    protected MsgBase () {
        this.type = Type.Unknown;
        this.uuid = iLead.getSelf();
        this.runId = iLead.getInstanceNum();
        this.ssid = iLead.getSsid();
    }

    // Populate members from a byte buffer
    public final void fromByteBuffer ( ByteBuffer bb, int length ) {
        // Validate length of the message for the subclass
        if ( length < getDatagramSize() ) {
            throw new RuntimeException("\nDatagram length validation failed");
        }

        // Pull out base class information from the buffer
        uuid = new UUID(bb.getLong(), bb.getLong());
        runId = bb.getInt();
        ssid = bb.getInt();

        // Do any subclass initialization
        fromByteBufferSub(bb);
    }

    protected void fromByteBufferSub ( ByteBuffer bb ) {
        return;
    }

    // Create a byte buffer from a message object
    public final ByteBuffer toByteBuffer () {
        // Allocate enough space for the byte buffer in the subclasses
        ByteBuffer bb = ByteBuffer.allocate(getDatagramSize());

        // Populate standard fields
        bb.putInt(type.ordinal());
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        bb.putInt(runId);
        bb.putInt(ssid);

        // Allow for subclass specialization
        toByteBufferSub(bb);

        // Return the buffer with the populated components
        return bb;
    }

    protected void toByteBufferSub(ByteBuffer bb) {
        return;
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

    public int getSsid() {
        return ssid;
    }

    public int getDatagramSize () {
        return minDatagramSize;
    }

    // Handle the message
    public abstract void Handle();
}