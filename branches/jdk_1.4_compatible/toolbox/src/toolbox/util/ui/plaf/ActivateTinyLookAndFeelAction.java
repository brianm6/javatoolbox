package toolbox.util.ui.plaf;

import java.io.File;

import de.muntjak.tinylookandfeel.Theme;

import org.apache.log4j.Logger;

import toolbox.util.ResourceUtil;

/**
 * Action that activates the Tiny Look and Feel and its varios themes.
 */    
public class ActivateTinyLookAndFeelAction extends ActivateLookAndFeelAction
{
    private static final Logger logger_ =
        Logger.getLogger(ActivateTinyLookAndFeelAction.class);
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates an ActivateTinyLookAndFeelAction.
     */
    public ActivateTinyLookAndFeelAction()
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
        setName(info.getProperty("theme.name"));
    }
    
    
    /**
     * @see toolbox.util.ui.plaf.ActivateLookAndFeelAction#activate()
     */
    public void activate() throws Exception
    {
        //super.activate();
        
        LAFInfo info = getLookAndFeelInfo();
        String  themeResource = info.getProperty("theme.file");
        File    themeFile = ResourceUtil.getResourceAsTempFile(themeResource);
        int     themeStyle = Integer.parseInt(info.getProperty("theme.style"));
        boolean b = Theme.loadTheme(themeFile, 2);
        
        logger_.debug("Tiny theme loaded: " + b);
        logger_.debug(info.toString());
        super.activate();
    }
}