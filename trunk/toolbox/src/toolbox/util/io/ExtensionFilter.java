package toolbox.util.io;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Filters files based on the file's extension
 */
public class ExtensionFilter implements FilenameFilter
{
    /** Extension to filter on **/
    private String extension;
    
    /**
     * Creates an Extension filter with the given file extension
     * 
     * @param  fileExtension   The file extension to filter on
     */   
    public ExtensionFilter(String fileExtension)
    {
        /* add a dot just in case */
        if(!fileExtension.startsWith("."))
            fileExtension = "." + fileExtension;
        extension = fileExtension;
    }
    
    /**
     * Filter out a files by extension
     * 
     * @param    dir   Directory file is contained in
     * @param    name  Name of file
     * @return   True if the file matches the extension, false otherwise
     */
    public boolean accept(File dir,String name)
    {
        return name.toLowerCase().endsWith(extension.toLowerCase());
    }
}