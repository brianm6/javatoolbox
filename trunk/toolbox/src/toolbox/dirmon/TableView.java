package toolbox.dirmon;

import java.awt.BorderLayout;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.io.FilenameUtils;

import com.l2fprod.common.swing.renderer.DateRenderer;

import toolbox.util.dirmon.DirectoryMonitorEvent;
import toolbox.util.dirmon.FileSnapshot;
import toolbox.util.dirmon.IDirectoryMonitorListener;
import toolbox.util.ui.table.JSmartTable;
import toolbox.util.ui.table.TableSorter;

public class TableView extends JPanel implements IDirectoryMonitorListener {

    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------
    
    private DateFormat dateTimeFormat = 
        SimpleDateFormat.getDateTimeInstance(
            SimpleDateFormat.SHORT, 
            SimpleDateFormat.MEDIUM);

    private JSmartTable table_;
    private DefaultTableModel model_;
    
    private static final int INDEX_SEQUENCE = 0;
    private static final int INDEX_ACTIVITY = 1;
    private static final int INDEX_DIR = 2;
    private static final int INDEX_FILE = 3;
    private static final int INDEX_BEFORE_SIZE = 4;
    private static final int INDEX_AFTER_SIZE = 5;
    private static final int INDEX_BEFORE_DATE = 6;
    private static final int INDEX_AFTER_DATE = 7;
    
    private String[] columnHeaders = new String[] {
        "#",
        "Activity", 
        "Dir", 
        "File", 
        "Old Size", 
        "New Size", 
        "Old Date", 
        "New Date"
    };
    
    private int sequenceNum;
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------
    
    public TableView() {
        this.sequenceNum = 1;
        buildView();
    }

    // -------------------------------------------------------------------------
    // Protected
    // -------------------------------------------------------------------------
    
    protected void buildView() {
        setLayout(new BorderLayout());
        model_ = new EventTableModel(columnHeaders, 0);
        TableSorter sorter = new TableSorter(model_);
        table_ = new JSmartTable(sorter);
        sorter.setTableHeader(table_.getTableHeader());
        add(BorderLayout.CENTER, new JScrollPane(table_));

        // Format dates specially with shortened mm/dd/yyyy
        table_.setDefaultRenderer(Date.class, new DateRenderer(dateTimeFormat));
    }
    
    // -------------------------------------------------------------------------
    // IDirectoryMonitorListener Interface 
    // -------------------------------------------------------------------------
    
    public void directoryActivity(DirectoryMonitorEvent event) throws Exception{
        TableRow row = new TableRow(event);
        model_.addRow(row.toData());
    }

    // -------------------------------------------------------------------------
    // TableRow 
    // -------------------------------------------------------------------------
    
    class TableRow {
        
        DirectoryMonitorEvent event;
        
        public TableRow(DirectoryMonitorEvent event) {
            this.event = event;
        }
        
        public Object[] toData() {
            Object[] data = new Object[columnHeaders.length];
            FileSnapshot before = event.getBeforeSnapshot();
            FileSnapshot after = event.getAfterSnapshot();
            data[INDEX_SEQUENCE] = new Integer(sequenceNum++);
                
            switch (event.getEventType()) {
                
                case DirectoryMonitorEvent.TYPE_CHANGED :
                    data[INDEX_ACTIVITY] = "Modified";
                    data[INDEX_DIR] = FilenameUtils.getPath(after.getAbsolutePath());
                    data[INDEX_FILE] = FilenameUtils.getName(after.getAbsolutePath());
                    data[INDEX_BEFORE_SIZE] = new Long(before.getLength());
                    data[INDEX_AFTER_SIZE] = new Long(after.getLength());
                    data[INDEX_BEFORE_DATE] = new Date(before.getLastModified());
                    data[INDEX_AFTER_DATE] = new Date(after.getLastModified());
                    break;
                    
                case DirectoryMonitorEvent.TYPE_CREATED :
                    data[INDEX_ACTIVITY] = "New";
                    data[INDEX_DIR] = FilenameUtils.getPath(after.getAbsolutePath());
                    data[INDEX_FILE] = FilenameUtils.getName(after.getAbsolutePath());
                    data[INDEX_BEFORE_SIZE] = null;
                    data[INDEX_AFTER_SIZE] = new Long(after.getLength());
                    data[INDEX_BEFORE_DATE] = null;
                    data[INDEX_AFTER_DATE] = new Date(after.getLastModified());
                    break;
                    
                case DirectoryMonitorEvent.TYPE_DELETED :
                    data[INDEX_ACTIVITY] = "Deleted";
                    data[INDEX_DIR] = FilenameUtils.getPath(before.getAbsolutePath());
                    data[INDEX_FILE] = FilenameUtils.getName(before.getAbsolutePath());
                    data[INDEX_BEFORE_SIZE] = new Long(before.getLength());
                    data[INDEX_AFTER_SIZE] = null;
                    data[INDEX_BEFORE_DATE] = new Date(before.getLastModified());
                    data[INDEX_AFTER_DATE] = null;
                    break;
    
                default:
                    throw new IllegalArgumentException(
                        "unrecognized event type: " 
                        + event.getEventType());
            }
            
            return data;
        }
    }

    // -------------------------------------------------------------------------
    // EventTableModel
    // -------------------------------------------------------------------------
    
    class EventTableModel extends DefaultTableModel {
        
        public EventTableModel(Object[] columnNames, int rowCount) {
            super(columnNames, rowCount);
        }

        public Class getColumnClass(int column) {
            Class dataType = super.getColumnClass(column);
            
            switch (column) {
                
                case INDEX_SEQUENCE :
                    dataType = Integer.class;
                    break;
                    
                case INDEX_AFTER_SIZE:
                case INDEX_BEFORE_SIZE:    
                    dataType = Long.class;
                    break;
                    
                case INDEX_ACTIVITY :
                case INDEX_DIR:
                case INDEX_FILE:
                    dataType = String.class;
                    break;
                    
                case INDEX_AFTER_DATE :
                case INDEX_BEFORE_DATE:
                    dataType = Date.class;
                    break;
            }
            
            return dataType;
          }         
    }
}