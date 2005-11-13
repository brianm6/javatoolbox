package toolbox.util.dirmon.trash;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ClassUtils;

import toolbox.util.dirmon.DirSnapshot;

/**
 * An activity that is capable of recognizing when files are deleted from a
 * directory.
 * 
 * @deprecated
 */
public class FileDeletedActivity implements IFileActivity {

    // --------------------------------------------------------------------------
    // Fields
    // --------------------------------------------------------------------------

    /**
     * Map of directories with their associated snapshot.
     * 
     * Key = String from File.getAbsolutePath() of a directory
     * Value = DirSnapshot
     */
    private Map dirSnapshots_;

    // --------------------------------------------------------------------------
    // Constructors
    // --------------------------------------------------------------------------

    /**
     * Creates a FileDeletedActivity.
     */
    public FileDeletedActivity() {
        dirSnapshots_ = new HashMap();
    }

    // --------------------------------------------------------------------------
    // IFileActivity Interface
    // --------------------------------------------------------------------------

    /**
     * Determines deleted files in a directory since the last time a snapshot 
     * was taken.
     * 
     * @see toolbox.util.dirmon.trash.IFileActivity#getAffectedFiles(java.io.File)
     */
    public List getAffectedFiles(File dir) {

        List deletedFileSnapshots = new ArrayList();
        String dirKey = dir.getAbsolutePath();
        DirSnapshot beforeDirSnapshot = (DirSnapshot) dirSnapshots_.get(dirKey);

        if (beforeDirSnapshot == null) {
            dirSnapshots_.put(dirKey, new DirSnapshot(dir));
        }
        else {
            DirSnapshot afterDirSnapshot = new DirSnapshot(dir);
            DirDiff diff = new DirDiff(beforeDirSnapshot, afterDirSnapshot);
            deletedFileSnapshots =  diff.getDeletedFiles();
            
            // Update the snapshot to the latest
            dirSnapshots_.put(dirKey, afterDirSnapshot);
        }
        return deletedFileSnapshots;
    }
    
    // -------------------------------------------------------------------------
    // Overrides java.lang.Object 
    // -------------------------------------------------------------------------
    
    /*
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return ClassUtils.getShortClassName(getClass());
    }
}