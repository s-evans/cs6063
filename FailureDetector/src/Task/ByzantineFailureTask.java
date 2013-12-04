import java.util.TimerTask;

public class ByzantineFailureTask extends TimerTask {

    public void run() {

        // Send byzantine failure message
        iTolerate.sendMsg(MsgBase.Type.ByzantineFailure);

        // Debug
        iTolerate.debugPrint("\nByzantine Failure Packet Sent");
        iTolerate.debugPrint("\n\tSent UUID: " + iTolerate.getSelf().toString());
    }
}
