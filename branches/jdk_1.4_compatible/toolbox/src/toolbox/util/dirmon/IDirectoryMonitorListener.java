package toolbox.util.dirmon;

import java.util.EventListener;

import toolbox.util.dirmon.event.FileEvent;
import toolbox.util.dirmon.event.StatusEvent;

/**
 * Listener interface for notification of events from a DirectoryMonitor.
 */
public interface IDirectoryMonitorListener extends EventListener {
    
    /**
     * Delivers notification that file activity has been recognized.
     * 
     * @param changeEvent Event to deliver.
     * @throws Exception on error.
     */
    void directoryActivity(FileEvent changeEvent) throws Exception;
    
    
    /**
     * Delivers notification that the status of the directory monitor has
     * changed.
     * 
     * @param statusEvent Event
     * @throws Exception on error.
     */
    void statusChanged(StatusEvent statusEvent) throws Exception;
}