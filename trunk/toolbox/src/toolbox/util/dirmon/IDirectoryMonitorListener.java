package toolbox.util.dirmon;

import java.util.EventListener;

/**
 * Listener interface for notification of events from a DirectoryMonitor.
 */
public interface IDirectoryMonitorListener extends EventListener {
    
    /**
     * Delivers an event to the DirectoryMonitor listener.
     * 
     * @param directoryMonitorEvent Event to deliver.
     * @throws Exception on error.
     */
    void directoryActivity(DirectoryMonitorEvent directoryMonitorEvent)
        throws Exception;
}