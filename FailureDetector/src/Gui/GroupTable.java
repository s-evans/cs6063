import javax.swing.*;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GroupTable extends JTable implements TableModel {
    private final String[] columnNames = {"UUID", "Instance Number", "Alive", "Consensus Value"};
    private Object[][] data;

    public GroupTable(HashMap<UUID, Record> processList) {
        data = new Object[iLead.processList.size()][columnNames.length];
        int entryPos = 0;
        for (Map.Entry<UUID, Record> entry : processList.entrySet()) {
            data[entryPos][0] = entry.getKey();
            Record entryRecord = entry.getValue();
            data[entryPos][1] = entryRecord.runId;
            data[entryPos][2] = entryRecord.alive;
            data[entryPos][3] = entryRecord.consensusValue;
            entryPos++;
        }
    }

    public void setValueAt(Object value, int row, int col) {
        data[row][col] = value;
    }

    @Override
    public void addTableModelListener(TableModelListener l) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void removeTableModelListener(TableModelListener l) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getRowCount() {
        return data.length;
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return data[rowIndex][columnIndex];
    }
}
