package toolbox.util.file;

import java.io.File;

/**
 * Acceptance criteria for activity within a directory
 */
public interface IFileActivity
{
    /**
     * Returns list of files that meet a certain activity criteria in a 
     * given directory.
     *
     * @param   dir  Directory to check for activity
     * @return  List of files that meet an activity's criteria
     */
    public File[] getFiles(File dir);
}