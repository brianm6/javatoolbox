package toolbox.util.service;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import javax.swing.JPanel;

import toolbox.util.ui.JSmartButton;
import toolbox.util.ui.SmartAction;

/**
 * ServiceView is basically a component that contains a bunch of buttons 
 * tied to the lifecycle methods on a Service. 
 */
public class ServiceView extends JPanel
{
    /**
     * Service attached to the view.
     */
    private Service service_;

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
        service_ = service;
        buildView();
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
        add(new JSmartButton(new StartAction()));
        add(new JSmartButton(new PauseAction()));
        add(new JSmartButton(new ResumeAction()));
        add(new JSmartButton(new StopAction()));
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
        }
    }
}