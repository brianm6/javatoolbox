package toolbox.util.file.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ClassUtils;

import toolbox.util.file.IFileActivity;
import toolbox.util.file.snapshot.DirDiff;
import toolbox.util.file.snapshot.DirSnapshot;
import toolbox.util.file.snapshot.FileSnapshot;

/**
 * An activity that is capable of recognizing when new files are added to a
 * directory.
 */
public class FileChangedActivity implements IFileActivity {

    // --------------------------------------------------------------------------
    // Fields
    // --------------------------------------------------------------------------

    /**
     * Key = String which is File.getAbsolutePath() for a directory
     * Value = DirSnapshot
     */
    private Map dirSnapshots_;

    // --------------------------------------------------------------------------
    // Constructors
    // --------------------------------------------------------------------------

    /**
     * Creates a FileCreatedActivity.
     */
    public FileChangedActivity() {
        dirSnapshots_ = new HashMap();
    }

    // --------------------------------------------------------------------------
    // IFileActivity Interface
    // --------------------------------------------------------------------------

    /**
     * Determines new files in a directory since the last time a snapshot was
     * taken.
     * 
     * @param dir Directory to analyze.
     * @return List of FileDiff of modified files.
     */
    public List getAffectedFiles(File dir) {

        List modifiedFiles = new ArrayList();
        String dirKey = dir.getAbsolutePath();
        DirSnapshot beforeDirSnapshot = (DirSnapshot) dirSnapshots_.get(dirKey);

        if (beforeDirSnapshot == null) {
            dirSnapshots_.put(dirKey, new DirSnapshot(dir));
        }
        else {
            DirSnapshot afterDirSnapshot = new DirSnapshot(dir);
            DirDiff diff = new DirDiff(beforeDirSnapshot, afterDirSnapshot);
            List modifiedFileDiffs =  diff.getModifiedFiles();
            
            for (Iterator i = modifiedFileDiffs.iterator(); i.hasNext();) {
                FileSnapshot fileSnapshot = (FileSnapshot) i.next();
                modifiedFiles.add(fileSnapshot);
            }
        }

        return modifiedFiles;
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