import java.util.Date;

public class Record {
    protected int sequenceNumber;
    protected Date time;

    public Record (int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
        time = new Date();
    }

    public int getSequenceNumber () {
        return sequenceNumber;
    }

    public Date getTime () {
        return time;
    }
}