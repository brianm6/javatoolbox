package toolbox.plugin.netmeter;

import java.awt.event.ActionEvent;

import javax.swing.JPanel;

import toolbox.util.ui.JSmartButton;
import toolbox.util.ui.SmartAction;
import toolbox.util.ui.layout.GridLayoutPlus;

/**
 * ServiceView
 */
public class ServiceView extends JPanel
{
    private Service service_;
    
    public ServiceView(Service service)
    {
        service_ = service;
        buildView();
    }
    
    protected void buildView()
    {
        setLayout(new GridLayoutPlus(1,4));
        add(new JSmartButton(new StartAction()));
        add(new JSmartButton(new PauseAction()));
        add(new JSmartButton(new ResumeAction()));
        add(new JSmartButton(new StopAction()));
    }
    
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
    
    
    interface ServiceListener
    {
        public void serviceStarted(Service service) throws ServiceException;
        public void serviceStopped(Service service) throws ServiceException;
        public void servicePaused(Service service) throws ServiceException;
        public void serviceResumed(Service service) throws ServiceException;
    }
}
