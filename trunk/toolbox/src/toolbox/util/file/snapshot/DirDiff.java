package toolbox.util.file.snapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;
import toolbox.util.CollectionUtil;

/**
 * Difference of two directory snapshots.
 */
public class DirDiff {

    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------
    
    private DirSnapshot beforeSnapshot;
    private DirSnapshot afterSnapshot;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------
    
    public DirDiff(DirSnapshot before, DirSnapshot after) {
        setBeforeSnapshot(before);
        setAfterSnapshot(after);
    }

    // -------------------------------------------------------------------------
    // Public
    // -------------------------------------------------------------------------
    
    /**
     * @return List of FileDiff of modified files.
     */
    public List getModifiedFiles() {
        List modified = new ArrayList();
        
        Assert.assertTrue(
            beforeSnapshot.getDirectory().equals(
                afterSnapshot.getDirectory()));
        
        Map beforeFileSnapshots = beforeSnapshot.getFileSnapshots();
        Map afterFileSnapshots = afterSnapshot.getFileSnapshots();
        
        Set beforeFileKeys = beforeFileSnapshots.keySet();
        Set afterFileKeys = afterFileSnapshots.keySet();
        
        Collection commonFileKeys = 
            CollectionUtil.intersection(beforeFileKeys, afterFileKeys);
        
        for (Iterator i = commonFileKeys.iterator(); i.hasNext();) {
            String fileKey = (String) i.next();

            FileDiff diff = new FileDiff(
                (FileSnapshot) beforeFileSnapshots.get(fileKey),
                (FileSnapshot) afterFileSnapshots.get(fileKey));
            
            if (diff.isModified())
                modified.add(diff);
        }
        
        return modified;
    }

    /**
     * @return List of FileSnapshot of the newly created files
     */
    public List getCreatedFiles() {
        List created = new ArrayList();
        
        Assert.assertTrue(
            beforeSnapshot.getDirectory().equals(
                afterSnapshot.getDirectory()));
        
        Map beforeFileSnapshots = beforeSnapshot.getFileSnapshots();
        Map afterFileSnapshots = afterSnapshot.getFileSnapshots();
        
        Set beforeFileKeys = beforeFileSnapshots.keySet();
        Set afterFileKeys = afterFileSnapshots.keySet();
        
        Collection allFileKeys = 
            CollectionUtil.union(beforeFileKeys, afterFileKeys);
        
        Collection createdFileKeys =
            CollectionUtil.difference(allFileKeys, beforeFileKeys);
        
        for (Iterator i = createdFileKeys.iterator(); i.hasNext();) {
            String fileKey = (String) i.next();

            created.add(afterFileSnapshots.get(fileKey));
        }
        
        return created;
    }
    
    /**
     * @return List of FileSnapshot of the deleted files.
     */
    public List getDeletedFiles() {
        List deleted = new ArrayList();
        
        Assert.assertTrue(
            beforeSnapshot.getDirectory().equals(
                afterSnapshot.getDirectory()));
        
        Map beforeFileSnapshots = beforeSnapshot.getFileSnapshots();
        Map afterFileSnapshots = afterSnapshot.getFileSnapshots();
        
        Set beforeFileKeys = beforeFileSnapshots.keySet();
        Set afterFileKeys = afterFileSnapshots.keySet();
        
        Collection allFileKeys = 
            CollectionUtil.union(beforeFileKeys, afterFileKeys);
        
        Collection deletedFileKeys =
            CollectionUtil.difference(allFileKeys, afterFileKeys);
        
        for (Iterator i = deletedFileKeys.iterator(); i.hasNext();) {
            String fileKey = (String) i.next();

            deleted.add(beforeFileSnapshots.get(fileKey));
        }
        
        return deleted;
    }
    
    // -------------------------------------------------------------------------
    // Javabean Methods
    // -------------------------------------------------------------------------
    
    public DirSnapshot getAfterSnapshot() {
        return afterSnapshot;
    }

    
    public void setAfterSnapshot(DirSnapshot afterSnapshot) {
        this.afterSnapshot = afterSnapshot;
    }

    
    public DirSnapshot getBeforeSnapshot() {
        return beforeSnapshot;
    }

    
    public void setBeforeSnapshot(DirSnapshot beforeSnapshot) {
        this.beforeSnapshot = beforeSnapshot;
    }
}