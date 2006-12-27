package toolbox.util.dirmon;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Snapshot of a directory at a given point in time.
 */
public class DirSnapshot{

    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------

    /**
     * Directory associated with this snapshot.
     */
    private File directory_;

    /**
     * Snapshots of all the files in this directory.
     * <ul>
     *  <li>Key = String returned from FileSnapshot.getKey() 
     *  <li>Value = FileSnapshot
     * </ul>
     */
    private Map fileSnapshots_;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public DirSnapshot(File dir){
        fileSnapshots_ = new HashMap();
        setDirectory(dir);
        snap();
    }

    // -------------------------------------------------------------------------
    // Public
    // -------------------------------------------------------------------------

    public void snap(){
        fileSnapshots_.clear();

        File[] files = getDirectory().listFiles();

        for (int i = 0; i < files.length; i++) {
            FileSnapshot snapshot = new FileSnapshot(files[i]);
            fileSnapshots_.put(snapshot.getKey(), snapshot);
            Thread.yield();
        }
    }


    // -------------------------------------------------------------------------
    // JavaBean Methods
    // -------------------------------------------------------------------------

    public File getDirectory(){
        return directory_;
    }


    public void setDirectory(File directory){
        directory_ = directory;
    }


    public Map getFileSnapshots(){
        return fileSnapshots_;
    }
}