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
 * Recognizes when new files are added to a directory.
 */
public class FileCreatedRecognizer implements IFileActivityRecognizer {

    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------

    private DirectoryMonitor monitor_;
    
    // --------------------------------------------------------------------------
    // Constructors
    // --------------------------------------------------------------------------

    public FileCreatedRecognizer(DirectoryMonitor monitor) {
        monitor_ = monitor;
    }

    // --------------------------------------------------------------------------
    // IFileActivityRecognizer Interface
    // --------------------------------------------------------------------------

    /*
     * @see toolbox.util.file.activity.IFileActivityRecognizer#getRecognizedEvents(toolbox.util.file.snapshot.DirSnapshot, toolbox.util.file.snapshot.DirSnapshot)
     */
    public List getRecognizedEvents(DirSnapshot before, DirSnapshot after) {
        
        Assert.assertTrue(before.getDirectory().equals(after.getDirectory()));
        
        List createdFileEvents = new ArrayList();
        Map beforeFileSnapshots = before.getFileSnapshots();
        Map afterFileSnapshots = after.getFileSnapshots();
        
        Set beforeFileKeys = beforeFileSnapshots.keySet();
        Set afterFileKeys = afterFileSnapshots.keySet();
        
        Collection allFileKeys = 
            CollectionUtil.union(beforeFileKeys, afterFileKeys);
        
        Collection createdFileKeys =
            CollectionUtil.difference(allFileKeys, beforeFileKeys);
        
        for (Iterator i = createdFileKeys.iterator(); i.hasNext();) {
            String fileKey = (String) i.next();

            FileEvent event = 
                new FileEvent(
                    FileEvent.TYPE_FILE_CREATED,
                    monitor_, 
                    (FileSnapshot) null, 
                    (FileSnapshot) after.getFileSnapshots().get(fileKey));

            createdFileEvents.add(event);
            
            // This really shouldn't be the responsiblity of the recognizer but
            // its the easiest to put here for right now
            
            // If the new file is a directory, add it to the master list...
            if (event.getAfterSnapshot().isDirectory())
                monitor_.internalAddDirectory(event.getAfterSnapshot().toFile());
        }
        
        return createdFileEvents;
    }
}