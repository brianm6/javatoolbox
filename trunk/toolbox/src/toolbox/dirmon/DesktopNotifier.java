package toolbox.dirmon;

import javax.swing.Icon;

import com.nitido.utils.toaster.Toaster;

import org.apache.commons.io.FilenameUtils;

import toolbox.util.FontUtil;
import toolbox.util.dirmon.DirectoryMonitorEvent;
import toolbox.util.dirmon.IDirectoryMonitorListener;
import toolbox.util.ui.ImageCache;

/**
 * Uses JToaster to create notification windows that popup at the bottom right
 * corner of the desktop and automatically disappear after a few seconds.
 */
class DesktopNotifier implements IDirectoryMonitorListener {

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    private Toaster toaster_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    DesktopNotifier() {
        toaster_ = new Toaster();
        toaster_.setDisplayTime(5000);
        toaster_.setToasterMessageFont(FontUtil.getPreferredSerifFont());
    }
    
    //--------------------------------------------------------------------------
    // IDirectoryMonitorListener Interface
    //--------------------------------------------------------------------------
    
    public void directoryActivity(DirectoryMonitorEvent event) throws Exception {
        
        StringBuffer shortMsg = new StringBuffer();
        Icon toasterIcon = null;
        
        switch (event.getEventType()) {
        
            case DirectoryMonitorEvent.TYPE_CHANGED :
                
                shortMsg.append("Modified: ");
                shortMsg.append(
                    FilenameUtils.getName(
                        event.getAfterSnapshot().getAbsolutePath()));
                
                toasterIcon = ImageCache.getIcon(ImageCache.IMAGE_COPY);
                break;
                
            case DirectoryMonitorEvent.TYPE_CREATED :
                
                shortMsg.append("Created: ");
                shortMsg.append(
                    FilenameUtils.getName(
                        event.getAfterSnapshot().getAbsolutePath()));
                
                toasterIcon = ImageCache.getIcon(ImageCache.IMAGE_INFO);
                break;

            case DirectoryMonitorEvent.TYPE_DELETED :
                
                shortMsg.append("Deleted: ");
                shortMsg.append(
                    FilenameUtils.getName(
                        event.getBeforeSnapshot().getAbsolutePath()));
                
                toasterIcon = ImageCache.getIcon(ImageCache.IMAGE_DELETE);
                break;

            default:
                throw new IllegalArgumentException(
                    "unrecognized event type: " 
                    + event.getEventType());
        }

        toaster_.showToaster(toasterIcon, shortMsg.toString());
    }
}