package toolbox.clearcase;

/**
 * IRevision represents a revision of a clearcase repository artifact.
 * 
 * @see toolbox.clearcase.IVersionable
 */
public interface IRevision
{
    /**
     * Returns the comment for this revision.
     * 
     * @return String
     */
    String getComment();


    /**
     * Sets the comment for this revision.
     * 
     * @param comment Comment text.
     */
    void setComment(String comment);


    /**
     * Returns the action that resulted in this revision being created.
     * 
     * @return String
     */
    String getAction();


    /**
     * Sets the action that resulted in this revision being created.
     * 
     * @param action Name of the action.
     */
    void setAction(String action);


    /**
     * Returns the user that created this revision.
     * 
     * @return String
     */
    String getUser();


    /**
     * Sets the user that created this revision.
     * 
     * @param user Username
     */
    void setUser(String user);


    /**
     * Returns the date that this revision was created.
     * 
     * @return String
     */
    String getDate();


    /**
     * Sets the date that this revision was created.
     * 
     * @param date Date string.
     */
    void setDate(String date);
}