package toolbox.log4j.im;

import java.util.Properties;

/**
 * Common instant messenger interface.
 */
public interface InstantMessenger
{
    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------
    
    /**
     * Property for the delay between successive messages.
     */
    public static final String PROP_THROTTLE = "throttle";
    
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Initializes the instant messenger with any properties needed for the
     * specific implementation.
     * 
     * @param props Initialization properties
     * @throws InstantMessengerException on initialization error.
     */
    void initialize(Properties props)
        throws InstantMessengerException;
    
    
    /**
     * Logs the given user onto the instant messaging service.
     * 
     * @param username Username
     * @param password Password in cleartext
     * @throws InstantMessengerException if authentication fails.
     */
    void login(String username, String password) 
        throws InstantMessengerException;
    
    
    /**
     * Sends an instant message to the given recipient.
     * 
     * @param recipient Recipient of the instant message
     * @param message Text of the message
     * @throws InstantMessengerException if sending fails.
     */
    void send(String recipient, String message)
        throws InstantMessengerException;
    
    
    /**
     * Logs the user off of the instant messaging system.
     * 
     * @throws InstantMessengerException if an error occurs
     */
    void logout() throws InstantMessengerException;
    
    
    /**
     * Shuts down the client.
     * 
     * @throws InstantMessengerException if an error occurs during shutdown
     */
    void shutdown() throws InstantMessengerException;
    
    
    /**
     * Returns true if we're successfully connected to the instant messaging
     * server.
     * 
     * @return True if connected, false otherwise
     */
    boolean isConnected();
}