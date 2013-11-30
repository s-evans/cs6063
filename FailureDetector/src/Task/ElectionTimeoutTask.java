import java.util.TimerTask;

public class ElectionTimeoutTask extends TimerTask {
    public void run() {
        // Debug
        iLead.debugPrint("\nElection timeout occurred");

        // Create/Handle an Election Msg Timeout Event
        iLead.getElectionState().Handle(new EventNoMsgTimeout());
    }
}