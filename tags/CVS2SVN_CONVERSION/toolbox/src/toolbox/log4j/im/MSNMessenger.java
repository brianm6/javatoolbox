package toolbox.log4j.im;

/**
 * MSN Instant Messenger client that supports login, send message, and logout.
 */
public class MSNMessenger extends AbstractMessenger
{
    public MSNMessenger()
    {
    }
    
    /**
     * @see toolbox.log4j.im.AbstractMessenger#getProtocol()
     */
    public int getProtocol()
    {
        return 1;
    }
}