package toolbox.clearcase.domain;

import java.util.ArrayList;
import java.util.List;

import toolbox.clearcase.IVersionable;
import toolbox.util.service.Nameable;

/**
 * VersionedFile represents a file stored in a clearcase repository.
 * 
 * @see toolbox.clearcase.IRevision
 */
public class VersionedFile implements IVersionable, Nameable
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Absolute name of this file.
     */
    private String name_;
    
    /**
     * Revisions associated with this file.
     */
    private List revisions_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a VersionedFile.
     */
    public VersionedFile()
    {
        setName("");
        revisions_ = new ArrayList(1);
    }
    
    //--------------------------------------------------------------------------
    // IVersionable Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.clearcase.IVersionable#getRevisions()
     */
    public List getRevisions()
    {
        return revisions_;
    }
    
    //--------------------------------------------------------------------------
    // Nameable Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.service.Nameable#getName()
     */
    public String getName()
    {
        return name_;
    }
    
    
    /**
     * @see toolbox.util.service.Nameable#setName(java.lang.String)
     */
    public void setName(String name)
    {
        name_ = name;
    }
    
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Adds a revision to the end of this files list of revisions.
     * 
     * @param r Revision to add.
     */
    public void addRevision(Revision r)
    {
        revisions_.add(r);
    }

    
    /**
     * Returns the latest revision of this file.
     * 
     * @return Revision
     */
    public Revision getCurrentRevision()
    {
        return revisions_.size() == 0 
            ? null 
            : (Revision) revisions_.iterator().next(); 
    }
}