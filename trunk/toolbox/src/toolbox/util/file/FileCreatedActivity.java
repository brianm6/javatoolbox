package toolbox.util.file;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * An activity that is capable of recognizing when new files are added to a 
 * directory.
 */
public class FileCreatedActivity implements IFileActivity
{
    /** 
     * Map of directories with their associated snapshot 
     */   
    private Map snapshots_;

    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
        
    /**
     * Constructor for FileCreatedActivity.
     */
    public FileCreatedActivity()
    {
        snapshots_ = new HashMap();
    }

    //--------------------------------------------------------------------------
    //  IFileActivity Interface
    //--------------------------------------------------------------------------
    
    /**
     * Determines new files in a directory since the last time a snapshot was
     * taken.
     * 
     * @param   dir  Directory to analyze
     * @return  List of new files
     */
    public File[] getFiles(File dir)
    {
        File[] newFiles = new File[0];
        
        Set history = (Set) snapshots_.get(dir);
        
        if (history == null)
        {
            // No previous snapshot so create the first 
            Set current = new HashSet();
            File[] init = dir.listFiles();
            current.addAll(Arrays.asList(init));
            snapshots_.put(dir, current);            
        }
        else
        {
            // Build current snapshot of dir
            Set current = new TreeSet();
            File[] now = dir.listFiles();
            current.addAll(Arrays.asList(now));
            
            // Get set difference between current and history
            // to identify new files
            Set diff = new HashSet(current);
            diff.removeAll(history);
            
            // New files have been found
            if (!diff.isEmpty())
            {
                // List of new files to return    
                newFiles = (File[])diff.toArray(newFiles);
                
                // Update snapshot in history map to that of the current
                snapshots_.put(dir, current);
            }
        }
        
        return newFiles;
    }
}
