package toolbox.clearcase;

/**
 * Revision
 */
public class Revision
{
    private String comment_;
    private String action_;
    private String user_;
    
    /**
     * Creates a Revision.
     * 
     */
    public Revision()
    {
    }

    
    public String getComment()
    {
        return comment_;
    }
    
    
    public void setComment(String comment)
    {
        comment_ = comment;
    }
    
    
    public String getAction()
    {
        return action_;
    }
    
    
    public void setAction(String action)
    {
        action_ = action;
    }
    
    public String getUser()
    {
        return user_;
    }
    
    public void setUser(String user)
    {
        user_ = user;
    }
}