package toolbox.tail;

/**
 * Listener interface for tail generated events.
 */
public interface TailListener 
{
    /**
     * Tail has started.
     * 
     * @param tail Tail that was started.
     */
    void tailStarted(Tail tail);
    
    
    /**
     * Tail has stopped. 
     * 
     * @param tail Tail that was stopped.
     */
    void tailStopped(Tail tail);
    
    
    /** 
     * Tail has ended.
     * 
     * @param tail Tail that ended.
     */
    void tailEnded(Tail tail);
    
    
    /**
     * Tail has been paused.
     * 
     * @param tail that was paused. 
     */
    void tailPaused(Tail tail);
    
    
    /**
     * Tail has been unpaused.
     * 
     * @param tail Tail that was unpaused.
     */
    void tailUnpaused(Tail tail);
    
    
    /**
     * Tail re-attached to source.
     * 
     * @param tail Tail that was reattached to its source.
     */
    void tailReattached(Tail tail);
    
    
    /**
     * Notification that the next line from the tail is available.
     * 
     * @param tail Tail that received a new line of text.
     * @param line Next line from the tailed stream.
     */
    void nextLine(Tail tail, String line);
}