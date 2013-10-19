import java.util.TimerTask;

public class ElectionTimeoutTask extends TimerTask {
    public void run() {
        main.debugPrint("\nTimeout occurred");

        // Create/Handle an Election Msg Timeout Event
        main.getElectionState().Handle(new EventNoMsgTimeout());
    }
}