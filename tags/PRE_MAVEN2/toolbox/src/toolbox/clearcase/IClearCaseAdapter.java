package toolbox.clearcase;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * IClearCaseAdapter represents an API interface to an adapter that has 
 * connectivity to a clearcase repository.
 * 
 * @see toolbox.clearcase.adapter.ClearCaseAdapterFactory
 */
public interface IClearCaseAdapter
{
    /**
     * Sets the path of the clearcase view.
     * 
     * @param path Absolute directory path.
     */
    void setViewPath(File path);

    
    /**
     * Returns the path to the clearcase view.
     * 
     * @return File
     */
    File getViewPath();
    
    
    /**
     * Returns a list of VersionedFiles that were checked in between the given
     * dates and satisfy the passed in filename filter.
     * 
     * @param start Start of date range.
     * @param end End of date range.
     * @param filter Filter for specific types of files.
     * @return List<VersionedFile>
     * @throws IOException on I/O error.
     */
    List findChangedFiles(Date start, Date end, FilenameFilter filter)
        throws IOException;
}