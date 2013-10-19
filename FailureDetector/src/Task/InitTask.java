import java.util.TimerTask;

public class InitTask extends TimerTask {
    public void run() {
        main.setElectionState(new ElectionStateNoLeader());
    }
}