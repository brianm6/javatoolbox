package toolbox.log4j.im;

import java.util.Map;

import toolbox.util.service.ServiceException;
import toolbox.util.service.ServiceState;

/**
 * NullMessenger for mock/testing purposes. 
 */
public class NullMessenger implements InstantMessenger
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
    
    
    /**
     * @see toolbox.util.service.Service#getState()
     */
    public ServiceState getState()
    {
        return null;
    }
    
    
    /**
     * @see toolbox.util.service.Destroyable#destroy()
     */
    public void destroy() throws IllegalStateException, ServiceException
    {
    }
    
    
    /**
     * @see toolbox.util.service.Initializable#initialize(java.util.Map)
     */
    public void initialize(Map configuration) throws IllegalStateException,
        ServiceException
    {
    }
}