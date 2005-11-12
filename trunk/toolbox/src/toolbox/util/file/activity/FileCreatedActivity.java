package toolbox.util.file.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ClassUtils;

import toolbox.util.file.IFileActivity;
import toolbox.util.file.snapshot.DirDiff;
import toolbox.util.file.snapshot.DirSnapshot;

/**
 * An activity that is capable of recognizing when new files are added to a
 * directory.
 * 
 * @deprecated
 */
public class FileCreatedActivity implements IFileActivity {

    // --------------------------------------------------------------------------
    // Fields
    // --------------------------------------------------------------------------

    /**
     * Map of directories with their associated snapshot.
     */
    private Map dirSnapshots_;

    // --------------------------------------------------------------------------
    // Constructors
    // --------------------------------------------------------------------------

    /**
     * Creates a FileCreatedActivity.
     */
    public FileCreatedActivity() {
        dirSnapshots_ = new HashMap();
    }

    // --------------------------------------------------------------------------
    // IFileActivity Interface
    // --------------------------------------------------------------------------

    /**
     * Determines new files in a directory since the last time a snapshot was
     * taken.
     * 
     * @see toolbox.util.file.IFileActivity#getAffectedFiles(java.io.File)
     */
    public List getAffectedFiles(File dir) {

//        File[] newFiles = new File[0];
//
//        Set history = (Set) snapshots_.get(dir);
//
//        if (history == null) {
//            // No previous snapshot so create the first
//            Set current = new HashSet();
//            File[] init = dir.listFiles();
//            current.addAll(Arrays.asList(init));
//            snapshots_.put(dir, current);
//        }
//        else {
//            // Build current snapshot of dir
//            Set current = new TreeSet();
//            File[] now = dir.listFiles();
//            current.addAll(Arrays.asList(now));
//
//            // Get set difference between current and history
//            // to identify new files
//            Set diff = new HashSet(current);
//            diff.removeAll(history);
//
//            // New files have been found
//            if (!diff.isEmpty()) {
//                // List of new files to return
//                newFiles = (File[]) diff.toArray(newFiles);
//
//                // Update snapshot in history map to that of the current
//                snapshots_.put(dir, current);
//            }
//        }
        
        List createdFileSnapshots = new ArrayList();
        String dirKey = dir.getAbsolutePath();
        DirSnapshot beforeDirSnapshot = (DirSnapshot) dirSnapshots_.get(dirKey);

        if (beforeDirSnapshot == null) {
            dirSnapshots_.put(dirKey, new DirSnapshot(dir));
        }
        else {
            DirSnapshot afterDirSnapshot = new DirSnapshot(dir);
            DirDiff diff = new DirDiff(beforeDirSnapshot, afterDirSnapshot);
            createdFileSnapshots =  diff.getCreatedFiles();
            
            // Update the snapshot to the latest
            dirSnapshots_.put(dirKey, afterDirSnapshot);
        }
        return createdFileSnapshots;
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