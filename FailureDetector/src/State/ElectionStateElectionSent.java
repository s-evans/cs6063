public class ElectionStateElectionSent extends ElectionStateBase {
    public void Handle ( EventInit evt ) {
        iLead.debugPrint("\nState = ElectionStateElectionSent");

        // Send election message
        iLead.sendMsg(MsgBase.Type.Election);

        // Set up a timeout event to occur
        iLead.setElectionMsgTimeout(1);
    }

    public void Handle ( EventNoMsgTimeout evt ) {
        iLead.debugPrint("\nHandling EventNoMsgTimeout");

        if ( iLead.isHighest() ) {
            // Send coordinator message
            iLead.sendMsg(MsgBase.Type.Coordinator);

            // Set state
            iLead.setElectionState(new ElectionStateHaveLeader(iLead.getSelf()));
        } else {
            // Send election message
            iLead.setElectionState(new ElectionStateElectionSent());
        }
    }

    public void Handle ( EventElectionMsgRecvd evt ) {
        iLead.debugPrint("\nHandling EventElectionMsgRecvd");

        // Validate UUID of the message
        if ( evt.getUuid().compareTo(iLead.getSelf()) == -1 ) {
            // Set state
            iLead.setElectionState(new ElectionStateOkSent());
        }
    }

    public void Handle ( EventOkMsgRecvd evt ) {
        iLead.debugPrint("\nHandling EventOkMsgRecvd");

        // Validate uuid
        if ( evt.getUuid().compareTo(iLead.getSelf()) != 1 ) {
             // Ignore OK msgs UUID's from those below us
            iLead.debugPrint("\nIgnoring EventOkMsgRecvd from not higher UUID");
            return;
        }

        // Change state
        iLead.setElectionState(new ElectionStateOkRecvd());
    }
}