package toolbox.clearcase.audit;

import org.apache.commons.io.FilenameUtils;

import toolbox.clearcase.IAuditResult;
import toolbox.clearcase.domain.VersionedFile;

/**
 * AuditResult is a default implementation of an IAuditResult.
 * 
 * @see toolbox.clearcase.IAudit
 */
public class AuditResult implements IAuditResult
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * File that was audited.
     */
    private VersionedFile file_;
    
    /**
     * Results of the auditing of the file.
     */
    private String reason_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates an AuditResult.
     * 
     * @param file File that was audited.
     */
    public AuditResult(VersionedFile file)
    {
        file_ = file;
    }

    //--------------------------------------------------------------------------
    // IAuditResult Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.clearcase.IAuditResult#getUsername()
     */
    public String getUsername()
    {
        return file_.getCurrentRevision().getUser();
    }
    
    
    /**
     * @see toolbox.clearcase.IAuditResult#getFilename()
     */
    public String getFilename() 
    {
        return file_.getName();
    }
    
    
    /**
     * @see toolbox.clearcase.IAuditResult#getFileOnly()
     */
    public String getFileOnly()
    {
        return FilenameUtils.getName(getFilename());
    }
    
    
    /**
     * @see toolbox.clearcase.IAuditResult#getPathOnly()
     */
    public String getPathOnly()
    {
        return FilenameUtils.getFullPathNoEndSeparator(getFilename());
    }
    
    
    /**
     * @see toolbox.clearcase.IAuditResult#getReason()
     */
    public String getReason() 
    {
        return reason_;
    }
    
    
    /**
     * @see toolbox.clearcase.IAuditResult#setReason(java.lang.String)
     */
    public void setReason(String reason)
    {
        reason_ = reason;
    }
    
    
    /**
     * @see toolbox.clearcase.IAuditResult#getDate()
     */
    public String getDate()
    {
        return file_.getCurrentRevision().getDate();
    }
}