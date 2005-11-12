package toolbox.util.file;

import java.util.EventListener;
import java.util.List;


/**
 * Listener interface for notification of certain file activity that meets an 
 * IFileActivity criteria.
 * 
 * @deprecated 
 */
public interface IDirectoryListener extends EventListener
{
    /**
     * Called when a file has met the criteria for a given IFileActivity.
     *
     * @param activity Activity that caused this notification.
     * @param files List of FileSnapshots of the affectef files.
     * @throws Exception on error.
     */
    void fileActivity(IFileActivity activity, List affectedFiles) 
        throws Exception;
}
