import java.util.TimerTask;
import java.util.UUID;

public class MsgTask extends TimerTask {
    protected MsgBase msg;

    public MsgTask ( MsgBase msg ) {
        this.msg = msg;
    }

    public void run() {

        // In grace period allowing duplicate message to be handled
        // allows real duplicate process to fail
        if(msg.type == MsgBase.Type.Duplicate && !iLead.hasJoinedGroup) {
           msg.Handle();
           return;
        }  else if (!iLead.hasJoinedGroup) {
           return;
        }

        if (iLead.isSelf(msg.getUuid(), msg.getRunId(), msg.getSsid())) {
            //System.out.println("\n\tReceived msg from self, not tracking...");
            return;
        }

        // Debug
//        System.out.println("\n\tRecvd UUID = " + msg.getUuid().toString());
//        System.out.println("\n\tRecvd type = " + msg.getType().ordinal());
//        System.out.println("\n\tRecvd runId = " + msg.getRunId());
//        System.out.println("\n\tRecvd ssid = " + msg.getSsid());
//        System.out.println("Current List: ");
//        for (UUID uuid : iLead.processList.keySet()) {
//            System.out.println(uuid.toString());
//        }

        // Get this process's entry in the process list
        Record rcd = iLead.processList.get(msg.getUuid());

        // If exists
        if ( rcd != null ) {

            if ( isDuplicate(msg, rcd) ) {
                // TODO: Handle duplicate process
                // TODO: Create new MsgDuplicate, send it, and return so that the rest of this logic is cut out
                System.out.println("Sending duplicate msg");
                iLead.sendMsg(MsgBase.Type.Duplicate);
                return;
            } else if ( isRestarted(msg, rcd) ) {
                ProcRestartTask restartTask = new ProcRestartTask(msg.getUuid());
                restartTask.run();
                // TODO: Handle new run
                // TODO: Create ProcRestartTask and schedule immediately
            }

            // Cancel the death task
            rcd.deathTask.cancel();
        }

        // Create a new death timeout task
        DeathTask dt = new DeathTask(msg.getUuid());

        // Add/replace entry
        Record newRcd = new Record(msg.getRunId(), dt, msg.getSsid(), true);
        iLead.processList.put(msg.getUuid(), newRcd);

        // Schedule the new death timeout event
        iLead.setProcessDeathTimeout(dt);

        // Handle the message
        msg.Handle();
    }

    public boolean isRestarted(MsgBase msg, Record existingRecord) {
        if (msg.getSsid() != existingRecord.ssid) {
            return false;
        }

        if (msg.getRunId() > existingRecord.runId) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isDuplicate(MsgBase msg, Record existingRecord) {
        if (msg.getSsid() != existingRecord.ssid) {
            return true;
        } else {
            return false;
        }
    }
}