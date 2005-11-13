package toolbox.util.dirmon;

import java.util.List;



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