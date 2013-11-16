import java.util.TimerTask;

/**
 * Handle messages while waiting to join group.
 */
public class GroupJoinTask extends TimerTask {

    @Override
    public void run() {
        iLead.hasJoinedGroup = true;
        iLead.debugPrint("Joined Group.");
    }
}
