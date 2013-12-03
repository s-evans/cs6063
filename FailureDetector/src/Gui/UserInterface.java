import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UserInterface implements ActionListener {
    private JTable peerStatusTable;
    private JTable nodeStatusTable;
    private JButton byzantineFailureButton;
    private JButton repairButton;
    public JPanel uiForm;
    private JScrollPane groupPane;
    private JToolBar buttonBar;
    private JScrollPane nodeStatus;
    private Timer peerUpdateTimer;

    private final String[] NODE_STATUS_COLUMNS = {"UUID", "Leader", "Consensus Value"};
    private final int PEER_TABLE_UPDATE_RATE = 500;
    private final int PEER_TABLE_UPDATE_DELAY = 1000;

    private void createUIComponents() {
        Object[][] nodeData = {{iTolerate.getSelf(), iTolerate.getLeader(), iTolerate.getConsensusValue()}};
        this.nodeStatusTable = new JTable(nodeData, NODE_STATUS_COLUMNS);

        this.peerStatusTable = new JTable(null, GroupTable.columnNames);

        this.peerStatusTable.setModel(new GroupTable(iTolerate.processList));

        //Periodically update the peer table
        peerUpdateTimer = new Timer(PEER_TABLE_UPDATE_RATE, this);
        peerUpdateTimer.setInitialDelay(PEER_TABLE_UPDATE_DELAY);
        peerUpdateTimer.setRepeats(true);
        peerUpdateTimer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.nodeStatusTable.setValueAt(iTolerate.getSelf(), 0, 0);
        this.nodeStatusTable.setValueAt(iTolerate.getLeader(), 0, 1);
        this.nodeStatusTable.setValueAt(iTolerate.getConsensusValue(), 0, 2);

        GroupTable peerTableModel = (GroupTable) this.peerStatusTable.getModel();
        peerTableModel.updateGroupList(iTolerate.processList);
    }
}
