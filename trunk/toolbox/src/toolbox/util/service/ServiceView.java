package toolbox.util.service;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPanel;

import toolbox.util.ui.JSmartButton;
import toolbox.util.ui.SmartAction;

/**
 * ServiceView a user interface component that presents a view on an object that
 * implements the Service interface. Features include:
 * <ul>
 *  <li>Start, stop, suspend, resume a Service
 *  <li>Displays the current state of the Service.
 * </ul>
 */
public class ServiceView extends JPanel
{
    //--------------------------------------------------------------------------
    // Action Constants
    //--------------------------------------------------------------------------
    
    /**
     * Action name to start the service.
     */
    private static final String ACTION_START = "start";

    /**
     * Action name to suspend the service.
     */
    private static final String ACTION_SUSPEND = "suspend";
    
    /**
     * Action name to resume the service.
     */
    private static final String ACTION_RESUME = "resume";
    
    /**
     * Action name to stop the service.
     */
    private static final String ACTION_STOP = "stop";
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Service thats services as the model for this view.
     */
    private Service service_;

    /**
     * Maps an action's name to its corresponding action.
     */
    private Map actions_;
    
    /**
     * Internal listener for this service.
     */
    private ServiceListener myServiceListener_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a ServiceView.
     * 
     * @param service Service that acts as the model for this view.
     */
    public ServiceView(Service service)
    {
        myServiceListener_ = new MyServiceListener();
        setService(service);
        actions_ = new HashMap(4);
        buildView();
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Returns the service.
     * 
     * @return Service
     */
    public Service getService()
    {
        return service_;
    }
    
    
    /**
     * Sets the value of service.
     * 
     * @param service The service to set.
     */
    public void setService(Service service)
    {
        // Remove the old one if one exists
        if (service_ != null)
            service_.removeServiceListener(myServiceListener_);
        
        service_ = service;
        
        // Transsfer the listener to the newly appointed service.
        service.addServiceListener(myServiceListener_);
    }
    
    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------

    /**
     * Builds the GUI.
     */
    protected void buildView()
    {
        setLayout(new FlowLayout());
        actions_.put(ACTION_START, new StartAction());
        actions_.put(ACTION_SUSPEND, new SuspendAction());
        actions_.put(ACTION_RESUME, new ResumeAction());
        actions_.put(ACTION_STOP, new StopAction());
        add(new JSmartButton((Action) actions_.get(ACTION_START)));
        add(new JSmartButton((Action) actions_.get(ACTION_SUSPEND)));
        add(new JSmartButton((Action) actions_.get(ACTION_RESUME)));
        add(new JSmartButton((Action) actions_.get(ACTION_STOP)));
    }
    
    
    //--------------------------------------------------------------------------
    // MyServicelistener
    //--------------------------------------------------------------------------
    
    /**
     * The internal listener is only interested in the INITIALIZED state so that
     * the initial state of the buttons can be set.
     */
    class MyServiceListener implements ServiceListener {
        
        /**
         * @see toolbox.util.service.ServiceListener#serviceStateChanged(
         *      toolbox.util.service.Service)
         */
        public void serviceStateChanged(Service service) throws ServiceException
        {
            if (service.getState() == ServiceState.INITIALIZED)
            {
                ((AbstractAction) actions_.get(ACTION_START)).setEnabled(true);
                ((AbstractAction) actions_.get(ACTION_STOP)).setEnabled(false);
                ((AbstractAction) actions_.get(ACTION_SUSPEND)).setEnabled(false);
                ((AbstractAction) actions_.get(ACTION_RESUME)).setEnabled(false);
            }
        }
    }
    
    //--------------------------------------------------------------------------
    // StartAction
    //--------------------------------------------------------------------------

    /**
     * StartAction starts the attached service and sets the state of the buttons
     * according to the next available set of state transitions.
     */
    class StartAction extends SmartAction
    {
        /**
         * Creates a StartAction.
         */
        public StartAction()
        {
            super("Start", true, false, null);
        }

        
        /**
         * @see toolbox.util.ui.SmartAction#runAction(
         *      java.awt.event.ActionEvent)
         */
        public void runAction(ActionEvent e) throws Exception
        {
            ((Startable) service_).start();
            ((AbstractAction) actions_.get(ACTION_START)).setEnabled(false);
            ((AbstractAction) actions_.get(ACTION_STOP)).setEnabled(true);
            ((AbstractAction) actions_.get(ACTION_SUSPEND)).setEnabled(true);
            ((AbstractAction) actions_.get(ACTION_RESUME)).setEnabled(false);
        }
    }

    //--------------------------------------------------------------------------
    // StopAction
    //--------------------------------------------------------------------------

    /**
     * StopAction stops the attached service and sets the state of the buttons
     * according to the next available set of state transitions.
     */
    class StopAction extends SmartAction
    {
        /**
         * Creates a StopAction.
         */
        public StopAction()
        {
            super("Stop", true, false, null);
        }

        
        /**
         * @see toolbox.util.ui.SmartAction#runAction(
         *      java.awt.event.ActionEvent)
         */
        public void runAction(ActionEvent e) throws Exception
        {
            ((Startable) service_).stop();
            ((AbstractAction) actions_.get(ACTION_START)).setEnabled(true);
            ((AbstractAction) actions_.get(ACTION_STOP)).setEnabled(false);
            ((AbstractAction) actions_.get(ACTION_SUSPEND)).setEnabled(false);
            ((AbstractAction) actions_.get(ACTION_RESUME)).setEnabled(false);
        }
    }

    //--------------------------------------------------------------------------
    // PauseAction
    //--------------------------------------------------------------------------

    /**
     * SuspendAction suspends the attached service and sets the state of the 
     * buttons according to the next available set of state transitions.
     */
    class SuspendAction extends SmartAction
    {
        /**
         * Creates a SuspendAction.
         */
        public SuspendAction()
        {
            super("Suspend", true, false, null);
        }

        
        /**
         * @see toolbox.util.ui.SmartAction#runAction(
         *      java.awt.event.ActionEvent)
         */
        public void runAction(ActionEvent e) throws Exception
        {
            ((Suspendable) service_).suspend();
            ((AbstractAction) actions_.get(ACTION_START)).setEnabled(false);
            ((AbstractAction) actions_.get(ACTION_STOP)).setEnabled(false);
            ((AbstractAction) actions_.get(ACTION_SUSPEND)).setEnabled(false);
            ((AbstractAction) actions_.get(ACTION_RESUME)).setEnabled(true);
        }
    }

    //--------------------------------------------------------------------------
    // ResumeAction
    //--------------------------------------------------------------------------

    /**
     * ResumeAction
     */
    class ResumeAction extends SmartAction
    {
        /**
         * Creates a ResumeAction.
         */
        public ResumeAction()
        {
            super("Resume", true, false, null);
        }

        
        /**
         * @see toolbox.util.ui.SmartAction#runAction(
         *      java.awt.event.ActionEvent)
         */
        public void runAction(ActionEvent e) throws Exception
        {
            ((Suspendable) service_).resume();
            ((AbstractAction) actions_.get(ACTION_START)).setEnabled(false);
            ((AbstractAction) actions_.get(ACTION_STOP)).setEnabled(true);
            ((AbstractAction) actions_.get(ACTION_SUSPEND)).setEnabled(true);
            ((AbstractAction) actions_.get(ACTION_RESUME)).setEnabled(false);
        }
    }
}