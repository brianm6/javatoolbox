package toolbox.util.dirmon;

import java.util.List;

/**
 * Acceptance criteria for file activity within a directory.
 */
public interface IFileActivityRecognizer {

    /**
     * Returns a list of DirectoryMonitorEvents that were recognized by 
     * comparing the before and after snapshots of a directory.
     * 
     * @param before Before snapshot of the directory.
     * @param after After Snapshot of the directory.
     * @return List of DirectorMonitorEvent
     */
    List getRecognizedEvents(DirSnapshot before, DirSnapshot after);
}