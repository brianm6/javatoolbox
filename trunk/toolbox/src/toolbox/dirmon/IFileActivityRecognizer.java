package toolbox.dirmon;

import java.util.List;

import toolbox.util.file.snapshot.DirSnapshot;


/**
 * Acceptance criteria for file activity within a directory.
 */
public interface IFileActivityRecognizer {

    /**
     * 
     * @param before
     * @param after
     * @return List of IFileEvent
     */
    List getRecognizedEvents(DirSnapshot before, DirSnapshot after);
}