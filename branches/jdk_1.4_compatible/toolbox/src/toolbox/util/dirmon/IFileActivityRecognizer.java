package toolbox.util.dirmon;

import java.util.List;

import toolbox.util.dirmon.event.BaseEvent;

/**
 * Interface that supports a the recognition of file activity within a 
 * directory.
 * 
 * @see toolbox.util.dirmon.DirectoryMonitor
 */
public interface IFileActivityRecognizer {

    /**
     * Returns a list of DirectoryMonitorEvents that were recognized by 
     * comparing the before and after snapshots of a directory.
     * 
     * @param before Snapshot of a directory before a point in time.
     * @param after Snapshot of a directory after a point in time.
     * @return List of {@link BaseEvent}s that describe changes between the
     *         directory snapshots.
     */
    List getRecognizedEvents(DirSnapshot before, DirSnapshot after);
}