import java.util.TimerTask;

public class MsgTask extends TimerTask {
    protected MsgBase msg;

    public MsgTask ( MsgBase msg ) {
        this.msg = msg;
    }

    public void run() {
        // Debug
        main.debugPrint("\n\tRecvd UUID = " + msg.getUuid().toString());
        main.debugPrint("\n\tRecvd type = " + msg.getType().ordinal());

        // Get this process's entry in the process list
        DeathTask dt = main.processList.get(msg.getUuid());

        // Check if it exists
        if ( dt != null ) {
            // Cancel the current death timeout event
            dt.cancel();
        }

        // Create a new death timeout task
        dt = new DeathTask(msg.getUuid());

        // Add/replace entry
        main.processList.put(msg.getUuid(), dt);

        // Schedule the new death timeout event
        main.setProcessDeathTimeout(dt);

        // Handle the message
        msg.Handle();
    }
}