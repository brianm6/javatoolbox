package toolbox.log4j.im;

import java.util.Properties;

import com.lotus.sametime.community.AdminMsgEvent;
import com.lotus.sametime.community.AdminMsgListener;
import com.lotus.sametime.community.CommunityService;
import com.lotus.sametime.community.LoginEvent;
import com.lotus.sametime.community.LoginListener;
import com.lotus.sametime.community.ServiceEvent;
import com.lotus.sametime.community.ServiceListener;
import com.lotus.sametime.core.comparch.ComponentListener;
import com.lotus.sametime.core.comparch.DuplicateObjectException;
import com.lotus.sametime.core.comparch.STCompApi;
import com.lotus.sametime.core.comparch.STSession;
import com.lotus.sametime.core.constants.EncLevel;
import com.lotus.sametime.core.constants.ImTypes;
import com.lotus.sametime.core.types.STId;
import com.lotus.sametime.core.types.STUser;
import com.lotus.sametime.im.Im;
import com.lotus.sametime.im.ImEvent;
import com.lotus.sametime.im.ImListener;
import com.lotus.sametime.im.ImServiceListener;
import com.lotus.sametime.im.InstantMessagingService;

import org.apache.log4j.helpers.LogLog;

import toolbox.util.PropertiesUtil;
import toolbox.util.concurrent.BlockingQueue;
import toolbox.util.invoker.Invoker;
import toolbox.util.invoker.QueuedInvoker;

/**
 * Lotus Sametime Instant Messenger client that supports the bare minimum to 
 * send an instant message.
 */
public class SametimeMessenger implements InstantMessenger
{
    /** 
     * Invoker used to handle the sending of messages 
     */
    private Invoker invoker_;

    /**
     * Sametime session
     */
    private STSession session_;
    
    /**
     * Community service 
     */
    private CommunityService communityService_;
    
    /**
     * Instant messaging service
     */
    private InstantMessagingService  messagingService_;
    
    /**
     * Instant messsage chat session
     */
    private Im chatSession_;

    /**
     * Listener for login events
     */
    private SametimeLoginListener loginListener_;

    /**
     * Listener for message service events
     */
    private ImServiceListener messagingServiceListener_;

    /**
     * Listener for chat session events
     */       
    private SametimeImListener chatSessionListener_;

    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a SametimeMessenger
     */
    public SametimeMessenger()
    {
    }

    //--------------------------------------------------------------------------
    // InstantMessenger Interface
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.log4j.im.InstantMessenger#initialize(java.util.Properties)
     */
    public void initialize(Properties props) throws InstantMessengerException
    {
        long delay = PropertiesUtil.getLong(props, PROP_THROTTLE, 1000);
        invoker_    = new QueuedInvoker(delay);
  
        try
        {
            session_ = new STSession("SametimeMessenger " + this);
            session_.addComponentListener(new SametimeComponentListener());
            
            session_.loadComponents(
                new String[] {
                    "com.lotus.sametime.community.STBase",
                    "com.lotus.sametime.im.ImComp",
                    "com.lotus.sametime.resourceloader.ResourceLoaderComp"});
            
            /*
            log4j: componentLoaded {com.lotus.sametime.conf.ConfComp(part), STSession = STSession {SametimeMessenger toolbox.log4j.im.SametimeMessenger@95c083}}
            
            log4j: componentLoaded {com.lotus.sametime.places.PlacesComp(part), STSession = STSession {SametimeMessenger toolbox.log4j.im.SametimeMessenger@95c083}}
            log4j: componentLoaded {com.lotus.sametime.post.PostComp(part), STSession = STSession {SametimeMessenger toolbox.log4j.im.SametimeMessenger@95c083}}
            log4j: componentLoaded {com.lotus.sametime.storage.StorageService(part), STSession = STSession {SametimeMessenger toolbox.log4j.im.SametimeMessenger@95c083}}
            log4j: componentLoaded {com.lotus.sametime.wih.WihComp(part), STSession = STSession {SametimeMessenger toolbox.log4j.im.SametimeMessenger@95c083}}
            log4j: componentLoaded {com.lotus.sametime.awareness.AwarenessComp(part), STSession = STSession {SametimeMessenger toolbox.log4j.im.SametimeMessenger@95c083}}
            log4j: componentLoaded {com.lotus.sametime.directory.DirectoryComp(part), STSession = STSession {SametimeMessenger toolbox.log4j.im.SametimeMessenger@95c083}}
            log4j: componentLoaded {com.lotus.sametime.lookup.LookupComp(part), STSession = STSession {SametimeMessenger toolbox.log4j.im.SametimeMessenger@95c083}}
            log4j: componentLoaded {com.lotus.sametime.token.TokenComp(part), STSession = STSession {SametimeMessenger toolbox.log4j.im.SametimeMessenger@95c083}}
            log4j: componentLoaded {com.lotus.sametime.names.NamesComp(part), STSession = STSession {SametimeMessenger toolbox.log4j.im.SametimeMessenger@95c083}}
            log4j: componentLoaded {com.lotus.sametime.filetransfer.FileTransferComp(part), STSession = STSession {SametimeMessenger toolbox.log4j.im.SametimeMessenger@95c083}}
            log4j: componentLoaded {com.lotus.sametime.announcement.AnnouncementComp(part), STSession = STSession {SametimeMessenger toolbox.log4j.im.SametimeMessenger@95c083}}
            log4j: componentLoaded {com.lotus.sametime.buddylist.BLComp(part), STSession = STSession {SametimeMessenger toolbox.log4j.im.SametimeMessenger@95c083}}
            
            log4j: componentLoaded {com.lotus.sametime.commui.CommUIComp(part), STSession = STSession {SametimeMessenger toolbox.log4j.im.SametimeMessenger@95c083}}
            log4j: componentLoaded {com.lotus.sametime.chatui.ChatUIComp(part), STSession = STSession {SametimeMessenger toolbox.log4j.im.SametimeMessenger@95c083}}
            log4j: componentLoaded {com.lotus.sametime.filetransferui.FileTransferUIComp(part), STSession = STSession {SametimeMessenger toolbox.log4j.im.SametimeMessenger@95c083}}
            log4j: componentLoaded {com.lotus.sametime.announcementui.AnnouncementUIComp(part), STSession = STSession {SametimeMessenger toolbox.log4j.im.SametimeMessenger@95c083}}            
            */
                        
            //session_.loadAllComponents();
            session_.start();
        }
        catch (DuplicateObjectException doe)
        {
            throw new InstantMessengerException(doe);
        }
    }


