package toolbox.dirmon;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.JPanel;

import toolbox.util.SwingUtil;
import toolbox.util.dirmon.DirectoryMonitor;
import toolbox.util.service.ServiceView;
import toolbox.util.ui.ImageCache;
import toolbox.util.ui.JSmartButton;
import toolbox.util.ui.JSmartLabel;
import toolbox.util.ui.SmartAction;

/**
 * Simple panel that smacks a UI on top of a 
 * {@link toolbox.util.dirmon.DirectoryMonitor} to start/stop/suspend/destroy
 * it. Aggregates the {@link toolbox.util.service.ServiceView} to reuse as
 * mush as possible.
 */
public class ControllerView extends JPanel {

    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------
    
    /**
     * This class really decorates a {@link ServiceView} by composition.
     */
    private ServiceView serviceView_;
    
    /**
     * Directory monitor to control with this view.
     */
    private DirectoryMonitor directoryMonitor_;
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------
    
    public ControllerView(DirectoryMonitor directoryMonitor) {
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
            + directoryMonitor_.getName()));
        
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
            Frame f = SwingUtil.getFrameAncestor(ControllerView.this);
            getParent().remove(ControllerView.this);
            f.validate();
        }
    }
}