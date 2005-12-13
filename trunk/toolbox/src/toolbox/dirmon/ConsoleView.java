package toolbox.dirmon;

import java.awt.BorderLayout;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import toolbox.util.ElapsedTime;
import toolbox.util.FontUtil;
import toolbox.util.dirmon.DirectoryMonitor;
import toolbox.util.dirmon.FileSnapshot;
import toolbox.util.dirmon.IDirectoryMonitorListener;
import toolbox.util.dirmon.event.FileEvent;
import toolbox.util.dirmon.event.StatusEvent;
import toolbox.util.ui.JSmartTextArea;

/**
 * Raw console for directory monitor generated events. Just a text area with
 * the events converted into strings with no formatting.
 */
public class ConsoleView extends JPanel implements IDirectoryMonitorListener {
    
    private static final DateFormat dateTimeFormat = 
        SimpleDateFormat.getDateTimeInstance();
    
    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------
    
    private JSmartTextArea messageArea_;

    /**
     * Map of start times for each directory scan. Used to sync up with the 
     * end time to determine the elapsed time for each scan.
     * <ul>
     *   <li>key = DirectoryMonitor.getName()
     *   <li>value = Date
     * </ul>
     * @see #statusChanged(StatusEvent)
     */
    private Map scanMap_;
    
    private Map discoveryMap_;
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------
    
    public ConsoleView() {
        buildView();
        scanMap_ = new HashMap();
        discoveryMap_ = new HashMap();
    }

    // -------------------------------------------------------------------------
    // Protected
    // -------------------------------------------------------------------------
    
    protected void buildView() {
        setLayout(new BorderLayout());
        
        messageArea_ = new JSmartTextArea("Welcome!\n", true, true);
        messageArea_.setRows(10);
        messageArea_.setColumns(80);
        messageArea_.setFont(FontUtil.getPreferredMonoFont());
        
        add(BorderLayout.CENTER, new JScrollPane(messageArea_));
    }
    
    // -------------------------------------------------------------------------
    // IDirectoryMonitorListener Interface 
    // -------------------------------------------------------------------------
    
    /*
     * @see toolbox.util.dirmon.IDirectoryMonitorListener#statusChanged(toolbox.util.dirmon.event.StatusEvent)
     */
    public void statusChanged(StatusEvent statusEvent) throws Exception {

        DirectoryMonitor monitor = statusEvent.getDirectoryMonitor();
        String name = monitor.getName();
        
        switch (statusEvent.getEventType()) {
            
            case StatusEvent.TYPE_START_SCAN :
                // Put scan start time in map
                scanMap_.put(monitor, new Date());
                break;
            
            case StatusEvent.TYPE_END_SCAN :
                // Extract scan start time from map and figure out elapsed time...
                Date endTime = new Date();
                Date startTime = (Date) scanMap_.remove(monitor);
                
                if (startTime != null) {
                    ElapsedTime elapsed = new ElapsedTime(startTime, endTime);
                    messageArea_.append(
                        "Scanned " 
                        + monitor.getMonitoredDirectories().size() 
                        + " dir(s) in " 
                        + name 
                        + " in " 
                        + elapsed 
                        + ". Snoozing for "
                        + new ElapsedTime(0, monitor.getDelay())
                        + "...\n");
                }
                else {
                    messageArea_.append(
                        "Start time map entry for scan " + name + " not found!\n");
                }
                break;
            
            case StatusEvent.TYPE_START_DISCOVERY:
                discoveryMap_.put(monitor, new Date());
                messageArea_.append("Discovery started for " + name + " ...\n");
                break;
                
            case StatusEvent.TYPE_END_DISCOVERY:
                // Extract scan start time from map and figure out elapsed time...
                Date end = new Date();
                Date start = (Date) discoveryMap_.remove(monitor);
                
                if (start != null) {
                    ElapsedTime elapsed = new ElapsedTime(start, end);
                    messageArea_.append(
                        "Discovered " 
                        + monitor.getMonitoredDirectories().size() 
                        + " dir(s) in " 
                        + name 
                        + " in " 
                        + elapsed 
                        + "\n");
                }
                else {
                    messageArea_.append("Start time map entry for discovery " + name + " not found!\n");
                }
                break;
                
            default:
                throw new IllegalArgumentException(
                    "Unrecognized event type: " 
                    + statusEvent.getEventType());
        }
    }
    
    /*
     * @see toolbox.util.dirmon.IDirectoryMonitorListener#directoryActivity(toolbox.util.dirmon.event.FileEvent)
     */
    public void directoryActivity(FileEvent event) throws Exception {
        
        StringBuffer msg = new StringBuffer();
        
        FileSnapshot afterSnapshot = event.getAfterSnapshot();
        FileSnapshot beforeSnapshot = event.getBeforeSnapshot();
        
        switch (event.getEventType()) {
        
            case FileEvent.TYPE_FILE_CHANGED :
                msg.append(
                    "File changed: " 
                    + afterSnapshot.getAbsolutePath() 
                    + "\n");
                
                msg.append(
                    "Size        : " 
                    + beforeSnapshot.getLength() 
                    + " -> " 
                    + afterSnapshot.getLength()
                    + "\n");
                
                msg.append(
                    "Timestamp   : " 
                    + dateTimeFormat.format(new Date(
                        beforeSnapshot.getLastModified())) 
                    + " -> " 
                    + dateTimeFormat.format(new Date(
                        afterSnapshot.getLastModified()))
                    + "\n");
                
                break;
                
                
            case FileEvent.TYPE_FILE_CREATED :
                msg.append(
                    "File created: " 
                    + afterSnapshot.getAbsolutePath() 
                    + "\n");
                
                msg.append(
                    "Size        : " 
                    + afterSnapshot.getLength()
                    + "\n");
                
                msg.append(
                    "Timestamp   : " 
                    + dateTimeFormat.format(new Date(
                        afterSnapshot.getLastModified()))
                    + "\n");
                
                break;

                
            case FileEvent.TYPE_FILE_DELETED :
                msg.append(
                    "File deleted: " 
                    + beforeSnapshot.getAbsolutePath() 
                    + "\n");
                
                msg.append(
                    "Size        : " 
                    + beforeSnapshot.getLength() 
                    + "\n");
                
                msg.append(
                    "Timestamp   : " 
                    + dateTimeFormat.format(new Date(
                        beforeSnapshot.getLastModified())) 
                    + "\n");
                
                break;

            default:
                throw new IllegalArgumentException(
                    "unrecognized event type: " 
                    + event.getEventType());
        }

        messageArea_.append(msg.toString());
        messageArea_.append("\n");
    }
}