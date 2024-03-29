import java.util.TimerTask;

public class MsgTask extends TimerTask {
    protected MsgBase msg;

    public MsgTask ( MsgBase msg ) {
        this.msg = msg;
    }

    public void run() {

        // In grace period allowing duplicate message to be handled
        // allows real duplicate process to fail
        if(msg.type == MsgBase.Type.Duplicate && !iTolerate.hasJoinedGroup) {
           msg.Handle();
           return;
        }  else if (!iTolerate.hasJoinedGroup) {
            iTolerate.debugPrint("Still in hasJoinedGroup wait...");
           return;
        }

        if (iTolerate.isSelf(msg.getUuid(), msg.getRunId())) {
            //iTolerate.logToGui("\n\tReceived msg from self, not tracking...");
            return;
        }

        // Debug
        iTolerate.debugPrint("\n\tRecvd UUID = " + msg.getUuid().toString());
        iTolerate.debugPrint("\n\tRecvd type = " + msg.getType().ordinal());
        iTolerate.debugPrint("\n\tRecvd runId = " + msg.getRunId());

        // Get this process's entry in the process list
        Record rcd = iTolerate.processList.get(msg.getUuid());

        // If exists
        if ( rcd != null ) {

            if ( isDuplicate(msg) ) {
                iTolerate.debugPrint("Sending duplicate msg");
                iTolerate.sendMsg(MsgBase.Type.Duplicate);
                return;
            } else if ( isRestarted(msg, rcd) ) {
                ProcRestartTask restartTask = new ProcRestartTask(msg.getUuid());
                restartTask.run();
            }

            // Cancel the death task
            rcd.deathTask.cancel();
        }

        // Create a new death timeout task
        DeathTask dt = new DeathTask(msg.getUuid());

        // Add/replace entry
        Record newRcd = new Record(msg.getRunId(), dt, true, msg.consensusValue);
        iTolerate.processList.put(msg.getUuid(), newRcd);

        // Schedule the new death timeout event
        iTolerate.setProcessDeathTimeout(dt);

        // Calculate majority value
        iTolerate.majorityValue = iTolerate.getMajority();

        if ( iTolerate.majorityValue != null ) {
            if ( iTolerate.majorityValue.compareTo(msg.consensusValue) != 0 ) {
                // Check for byzantine leader
                if ( iTolerate.isLeader(msg.uuid) ) {
                    iTolerate.getElectionState().Handle(new EventByzantineLeader());
                }

                // Don't handle messages from non-agreeing processes
                return;
            }
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
        if (msg.getUuid().compareTo(iTolerate.getSelf()) == 0) {
            return true;
        } else {
            return false;
        }
    }
}