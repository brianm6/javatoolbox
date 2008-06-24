package toolbox.util.ui.plaf;

import com.l2fprod.gui.plaf.skin.Skin;
import com.l2fprod.gui.plaf.skin.SkinLookAndFeel;

import org.apache.log4j.Logger;

import toolbox.util.ResourceUtil;

/**
 * Action that activates a Skin Look And Feel with a known theme pack.
 */
public class ActivateSkinLookAndFeelAction extends ActivateLookAndFeelAction
{
    private static final Logger logger_ =
        Logger.getLogger(ActivateSkinLookAndFeelAction.class);
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a ActivateSkinLookAndFeelAction.
     */
    public ActivateSkinLookAndFeelAction()
    {
    }

    //--------------------------------------------------------------------------
    // Overrides ActivateLookAndFeelAction
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.ui.plaf.ActivateLookAndFeelAction#setLookAndFeelInfo(
     *      toolbox.util.ui.plaf.LAFInfo)
     */
    public void setLookAndFeelInfo(LAFInfo lookAndFeelInfo)
    {
        super.setLookAndFeelInfo(lookAndFeelInfo);
        setName(lookAndFeelInfo.getProperty("theme.name"));
    }
    
    
    /**
     * @see toolbox.util.ui.plaf.ActivateLookAndFeelAction#activate()
     */
    public void activate() throws Exception
    {
        LAFInfo info = getLookAndFeelInfo();
        String name = info.getProperty("theme.name");
        String path = info.getProperty("theme.path");
        
        logger_.debug("Activating " + info.getName() + " " + name + " " + path);
        
        Skin skin = 
            SkinLookAndFeel.loadThemePack(ResourceUtil.getResource(path));
        
        SkinLookAndFeel.setSkin(skin);
        super.activate();
    }
}