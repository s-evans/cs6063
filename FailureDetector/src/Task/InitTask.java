import java.util.TimerTask;

public class InitTask extends TimerTask {
    public void run() {
        iLead.setElectionState(new ElectionStateNoLeader());
    }
}