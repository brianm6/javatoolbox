package toolbox.util.ui.plaf;

import javax.swing.UIManager.LookAndFeelInfo;

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
    
    /**
     * Maps the Look And Feel name to the appropriate SkinLF themepack.
     */
//    private static Map themeMap_;
    
//    static
//    {
//        themeMap_ = new HashMap();
//        themeMap_.put("Aqua", "skinlf/aqua.zip");
//        themeMap_.put("BBJ", "skinlf/bbj.zip");
//        themeMap_.put("Beos", "skinlf/beos.zip");
//        themeMap_.put("Cell Shaded", "skinlf/cellshaded.zip");
//        themeMap_.put("Mac", "skinlf/macos.zip");
//        themeMap_.put("Modern", "skinlf/modern.zip");
//        themeMap_.put("Toxic", "skinlf/toxic.zip");
//        themeMap_.put("Whistler", "skinlf/whistler.zip");
//        themeMap_.put("XP", "skinlf/xpluna.zip");
//        themeMap_.put("SkinLF", "skinlf/toxic.zip");
//    }

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a ActivateSkinLookAndFeelAction.
     *
     * @param info
     */
    public ActivateSkinLookAndFeelAction()
    {
    }
    
    /**
     * Creates a ActivateSkinLookAndFeelAction.
     *
     * @param info
     */
    public ActivateSkinLookAndFeelAction(LookAndFeelInfo info)
    {
        //super(info);
    }

    //--------------------------------------------------------------------------
    // Overrides ActivateLookAndFeelAction
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.ui.plaf.ActivateLookAndFeelAction#setLookAndFeelInfo(toolbox.util.ui.plaf.LAFInfo)
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