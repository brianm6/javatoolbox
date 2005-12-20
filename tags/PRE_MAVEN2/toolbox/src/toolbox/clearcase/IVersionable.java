package toolbox.clearcase;

import java.util.List;

/**
 * IVersionable represents a versionable clearcase artifact.
 * 
 * @see toolbox.clearcase.IRevision
 */
public interface IVersionable
{
    /**
     * Returns the list of revisions for this versionable clearcase artifact.
     * 
     * @return List<IRevision>
     */
    List getRevisions();
}