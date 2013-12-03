import java.util.TimerTask;

public class ElectionTimeoutTask extends TimerTask {
    public void run() {
        // Debug
        iTolerate.debugPrint("\nElection timeout occurred");

        // Create/Handle an Election Msg Timeout Event
        iTolerate.getElectionState().Handle(new EventNoMsgTimeout());
    }
}