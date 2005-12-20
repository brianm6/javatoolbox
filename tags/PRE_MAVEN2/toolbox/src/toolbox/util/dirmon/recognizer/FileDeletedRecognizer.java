package toolbox.util.dirmon.recognizer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;
import toolbox.util.CollectionUtil;
import toolbox.util.dirmon.DirSnapshot;
import toolbox.util.dirmon.DirectoryMonitor;
import toolbox.util.dirmon.FileSnapshot;
import toolbox.util.dirmon.IFileActivityRecognizer;
import toolbox.util.dirmon.event.FileEvent;

/**
 * Recognizes when files are deleted from a directory.
 */
public class FileDeletedRecognizer implements IFileActivityRecognizer {

    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------

    private DirectoryMonitor monitor_;
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public FileDeletedRecognizer(DirectoryMonitor monitor) {
        monitor_ = monitor;
    }

    // -------------------------------------------------------------------------
    // IFileActivityRecognizer Interface
    // -------------------------------------------------------------------------

    /*
     * @see toolbox.util.file.activity.IFileActivityRecognizer#getRecognizedEvents(toolbox.util.file.snapshot.DirSnapshot, toolbox.util.file.snapshot.DirSnapshot)
     */
    public List getRecognizedEvents(DirSnapshot before, DirSnapshot after) {
        
        Assert.assertTrue(before.getDirectory().equals(after.getDirectory()));
        
        List deletedFileEvents = new ArrayList();
        Map beforeFileSnapshots = before.getFileSnapshots();
        Map afterFileSnapshots = after.getFileSnapshots();
        
        Set beforeFileKeys = beforeFileSnapshots.keySet();
        Set afterFileKeys = afterFileSnapshots.keySet();
        
        Collection allFileKeys = 
            CollectionUtil.union(beforeFileKeys, afterFileKeys);
        
        Collection deletedFileKeys =
            CollectionUtil.difference(allFileKeys, afterFileKeys);
        
        for (Iterator i = deletedFileKeys.iterator(); i.hasNext();) {
            String fileKey = (String) i.next();

            FileEvent event = 
                new FileEvent(
                    FileEvent.TYPE_FILE_DELETED,
                    monitor_, 
                    (FileSnapshot) before.getFileSnapshots().get(fileKey),
                    (FileSnapshot) null); 
            
            deletedFileEvents.add(event);
        }
        
        return deletedFileEvents;
    }
}