package toolbox.dirmon;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JPanel;

import toolbox.util.file.DirectoryMonitor;
import toolbox.util.service.ServiceView;
import toolbox.util.ui.JSmartLabel;


public class DirectoryMonitorView extends JPanel {

    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------
    
    private ServiceView serviceView_;
    private DirectoryMonitor directoryMonitor_;
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------
    
    public DirectoryMonitorView(DirectoryMonitor directoryMonitor) {
        directoryMonitor_ = directoryMonitor;
        buildView();
    }
    
    // -------------------------------------------------------------------------
    // Protected
    // -------------------------------------------------------------------------
    
    protected void buildView() {
        serviceView_ = new ServiceView(directoryMonitor_);
        setLayout(new BorderLayout());
        
        List dirs = directoryMonitor_.getMonitoredDirectories();
        
        add(BorderLayout.WEST, new JSmartLabel(
            "Monitoring " 
            + dirs.size() 
            + " directories in "   
            + directoryMonitor_.getMonitoredDirectories().get(0)));
        
        add(BorderLayout.CENTER, serviceView_);
    }
}