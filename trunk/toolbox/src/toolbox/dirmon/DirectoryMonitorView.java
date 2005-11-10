package toolbox.dirmon;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.JPanel;

import toolbox.util.SwingUtil;
import toolbox.util.file.DirectoryMonitor;
import toolbox.util.service.ServiceView;
import toolbox.util.ui.ImageCache;
import toolbox.util.ui.JSmartButton;
import toolbox.util.ui.JSmartLabel;
import toolbox.util.ui.SmartAction;

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
        
        JSmartButton removeButton = 
            new JSmartButton(new RemoveDirectoryMonitorViewAction());
        
        removeButton.setMaximumSize(new Dimension(16,16));
        add(BorderLayout.EAST, removeButton);
    }
    
    // -------------------------------------------------------------------------
    // RemoveDirectoryMonitorViewAction
    // -------------------------------------------------------------------------
    
    class RemoveDirectoryMonitorViewAction extends SmartAction {
    
        public RemoveDirectoryMonitorViewAction() {
            super(null, true, false, null);
            putValue(SMALL_ICON, ImageCache.getIcon(ImageCache.IMAGE_DELETE));
        }

        
        /*
         * @see toolbox.util.ui.SmartAction#runAction(java.awt.event.ActionEvent)
         */
        public void runAction(ActionEvent e) throws Exception {
            Frame f = SwingUtil.getFrameAncestor(DirectoryMonitorView.this);
            getParent().remove(DirectoryMonitorView.this);
            f.validate();
        }
    }
}