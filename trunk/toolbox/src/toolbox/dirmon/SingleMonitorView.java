package toolbox.dirmon;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JPanel;

import org.apache.log4j.Logger;

import toolbox.util.dirmon.DirectoryMonitor;
import toolbox.util.ui.tabbedpane.JSmartTabbedPane;
import toolbox.util.ui.tabbedpane.SmartTabbedPaneListener;

/**
 * SingleMonitorView is responsible for viewing and controlling a single
 * DirectoryMonitor.
 */
public class SingleMonitorView extends JPanel implements SmartTabbedPaneListener {

    private static final Logger logger_ = 
        Logger.getLogger(SingleMonitorView.class);
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    private DirectoryMonitor monitor;
    private EventTableView tableView;
 
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    public SingleMonitorView(DirectoryMonitor monitor) {
        setMonitor(monitor);
        buildView();
    }
    
    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------
    
    protected void buildView() {
        setLayout(new BorderLayout());
        tableView = new EventTableView(getMonitor().getRootDirectory().getAbsolutePath());
        getMonitor().addDirectoryMonitorListener(tableView);
        ControllerView controllerView = new ControllerView(this.monitor);
        add(BorderLayout.CENTER, tableView);
        add(BorderLayout.SOUTH, controllerView);
    }
 
    //--------------------------------------------------------------------------
    // SmartTabbedPaneListener Interface
    //--------------------------------------------------------------------------
    
    /*
     * @see toolbox.util.ui.tabbedpane.SmartTabbedPaneListener#tabClosing(toolbox.util.ui.tabbedpane.JSmartTabbedPane, int)
     */
    public void tabClosing(JSmartTabbedPane tabbedPane, int tabIndex) {
        Component source = tabbedPane.getComponentAt(tabIndex);
        if (source == this) {
            logger_.debug("Recognized tab closing for directory monitor " + monitor);
            
            if (monitor.isSuspended())
                monitor.resume();
            
            if (monitor.isRunning())
                monitor.stop();
            
            monitor.removeDirectoryMonitorListener(tableView);
        }
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Returns the monitor.
     * 
     * @return DirectoryMonitor
     */
    public DirectoryMonitor getMonitor() {
        return monitor;
    }

    
    /**
     * Sets the value of monitor.
     * 
     * @param monitor The monitor to set.
     */
    public void setMonitor(DirectoryMonitor monitor) {
        this.monitor = monitor;
    }
}
