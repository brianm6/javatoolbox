package toolbox.clearcase;

import java.util.List;

/**
 * IAudit represents a clearcase audit operation on a list of one or more files.
 * 
 * @see toolbox.clearcase.domain.VersionedFile
 * @see toolbox.clearcase.IAuditResult
 */
public interface IAudit
{
    /**
     * Audits given list of versioned files and returns a list of the audit
     * results.
     * 
     * @param versionedFiles List of VersionedFile to be audited.
     * @return List<IAuditResult>
     */
    List audit(List versionedFiles);
}