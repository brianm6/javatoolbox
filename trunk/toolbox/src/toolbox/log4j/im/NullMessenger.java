package toolbox.log4j.im;

import toolbox.util.service.ServiceAdapter;

/**
 * NullMessenger for mock/testing purposes. 
 */
public class NullMessenger extends ServiceAdapter implements InstantMessenger
{
    //--------------------------------------------------------------------------
    // InstantMessenger Interface 
    //--------------------------------------------------------------------------
    
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
     * @see toolbox.log4j.im.InstantMessenger#isConnected()
     */
    public boolean isConnected()
    {
        return false;
    }
}