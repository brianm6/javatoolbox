package toolbox.workspace.lookandfeel;

import java.awt.event.ActionEvent;

import javax.swing.UIManager;

import org.apache.log4j.Logger;

import toolbox.util.SwingUtil;
import toolbox.workspace.WorkspaceAction;

/**
 * Action that activates a given {@LookAndFeel}
 */    
public class ActivateLookAndFeelAction extends WorkspaceAction
{
    private static final Logger logger_ =
        Logger.getLogger(ActivateLookAndFeelAction.class);
    
    /**
     * The look and feel to activate
     */    
    private UIManager.LookAndFeelInfo info_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates an ActivateLookAndFeelAction
     * 
     * @param lookAndFeel LookAndFeelInfo of look and feel to activate
     */
    ActivateLookAndFeelAction(UIManager.LookAndFeelInfo info)
    {
        super(info.getName(), false, null, null);
        info_ = info;
    }

    //--------------------------------------------------------------------------
    // SmartAction Interface
    //--------------------------------------------------------------------------
        
    /**
     * @see toolbox.util.ui.SmartAction#runAction(java.awt.event.ActionEvent)
     */
    public void runAction(ActionEvent e) throws Exception
    {
        logger_.debug("Activating " + info_.getClassName());
        LookAndFeelManager.setLookAndFeel(info_.getClassName());
        SwingUtil.propagateChangeInLAF();
    }
}