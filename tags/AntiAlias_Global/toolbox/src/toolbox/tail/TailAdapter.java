package toolbox.tail;

/**
 * Adapter class for TailListener 
 */
public class TailAdapter implements TailListener
{
    /*
     * @see toolbox.tail.TailListener#tailStarted(toolbox.tail.Tail)
     */
    public void tailStarted(Tail tail)
    {
    }

    /*
     * @see toolbox.tail.TailListener#tailStopped(toolbox.tail.Tail)
     */
    public void tailStopped(Tail tail)
    {
    }

    /*
     * @see toolbox.tail.TailListener#tailEnded(toolbox.tail.Tail)
     */
    public void tailEnded(Tail tail)
    {
    }

    /*
     * @see toolbox.tail.TailListener#tailPaused(toolbox.tail.Tail)
     */
    public void tailPaused(Tail tail)
    {
    }

    /*
     * @see toolbox.tail.TailListener#tailUnpaused(toolbox.tail.Tail)
     */
    public void tailUnpaused(Tail tail)
    {
    }

    /*
     * @see toolbox.tail.TailListener#tailReattached(toolbox.tail.Tail)
     */
    public void tailReattached(Tail tail)
    {
    }

    /*
     * @see toolbox.tail.TailListener#nextLine(toolbox.tail.Tail, 
     *      java.lang.String)
     */
    public void nextLine(Tail tail, String line)
    {
    }
}
