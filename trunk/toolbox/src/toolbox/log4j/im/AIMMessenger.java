package toolbox.log4j.im;

import hamsam.api.Buddy;
import hamsam.api.IMAdapter;
import hamsam.api.Message;
import hamsam.api.TextComponent;
import hamsam.exception.IllegalStateException;
import hamsam.net.ProxyInfo;
import hamsam.protocol.Protocol;
import hamsam.protocol.ProtocolManager;

import java.util.Properties;

import org.apache.log4j.helpers.LogLog;

import toolbox.util.ArrayUtil;
import toolbox.util.PropertiesUtil;
import toolbox.util.ThreadUtil;
import toolbox.util.concurrent.BlockingQueue;
import toolbox.util.invoker.Invoker;
import toolbox.util.invoker.QueuedInvoker;

/**
 * AIM Instant Messenger client that supports login, send message, and logout.
 */
public class AIMMessenger implements InstantMessenger
{
     // NOTE: Cannot use Log4J logging since this is included in the 
     //       implementation of a Log4J appender.
    
    /** 
     * Return code for a successful connection. 
     */
    public static final String CONNECT_SUCCEEDED = "Connect succeeded!";
    
    /** 
     * Return code for a failed connection.
     */
    public static final String CONNECT_FAILED = "Connect failed!";
    
    /** 
     * Available instant messaging protocols. 
     */
    private Protocol[] protocols;
    
    /** 
     * AIM instant messaging protocol.
     */
    private Protocol aim_;
    
    /** 
     * Listener for client and server side generated Yahoo events. 
     */
    private AIMListener listener_;
    
    /** 
     * Flag that tracks the connection state.
     */
    private boolean connected_;
    
    /** 
     * Invoker used to handle the sending of messages. 
     */
    private Invoker invoker_;
    
    /**
     * Throttle delay for consecutive messages so the messenger server does 
     * not get overwhelmed.
     */
    private int throttle_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates an AIMMessenger.
     */
    public AIMMessenger()
    {
    }

    //--------------------------------------------------------------------------
    // InstantMessenger Interface
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.log4j.im.InstantMessenger#initialize(java.util.Properties)
     */
    public void initialize(Properties props)
    {
        invoker_ = new QueuedInvoker();
        protocols = ProtocolManager.getAvailableProtocols();
        System.out.println(ArrayUtil.toString(protocols));
        aim_ = protocols[2];
        aim_.setListener(listener_ = new AIMListener());
        
        throttle_ = PropertiesUtil.getInteger(
            props, PROP_THROTTLE, InstantMessengerAppender.DEFAULT_THROTTLE);
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
            
        ProxyInfo info = new ProxyInfo();
        
        try
        {
            aim_.connect(username, password, info);
            String returnCode = listener_.waitForConnect();
            
            if (returnCode.equals(CONNECT_SUCCEEDED))
            {
                connected_ = true;
            }
            else if (returnCode.equals(CONNECT_FAILED))
            {
                throw new InstantMessengerException(
                    "Authentication failed for username '" + username + "'");
            }
            else
            {
                throw new IllegalArgumentException(
                    "Return code '" + returnCode + "' is invalid.");
            }
        }
        catch (Exception e)
        {
            connected_ = false;
            throw new InstantMessengerException(e);    
        }
    }
    
    
    /**
     * Logs out from AIM. 
     */
    public void logout() throws InstantMessengerException
    {
        try
        {
            QueuedInvoker qi = (QueuedInvoker) invoker_;
            
            while (!qi.isEmpty())
            {
                LogLog.debug("Messages waiting to be sent before logout: " + 
                    qi.getSize());
                    
                ThreadUtil.sleep(throttle_);
            }        
            
            aim_.disconnect();
            LogLog.debug("Waiting for disconnect ack...");
            listener_.waitForDisconnect();
            connected_ = false;
        }
        catch (Exception e)
        {
            throw new InstantMessengerException(e);
        }
    }
    
    
    /**
     * Sends message to the recipient using a queue invoker strategy.
     */
    public void send(String recipient, String message)
    {
        LogLog.debug("Appending: " + message);
        
        final Buddy buddy = new Buddy(aim_, recipient);
        final Message msg = new Message();
        msg.addComponent(new TextComponent(message));
        
        try
        {
            invoker_.invoke(new Runnable()
            {
                public void run()
                {
                    try
                    {
                        aim_.sendInstantMessage(buddy, msg);
                    }
                    catch (IllegalStateException e)
                    {
                        LogLog.error("send", e);
                    }
                    finally
                    {
                        ThreadUtil.sleep(throttle_);
                    }
                }
            });
        }
        catch (Exception e)
        {
            LogLog.error("send", e);
        }
    }


