package toolbox.log4j.im;

import java.util.Properties;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Instant Messenger Appender is a Log4J appender capable of sending logging
 * output to a few of the more popular instant messaging systems.
 */
public class InstantMessengerAppender extends AppenderSkeleton
{
    private InstantMessenger messenger_;
    private String messengerType_;
    private String username_;
    private String password_;
    private String recipient_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates an InstantMessengerAppender
     */
    public InstantMessengerAppender()
    {
        //init();
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * @param string
     */
    public void setMessenger(String string)
    {
        LogLog.debug("setMessenger(" + string + ")");
        messengerType_ = string;
    }

    /**
     * @param string
     */
    public void setPassword(String string)
    {
        password_ = string;
    }

    /**
     * @param string
     */
    public void setRecipient(String string)
    {
        recipient_ = string;
    }

    /**
     * @param string
     */
    public void setUsername(String string)
    {
        username_ = string;
    }

    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------

    /** 
     * Initializes the appender
     */
    protected void init()
    {
        //setMessenger("yahoo");
        //setUsername("supahfuzz");
        //setPassword("techno");
        //setRecipient("analogue");
        
        messenger_ = InstantMessengerFactory.create(messengerType_);
    }

    /** 
     * Connects to the instant messenger server and authenticates the user.
     * 
     * @throws InstantMessengerException on error
     */
    protected void connect() throws InstantMessengerException
    {
        if (messenger_ == null)
            init();
            
        LogLog.debug("Connect called: username=" + username_);
        
        messenger_.initialize(new Properties());
        messenger_.login(username_, password_);
    }

    //--------------------------------------------------------------------------
    // Abstract Implementation of org.apache.log4j.AppenderSkeleton
    //--------------------------------------------------------------------------
    
    /**
     * @see org.apache.log4j.AppenderSkeleton#append(org.apache.log4j.spi.LoggingEvent)
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
