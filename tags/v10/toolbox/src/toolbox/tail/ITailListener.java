package toolbox.tail;

/**
 * Interface exposing behavior that a tail generates
 */
public interface ITailListener 
{
    public void tailStarted();
    public void tailStopped();
    public void tailEnded();
    public void tailPaused();
    public void tailUnpaused();
    
    /**
     * Notification that the next line from the tail is available
     * 
     * @param  line  Next line from the tailed stream
     */
    public void nextLine(String line);
}