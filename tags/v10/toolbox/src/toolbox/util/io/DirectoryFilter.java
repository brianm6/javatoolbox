package toolbox.util.io;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Filters directories
 */
public class DirectoryFilter implements FilenameFilter
{
    /**
     * Filter out directories
     * 
     * @param    dir   Directory file is contained in
     * @param    name  Name of file
     * @return   True if the file matches the extension, false otherwise
     */
    public boolean accept(File dir,String name)
    {
        File f = new File(dir, name);
        return f.isDirectory();
    }
}

