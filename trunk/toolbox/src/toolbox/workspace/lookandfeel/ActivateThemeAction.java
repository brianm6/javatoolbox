package toolbox.workspace.lookandfeel;

import java.awt.event.ActionEvent;

import javax.swing.UIManager;

import com.jgoodies.plaf.plastic.PlasticLookAndFeel;
import com.jgoodies.plaf.plastic.PlasticTheme;

import toolbox.util.SwingUtil;
import toolbox.workspace.WorkspaceAction;

/**
 * Action that sets the plastic theme.
 */    
public class ActivateThemeAction extends WorkspaceAction
{
    /** 
     * The plastic theme to activate.
     */
    private PlasticTheme theme_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates an ActivateThemeAction.
     * 
     * @param theme Theme to activate.
     */
    public ActivateThemeAction(PlasticTheme theme)
    {
        super(theme.getName(), false, null, null);
        theme_ = theme;
    }

    //--------------------------------------------------------------------------
    // SmartAction Implementation
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.ui.SmartAction#runAction(java.awt.event.ActionEvent)
     */
    public void runAction(ActionEvent e) throws Exception
    {
        PlasticLookAndFeel.setMyCurrentTheme(theme_);
        UIManager.setLookAndFeel(UIManager.getLookAndFeel());
        SwingUtil.propagateChangeInLAF();
    }
}