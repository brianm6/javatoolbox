package toolbox.util.file.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.apache.commons.lang.ClassUtils;

import toolbox.util.CollectionUtil;
import toolbox.util.file.IFileActivity;
import toolbox.util.file.snapshot.DirDiff;
import toolbox.util.file.snapshot.DirSnapshot;

/**
 * An activity that is capable of recognizing when new files are added to a
 * directory.
 */
public class FileCreatedRecognizer implements IDirectoryRecognizer {

    // --------------------------------------------------------------------------
    // Fields
    // --------------------------------------------------------------------------

    /**
     * Map of directories with their associated snapshot.
     */
    private Map dirSnapshots_;

    // --------------------------------------------------------------------------
    // Constructors
    // --------------------------------------------------------------------------

    /**
     * Creates a FileCreatedActivity.
     */
    public FileCreatedRecognizer() {
        dirSnapshots_ = new HashMap();
    }

    // --------------------------------------------------------------------------
    // IDirectoryRecognizer Interface
    // --------------------------------------------------------------------------

    /*
     * @see toolbox.util.file.activity.IDirectoryRecognizer#getRecognizedEvents(toolbox.util.file.snapshot.DirSnapshot, toolbox.util.file.snapshot.DirSnapshot)
     */
    public List getRecognizedEvents(DirSnapshot before, DirSnapshot after) {
        Assert.assertTrue(before.getDirectory().equals(after.getDirectory()));
        
        List createdFileEvents = new ArrayList();
        //DirDiff diff = new DirDiff(before, after);
        
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

            //created.add(afterFileSnapshots.get(fileKey));
            
            DirectoryMonitorEvent event = new DirectoryMonitorEvent()
            
        }
        
        createdFileEvents =  diff.getCreatedFiles();
        return createdFileEvents;
    }
    
//    // -------------------------------------------------------------------------
//    // Overrides java.lang.Object 
//    // -------------------------------------------------------------------------
//    
//    /*
//     * @see java.lang.Object#toString()
//     */
//    public String toString() {
//        return ClassUtils.getShortClassName(getClass());
//    }
}