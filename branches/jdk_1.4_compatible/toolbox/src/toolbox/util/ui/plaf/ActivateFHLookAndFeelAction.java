package toolbox.util.ui.plaf;

import com.shfarr.ui.plaf.fh.theme.ThemeManager;

/**
 * Action that activates FH Look and Feel and its available themes.
 */    
public class ActivateFHLookAndFeelAction extends ActivateLookAndFeelAction
{
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates an ActivateFHLookAndFeelAction.
     */
    public ActivateFHLookAndFeelAction()
    {
    }
    
    //--------------------------------------------------------------------------
    // Overrides ActivateLookAndFeelAction
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.util.ui.plaf.ActivateLookAndFeelAction#setLookAndFeelInfo(
     *      toolbox.util.ui.plaf.LAFInfo)
     */
    public void setLookAndFeelInfo(LAFInfo info)
    {
        super.setLookAndFeelInfo(info);
    }
    
    
    /**
     * @see toolbox.util.ui.plaf.ActivateLookAndFeelAction#activate()
     */
    public void activate() throws Exception
    {
        LAFInfo info = getLookAndFeelInfo();
        String themeName  = info.getProperty("theme.name");
        ThemeManager.instance().getPreferences().setCurrentTheme(themeName);
        super.activate();
    }
}