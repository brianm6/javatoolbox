package toolbox.util.io.filter;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Filters files only.
 */
public class FileOnlyFilter implements FilenameFilter
{
    //--------------------------------------------------------------------------
    // Static Instance
    //--------------------------------------------------------------------------
    
    /**
     * Shared static instance.
     */
    public static final FileOnlyFilter INSTANCE = new FileOnlyFilter();
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Prevent construction. Use publicly available static instance.
     */
    private FileOnlyFilter()
    {
    }
    
    //--------------------------------------------------------------------------
    //  FilenameFilter Interface
    //--------------------------------------------------------------------------
    
    /**
     * Filter out files only.
     * 
     * @param dir Directory file is contained in.
     * @param name Name of file.
     * @return True if the file is a real file, false otherwise.
     */
    public boolean accept(File dir, String name)
    {
        return new File(dir, name).isFile();
    }
}

