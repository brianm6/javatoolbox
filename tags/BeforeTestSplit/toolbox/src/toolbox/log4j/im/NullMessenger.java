package toolbox.log4j.im;

import java.util.Properties;

/**
 * NullMessenger for mock/testing purposes. 
 */
public class NullMessenger implements InstantMessenger
{
    //--------------------------------------------------------------------------
    // InstantMessenger Interface 
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.log4j.im.InstantMessenger#initialize(java.util.Properties)
     */
    public void initialize(Properties props) throws InstantMessengerException
    {
    }

    
    /**
     * @see toolbox.log4j.im.InstantMessenger#login(java.lang.String,
     *      java.lang.String)
     */
    public void login(String username, String password)
        throws InstantMessengerException
    {
    }

    
    /**
     * @see toolbox.log4j.im.InstantMessenger#send(java.lang.String,
     *      java.lang.String)
     */
    public void send(String recipient, String message)
        throws InstantMessengerException
    {
    }

    
    /**
     * @see toolbox.log4j.im.InstantMessenger#logout()
     */
    public void logout() throws InstantMessengerException
    {
    }

    
    /**
     * @see toolbox.log4j.im.InstantMessenger#shutdown()
     */
    public void shutdown() throws InstantMessengerException
    {
    }

    
    /**
     * @see toolbox.log4j.im.InstantMessenger#isConnected()
     */
    public boolean isConnected()
    {
        return false;
    }
}
