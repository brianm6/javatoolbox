package toolbox.util.io.filter;

import java.io.File;
import java.io.FilenameFilter;

import org.apache.log4j.Logger;

/**
 * ExtensionFilter filters files based on a file's extension.
 */
public class ExtensionFilter implements FilenameFilter
{
    private static final Logger logger_ = 
        Logger.getLogger(ExtensionFilter.class);
        
    /** 
     * Extension to filter on. 
     */
    private String extension_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates an Extension filter with the given file extension. The extension
     * can optionally contain the leading dot.
     * 
     * @param fileExtension File extension to filter on
     */   
    public ExtensionFilter(String fileExtension)
    {
        // add a dot just in case 
        if(!fileExtension.startsWith("."))
            fileExtension = "." + fileExtension;
            
        extension_ = fileExtension;
    }
    
    //--------------------------------------------------------------------------
    // FilenameFilter Interface
    //--------------------------------------------------------------------------
    
    /**
     * Filter out a files by extension.
     * 
     * @param dir Directory file is contained in
     * @param name Name of file
     * @return True if the file matches the extension, false otherwise
     */
    public boolean accept(File dir, String name)
    {
        return name.toLowerCase().endsWith(extension_.toLowerCase());
    }
}