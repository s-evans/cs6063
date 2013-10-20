import java.util.TimerTask;

public class HeartBeatTask extends TimerTask {
    public void run() {
        // Send heart beat message
        iLead.sendMsg(MsgBase.Type.HeartBeat);

        // Debug
        iLead.debugPrint("\nHeart Beat Packet Sent");
        iLead.debugPrint("\n\tSent UUID: " + iLead.getSelf().toString());
    }
}