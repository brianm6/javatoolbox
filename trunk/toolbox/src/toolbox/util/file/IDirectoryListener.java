package toolbox.util.file;

import java.io.File;
import java.util.EventListener;

/**
 * Listener interface for notification of certain file 
 * activity that meets an IFileActivity criteria
 */
public interface IDirectoryListener extends EventListener
{
    /**
     * Called when a file has met the criteria for an IFileActivity
     *
     * @param  file   File that meets activity criteria
     */
    public void fileActivity(IFileActivity activity, File[] files) 
        throws Exception;
}
