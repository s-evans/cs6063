import java.util.TimerTask;

public class ConsensusTimeoutTask extends TimerTask {
    public void run() {
        // Debug
        iLead.debugPrint("\nConsensus timeout occurred");

        // Create/Handle an Election Msg Timeout Event
        iLead.getConsensusState().Handle(new EventConsensusRoundEnd());
    }
}