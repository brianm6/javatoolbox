package toolbox.log4j.im;

/**
 * Factory class for the various instant messenger implementations.
 */
public class InstantMessengerFactory
{
    public static InstantMessenger create(String messenger)
    {
        InstantMessenger im = null;
        
        if (messenger.equalsIgnoreCase("yahoo"))
        {
            im = new YahooMessenger();
        }
        else
        {
            throw new IllegalArgumentException(
                "Messenger type '" + messenger + "' not valid.");
        }
            
        return im;    
    }
}
