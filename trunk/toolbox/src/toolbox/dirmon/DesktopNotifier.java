package toolbox.dirmon;

import javax.swing.Icon;

import org.apache.commons.io.FilenameUtils;

import com.nitido.utils.toaster.Toaster;

import toolbox.util.FontUtil;
import toolbox.util.dirmon.IDirectoryMonitorListener;
import toolbox.util.dirmon.event.FileEvent;
import toolbox.util.dirmon.event.StatusEvent;
import toolbox.util.ui.ImageCache;

/**
 * Uses JToaster to create notification windows that popup at the bottom right
 * corner of the desktop and automatically disappear after a few seconds.
 */
class DesktopNotifier implements IDirectoryMonitorListener {

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * Manages popup message windows in bottom right hand section of the screen.
     */
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
    
    public void statusChanged(StatusEvent statusEvent) throws Exception {
        // Ignore
    }

    /** 
     * On any sort of directory activity, turn the event into a meaningful
     * string with the appropriate icon and show in a toaster popup window.
     * 
     * @see toolbox.util.dirmon.IDirectoryMonitorListener#directoryActivity(toolbox.util.dirmon.event.FileEvent)
     */
    public void directoryActivity(FileEvent event) throws Exception {
        
        StringBuffer shortMsg = new StringBuffer();
        Icon toasterIcon = null;
        
        switch (event.getEventType()) {
        
            case FileEvent.TYPE_FILE_CHANGED :
                shortMsg.append("Modified: ");
                shortMsg.append(
                    FilenameUtils.getName(
                        event.getAfterSnapshot().getAbsolutePath()));
                
                toasterIcon = ImageCache.getIcon(ImageCache.IMAGE_COPY);
                break;
                
            case FileEvent.TYPE_FILE_CREATED :
                shortMsg.append("Created: ");
                shortMsg.append(
                    FilenameUtils.getName(
                        event.getAfterSnapshot().getAbsolutePath()));
                
                toasterIcon = ImageCache.getIcon(ImageCache.IMAGE_INFO);
                break;

            case FileEvent.TYPE_FILE_DELETED :
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

        String dirmonName = event.getDirectoryMonitor().getName();
        
        if (dirmonName != null) 
            shortMsg.insert(0, dirmonName + "\n");
        
        toaster_.showToaster(toasterIcon, shortMsg.toString());
    }
}