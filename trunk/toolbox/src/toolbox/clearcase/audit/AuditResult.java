package toolbox.clearcase.audit;

import toolbox.clearcase.IAuditResult;
import toolbox.clearcase.domain.VersionedFile;

/**
 * AuditResult is responsible for ___.
 */
public class AuditResult implements IAuditResult
{
    private VersionedFile file_;
    private String reason_;

    /**
     * Creates a AuditResult.
     */
    public AuditResult(VersionedFile file)
    {
        file_ = file;
    }

    public String getUsername()
    {
        return file_.getLastRevision().getUser();
    }
    
    public String getFilename() 
    {
        return file_.getName();
    }
    
    public String getReason() 
    {
        return reason_;
    }
    
    public void setReason(String reason)
    {
        reason_ = reason;
    }
    
    public String getDate()
    {
        return file_.getLastRevision().getDate();
    }
}
