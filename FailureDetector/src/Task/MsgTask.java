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
            iLead.debugPrint("Still in hasJoinedGroup wait...");
           return;
        }

        if (iLead.isSelf(msg.getUuid(), msg.getRunId())) {
            //System.out.println("\n\tReceived msg from self, not tracking...");
            return;
        }

        // Debug
        iLead.debugPrint("\n\tRecvd UUID = " + msg.getUuid().toString());
        iLead.debugPrint("\n\tRecvd type = " + msg.getType().ordinal());
        iLead.debugPrint("\n\tRecvd runId = " + msg.getRunId());

        // Get this process's entry in the process list
        Record rcd = iLead.processList.get(msg.getUuid());
        Integer consensusValue = null;

        // If exists
        if ( rcd != null ) {

            if ( isDuplicate(msg) ) {
                iLead.debugPrint("Sending duplicate msg");
                iLead.sendMsg(MsgBase.Type.Duplicate);
                return;
            } else if ( isRestarted(msg, rcd) ) {
                ProcRestartTask restartTask = new ProcRestartTask(msg.getUuid());
                restartTask.run();
            }

            // Cancel the death task
            rcd.deathTask.cancel();

            // Copy forward the consensus value from the record
            consensusValue = rcd.consensusValue;
        }

        boolean pre = iLead.quorumExists();

        // Create a new death timeout task
        DeathTask dt = new DeathTask(msg.getUuid());

        // Add/replace entry
        Record newRcd = new Record(msg.getRunId(), dt, true, consensusValue);
        iLead.processList.put(msg.getUuid(), newRcd);

        // Schedule the new death timeout event
        iLead.setProcessDeathTimeout(dt);

        // Check for freshly established quorum
        if ( !pre && iLead.quorumExists() ) {
            iLead.getConsensusState().Handle(new EventQuorumReached());
        }

        // Handle the message
        msg.Handle();
    }

    public boolean isRestarted(MsgBase msg, Record existingRecord) {
        if (msg.getRunId() > existingRecord.runId) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isDuplicate(MsgBase msg) {
        if (msg.getUuid().compareTo(iLead.getSelf()) == 0) {
            return true;
        } else {
            return false;
        }
    }
}