
package toolbox.clearcase;

/**
 * IAuditResult is responsible for ___.
 */
public interface IAuditResult
{
    String getUsername();


    String getFilename();


    String getReason();
    
    String getDate();
    
    void setReason(String reason);
}