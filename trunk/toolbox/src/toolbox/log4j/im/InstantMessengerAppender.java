package toolbox.log4j.im;

import java.util.Properties;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;

/**
 * InstantMessengerAppender is a Log4J appender capable of sending logging
 * messages to an online buddy via an instant messenging network. AOL and
 * Yahoo are supported at this time.
 * <br>
 * Example configuration of an appender which logs messages via Yahoo Messenger:
 * <pre>
 * 
 * &lt;appender name=&quot;instantmessenger&quot;
 *           class=&quot;toolbox.log4j.im.InstantMessengerAppender&quot;&gt;
 *     &lt;param name=&quot;Messenger&quot; value=&quot;Yahoo&quot;/&gt;
 *     &lt;param name=&quot;Username&quot;  value=&quot;my_username&quot;/&gt;
 *     &lt;param name=&quot;Password&quot;  value=&quot;my_password&quot;/&gt;
 *     &lt;param name=&quot;Recipient&quot; value=&quot;my_recipient&quot;/&gt;
 *     &lt;param name=&quot;Throttle&quot;     value=&quot;500&quot;/&gt;
 * &lt;/appender&gt;
 *  
 * &lt;logger name=&quot;errorlogger&quot;&gt;
 *     &lt;level value=&quot;ERROR&quot;/&gt;
 *     &lt;appender-ref ref=&quot;instantmessenger&quot;/&gt;
 * &lt;/logger&gt; 
 * </pre> 
 */
public class InstantMessengerAppender extends AppenderSkeleton
{
    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------
    
    /**
     * Default throttle delay is 500 ms for sending of subsequent messages.
     */
    public static final int DEFAULT_THROTTLE = 500;
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
        
    /**
     * IM network specific instant messenger interface.
     */
    private InstantMessenger messenger_;
    
    /**
     * Identifies the IM network to send messages to. (AOL, Yahoo, etc).
     */
    private String messengerType_;
    
    /**
     * Username to logon the the IM network.
     */
    private String username_;
    
    /**
     * Clear text password used for authentication.
     */
    private String password_;
    
    /**
     * Username of the online buddy to send the log messages to. 
     */
    private String recipient_;

    /**
     * Forced delay in milliseconds between instant messages. Set to zero for
     * no delay.
     */
    private int throttle_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates an InstantMessengerAppender.
     */
    public InstantMessengerAppender()
    {
        throttle_ = DEFAULT_THROTTLE;
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Sets the IM network. 
     * 
     * @param string IM network type. Valid values include AOL and Yahoo.
     */
    public void setMessenger(String string)
    {
        LogLog.debug("setMessenger(" + string + ")");
        messengerType_ = string;
    }


    /**
     * Sets the password. 
     * 
     * @param string Cleartext password
     */
    public void setPassword(String string)
    {
        password_ = string;
    }


    /**
     * Sets the recipients username.
     * 
     * @param string Recipient
     */
    public void setRecipient(String string)
    {
        recipient_ = string;
    }


    /**
     * Sets the username.
     * 
     * @param string Username to use for the message origin
     */
    public void setUsername(String string)
    {
        username_ = string;
    }


    /**
     * Sets the throttle in milliseconds between message sends.
     * 
     * @param throttle Delay in milliseconds between sending of successive 
     *        instant messages.
     */
    public void setThrottle(int throttle)
    {
        throttle_ = throttle;
    }

    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------

    /** 
     * Initializes the appender.
     */
    protected void init()
    {
        throttle_ = 0;
        messenger_ = InstantMessengerFactory.create(messengerType_);
    }


    /** 
     * Connects to the instant messenger server and authenticates the user.
     * 
     * @throws InstantMessengerException on connection or authentication error
     */
    protected void connect() throws InstantMessengerException
    {
        if (messenger_ == null)
            init();
            
        LogLog.debug("Connect called: username=" + username_);
        
        Properties props = new Properties();
        props.put(InstantMessenger.PROP_THROTTLE, throttle_ + "");
        
        messenger_.initialize(props);
        messenger_.login(username_, password_);
    }

    //--------------------------------------------------------------------------
    // Abstract Implementation of org.apache.log4j.AppenderSkeleton
    //--------------------------------------------------------------------------
    
    /**
     * @see org.apache.log4j.AppenderSkeleton#append(
     *      org.apache.log4j.spi.LoggingEvent)
     */
    protected void append(LoggingEvent event)
    {
        //System.out.println("append called");
        
        try
        {
            if (messenger_ == null)
                init();
                
            if (!messenger_.isConnected())
                connect();
            
            if (messenger_.isConnected())
            {
                messenger_.send(recipient_, event.getMessage().toString());
            } 
                //layout.format(event) );
        }
        catch (Exception e)
        {
            LogLog.error("append", e);
        }
    }

    //--------------------------------------------------------------------------
    // Overrides org.apache.log4j.AppenderSkeleton
    //--------------------------------------------------------------------------

    /**
     * @see org.apache.log4j.Appender#close()
     */
    public void close()
    {
        try
        {
            messenger_.logout();
            messenger_.shutdown();
        }
        catch (InstantMessengerException e)
        {
            LogLog.error("close", e);
        }
    }


    /**
     * @see org.apache.log4j.Appender#requiresLayout()
     */
    public boolean requiresLayout()
    {
        return false;
    }
}