package toolbox.clearcase;

/**
 * IAuditResult represents the results of an audit operation on a clearcase
 * repository artifact.
 * 
 * @see toolbox.clearcase.IAudit
 */
public interface IAuditResult
{
    /**
     * Returns the username associated with the audit operation.
     * 
     * @return String
     */
    String getUsername();

    
    /**
     * Returns the absolute name of the file that was audited.
     * 
     * @return String
     */
    String getFilename();

    
    /**
     * Returns the results of the audit operation.
     * 
     * @return String
     */
    String getReason();
    
    
    /**
     * Sets the results of the audit operation.
     * 
     * @param reason Text describing the audit operation results.
     */
    void setReason(String reason);
    
    
    /**
     * Returns the timestamp associated with the file that was audited.
     * 
     * @return String
     */
    String getDate();
}