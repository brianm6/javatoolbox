package toolbox.util.ui.plaf;

import java.awt.Color;
import java.awt.Font;

import javax.swing.UIManager;
import javax.swing.border.LineBorder;

import com.incors.plaf.alloy.AlloyLookAndFeel;
import com.incors.plaf.alloy.AlloyTheme;
import com.incors.plaf.alloy.DefaultAlloyTheme;

import org.apache.log4j.Logger;

import toolbox.util.StringUtil;

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
     */
    public ActivateAlloyLookAndFeelAction()
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
        String clazz = info.getProperty("theme.class");
        String license = info.getProperty("theme.license");
      
        AlloyLookAndFeel.setProperty("alloy.licenseCode", license);
        AlloyTheme defaultTheme = new DefaultAlloyTheme();
        AlloyTheme theme = defaultTheme;
        
        if (!StringUtil.isNullOrEmpty(clazz))
        {
            try
            {
                theme = (AlloyTheme) Class.forName(clazz).newInstance();
            }
            catch (Exception e)
            {
                logger_.warn("Error setting Alloy theme: " + info, e);
                theme = defaultTheme;
            }
        }
        
        AlloyLookAndFeel alloy = new AlloyLookAndFeel(theme);
        UIManager.setLookAndFeel(alloy);

        // Fix alloy fonts
        Font f = new Font("Tahoma", Font.PLAIN, 11);
        //Font f = FontUtil.getPreferredSerifFont();
        UIManager.put("Label.font", f);
        UIManager.put("Button.font", f);
        UIManager.put("ComboBox.font", f);
        UIManager.put("CheckBox.font", f);
        UIManager.put("ToolTip.border", new LineBorder(Color.black));
        
        UIManager.put(
            "ToolTip.background", UIManager.getColor("Panel.background"));
        
        UIManager.getLookAndFeel().getDefaults().put(
            LAFInfo.PROP_HIDDEN_KEY, getLookAndFeelInfo());
        
        LookAndFeelUtil.propagateChangeInLAF();
    }
}