package toolbox.util.file;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * An activity that monitors the creation of new files
 */
public class FileCreatedActivity implements IFileActivity
{
    
    private Map snapshots_ = new HashMap();
    
    
    /**
     * Constructor for FileCreatedActivity.
     */
    public FileCreatedActivity()
    {
    }

    /**
     * @see toolbox.util.file.IFileActivity#getFiles(File)
     */
    public File[] getFiles(File dir)
    {
        File[] newFiles = new File[0];
        
        Set history = (Set)snapshots_.get(dir);
        
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

    /**
     * @return  Simple name
     */    
    public String toString()
    {
        return "FileCreatedActivity";
    }
}
