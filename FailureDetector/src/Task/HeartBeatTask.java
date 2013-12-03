import java.util.TimerTask;

public class HeartBeatTask extends TimerTask {
    public void run() {
        // Send heart beat message
        iTolerate.sendMsg(MsgBase.Type.HeartBeat);

        // Debug
        iTolerate.debugPrint("\nHeart Beat Packet Sent");
        iTolerate.debugPrint("\n\tSent UUID: " + iTolerate.getSelf().toString());
    }
}