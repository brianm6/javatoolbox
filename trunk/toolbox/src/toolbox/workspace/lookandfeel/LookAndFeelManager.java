package toolbox.workspace.lookandfeel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.UIManager;

import com.jgoodies.plaf.plastic.PlasticLookAndFeel;
import com.jgoodies.plaf.plastic.PlasticTheme;

import nu.xom.Attribute;
import nu.xom.Element;

import org.apache.log4j.Logger;

import toolbox.util.StringUtil;
import toolbox.util.SwingUtil;
import toolbox.util.XOMUtil;
import toolbox.util.ui.JSmartCheckBoxMenuItem;
import toolbox.util.ui.JSmartMenu;
import toolbox.util.ui.JSmartMenuItem;

/**
 * LookAndFeelManager manages the available Swing Look & Feels and provides
 * convenience methods to generate menus and switch between them.
 */
public class LookAndFeelManager
{
    // TODO: Added themes for Tiny Look and Feel
    // TODO: Fix resource loading for Skin LFs
    // TODO: Fix frame decoration for LAFs that don't support it
    // TODO: Gracefully fail when toolbox-lookandfeel.jar is not on the classpath
       
    private static final Logger logger_ =
        Logger.getLogger(LookAndFeelManager.class);

    // XML nodes & attributes
    public static final String NODE_LOOKANDFEEL    = "LookAndFeel";
    public static final String   ATTR_CLASS        = "class";
    public static final String   NODE_PROPERTY     = "Property";
    public static final String     ATTR_NAME       = "name";
    public static final String     ATTR_VALUE      = "value";

    static
    {
        UIManager.installLookAndFeel(
            "SkinLF - Cell Shaded", 
            "toolbox.workspace.lookandfeel.skinlf.CellShadedLookAndFeel");
            
        UIManager.installLookAndFeel(
            "SkinLF - Mac", 
            "toolbox.workspace.lookandfeel.skinlf.MacLookAndFeel");
            
        UIManager.installLookAndFeel(
            "SkinLF - XP", 
            "toolbox.workspace.lookandfeel.skinlf.XPLookAndFeel");
            
        UIManager.installLookAndFeel(
            "SkinLF - Modern", 
            "toolbox.workspace.lookandfeel.skinlf.ModernLookAndFeel");
            
        UIManager.installLookAndFeel(
            "SkinLF - Whistler", 
            "toolbox.workspace.lookandfeel.skinlf.WhistlerLookAndFeel");

        UIManager.installLookAndFeel(
            "SkinLF - Aqua", 
            "toolbox.workspace.lookandfeel.skinlf.AquaLookAndFeel");
            
        UIManager.installLookAndFeel(
            "SkinLF - Beos", 
            "toolbox.workspace.lookandfeel.skinlf.BeosLookAndFeel");
            
        UIManager.installLookAndFeel(
            "SkinLF - BBJ", 
            "toolbox.workspace.lookandfeel.skinlf.BBJLookAndFeel");
        
        UIManager.installLookAndFeel(
            "SkinLF - Toxic", 
            "toolbox.workspace.lookandfeel.skinlf.ToxicLookAndFeel");
    }

    /**
     * Maps look and feel classname to its corresponding menu item.
     */
    private Map menuItemMap_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a LookAndFeelManager.
     */
    public LookAndFeelManager()
    {
        menuItemMap_ = new HashMap();
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------

    /**
     * Replacement for UIManager.setLookAndFeel(). 
     *
     * @param className Classname of look and feel to set.
     * @throws LookAndFeelException usually on class instantiation error.
     */
    public static void setLookAndFeel(String className) 
        throws LookAndFeelException
    {
        try
        {
            UIManager.setLookAndFeel(className);
        }
        catch (Exception e)
        {
            throw new LookAndFeelException(e);
        }
    }    


    /**
     * Selects the currently active look and feel on the look and feel menu.
     */ 
    public void selectOnMenu()
    {
        String clazz = UIManager.getLookAndFeel().getClass().getName();
        JCheckBoxMenuItem cb = (JCheckBoxMenuItem) menuItemMap_.get(clazz);
        
        if (cb != null)
            cb.setSelected(true);
    }


    /**
     * Sets the look and feel given a DOM.
     * 
     * @param prefs DOM to read preferences from
     * @throws LookAndFeelException on error
     */
    public void setLookAndFeel(Element prefs) throws LookAndFeelException
    {
        Element lookAndFeelNode = XOMUtil.getFirstChildElement(
            prefs, 
            NODE_LOOKANDFEEL, 
            new Element(NODE_LOOKANDFEEL));

        String lookAndFeelClassName = XOMUtil.getStringAttribute(
            lookAndFeelNode, 
            ATTR_CLASS, 
            null /*UIManager.getCrossPlatformLookAndFeelClassName()*/);

        if (!StringUtil.isNullOrEmpty(lookAndFeelClassName))
        {
	        logger_.debug(
	            "LookAndFeel class read from prefs " + lookAndFeelClassName);
	
	        setLookAndFeel(lookAndFeelClassName);
	        SwingUtil.propagateChangeInLAF();
        }
        else
        {   
			logger_.warn("Skipping setting of look and feel");
        }
            
    }


    /**
     * Creates the look and feel menu by querying the UIManager for all 
     * installed look and feels.
     * 
     * @return Menu with all look and feels installed.
     */
    public JMenu createLookAndFeelMenu()
    {
        JMenu lookAndFeelMenu = new JSmartMenu("Look and Feel");
        lookAndFeelMenu.setMnemonic('L');
        ButtonGroup group = new ButtonGroup();
        UIManager.LookAndFeelInfo[] lookAndFeels = SwingUtil.getLAFs();
        
        for (int i=0; i<lookAndFeels.length; i++)
        {
            logger_.debug(
                "LAF[" + StringUtil.left(i+"",2) + "]: " + 
                StringUtil.left(lookAndFeels[i].getName(), 20) + " "  + 
                lookAndFeels[i].getClassName());

            JSmartCheckBoxMenuItem cb = new JSmartCheckBoxMenuItem(
                new ActivateLookAndFeelAction(lookAndFeels[i]));

            group.add(cb);
            lookAndFeelMenu.add(cb);
            menuItemMap_.put(lookAndFeels[i].getClassName(), cb);
        }
        
        lookAndFeelMenu.addSeparator();
        lookAndFeelMenu.add(createThemesMenu());
        
        
        // TODO: Activate the currently selected look and feel
        return lookAndFeelMenu;
    }


    /**
     * Creates a themes menu for the plastic jgoodies.com look and feels.
     * 
     * @return JMenu 
     */
    protected JMenu createThemesMenu()
    {
        JMenu menu = new JSmartMenu("Themes");
        List themes = PlasticLookAndFeel.getInstalledThemes();
        
        for (int i=0, n=themes.size(); i<n; i++)
            menu.add(new JSmartMenuItem(
                new ActivateThemeAction((PlasticTheme) themes.get(i))));
        
        return menu;
    }


    /**
     * Saves currently active look and feel to a node in the given
     * element.
     *
     * @param workspace Node in which to save look and feel info
     */
    public void savePrefs(Element workspace)
    {
        Element lafNode = new Element(NODE_LOOKANDFEEL);

        lafNode.addAttribute(new Attribute(ATTR_CLASS, 
            UIManager.getLookAndFeel().getClass().getName()));

        XOMUtil.insertOrReplace(workspace, lafNode);   
    }
}