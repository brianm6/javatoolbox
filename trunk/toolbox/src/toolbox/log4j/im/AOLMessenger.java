package toolbox.log4j.im;

import java.io.IOException;
import java.util.Properties;

import com.wilko.jaim.ConnectionLostTocResponse;
import com.wilko.jaim.ErrorTocResponse;
import com.wilko.jaim.JaimConnection;
import com.wilko.jaim.JaimEvent;
import com.wilko.jaim.JaimEventListener;
import com.wilko.jaim.LoginCompleteTocResponse;
import com.wilko.jaim.TocResponse;

import org.apache.log4j.helpers.LogLog;

import toolbox.util.PropertiesUtil;
import toolbox.util.concurrent.BlockingQueue;
import toolbox.util.invoker.Invoker;
import toolbox.util.invoker.QueuedInvoker;

/**
 * AOL Instant Messenger client that supports the bare minimum to send an
 * instant message.
 */
public class AOLMessenger implements InstantMessenger
{
     // NOTE: Cannot use Log4J logging since this is included in the 
     //       implementation of a Log4J appender.
    
    /** 
     * Return code for a successful connection 
     */
    public static final String CONNECT_SUCCEEDED = "Connect succeeded!";
    
    /** 
     * Return code for a failed connection 
     */
    public static final String CONNECT_FAILED = "Connect failed!";
    
    /** 
     * Connection to the AOL instant messaging server 
     */
    private JaimConnection connection_;
    
    /** 
     * Listener for server side generated AOL events 
     */
    private AOLListener listener_;
    
    /** 
     * Flag that tracks the connection state 
     */
    private boolean connected_;
    
    /** 
     * Invoker used to handle the sending of messages 
     */
    private Invoker invoker_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates an AOLMessenger
     */
    public AOLMessenger()
    {
    }

    //--------------------------------------------------------------------------
    // InstantMessenger Interface
    //--------------------------------------------------------------------------

    public void initialize(Properties props) throws InstantMessengerException
    {
        long delay = PropertiesUtil.getLong(props, PROP_DELAY, 750);
        
        invoker_    = new QueuedInvoker(delay);
        connection_ = new JaimConnection("toc.oscar.aol.com", 9898);
        listener_   = new AOLListener();
        
        connection_.addEventListener(listener_);
        connection_.setDebug(false);
        
        try
        {
            connection_.connect();
        }
        catch (IOException e)
        {
            throw new InstantMessengerException(e);
        }
    }

    /**
     * Synchronized method since whole send/recv is async. Waiters in the
     * queue will return immediately because the connected_ flag gets checked
     * before anything happens. 
     */
    public synchronized void login(String username, String password) 
        throws InstantMessengerException
    {
        if (connected_)
            return;

        try
        {         
            connection_.logIn(username,password,50000);
            listener_.waitForLogin();
            
            while (!connection_.isLoginComplete());
            
            connected_ = true;
        }
        catch (Exception e)
        {
            connected_ = false;
            throw new InstantMessengerException(e);    
        }
    }

    /**
     * Logs out from AOL 
     */
    public void logout() throws InstantMessengerException
    {
        connection_.logOut();
        connected_ = false;
    }
    
    /**
     * Sends message to the recipient using a queue invoker strategy.
     */
    public void send(final String recipient, final String message)
        throws InstantMessengerException
    {
        LogLog.debug("Sending IM: " + message);
        
        try
        {
            invoker_.invoke(new Runnable()
            {
                public void run()
                {
                    try
                    {
                        connection_.sendIM(recipient, message);
                    }
                    catch (IOException e)
                    {
                        LogLog.error("run", e);
                    }
                }
            });
        }
        catch (Exception e)
        {
            throw new InstantMessengerException("send", e);
        }
    }

    public void shutdown() throws InstantMessengerException
    {
        try
        {
            connection_.disconnect();
            invoker_.shutdown();
        }
        catch (Exception e)
        {
            throw new InstantMessengerException(e);
        }
    }
    
    public boolean isConnected()
    {
        return connected_;
    }
        
    //--------------------------------------------------------------------------
    // Inner Classes
    //--------------------------------------------------------------------------
    
    /** 
     * Listener for server generated AOL events 
     */
    class AOLListener implements JaimEventListener
    {
        /** 
         * Login success and failures both go in this queue 
         */
        BlockingQueue loginQueue_;
        
        /** 
         * Disconnect notification goes into this queue 
         */
        BlockingQueue disconnected_;
        
        //----------------------------------------------------------------------
        // Constructors
        //----------------------------------------------------------------------

        /** 
         * Creates an AOLListener
         */
        public AOLListener()
        {
            loginQueue_   = new BlockingQueue();
            disconnected_ = new BlockingQueue();
        }
        
        //----------------------------------------------------------------------
        // Public
        //----------------------------------------------------------------------

        /**
         * Waits for a login (failure or success)
         * 
         * @return TocResponse signalling login completion
         * @throws InterruptedException if interrupted while pulling from the 
         *         <code>connected_</code> queue. 
         */                
        public TocResponse waitForLogin() throws InterruptedException
        {
            return (TocResponse) loginQueue_.pull();
        }

        /**
         * Waits for a successful disconnect
         * 
         * @return Protocol that was disconnected.
         * @throws InterruptedExceptin if interrupted while pulling from the 
         *         <code>disconnected_</code> queue.
         */        
        public TocResponse waitForDisconnect() throws InterruptedException
        {
            return (TocResponse) disconnected_.pull();
        }

        //----------------------------------------------------------------------
        // com.wilko.jaim.JaimEventListener Interface
        //----------------------------------------------------------------------
        
        public void receiveEvent(JaimEvent event)
        {
            TocResponse response = event.getTocResponse();
            String      type     = response.getResponseType();

            if (type.equalsIgnoreCase(ErrorTocResponse.RESPONSE_TYPE))
            {
                ErrorTocResponse er = (ErrorTocResponse) response;
                LogLog.error("Error code: " + er.getErrorCode());
                LogLog.error("Error text: " + er.getErrorText());
                LogLog.error("Error desc: " + er.getErrorDescription());
            }
            else if (type.equalsIgnoreCase(
                LoginCompleteTocResponse.RESPONSE_TYPE))
            {
                LogLog.debug("Connected to AOL!");
                
                try
                {
                    loginQueue_.push(response);
                }
                catch (InterruptedException e)
                {
                    LogLog.error("receiveEvent", e);
                }
            }
            else if (type.equalsIgnoreCase(
                ConnectionLostTocResponse.RESPONSE_TYPE))
            {
                try
                {
                    disconnected_.push(response);
                    LogLog.debug("Disconnected from AOL");
                }
                catch (InterruptedException e)
                {
                    LogLog.error("receiveEvent", e);
                }
            }
            else
            {
                LogLog.debug(
                    "Unhandled TOC Response: " + response.getResponseType());
                    
                LogLog.debug("Unhandled TOC Response: " + response);
            }
        }
    }
}