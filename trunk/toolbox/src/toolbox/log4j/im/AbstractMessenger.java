package toolbox.log4j.im;

import hamsam.api.Buddy;
import hamsam.api.IMAdapter;
import hamsam.api.Message;
import hamsam.api.TextComponent;
import hamsam.exception.IllegalStateException;
import hamsam.net.ProxyInfo;
import hamsam.protocol.Protocol;
import hamsam.protocol.ProtocolManager;

import java.util.Map;

import edu.emory.mathcs.util.concurrent.BlockingQueue;
import edu.emory.mathcs.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.collections.MapUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.helpers.LogLog;

import toolbox.util.ThreadUtil;
import toolbox.util.invoker.Invoker;
import toolbox.util.invoker.QueuedInvoker;
import toolbox.util.service.AbstractService;
import toolbox.util.service.ServiceException;
import toolbox.util.service.ServiceTransition;

/**
 * Abstract Instant Messenger client that supports login, send message, and
 * logout.
 */
public abstract class AbstractMessenger extends AbstractService 
    implements InstantMessenger
{
    private static final Logger logger_ = 
        Logger.getLogger(AbstractMessenger.class);
    
    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------

    /**
     * Return code for a successful connection.
     */
    public static final String CONNECT_SUCCEEDED = "Connect succeeded!";

    /**
     * Return code for a failed connection.
     */
    public static final String CONNECT_FAILED = "Connect failed!";

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * Available instant messaging protocols.
     */
    private Protocol[] protocols_;

    /**
     * Instant messaging protocol.
     */
    private Protocol messenger_;

    /**
     * Listener for client and server side generated messenger events.
     */
    private MessengerListener listener_;

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
     * Creates an AbstractMessenger with strict service state transitions.
     */
    protected AbstractMessenger()
    {
    }
    
    //--------------------------------------------------------------------------
    // Abstract
    //--------------------------------------------------------------------------

    /**
     * Returns the index into the array of protocols to use for this messenger.
     * 
     * @return int
     */
    public abstract int getProtocol();

    //--------------------------------------------------------------------------
    // Service Interface
    //--------------------------------------------------------------------------

    /**
     * Initializes this instant messenger. 
     * 
     * @param configuration Can contain an option property PROP_THROTTLE
     * @see toolbox.util.service.Initializable#initialize(java.util.Map)
     */
    public void initialize(Map configuration) throws ServiceException 
    {
        checkTransition(ServiceTransition.INITIALIZE);
        
        invoker_ = new QueuedInvoker();
        protocols_ = ProtocolManager.getAvailableProtocols();
        messenger_ = protocols_[getProtocol()];
        messenger_.setListener(listener_ = new MessengerListener());

        throttle_ = 
            MapUtils.getIntValue(
                configuration, 
                PROP_THROTTLE, 
                InstantMessengerAppender.DEFAULT_THROTTLE);
        
        transition(ServiceTransition.INITIALIZE);
    }

    
    /**
     * @see toolbox.util.service.Destroyable#destroy()
     */
    public void destroy() throws ServiceException 
    {
        checkTransition(ServiceTransition.DESTROY);
        
        try
        {
            invoker_.destroy();
        }
        catch (Exception e)
        {
            throw new ServiceException(e);
        }
        
        transition(ServiceTransition.DESTROY);
    }
    
    //--------------------------------------------------------------------------
    // InstantMessenger Interface
    //--------------------------------------------------------------------------

    /**
     * Synchronized method since whole send/recv is async. Waiters in the
     * queue will return immediately because the connected_ flag gets checked
     * before anything happens.
     *
     * @see toolbox.log4j.im.InstantMessenger#login(java.lang.String,
     *      java.lang.String)
     */
    public synchronized void login(String username, String password)
        throws InstantMessengerException
    {
        if (isConnected())
            return;

        ProxyInfo info = new ProxyInfo();

        try
        {
            messenger_.connect(username, password, info);
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
     * @see toolbox.log4j.im.InstantMessenger#logout()
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

            messenger_.disconnect();
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
     * @see toolbox.log4j.im.InstantMessenger#send(java.lang.String,
     *      java.lang.String)
     */
    public void send(String recipient, String message)
    {
        LogLog.debug("Appending: " + message);

        final Buddy buddy = new Buddy(messenger_, recipient);
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
                        //getPrivateChatInstance(buddy).sendMessage(msg);
                        messenger_.sendInstantMessage(buddy, msg);

                    }
                    catch (IllegalStateException e)
                    {
                        e.printStackTrace();
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
     * @see toolbox.log4j.im.InstantMessenger#isConnected()
     */
    public boolean isConnected()
    {
        return connected_;
    }

    //--------------------------------------------------------------------------
    // MessengerListener
    //--------------------------------------------------------------------------

    /**
     * Listener for client and server side generated messenger events.
     */
    class MessengerListener extends IMAdapter
    {
        //----------------------------------------------------------------------
        // Fields
        //----------------------------------------------------------------------
        
        /**
         * Login success and failures both go in this queue.
         */
        private BlockingQueue connected_;

        /**
         * Disconnect notification goes into this queue.
         */
        private BlockingQueue disconnected_;

        //----------------------------------------------------------------------
        // Constructors
        //----------------------------------------------------------------------

        /**
         * Creates a MessengerListener.
         */
        public MessengerListener()
        {
            connected_    = new LinkedBlockingQueue();
            disconnected_ = new LinkedBlockingQueue();
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
            LogLog.debug("Waiting for connect ack...");
            String ack = (String) connected_.take();
            LogLog.debug("Received connect ack!");
            return ack;
        }


        /**
         * Waits for a successful disconnect.
         *
         * @return Protocol that was disconnected.
         * @throws InterruptedException if interrupted while pulling from the
         *         <code>disconnected_</code> queue.
         */
        public Protocol waitForDisconnect() throws InterruptedException
        {
            return (Protocol) disconnected_.take();
        }

        //----------------------------------------------------------------------
        // hamsam.api.IMListener Interface
        //----------------------------------------------------------------------

        /**
         * @see hamsam.api.IMListener#connected(hamsam.protocol.Protocol)
         */
        public void connected(Protocol protocol)
        {
            LogLog.debug("Connected to " + protocol.getProtocolName());
            try
            {
                connected_.put(CONNECT_SUCCEEDED);
            }
            catch (InterruptedException e)
            {
                logger_.error(e);
            }
        }


        /**
         * @see hamsam.api.IMListener#connectFailed(
         *      hamsam.protocol.Protocol, java.lang.String)
         */
        public void connectFailed(Protocol protocol, String reasonMessage)
        {

            LogLog.debug(
                "Connect to " + protocol.getProtocolName() +
                " failed: " + reasonMessage);

            try
            {
                connected_.put(CONNECT_FAILED);
            }
            catch (InterruptedException e)
            {
                logger_.error(e);
            }
        }


        /**
         * @see hamsam.api.IMListener#connecting(hamsam.protocol.Protocol)
         */
        public void connecting(Protocol protocol)
        {
            LogLog.debug("Connecting to " + protocol.getProtocolName() + "...");
        }


        /**
         * @see hamsam.api.IMListener#disconnected(hamsam.protocol.Protocol)
         */
        public void disconnected(Protocol protocol)
        {
            try
            {
                disconnected_.put(protocol);
            }
            catch (InterruptedException e)
            {
                logger_.error(e);
            }
            LogLog.debug("Disconnected from " + protocol.getProtocolName());
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