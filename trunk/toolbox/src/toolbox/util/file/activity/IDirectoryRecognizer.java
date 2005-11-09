package toolbox.util.file.activity;

import java.util.List;

import toolbox.util.file.snapshot.DirSnapshot;

/**
 * Acceptance criteria for file activity within a directory.
 */
public interface IDirectoryRecognizer {

    /**
     * 
     * @param before
     * @param after
     * @return List of IFileEvent
     */
    List getRecognizedEvents(DirSnapshot before, DirSnapshot after);
}