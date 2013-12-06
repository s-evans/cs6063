import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class UserInterface implements ActionListener {
    private JTable peerStatusTable;
    private JTable nodeStatusTable;
    private JButton byzantineFailureButton;
    private JButton repairButton;
    public JPanel uiForm;
    private JToolBar buttonBar;
    private JScrollPane nodeStatus;
    private JTabbedPane tabbedPane1;
    private JScrollPane groupPane;
    private JPanel statusPane;
    private JPanel logPane;
    private JTextArea textArea1;
    private Timer peerUpdateTimer;

    private final String[] NODE_STATUS_COLUMNS = {"UUID", "Leader", "Proposed Consensus Value", "Group Consensus Majority"};
    private final int PEER_TABLE_UPDATE_RATE = 500;
    private final int PEER_TABLE_UPDATE_DELAY = 1000;

    public UserInterface() {
        byzantineFailureButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                iTolerate.startByzantineFailure();
                byzantineFailureButton.setEnabled(false);
                repairButton.setEnabled(true);
            }
        });

        repairButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                iTolerate.repairNode();
                byzantineFailureButton.setEnabled(true);
                repairButton.setEnabled(false);
            }
        });
    }

    public void updateLogPanel(String message) {
        textArea1.append(message);
    }

    private void createUIComponents() {
        Object[][] nodeData = {{iTolerate.getSelf(), iTolerate.getLeader(), iTolerate.getConsensusValue(), iTolerate.majorityValue}};

        this.nodeStatusTable = new JTable(nodeData, NODE_STATUS_COLUMNS);

        this.peerStatusTable = new JTable();
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
        this.nodeStatusTable.setValueAt(iTolerate.majorityValue, 0, 3);

        GroupTable peerTableModel = (GroupTable) this.peerStatusTable.getModel();
        peerTableModel.updateGroupList(iTolerate.processList);
    }
}
