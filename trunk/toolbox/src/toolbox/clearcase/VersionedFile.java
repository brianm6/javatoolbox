package toolbox.clearcase;

import java.util.ArrayList;
import java.util.List;

import toolbox.util.service.Nameable;

/**
 * VersionedFile is responsible for ___.
 */
public class VersionedFile implements Versionable, Nameable
{
    private String name_;
    private List revisions_;
    private String version_;
    
    /**
     * Creates a VersionedFile.
     * 
     */
    public VersionedFile()
    {
        setName("");
        setVersion("");
        revisions_ = new ArrayList(1);
    }
    
    //--------------------------------------------------------------------------
    // Versionable Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.clearcase.Versionable#getVersion()
     */
    public String getVersion()
    {
        return version_;
    }

    public void setVersion(String version)
    {
        version_ = version;
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
    
    public void addRevision(Revision r)
    {
        revisions_.add(r);
    }
    
    
    public List getRevisions()
    {
        return revisions_;
    }
}