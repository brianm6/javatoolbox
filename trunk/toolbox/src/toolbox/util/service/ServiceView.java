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
 * ServiceView is basically a component that contains a bunch of buttons 
 * tied to the lifecycle methods on a Service. 
 */
public class ServiceView extends JPanel
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Service attached to this view.
     */
    private Service service_;

    /**
     * Maps action name to action.
     */
    private Map actions_;
    
    
    private ServiceListener myServiceListener_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a ServiceView.
     * 
     * @param service Service to attached to this view.
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
        if (service_ != null)
            service_.removeServiceListener(myServiceListener_);
        
        service_ = service;
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
        actions_.put("start", new StartAction());
        actions_.put("pause", new PauseAction());
        actions_.put("resume", new ResumeAction());
        actions_.put("stop", new StopAction());
        add(new JSmartButton((Action) actions_.get("start")));
        add(new JSmartButton((Action) actions_.get("pause")));
        add(new JSmartButton((Action) actions_.get("resume")));
        add(new JSmartButton((Action) actions_.get("stop")));
    }
    
    
    class MyServiceListener implements ServiceListener {
        
        /**
         * @see toolbox.util.service.ServiceListener#serviceChanged(toolbox.util.service.Service)
         */
        public void serviceChanged(Service service) throws ServiceException
        {
            ServiceState state = service.getState();
            
            if (state == ServiceState.INITIALIZED)
            {
                ((AbstractAction) actions_.get("start")).setEnabled(true);
                ((AbstractAction) actions_.get("stop")).setEnabled(false);
                ((AbstractAction) actions_.get("pause")).setEnabled(false);
                ((AbstractAction) actions_.get("resume")).setEnabled(false);
            }
            
        }
    }
    
    //--------------------------------------------------------------------------
    // StartAction
    //--------------------------------------------------------------------------

    /**
     * StartAction
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
            service_.start();
            ((AbstractAction) actions_.get("start")).setEnabled(false);
            ((AbstractAction) actions_.get("stop")).setEnabled(true);
            ((AbstractAction) actions_.get("pause")).setEnabled(true);
            ((AbstractAction) actions_.get("resume")).setEnabled(false);
        }
    }

    //--------------------------------------------------------------------------
    // StopAction
    //--------------------------------------------------------------------------

    /**
     * StopAction
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
            service_.stop();
            ((AbstractAction) actions_.get("start")).setEnabled(true);
            ((AbstractAction) actions_.get("stop")).setEnabled(false);
            ((AbstractAction) actions_.get("pause")).setEnabled(false);
            ((AbstractAction) actions_.get("resume")).setEnabled(false);
        }
    }

    //--------------------------------------------------------------------------
    // PauseAction
    //--------------------------------------------------------------------------

    /**
     * PauseAction
     */
    class PauseAction extends SmartAction
    {
        /**
         * Creates a PauseAction.
         * 
         */
        public PauseAction()
        {
            super("Pause", true, false, null);
        }

        
        /**
         * @see toolbox.util.ui.SmartAction#runAction(
         *      java.awt.event.ActionEvent)
         */
        public void runAction(ActionEvent e) throws Exception
        {
            service_.pause();
            ((AbstractAction) actions_.get("start")).setEnabled(false);
            ((AbstractAction) actions_.get("stop")).setEnabled(false);
            ((AbstractAction) actions_.get("pause")).setEnabled(false);
            ((AbstractAction) actions_.get("resume")).setEnabled(true);
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
            service_.resume();
            ((AbstractAction) actions_.get("start")).setEnabled(false);
            ((AbstractAction) actions_.get("stop")).setEnabled(true);
            ((AbstractAction) actions_.get("pause")).setEnabled(true);
            ((AbstractAction) actions_.get("resume")).setEnabled(false);
        }
    }
}