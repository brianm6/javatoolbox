package toolbox.clearcase.domain;

import toolbox.clearcase.IRevision;

/**
 * Revision is a version of a {@link toolbox.clearcase.domain.VersionedFile}.
 * 
 * @see toolbox.clearcase.IVersionable
 */
public class Revision implements IRevision
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
 
    /**
     * Comment for this revision.
     */
    private String comment_;
    
    /**
     * Repository action for this revision. Checkin/checkout/etc.
     */
    private String action_;
    
    /**
     * User that created this revision.
     */
    private String user_;
    
    /**
     * Timestamp of this revision.
     */
    private String date_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a Revision.
     */
    public Revision()
    {
    }

    //--------------------------------------------------------------------------
    // IRevision Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.clearcase.IRevision#getComment()
     */
    public String getComment()
    {
        return comment_;
    }
    
    
    /**
     * @see toolbox.clearcase.IRevision#setComment(java.lang.String)
     */
    public void setComment(String comment)
    {
        comment_ = comment;
    }
    
    
    /**
     * @see toolbox.clearcase.IRevision#getAction()
     */
    public String getAction()
    {
        return action_;
    }
    
    
    /**
     * @see toolbox.clearcase.IRevision#setAction(java.lang.String)
     */
    public void setAction(String action)
    {
        action_ = action;
    }
    
    
    /**
     * @see toolbox.clearcase.IRevision#getUser()
     */
    public String getUser()
    {
        return user_;
    }
    
    
    /**
     * @see toolbox.clearcase.IRevision#setUser(java.lang.String)
     */
    public void setUser(String user)
    {
        user_ = user;
    }
    
    
    /**
     * @see toolbox.clearcase.IRevision#getDate()
     */
    public String getDate()
    {
        return date_;
    }
    
    
    /**
     * @see toolbox.clearcase.IRevision#setDate(java.lang.String)
     */
    public void setDate(String date)
    {
        date_ = date;
    }
}