    /**
     * @see toolbox.log4j.im.InstantMessenger#login(
     *          java.lang.String, java.lang.String)
     */
    public synchronized void login(String username, String password) 
        throws InstantMessengerException
    {
        try
        {
            if (communityService_ != null && communityService_.isLoggedIn())
            {
                LogLog.warn("Already logged in.");
            }
            else
            {
                communityService_ = (CommunityService) 
                    session_.getCompApi(CommunityService.COMP_NAME);
                    
                communityService_.addLoginListener(
                    loginListener_ = new SametimeLoginListener());
                
                communityService_.addServiceListener(
                    new SametimeServiceListener());
                    
                communityService_.addAdminMsgListener(
                    new SametimeAdminMsgListener());
                             
                communityService_.loginByPassword(
                    "sametime.ibm.com",username,password);
                
                loginListener_.waitForLogin();
            }
        }
        catch (InterruptedException ie)
        {
            throw new InstantMessengerException(ie);
        }
    }


    /**
     * @see toolbox.log4j.im.InstantMessenger#send(
     *          java.lang.String, java.lang.String)
     */
    public void send(final String recipient, final String message)
        throws InstantMessengerException
    {
        LogLog.debug("Sending IM: " + message);
        
        // Establish a chat session if this is the first message
        // sent to the recipient
        
        if (!communityService_.isLoggedIn())
        {
            STUser partner = new STUser(
                new STId(recipient, "??? communityname"), recipient, "desc");
    
            chatSession_ = messagingService_.createIm(
                partner, EncLevel.ENC_LEVEL_NONE, ImTypes.IM_TYPE_CHAT);
            
            chatSession_.addImListener(
                chatSessionListener_ = new SametimeImListener());
                
            chatSession_.open();
        }
        
        try
        {
            invoker_.invoke(new Runnable()
            {
                public void run()
                {
                   chatSession_.sendText(false, message);
                }
            });
        }
        catch (Exception e)
        {
            throw new InstantMessengerException("send", e);
        }
    }


    /**
     * @see toolbox.log4j.im.InstantMessenger#logout()
     */
    public void logout() throws InstantMessengerException
    {
        chatSession_.removeImListener(chatSessionListener_);
        chatSession_.close(0); // TODO: find proper code
        messagingService_.removeImServiceListener(messagingServiceListener_);
        communityService_.removeLoginListener(loginListener_);
        communityService_.logout();
    }


