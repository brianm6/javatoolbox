package toolbox.util.io.filter;

import java.io.File;
import java.io.FilenameFilter;

import org.apache.log4j.Logger;

/**
 * Filters files based on the file's extension
 */
public class ExtensionFilter implements FilenameFilter
{
    /** Logger **/
    private static final Logger logger_ = 
        Logger.getLogger(ExtensionFilter.class);
        
    /** 
     * Extension to filter on 
     */
    private String extension_;
    
    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates an Extension filter with the given file extension
     * 
     * @param  fileExtension   The file extension to filter on
     */   
    public ExtensionFilter(String fileExtension)
    {
        // add a dot just in case 
        if(!fileExtension.startsWith("."))
            fileExtension = "." + fileExtension;
        extension_ = fileExtension;
    }
    
    //--------------------------------------------------------------------------
    //  FilenameFilter Interface
    //--------------------------------------------------------------------------
    
    /**
     * Filter out a files by extension
     * 
     * @param    dir   Directory file is contained in
     * @param    name  Name of file
     * @return   True if the file matches the extension, false otherwise
     */
    public boolean accept(File dir,String name)
    {
        boolean b = name.toLowerCase().endsWith(extension_.toLowerCase());
        
        //if (b)
        //    logger_.debug("Accepted " + name);
        
        return b;
    }
}