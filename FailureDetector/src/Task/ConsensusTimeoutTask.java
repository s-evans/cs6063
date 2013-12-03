import java.util.TimerTask;

public class ConsensusTimeoutTask extends TimerTask {
    public void run() {
        // Debug
        iTolerate.debugPrint("\nConsensus timeout occurred");

        // Create/Handle an Election Msg Timeout Event
        iTolerate.getConsensusState().Handle(new EventConsensusRoundEnd());
    }
}