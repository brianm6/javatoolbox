package toolbox.clearcase;

import java.util.List;

/**
 * IAudit is responsible for ___.
 */
public interface IAudit
{
    /**
     * Audits given list of versioned files.
     * 
     * @param versionedFiles List of VersionedFiles.
     * @return List<IAuditResult>
     */
    List audit(List versionedFiles);
}
