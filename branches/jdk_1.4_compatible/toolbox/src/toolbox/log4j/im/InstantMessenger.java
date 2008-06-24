package toolbox.log4j.im;

import toolbox.util.service.Destroyable;
import toolbox.util.service.Initializable;

/**
 * Common instant messenger interface.
 */
public interface InstantMessenger extends Initializable, Destroyable
{
    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------
    
    /**
     * Property for the delay between successive messages.
     */
    String PROP_THROTTLE = "throttle";
    
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Logs the given user onto the instant messaging service.
     * 
     * @param username Username.
     * @param password Password in cleartext.
     * @throws InstantMessengerException if authentication fails.
     */
    void login(String username, String password) 
        throws InstantMessengerException;
    
    
    /**
     * Sends an instant message to the given recipient.
     * 
     * @param recipient Recipient of the instant message.
     * @param message Text of the message.
     * @throws InstantMessengerException if sending fails.
     */
    void send(String recipient, String message)
        throws InstantMessengerException;
    
    
    /**
     * Logs the user off of the instant messaging system.
     * 
     * @throws InstantMessengerException if an error occurs.
     */
    void logout() throws InstantMessengerException;
    
    
    /**
     * Returns true if we're successfully connected to the instant messaging
     * server.
     * 
     * @return True if connected, false otherwise.
     */
    boolean isConnected();
}