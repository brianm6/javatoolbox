package toolbox.dirmon;

import java.awt.BorderLayout;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import toolbox.util.FontUtil;
import toolbox.util.dirmon.DirectoryMonitorEvent;
import toolbox.util.dirmon.IDirectoryMonitorListener;
import toolbox.util.ui.JSmartTextArea;

public class ConsoleView extends JPanel implements IDirectoryMonitorListener {

    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------
    
    private JSmartTextArea messageArea_;

    private DateFormat dateTimeFormat = SimpleDateFormat.getDateTimeInstance();

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------
    
    public ConsoleView() {
        buildView();
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
    
    public void directoryActivity(DirectoryMonitorEvent event) throws Exception{
        
        StringBuffer msg = new StringBuffer();
        
        switch (event.getEventType()) {
        
            case DirectoryMonitorEvent.TYPE_CHANGED :
                msg.append(
                    "File changed: " 
                    + event.getAfterSnapshot().getAbsolutePath() 
                    + "\n");
                
                msg.append(
                    "Size        : " 
                    + event.getBeforeSnapshot().getLength() 
                    + " -> " 
                    + event.getAfterSnapshot().getLength()
                    + "\n");
                
                msg.append(
                    "Timestamp   : " 
                    + dateTimeFormat.format(new Date(
                        event.getBeforeSnapshot().getLastModified())) 
                    + " -> " 
                    + dateTimeFormat.format(new Date(
                        event.getAfterSnapshot().getLastModified()))
                    + "\n");
                
                break;
                
                
            case DirectoryMonitorEvent.TYPE_CREATED :
                msg.append(
                    "File created: " 
                    + event.getAfterSnapshot().getAbsolutePath() 
                    + "\n");
                
                msg.append(
                    "Size        : " 
                    + event.getAfterSnapshot().getLength()
                    + "\n");
                
                msg.append(
                    "Timestamp   : " 
                    + dateTimeFormat.format(new Date(
                        event.getAfterSnapshot().getLastModified()))
                    + "\n");
                
                break;

                
            case DirectoryMonitorEvent.TYPE_DELETED :
                msg.append(
                    "File deleted: " 
                    + event.getBeforeSnapshot().getAbsolutePath() 
                    + "\n");
                
                msg.append(
                    "Size        : " 
                    + event.getBeforeSnapshot().getLength() 
                    + "\n");
                
                msg.append(
                    "Timestamp   : " 
                    + dateTimeFormat.format(new Date(
                        event.getBeforeSnapshot().getLastModified())) 
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