package toolbox.util.service;

import java.awt.event.ActionEvent;

import javax.swing.JPanel;

import toolbox.util.ui.JSmartButton;
import toolbox.util.ui.SmartAction;
import toolbox.util.ui.layout.GridLayoutPlus;

/**
 * ServiceView associated a UI component with a service so that it can be 
 * manipulated.
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
        setLayout(new GridLayoutPlus(1,4));
        add(new JSmartButton(new StartAction()));
        add(new JSmartButton(new PauseAction()));
        add(new JSmartButton(new ResumeAction()));
        add(new JSmartButton(new StopAction()));
    }
    
    //--------------------------------------------------------------------------
    // StartAction
    //--------------------------------------------------------------------------
    
    class StartAction extends SmartAction
    {
        public StartAction()
        {
            super("Start", true, false, null);
        }

        public void runAction(ActionEvent e) throws Exception
        {
            service_.start();
        }
    }
    
    //--------------------------------------------------------------------------
    // StopAction
    //--------------------------------------------------------------------------
    
    class StopAction extends SmartAction
    {
        public StopAction()
        {
            super("Stop", true, false, null);
        }

        public void runAction(ActionEvent e) throws Exception
        {
            service_.stop();
        }
    }
    
    //--------------------------------------------------------------------------
    // PauseAction
    //--------------------------------------------------------------------------
    
    class PauseAction extends SmartAction
    {
        public PauseAction()
        {
            super("Pause", true, false, null);
        }

        public void runAction(ActionEvent e) throws Exception
        {
            service_.pause();
        }
    }
    
    //--------------------------------------------------------------------------
    // ResumeAction
    //--------------------------------------------------------------------------
    
    class ResumeAction extends SmartAction
    {
        public ResumeAction()
        {
            super("Resume", true, false, null);
        }

        public void runAction(ActionEvent e) throws Exception
        {
            service_.resume();
        }
    }
}