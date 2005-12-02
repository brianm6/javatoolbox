package toolbox.dirmon;

import java.awt.BorderLayout;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import com.l2fprod.common.swing.renderer.DateRenderer;

import toolbox.util.dirmon.DirectoryMonitor;
import toolbox.util.dirmon.DirectoryMonitorEvent;
import toolbox.util.dirmon.FileSnapshot;
import toolbox.util.dirmon.IDirectoryMonitorListener;
import toolbox.util.ui.table.BorderedCellRenderer;
import toolbox.util.ui.table.JSmartTable;
import toolbox.util.ui.table.TableSorter;

/**
 * Directory monitor view that shows DirectoryMonitorEvents in a table. 
 */
public class EventTableView extends JPanel implements IDirectoryMonitorListener {

    private static Logger logger_ =  Logger.getLogger(DirectoryMonitor.class);

    // -------------------------------------------------------------------------
    // Constants
    // -------------------------------------------------------------------------
    
    private static final int INDEX_SEQUENCE = 0;
    private static final int INDEX_ACTIVITY = 1;
    private static final int INDEX_DIR = 2;
    private static final int INDEX_FILE = 3;
    private static final int INDEX_BEFORE_SIZE = 4;
    private static final int INDEX_AFTER_SIZE = 5;
    private static final int INDEX_BEFORE_DATE = 6;
    private static final int INDEX_AFTER_DATE = 7;
    
    private static final String[] columnHeaders = new String[] {
        "#",
        "Activity", 
        "Dir", 
        "File", 
        "Old Size", 
        "New Size", 
        "Old Date", 
        "New Date"
    };
    
    private static final DateFormat dateTimeFormat = 
        SimpleDateFormat.getDateTimeInstance(
            SimpleDateFormat.SHORT, 
            SimpleDateFormat.SHORT);

    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------
    
    private JSmartTable table_;
    private DefaultTableModel model_;
    private int sequenceNum_;
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------
    
    public EventTableView() {
        this.sequenceNum_ = 1;
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

        // Decorate the default cell renderer with extra padding so its not so
        // scrunched up together
        Border paddedBorder = BorderFactory.createEmptyBorder(0,5,0,5);
        
        TableCellRenderer decoratedRenderer =
            new BorderedCellRenderer(
                new DefaultTableCellRenderer(), 
                paddedBorder);
                
        table_.setDefaultRenderer(Object.class, decoratedRenderer);
        table_.setDefaultRenderer(Integer.class, decoratedRenderer);
        table_.setDefaultRenderer(Long.class, decoratedRenderer);
        
        // Format dates specially with shortened mm/dd/yyyy
        table_.setDefaultRenderer(Date.class,
            new BorderedCellRenderer(
                new DateRenderer(dateTimeFormat),
                paddedBorder));
        
        table_.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        table_.setAutoTail(true);
        table_.setRowHeight((int) (table_.getRowHeight() * 1.1));
    }

    // -------------------------------------------------------------------------
    // IDirectoryMonitorListener Interface 
    // -------------------------------------------------------------------------
    
    /*
     * @see toolbox.util.dirmon.IDirectoryMonitorListener#directoryActivity(toolbox.util.dirmon.DirectoryMonitorEvent)
     */
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
            data[INDEX_SEQUENCE] = new Integer(sequenceNum_++);
                
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