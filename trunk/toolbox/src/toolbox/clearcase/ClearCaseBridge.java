package toolbox.clearcase;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * ClearCaseBridge is responsible for ___.
 */
public interface ClearCaseBridge
{
    void setViewPath(File path);
    
    File getViewPath();
    
    List findChangedFiles(Date start, Date end, FilenameFilter filter)
        throws IOException;
}
