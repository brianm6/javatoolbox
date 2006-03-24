package toolbox.dirmon;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.l2fprod.common.swing.renderer.DateRenderer;

import toolbox.util.AppLauncher;
import toolbox.util.Platform;
import toolbox.util.dirmon.DirectoryMonitor;
import toolbox.util.dirmon.FileSnapshot;
import toolbox.util.dirmon.IDirectoryMonitorListener;
import toolbox.util.dirmon.event.FileEvent;
import toolbox.util.dirmon.event.StatusEvent;
import toolbox.util.ui.ImageCache;
import toolbox.util.ui.JHeaderPanel;
import toolbox.util.ui.JSmartToggleButton;
import toolbox.util.ui.SmartAction;
import toolbox.util.ui.table.BorderedCellRenderer;
import toolbox.util.ui.table.JSmartTable;
import toolbox.util.ui.table.TableSorter;
import toolbox.util.ui.table.action.AutoTailAction;

/**
 * View UI component for a {@link toolbox.util.dirmon.DirectoryMonitor} that 
 * shows all generated {@link toolbox.util.dirmon.event.StatusEvent}s in a 
 * table. Features include:
 * <ul>
 *  <li>Built in table header button to launch selected file.
 *  <li>Built in table header button to diff files.
 *  <li>Built in table header button to show file history.
 *  <li>Built in table header button to autoscroll tables as rows are added.
 * </ul> 
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
        
        //add(BorderLayout.CENTER, new JScrollPane(table_));

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
        
        // Format dates with shortened mm/dd/yyyy
        table_.setDefaultRenderer(Date.class,
            new BorderedCellRenderer(
                new DateRenderer(dateTimeFormat),
                paddedBorder));
        
        ////////////////////////////////////////////////////////////////////////
        
        JButton launchButton =
            JHeaderPanel.createButton(
                ImageCache.getIcon(ImageCache.IMAGE_PLAY),
                "Launch File",
                new LaunchAction());
        
        JButton diffButton =
            JHeaderPanel.createButton(
                ImageCache.getIcon(ImageCache.IMAGE_BRACES),
                "Diff File",
                new DiffAction());

        JButton historyButton =
            JHeaderPanel.createButton(
                ImageCache.getIcon(ImageCache.IMAGE_COLUMNS),
                "File History",
                new HistoryAction());
        
        JSmartToggleButton autoTailButton =
            JHeaderPanel.createToggleButton(
                ImageCache.getIcon(ImageCache.IMAGE_LOCK),
                "Automatically tail output",
                new AutoTailAction(table_));

        autoTailButton.toggleOnProperty(table_, JSmartTable.PROP_AUTOTAIL);
        
        try {
            // TODO: Move to JSmartToggleButton
            autoTailButton.setSelected(
                !(new Boolean(BeanUtils.getProperty(
                    table_, JSmartTable.PROP_AUTOTAIL)).booleanValue()));
        }
        catch (IllegalAccessException e) {
            logger_.warn(e);
        }
        catch (InvocationTargetException e) {
            logger_.warn(e);
        }
        catch (NoSuchMethodException e) {
            logger_.warn(e);
        }
        
        JToolBar tb = JHeaderPanel.createToolBar();
        
        if (Platform.isWindows())
            tb.add(launchButton);
        
        tb.add(diffButton);
        tb.add(historyButton);
        tb.add(autoTailButton);
        
        JHeaderPanel tablePanel = 
            new JHeaderPanel("Activity", tb, new JScrollPane(table_));

        add(BorderLayout.CENTER, tablePanel);
        
        table_.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        logger_.debug("Setting tail flag in eventtable...");
        table_.setAutoTail(false);
        table_.setRowHeight((int) (table_.getRowHeight() * 1.1));
    }

    // -------------------------------------------------------------------------
    // IDirectoryMonitorListener Interface 
    // -------------------------------------------------------------------------
    
    /*
     * @see toolbox.util.dirmon.IDirectoryMonitorListener#statusChanged(toolbox.util.dirmon.event.StatusEvent)
     */
    public void statusChanged(StatusEvent statusEvent) throws Exception {
    }
    
    /*
     * @see toolbox.util.dirmon.IDirectoryMonitorListener#directoryActivity(toolbox.util.dirmon.DirectoryMonitorEvent)
     */
    public void directoryActivity(FileEvent event) throws Exception{
        TableRow row = new TableRow(event);
        model_.addRow(row.toData());
    }

    // -------------------------------------------------------------------------
    // TableRow 
    // -------------------------------------------------------------------------
    
    class TableRow {
        
        FileEvent event;
        
        public TableRow(FileEvent event) {
            this.event = event;
        }
        
        public Object[] toData() {
            Object[] data = new Object[columnHeaders.length];
            FileSnapshot before = event.getBeforeSnapshot();
            FileSnapshot after = event.getAfterSnapshot();
            data[INDEX_SEQUENCE] = new Integer(sequenceNum_++);
                
            switch (event.getEventType()) {
                
                case FileEvent.TYPE_FILE_CHANGED :
                    data[INDEX_ACTIVITY] = "Modified";
                    data[INDEX_DIR] = FilenameUtils.getFullPathNoEndSeparator(after.getAbsolutePath());
                    data[INDEX_FILE] = FilenameUtils.getName(after.getAbsolutePath());
                    data[INDEX_BEFORE_SIZE] = new Long(before.getLength());
                    data[INDEX_AFTER_SIZE] = new Long(after.getLength());
                    data[INDEX_BEFORE_DATE] = new Date(before.getLastModified());
                    data[INDEX_AFTER_DATE] = new Date(after.getLastModified());
                    break;
                    
                case FileEvent.TYPE_FILE_CREATED :
                    data[INDEX_ACTIVITY] = "New";
                    data[INDEX_DIR] = FilenameUtils.getFullPathNoEndSeparator(after.getAbsolutePath());
                    data[INDEX_FILE] = FilenameUtils.getName(after.getAbsolutePath());
                    data[INDEX_BEFORE_SIZE] = null;
                    data[INDEX_AFTER_SIZE] = new Long(after.getLength());
                    data[INDEX_BEFORE_DATE] = null;
                    data[INDEX_AFTER_DATE] = new Date(after.getLastModified());
                    break;
                    
                case FileEvent.TYPE_FILE_DELETED :
                    data[INDEX_ACTIVITY] = "Deleted";
                    data[INDEX_DIR] = FilenameUtils.getFullPathNoEndSeparator(before.getAbsolutePath());
                    data[INDEX_FILE] = FilenameUtils.getName(before.getAbsolutePath());
                    data[INDEX_BEFORE_SIZE] = new Long(before.getLength());
                    data[INDEX_AFTER_SIZE] = null;
                    data[INDEX_BEFORE_DATE] = new Date(before.getLastModified());
                    data[INDEX_AFTER_DATE] = null;
                    break;
    
                default:
                    throw new IllegalArgumentException("unrecognized event type: " + event.getEventType());
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
    
    // -------------------------------------------------------------------------
    // DiffAction 
    // -------------------------------------------------------------------------
    
    class DiffAction extends SmartAction {
        
        DiffAction() {
            super("Diff", true, false, null);
            putValue(SHORT_DESCRIPTION, "Diffs the selected file");
        }
        
        
        public void runAction(ActionEvent e) throws Exception {
            
            int idx = table_.getSelectedRow();
            
            if (idx >= 0) {
                String dir = (String) model_.getValueAt(idx, INDEX_DIR); 
                String file  = (String) model_.getValueAt(idx,INDEX_FILE);
                String path = dir + File.separator + file;
                String command = "cleartool diff -graphical -predecessor \"" + path + "\"";
                //String command = "ls -l";
                
                Process p = Runtime.getRuntime().exec(command);
                InputStream out = p.getInputStream();
                InputStream err = p.getErrorStream();
                logger_.debug("Diff Output: " + IOUtils.toString(out));
                logger_.debug("Diff Error: " + IOUtils.toString(err));
                logger_.debug("Diff Exit value: " + p.exitValue());
                
//                JTextArea textArea = new JTextArea(40, 80);
//                textArea.setFont(FontUtil.getPreferredMonoFont());
//                textArea.setText(
//                    "Output:\n"
//                    + output
//                    +"\n\nError:\n"
//                    + IOUtils.toString(err));
//                
//                JSmartOptionPane.showMessageDialog(
//                    SwingUtil.getFrameAncestor(EventTableView.this),
//                    new JScrollPane(textArea));
            }
        }
    }

    // -------------------------------------------------------------------------
    // HistoryAction 
    // -------------------------------------------------------------------------
    
    class HistoryAction extends SmartAction {
        
        HistoryAction() {
            super("History", true, false, null);
            putValue(SHORT_DESCRIPTION, "File history");
        }
        
        
        public void runAction(ActionEvent e) throws Exception {
            
            int idx = table_.getSelectedRow();
            
            if (idx >= 0) {
                String dir = (String) model_.getValueAt(idx, INDEX_DIR); 
                String file  = (String) model_.getValueAt(idx,INDEX_FILE);
                String path = dir + File.separator + file;
                String command = "cleartool lshistory -graphical \"" + path + "\"";
                //String command = "ls -l";
                
                Process p = Runtime.getRuntime().exec(command);
                InputStream out = p.getInputStream();
                InputStream err = p.getErrorStream();
                logger_.debug("History Output: " + IOUtils.toString(out));
                logger_.debug("History Error: " + IOUtils.toString(err));
                logger_.debug("History Exit value: " + p.exitValue());
            }
        }
    }
    
    // -------------------------------------------------------------------------
    // LaunchAction 
    // -------------------------------------------------------------------------
    
    class LaunchAction extends SmartAction {
        
        LaunchAction() {
            super("Launch", true, true, null);
            putValue(SHORT_DESCRIPTION, "Launch file");
        }
        
        
        public void runAction(ActionEvent e) throws Exception {
            
            int idx = table_.getSelectedRow();
            
            if (idx >= 0) {
                String dir = (String) model_.getValueAt(idx, INDEX_DIR); 
                String file  = (String) model_.getValueAt(idx,INDEX_FILE);
                String path = dir + File.separator + file;
                AppLauncher.launch(path);
            }
        }
    }    
}