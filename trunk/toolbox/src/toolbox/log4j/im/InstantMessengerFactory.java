package toolbox.log4j.im;

/**
 * Factory class for the various instant messenger implementations.
 */
public class InstantMessengerFactory
{
    /**
     * Creates an InstantMessenger given the name of the instant messaging
     * network.
     *
     * @param network Instant messaging network.
     *         - yahoo
     *         - msn
     *         - aol / aim
     * @return InstantMessenger for the given instant messaging network.
     */
    public static InstantMessenger create(String network)
    {
        InstantMessenger im = null;

        if (network.equalsIgnoreCase("yahoo"))
        {
            im = new YahooMessenger();
        }
        else if (network.equalsIgnoreCase("msn"))
        {
            im = new MSNMessenger();
        }
        else if (network.equalsIgnoreCase("aol") ||
                  network.equalsIgnoreCase("aim"))
        {
            im = new AIMMessenger();
        }
        else if (network.equalsIgnoreCase("null"))
        {
            im = new NullMessenger();
        }
        else
        {
            throw new IllegalArgumentException(
                "Messenger type '" + network + "' not valid.");
        }

        return im;
    }
}