package toolbox.util.ui.plaf;

import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import com.incors.plaf.alloy.AlloyLookAndFeel;
import com.incors.plaf.alloy.AlloyTheme;
import com.incors.plaf.alloy.themes.acid.AcidTheme;
import com.incors.plaf.alloy.themes.bedouin.BedouinTheme;
import com.incors.plaf.alloy.themes.glass.GlassTheme;

import org.apache.log4j.Logger;

import toolbox.util.ResourceUtil;

/**
 * Action that activates the Alloy Look And Feel.
 */
public class ActivateAlloyLookAndFeelAction extends ActivateLookAndFeelAction
{
    private static final Logger logger_ =
        Logger.getLogger(ActivateAlloyLookAndFeelAction.class);

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a ActivateAlloyLookAndFeelAction.
     *
     * @param info
     */
    public ActivateAlloyLookAndFeelAction()
    {
        // for newInstance()
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

        
        //com.incors.plaf.alloy.AlloyTheme theme = new com.incors.plaf.alloy.themes.bedouin.BedouinTheme();  // The lines below can be used for setting the other themes.  // com.incors.plaf.alloy.AlloyTheme theme = new com.incors.plaf.alloy.themes.glass.GlassTheme();  // com.incors.plaf.alloy.AlloyTheme theme = new com.incors.plaf.alloy.themes.acid.AcidTheme();  javax.swing.LookAndFeel alloyLnF = new com.incors.plaf.alloy.AlloyLookAndFeel(theme);
        com.incors.plaf.alloy.AlloyTheme themex = new com.incors.plaf.alloy.DefaultAlloyTheme();
        
        LAFInfo info = getLookAndFeelInfo();
        String name = info.getProperty("theme.name");
        String license = info.getProperty("theme.license");

        
        //"2#Horst_Heistermann#1w2sca#5qzosw");
        
        AlloyLookAndFeel.setProperty("alloy.licenseCode", license); 
        //AlloyTheme theme = new GlassTheme();        
        //AlloyTheme theme = new BedouinTheme();
        AlloyTheme theme = new AcidTheme();
        AlloyLookAndFeel alloy = new AlloyLookAndFeel(theme);

        UIManager.setLookAndFeel(alloy);
        
        UIManager.getLookAndFeel().getDefaults().put(
                LAFInfo.PROP_HIDDEN_KEY, getLookAndFeelInfo());
        
        LookAndFeelUtil.propagateChangeInLAF();
    }
}