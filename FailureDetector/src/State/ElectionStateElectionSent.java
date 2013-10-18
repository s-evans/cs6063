public class ElectionStateElectionSent extends ElectionStateBase {
    public void Handle ( EventInit evt ) {
        // Send election message
        main.sendMsg(MsgBase.Type.Election);

        // TODO: Spawn timeout wait thread
    }

    public void Handle ( EventNoMsgTimeout evt ) {
        // Send coordinator message
        main.sendMsg(MsgBase.Type.Coordinator);
    }

    public void Handle ( EventOkMsgRecvd evt ) {
        // Validate uuid
        if ( evt.getUuid().compareTo(main.getSelf()) != 1 ) {
             // Ignore OK msgs UUID's from those below us
            return;
        }

        // Change state
        main.setElectionState(new ElectionStateOkRecvd());
    }
}