    /**
     * @see toolbox.log4j.im.InstantMessenger#shutdown()
     */
    public void shutdown() throws InstantMessengerException
    {
        try
        {
            session_.stop();
            session_.unloadSession();
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
        return communityService_.isLoggedIn();
    }
        
    //--------------------------------------------------------------------------
    // Inner Classes
    //--------------------------------------------------------------------------
    
    /**
     * Listener for login events
     */
    class SametimeLoginListener implements LoginListener
    {
        private BlockingQueue loginQueue_ = new BlockingQueue();
        
        /**
         * @see com.lotus.sametime.community.LoginListener#loggedIn(
         *          com.lotus.sametime.community.LoginEvent)
         */
        public void loggedIn(LoginEvent arg0)
        {
            LogLog.debug("Logged in");
        
            String myName = 
                communityService_.getLogin().
                    getMyUserInstance().
                        getDisplayName();
                                             
            messagingService_ = (InstantMessagingService)
                session_.getCompApi(InstantMessagingService.COMP_NAME);
            
            messagingService_.registerImType(ImTypes.IM_TYPE_CHAT);           

            messagingService_.addImServiceListener(
                messagingServiceListener_ = 
                    new SametimeMessagingServiceListener());
                    
            loginQueue_.push("LOGIN");
        }

        /**
         * @see com.lotus.sametime.community.LoginListener#loggedOut(
         *          com.lotus.sametime.community.LoginEvent)
         */
        public void loggedOut(LoginEvent arg0)
        {
            LogLog.debug("Logged out : " + arg0);
        }
        
        public void waitForLogin() throws InterruptedException
        {
            LogLog.debug("Waiting for login event..");
            loginQueue_.pull();
            LogLog.debug("Login event received");    
        }
    }

    /**
     * Listener for instant messaging service events
     */    
    class SametimeMessagingServiceListener implements ImServiceListener 
    {
        /**
         * @see com.lotus.sametime.im.ImServiceListener#imReceived(
         *          com.lotus.sametime.im.ImEvent)
         */
        public void imReceived(ImEvent arg0)
        {
            LogLog.debug("imReceived: " + arg0);
        }
    }

    /**
     * Listener for chat session events
     */        
    class SametimeImListener implements ImListener
    {
        /**
         * @see com.lotus.sametime.im.ImListener#dataReceived(
         *          com.lotus.sametime.im.ImEvent)
         */
        public void dataReceived(ImEvent arg0)
        {
            LogLog.debug("dataReceived");
        }

        /**
         * @see com.lotus.sametime.im.ImListener#imClosed(
         *          com.lotus.sametime.im.ImEvent)
         */
        public void imClosed(ImEvent arg0)
        {
            LogLog.debug("imClosed");
        }

        /**
         * @see com.lotus.sametime.im.ImListener#imOpened(
         *          com.lotus.sametime.im.ImEvent)
         */
        public void imOpened(ImEvent arg0)
        {
            LogLog.debug("imOpened");
        }

        /**
         * @see com.lotus.sametime.im.ImListener#openImFailed(
         *          com.lotus.sametime.im.ImEvent)
         */
        public void openImFailed(ImEvent arg0)
        {
            LogLog.debug("openImFailed");
        }

        /**
         * @see com.lotus.sametime.im.ImListener#textReceived(
         *          com.lotus.sametime.im.ImEvent)
         */
        public void textReceived(ImEvent arg0)
        {
            LogLog.debug("textReceived");
        }
    }
    
    /**
     * Session component listener 
     */
    class SametimeComponentListener implements ComponentListener
    {
        public void componentLoaded(STCompApi arg0)
        {
            LogLog.debug("componentLoaded " + arg0);
        }
    }
    
    class SametimeServiceListener implements ServiceListener
    {
        /**
         * @see com.lotus.sametime.community.ServiceListener#serviceAvailable(
         *          com.lotus.sametime.community.ServiceEvent)
         */
        public void serviceAvailable(ServiceEvent arg0)
        {
            LogLog.debug("serviceAvailable: " + arg0);
        }
    }
    
    class SametimeAdminMsgListener implements AdminMsgListener
    {
        /**
         * @see com.lotus.sametime.community.AdminMsgListener#adminMsgReceived(
         *          com.lotus.sametime.community.AdminMsgEvent)
         */
        public void adminMsgReceived(AdminMsgEvent arg0)
        {
            LogLog.debug("adminMsgReceived: " + arg0);
        }
    }
}