    /**
     * @see toolbox.log4j.im.InstantMessenger#shutdown()
     */
    public void shutdown() throws InstantMessengerException 
    {
        try
        {
            invoker_.shutdown();
        }
        catch (Exception e)
        {
            throw new InstantMessengerException(e);
        }
    }
    
        
    /**
     * @see toolbox.log4j.im.InstantMessenger#isConnected()
     */
    public boolean isConnected()
    {
        return connected_;
    }
    
    //--------------------------------------------------------------------------
    // Inner Classes
    //--------------------------------------------------------------------------
    
    /** 
     * Listener for client and server side generated Yahoo events. 
     */
    class AIMListener extends IMAdapter
    {
        /** 
         * Login success and failures both go in this queue.
         */
        BlockingQueue connected_;
        
        /** 
         * Disconnect notification goes into this queue. 
         */
        BlockingQueue disconnected_;
        
        //----------------------------------------------------------------------
        // Constructors
        //----------------------------------------------------------------------

        /** 
         * Creates an AIMListener
         */
        public AIMListener()
        {
            connected_    = new BlockingQueue();
            disconnected_ = new BlockingQueue();
        }
        
        //----------------------------------------------------------------------
        // Public
        //----------------------------------------------------------------------

        /**
         * Waits for a connect (failure or success).
         * 
         * @return CONNECT_SUCCEEDED or CONNECT_FAILED
         * @throws InterruptedException if interrupted while pulling from the 
         *         <code>connected_</code> queue. 
         */                
        public String waitForConnect() throws InterruptedException
        {
            return (String) connected_.pull();
        }


        /**
         * Waits for a successful disconnect.
         * 
         * @return Protocol that was disconnected.
         * @throws InterruptedExceptin if interrupted while pulling from the 
         *         <code>disconnected_</code> queue.
         */        
        public Protocol waitForDisconnect() throws InterruptedException
        {
            return (Protocol) disconnected_.pull();
        }

        //----------------------------------------------------------------------
        // hamsam.api.IMListener Interface
        //----------------------------------------------------------------------

        /**
         * @see hamsam.api.IMListener#connected(hamsam.protocol.Protocol)
         */
        public void connected(Protocol protocol)
        {
            LogLog.debug("Connected to Yahoo!");
            connected_.push(CONNECT_SUCCEEDED);
        }
        
        
        /**
         * @see hamsam.api.IMListener#connectFailed(
         *      hamsam.protocol.Protocol, java.lang.String)
         */
        public void connectFailed(Protocol protocol, String reasonMessage)
        {
            
            LogLog.debug("Connect to Yahoo failed: " + reasonMessage);
            connected_.push(CONNECT_FAILED);
        }
       
       
        /**
         * @see hamsam.api.IMListener#connecting(hamsam.protocol.Protocol)
         */
        public void connecting(Protocol protocol)
        {
            LogLog.debug("Connecting to Yahoo...");
        }


        /**
         * @see hamsam.api.IMListener#disconnected(hamsam.protocol.Protocol)
         */
        public void disconnected(Protocol protocol)
        {
            disconnected_.push(protocol);
            LogLog.debug("Disconnected from Yahoo");
        }

        
        /**
         * @see hamsam.api.IMListener#protocolMessageReceived(
         *      hamsam.protocol.Protocol, hamsam.api.Message)
         */
        public void protocolMessageReceived(Protocol protocol, Message message)
        {
            LogLog.debug("Protocol message: " + message.toString());
        }
    }
}