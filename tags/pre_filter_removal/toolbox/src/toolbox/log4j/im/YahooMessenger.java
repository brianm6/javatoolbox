package toolbox.log4j.im;


/**
 * Yahoo Instant Messenger client that supports login, send message, and logout.
 */
public class YahooMessenger extends AbstractMessenger
{
    public YahooMessenger()
    {
    }
    
    /**
     * @see toolbox.log4j.im.AbstractMessenger#getProtocol()
     */
    public int getProtocol()
    {
        return 0;
    }
}