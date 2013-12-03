public class ElectionStateElectionSent extends ElectionStateBase {
    public void Handle ( EventInit evt ) {
        iTolerate.debugPrint("\nState = ElectionStateElectionSent");

        // Send election message
        iTolerate.sendMsg(MsgBase.Type.Election);

        // Set up a timeout event to occur
        iTolerate.setElectionMsgTimeout(1);
    }

    public void Handle ( EventNoMsgTimeout evt ) {
        iTolerate.debugPrint("\nHandling EventNoMsgTimeout");

        if ( iTolerate.isHighest() ) {
            // Send coordinator message
            iTolerate.sendMsg(MsgBase.Type.Coordinator);

            // Set state
            iTolerate.setElectionState(new ElectionStateHaveLeader(iTolerate.getSelf()));
        } else {
            // Send election message
            iTolerate.setElectionState(new ElectionStateElectionSent());
        }
    }

    public void Handle ( EventElectionMsgRecvd evt ) {
        iTolerate.debugPrint("\nHandling EventElectionMsgRecvd");

        // Validate UUID of the message
        if ( evt.getUuid().compareTo(iTolerate.getSelf()) == -1 ) {
            // Set state
            iTolerate.setElectionState(new ElectionStateOkSent());
        }
    }

    public void Handle ( EventOkMsgRecvd evt ) {
        iTolerate.debugPrint("\nHandling EventOkMsgRecvd");

        // Validate uuid
        if ( evt.getUuid().compareTo(iTolerate.getSelf()) != 1 ) {
             // Ignore OK msgs UUID's from those below us
            iTolerate.debugPrint("\nIgnoring EventOkMsgRecvd from not higher UUID");
            return;
        }

        // Change state
        iTolerate.setElectionState(new ElectionStateOkRecvd());
    }
}