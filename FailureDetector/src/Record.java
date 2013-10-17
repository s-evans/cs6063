import java.util.Date;

public class Record {
    protected Date time;

    public Record () {
        time = new Date();
    }

    public Date getTime () {
        return time;
    }
}