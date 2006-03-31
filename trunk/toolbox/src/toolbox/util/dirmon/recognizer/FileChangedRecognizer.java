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
 * Recognizes when a file has changed based on the last modified timestamp.
 */
public class FileChangedRecognizer implements IFileActivityRecognizer {
    
    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------
    
    private DirectoryMonitor monitor_;
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public FileChangedRecognizer(DirectoryMonitor monitor) {
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
        
        List changedFileEvents = new ArrayList();
        Map beforeFileSnapshots = before.getFileSnapshots();
        Map afterFileSnapshots = after.getFileSnapshots();
        
        Set beforeFileKeys = beforeFileSnapshots.keySet();
        Set afterFileKeys = afterFileSnapshots.keySet();
        
        Collection commonFileKeys = 
            CollectionUtil.intersection(beforeFileKeys, afterFileKeys);
        
        for (Iterator i = commonFileKeys.iterator(); i.hasNext();) {
            String fileKey = (String) i.next();
            
            FileSnapshot beforeFileSnapshot = 
                (FileSnapshot) beforeFileSnapshots.get(fileKey);
                
            FileSnapshot afterFileSnapshot = 
                (FileSnapshot) afterFileSnapshots.get(fileKey);
            
            // check for change in timestamp or file size 
            if (beforeFileSnapshot.getLastModified() != afterFileSnapshot.getLastModified() ||
                beforeFileSnapshot.getLength() != afterFileSnapshot.getLastModified()) {
                
                FileEvent event = 
                    new FileEvent(
                        FileEvent.TYPE_FILE_CHANGED,
                        monitor_, 
                        beforeFileSnapshot,
                        afterFileSnapshot);
                
                changedFileEvents.add(event);
            }
        }
        
        return changedFileEvents;
    }
}