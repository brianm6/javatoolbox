package toolbox.util.ui.plaf;

import java.awt.event.ActionEvent;

import javax.swing.UIManager;

import org.apache.log4j.Logger;

import toolbox.workspace.WorkspaceAction;

/**
 * Action that activates a given look and feel.
 */    
public class ActivateLookAndFeelAction extends WorkspaceAction 
    implements LookAndFeelActivator
{
    private static final Logger logger_ =
        Logger.getLogger(ActivateLookAndFeelAction.class);
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * The look and feel to activate.
     */    
    private LAFInfo info_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates an ActivateLookAndFeelAction.
     */
    ActivateLookAndFeelAction()
    {
        super("?? Set name ??", false, null, null);
    }

    //--------------------------------------------------------------------------
    // LookAndFeelActivator Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.ui.plaf.LookAndFeelActivator#setLookAndFeelInfo(
     *      toolbox.util.ui.plaf.LAFInfo)
     */
    public void setLookAndFeelInfo(LAFInfo lookAndFeelInfo)
    {
        info_ = lookAndFeelInfo;
        setName(info_.getName());
    }
    
    
    /**
     * @see toolbox.util.ui.plaf.LookAndFeelActivator#activate()
     */
    public void activate() throws Exception
    {
        
        logger_.debug("Activating " + info_.getClassName());
        UIManager.setLookAndFeel(info_.getClassName());
        
        UIManager.getLookAndFeel().getDefaults().put(
                LAFInfo.PROP_HIDDEN_KEY, getLookAndFeelInfo());
        
        //boolean decorate = 
        //    UIManager.getLookAndFeel().getSupportsWindowDecorations();
        //
        //JFrame.setDefaultLookAndFeelDecorated(decorate);// to decorate frames
        //JDialog.setDefaultLookAndFeelDecorated(decorate);//to decorate dialogs
         
        LookAndFeelUtil.propagateChangeInLAF();
    }
    
    //--------------------------------------------------------------------------
    // Abstract SmartAction Impl
    //--------------------------------------------------------------------------
        
    /**
     * @see toolbox.util.ui.SmartAction#runAction(java.awt.event.ActionEvent)
     */
    public void runAction(ActionEvent e) throws Exception
    {
        activate();
    }
    
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Returns the look and feel info.
     * 
     * @return LAFInfo
     */
    public LAFInfo getLookAndFeelInfo()
    {
        return info_;
    }
}