import java.util.TimerTask;

public class InitTask extends TimerTask {
    public void run() {
        iTolerate.setElectionState(new ElectionStateNoLeader());
    }
}