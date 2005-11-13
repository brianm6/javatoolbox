package toolbox.util.dirmon;

import java.util.EventListener;



/**
 * Listener interface for notification of certain file activity that meets an 
 * IFileActivity criteria.
 */
public interface IDirectoryMonitorListener extends EventListener
{
    /**
     * Called when a file has met the criteria for a given IFileActivity.
     *
     * @param DirectoryMonitorEvent
     * @throws Exception on error.
     */
    void directoryActivity(DirectoryMonitorEvent directoryMonitorEvent) 
        throws Exception;
}
