package toolbox.clearcase;

/**
 * IRevision represents a revision of a clearcase repository artifact.
 * 
 * @see toolbox.clearcase.IVersionable
 */
public interface IRevision
{
    /**
     * @return
     */
    String getComment();


    /**
     * @param comment
     */
    void setComment(String comment);


    /**
     * @return
     */
    String getAction();


    /**
     * @param action
     */
    void setAction(String action);


    /**
     * @return
     */
    String getUser();


    /**
     * @param user
     */
    void setUser(String user);


    /**
     * @return
     */
    String getDate();


    /**
     * @param date
     */
    void setDate(String date);
}