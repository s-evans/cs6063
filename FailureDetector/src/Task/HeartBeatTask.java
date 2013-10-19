import java.util.TimerTask;

public class HeartBeatTask extends TimerTask {
    public void run() {
        // Send heart beat message
        main.sendMsg(MsgBase.Type.HeartBeat);

        // Debug
        main.debugPrint("\nHeart Beat Packet Sent");
        main.debugPrint("\n\tSent UUID: " + main.getSelf().toString());
    }
}