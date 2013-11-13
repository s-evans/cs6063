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

            // Check runId of the process against that in the process list
            if ( msg.getRunId() > rcd.runId ) {
                System.out.println(msg.getUuid().toString() + " has restarted");
                // TODO: Handle new run
                // TODO: Create ProcRestartTask and schedule immediately
            } else if ( msg.getRunId() < rcd.runId ) {
                // TODO: Handle duplicate process
                // TODO: Create new MsgDuplicate, send it, and return so that the rest of this logic is cut out
            }

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

        // Handle the message
        msg.Handle();
    }
}