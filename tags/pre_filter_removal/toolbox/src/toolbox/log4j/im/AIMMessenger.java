package toolbox.log4j.im;


/**
 * AIM Instant Messenger client that supports login, send message, and logout.
 */
public class AIMMessenger extends AbstractMessenger implements InstantMessenger
{
    public AIMMessenger()
    {
    }
    
    /**
     * @see toolbox.log4j.im.AbstractMessenger#getProtocol()
     */
    public int getProtocol()
    {
        return 2;
    }
}