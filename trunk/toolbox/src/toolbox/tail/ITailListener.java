package toolbox.tail;

/**
 * Interface that allows notification of various Tail generated events
 */
public interface ITailListener 
{
    /**
     * Tail has started 
     */
    public void tailStarted();
    
    /**
     * Tail has stopped 
     */
    public void tailStopped();
    
    /** 
     * Tail has ended
     */
    public void tailEnded();
    
    /**
     * Tail has been paused 
     */
    public void tailPaused();
    
    /**
     * Tail has been unpaused
     */
    public void tailUnpaused();
    
    /**
     * Notification that the next line from the tail is available
     * 
     * @param  line  Next line from the tailed stream
     */
    public void nextLine(String line);
}