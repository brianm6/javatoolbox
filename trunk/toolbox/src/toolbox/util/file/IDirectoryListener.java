package toolbox.util.file;

import java.io.File;
import java.util.EventListener;

/**
 * Listener interface for notification of certain file activity that meets an 
 * IFileActivity criteria.
 */
public interface IDirectoryListener extends EventListener
{
    /**
     * Called when a file has met the criteria for a given IFileActivity.
     *
     * @param activity Activity that caused this notification
     * @param files Files that meets activity criteria
     * @throws Exception on error
     */
    void fileActivity(IFileActivity activity, File[] files) throws Exception;
}
