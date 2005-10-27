package toolbox.util.file;

import java.io.File;
import java.util.List;

/**
 * Acceptance criteria for file activity within a directory.
 */
public interface IFileActivity
{
    /**
     * Returns list of files that meet a certain activity criteria in a given
     * directory.
     * 
     * @param dir Directory to check for activity.
     * @return List of files that meet this activity's criteria.
     */
    List getAffectedFiles(File dir);
}