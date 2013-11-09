import java.util.TimerTask;

public class MsgTask extends TimerTask {
    protected MsgBase msg;

    public MsgTask ( MsgBase msg ) {
        this.msg = msg;
    }

    public void run() {
        // Debug
        iLead.debugPrint("\n\tRecvd UUID = " + msg.getUuid().toString());
        iLead.debugPrint("\n\tRecvd type = " + msg.getType().ordinal());
        iLead.debugPrint("\n\tRecvd runId = " + msg.getRunId());

        // Get this process's entry in the process list
        Record rcd = iLead.processList.get(msg.getUuid());

        // If exists
        if ( rcd != null ) {
            // Cancel the death task
            rcd.deathTask.cancel();
        }

        // Create a new death timeout task
        DeathTask dt = new DeathTask(msg.getUuid());

        // Add/replace entry
        Record newRcd = new Record(msg.getRunId(), dt);
        iLead.processList.put(msg.getUuid(), newRcd);

        // Schedule the new death timeout event
        iLead.setProcessDeathTimeout(dt);

        // TODO: Check runId of the process against that in the process list; On increment, that's a new run; On decrement, that's a duplicate; If runId is as expected, handle the message as normal;
        // TODO: Add DuplicateDetectedTask class, where we send a duplicate msg to the client. DuplicateMsg should contain UUID and RunId of the duplicate process; This will keep all messages the same size, while also not disturbing the legit process;
        // TODO: Add ProcRestartTask class, where we print something about restart, and check if it's the leader that restarted, and if so start a new election
        // TODO: Add DuplicateMsg, where we handle the duplicate message by checking the UUID and RunId against our own, and killing ourselves if it matches

        // Handle the message
        msg.Handle();
    }
}