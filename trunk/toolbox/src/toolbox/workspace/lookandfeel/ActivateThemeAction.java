package toolbox.workspace.lookandfeel;

import java.awt.event.ActionEvent;

import javax.swing.UIManager;

import com.jgoodies.plaf.plastic.PlasticLookAndFeel;
import com.jgoodies.plaf.plastic.PlasticTheme;

import toolbox.util.SwingUtil;
import toolbox.util.ui.plugin.WorkspaceAction;


/**
 * Action that sets the plastic theme
 */    
public class ActivateThemeAction extends WorkspaceAction
{
    private PlasticTheme theme_;
    
    public ActivateThemeAction(PlasticTheme theme)
    {
        super(theme.getName(), false, null, null);
        theme_ = theme;
    }

    public void runAction(ActionEvent e) throws Exception
    {
        PlasticLookAndFeel.setMyCurrentTheme(theme_);
        UIManager.setLookAndFeel(UIManager.getLookAndFeel());
        SwingUtil.propagateChangeInLAF();
    }
}