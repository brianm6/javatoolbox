package toolbox.util.ui.plaf;

import java.awt.Frame;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.jgoodies.plaf.plastic.PlasticLookAndFeel;
import com.jgoodies.plaf.plastic.PlasticTheme;

import nu.xom.Builder;
import nu.xom.Element;
import nu.xom.Elements;

import org.apache.log4j.Logger;

import toolbox.util.ArrayUtil;
import toolbox.util.ResourceUtil;
import toolbox.util.StreamUtil;
import toolbox.util.StringUtil;
import toolbox.util.XOMUtil;
import toolbox.util.ui.JButtonGroup;
import toolbox.util.ui.JSmartCheckBoxMenuItem;
import toolbox.util.ui.JSmartMenu;
import toolbox.util.ui.JSmartMenuItem;

/**
 * LookAndFeelUtil manages the available Swing Look & Feels and provides
 * convenience methods to generate menus and switch between them.
 */
public class LookAndFeelUtil
{
    // TODO: Added themes for Tiny Look and Feel
    // TODO: Fix frame decoration for LAFs that don't support it
    // TODO: Gracefully fail when toolbox-lookandfeel.jar is not on the classpath
       
    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------
    
    private static final Logger logger_ =
        Logger.getLogger(LookAndFeelUtil.class);

    // XML nodes & attributes
    public static final String NODE_LOOKANDFEEL = "LookAndFeel";
    public static final String   ATTR_NAME      = "name";
    public static final String   ATTR_CLASS     = "class";
    public static final String   ATTR_ACTION    = "action";
    public static final String   NODE_PROPERTY  = "Property";
    public static final String     ATTR_VALUE   = "value";

    /**
     * XML configuration file containing the list of look and feels supported
     * by this application. As new look and feels are created, they simply 
     * need to be added to this file to show up on the generated look and feel
     * menu.
     */
    public static final String FILE_LOOKANDFEEL_CONFIG = "lookandfeel.xml";
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * Maps look and feel classname to its corresponding menu item.
     */
    private static Map menuItemMap_;

    /**
     * Maps look and feel name to LAFInfo.
     */
    private static Map lookAndFeelMap_;
    
    private static List lookAndFeels_;

    private static JButtonGroup group_;
    
    //--------------------------------------------------------------------------
    // StaticO
    //--------------------------------------------------------------------------
    
    static
    {
        lookAndFeelMap_ = new HashMap();
        menuItemMap_ = new HashMap();
        lookAndFeels_ = new ArrayList();
        
        initLookAndFeels();
        
//        UIManager.installLookAndFeel("Metouia", 
//                "net.sourceforge.mlf.metouia.MetouiaLookAndFeel");
//        
//        UIManager.installLookAndFeel("Kunststoff",    
//                "com.incors.plaf.kunststoff.KunststoffLookAndFeel");
//        
//        UIManager.installLookAndFeel("JGoodies Plastic",
//                "com.jgoodies.plaf.plastic.PlasticLookAndFeel");
//        
//        UIManager.installLookAndFeel("JGoodies Plastic 3D",
//                "com.jgoodies.plaf.plastic.Plastic3DLookAndFeel");
//
//        UIManager.installLookAndFeel("JGoodies Plastic XP",
//                "com.jgoodies.plaf.plastic.PlasticXPLookAndFeel");
//        
//        UIManager.installLookAndFeel("XPLookAndFeel", 
//                "com.stefankrause.xplookandfeel.XPLookAndFeel");
//        
//        UIManager.installLookAndFeel("TinyLookAndFeel", 
//                "de.muntjak.tinylookandfeel.TinyLookAndFeel");
//        
//        UIManager.installLookAndFeel("Tonic L&F", 
//                "com.digitprop.tonic.TonicLookAndFeel");
//
//        UIManager.installLookAndFeel("Liquid L&F", 
//                "com.birosoft.liquid.LiquidLookAndFeel");
//
//        UIManager.installLookAndFeel(
//                "Cell Shaded", 
//                "com.l2fprod.gui.plaf.skin.SkinLookAndFeel");
//        
//        UIManager.installLookAndFeel(
//                "Mac", 
//                "com.l2fprod.gui.plaf.skin.SkinLookAndFeel");
//        
//        UIManager.installLookAndFeel(
//                "XP", 
//                "com.l2fprod.gui.plaf.skin.SkinLookAndFeel");
//        
//        UIManager.installLookAndFeel(
//                "Modern", 
//                "com.l2fprod.gui.plaf.skin.SkinLookAndFeel");
//        
//        UIManager.installLookAndFeel(
//                "Whistler", 
//                "com.l2fprod.gui.plaf.skin.SkinLookAndFeel");
//        
//        UIManager.installLookAndFeel(
//                "Aqua", 
//                "com.l2fprod.gui.plaf.skin.SkinLookAndFeel");
//        
//        UIManager.installLookAndFeel(
//                "Beos", 
//                "com.l2fprod.gui.plaf.skin.SkinLookAndFeel");
//        
//        UIManager.installLookAndFeel(
//                "BBJ", 
//                "com.l2fprod.gui.plaf.skin.SkinLookAndFeel");
//        
//        UIManager.installLookAndFeel(
//                "Toxic", 
//                "com.l2fprod.gui.plaf.skin.SkinLookAndFeel");
//        
//        UIManager.installLookAndFeel(
//                "Novaworx", 
//                "novaworx.plaf.novaworx.NovaworxLookAndFeel");
        
        //UIManager.installLookAndFeel("FH Look & Feel",
        //    "com.shfarr.ui.plaf.fh.FhLookAndFeel");
        
        //UIManager.installLookAndFeel("GTK+",
        //    "com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
        
        //UIManager.installLookAndFeel("Oyoaha",    
        //    "com.oyoaha.swing.plaf.oyoaha.OyoahaLookAndFeel");


        //SlafLookAndFeel.setCurrentTheme("Edbgruppen");
        
        //UIManager.installLookAndFeel("Slaf",    
        //    "com.memoire.slaf.SlafLookAndFeel");                        
    }
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a LookAndFeelUtil.
     */
    private LookAndFeelUtil()
    {
        
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Selects the given look and feel on the look and feel menu.
     */ 
    public static void selectOnMenu(LAFInfo info)
    {
        JCheckBoxMenuItem cbmi = (JCheckBoxMenuItem) menuItemMap_.get(info); 
        
        if (cbmi != null)
        {    
            cbmi.setSelected(true);
            
            logger_.debug(StringUtil.addBars(
                "Successlly selected laf\n" + info));
         }
         else
         {    
             StringBuffer sb = new StringBuffer();
             sb.append("Could not find lafinfo in menu map.\n" + info + "\n");
             sb.append(ArrayUtil.toString(menuItemMap_.keySet().toArray(), true));
             logger_.debug(StringUtil.addBars(sb.toString()));
         }
    }


    /**
     * Sets the look and feel given a DOM.
     * 
     * @param prefs DOM to read preferences from
     */
    public static void setLookAndFeel(Element prefs) throws Exception
    {
        Element lafNode = XOMUtil.getFirstChildElement(
            prefs, NODE_LOOKANDFEEL, null);

        if (lafNode == null)
            logger_.info("Look and Feel not set. DOM is null");
        else
            setLookAndFeel(new LAFInfo(lafNode));
    }

    
    /**
     * @param info
     * @throws Exception
     */
    public static void setLookAndFeel(LAFInfo info) throws Exception
    {
        LookAndFeelActivator activator = (LookAndFeelActivator) 
            Class.forName(info.getAction()).newInstance();
        
        activator.setLookAndFeelInfo(info);
        activator.activate();
        selectOnMenu(info);
    }
    
    
    /**
     * Creates the look and feel menu by querying the UIManager for all 
     * installed look and feels.
     * 
     * @return Menu with all look and feels installed.
     */
    public static JMenu createLookAndFeelMenu()
    {
        JMenu menu = new JSmartMenu("Look and Feel");
        menu.setMnemonic('L');
        group_ = new JButtonGroup();
        
        for (Iterator i = lookAndFeels_.iterator(); i.hasNext();)
        {   
            try
            {
                LAFInfo info = (LAFInfo) i.next();
                
                logger_.debug(
                    "LAF[" + StringUtil.left(i+"",2) + "]: " + 
                    StringUtil.left(info.getName(), 20) + " "  + 
                    info.getClassName());
                
                String actionClassName = info.getAction();
                Class actionClass = Class.forName(actionClassName);
                
                LookAndFeelActivator activator = 
                    (LookAndFeelActivator) actionClass.newInstance();
                
                activator.setLookAndFeelInfo(info);

                JCheckBoxMenuItem cb = 
                    new JSmartCheckBoxMenuItem((Action) activator);

                group_.add(cb);
                menu.add(cb);
                menuItemMap_.put(info, cb);
            }
            catch (Exception e)
            {
                logger_.error(e);
            }
        }
        
        //
        // Select the active look and feel if it is available on the menu.
        //
        
        LAFInfo current = (LAFInfo) 
            UIManager.getLookAndFeel().getDefaults().get(
                LAFInfo.PROP_HIDDEN_KEY);

        if (current != null)
            selectOnMenu(current);
        else
            logger_.debug(StringUtil.addBars(
                "Hidden key not found for LAF " + UIManager.getLookAndFeel()));
        
        return menu;
    }


    /**
     * Creates a themes menu for the plastic jgoodies.com look and feels.
     * 
     * @return JMenu 
     */
    protected static JMenu createThemesMenu()
    {
        JMenu menu = new JSmartMenu("Themes");
        List themes = PlasticLookAndFeel.getInstalledThemes();
        
        for (int i=0, n=themes.size(); i<n; i++)
            menu.add(new JSmartMenuItem(
                new ActivatePlasticLookAndFeelAction((PlasticTheme) themes.get(i))));
        
        return menu;
    }


    /**
     * Saves currently active look and feel to a node in the given element.
     *
     * @param workspace Node in which to save look and feel info.
     */
    public static void savePrefs(Element workspace) throws Exception
    {
        AbstractButton button = group_.getSelected();
        
        logger_.info("Selected button: " + button);
        
        Action action = button.getAction();
        
        logger_.info("Selected action: " + action);
        
        LookAndFeelActivator activator = 
            (LookAndFeelActivator) group_.getSelected().getAction();
        
        LAFInfo info = activator.getLookAndFeelInfo();
        info.savePrefs(workspace);
    }

    
    /**
     * Propagates the change in LookAndFeel selection to all known windows.
     * Appropriate to be called immediately after UIManager.setLookAndFeel().
     */
    public static void propagateChangeInLAF()
    {
        Frame[] frames = Frame.getFrames();
        
        for (int i=0; i<frames.length; i++)
            SwingUtilities.updateComponentTreeUI(frames[i]);                    
    }

    
    /**
     * Sets the Look and Feel to Metal.
     * 
     * @throws Exception on error
     */    
    public static void setMetalLAF() throws Exception
    {
        UIManager.setLookAndFeel(
            "javax.swing.plaf.metal.MetalLookAndFeel");
            
        propagateChangeInLAF();
    }

    
    /**
     * Sets the Look and Feel to Motif/CDE.
     * 
     * @throws Exception on error
     */   
    public static void setMotifLAF() throws Exception
    {
        UIManager.setLookAndFeel(
            "com.sun.java.swing.plaf.motif.MotifLookAndFeel");
            
        propagateChangeInLAF();            
    }

    
    /**
     * Sets the Look and Feel to Metouia.
     * 
     * @throws Exception on error
     */
    public static void setMetouiaLAF() throws Exception
    { 
        UIManager.setLookAndFeel(
            "net.sourceforge.mlf.metouia.MetouiaLookAndFeel");
            
        propagateChangeInLAF();            
    }

    /**
     * Sets the Skin LAF.
     * 
     * @throws Exception on error
     */
    public static void setSkinLAF() throws Exception
    { 
        UIManager.setLookAndFeel("com.l2fprod.gui.plaf.skin.SkinLookAndFeel");
        propagateChangeInLAF();
    }

    
    /**
     * Sets the preferred Look and Feel.
     * 
     * @throws Exception on error
     */
    public static void setPreferredLAF() throws Exception
    { 
        UIManager.setLookAndFeel(
            "com.jgoodies.plaf.plastic.PlasticXPLookAndFeel");
        
        propagateChangeInLAF();            
    }

    
    /**
     * Sets the Look and Feel to Windows.
     * 
     * @throws Exception on error
     */
    public static void setWindowsLAF() throws Exception
    {
        UIManager.setLookAndFeel(
            "com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            
        propagateChangeInLAF();            
    }
    
    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------
    
    public static void initLookAndFeels() 
    {
        InputStream is = null;
        
        try
        {
            is = ResourceUtil.getResource(FILE_LOOKANDFEEL_CONFIG);
            Element root = new Builder().build(is).getRootElement();
            Elements lafs = root.getChildElements(NODE_LOOKANDFEEL);
            
            for (int i=0; i<lafs.size(); i++)
            {    
                LAFInfo info = new LAFInfo(lafs.get(i));

                logger_.debug("Loaded " + info.getName());
                
                // Only install a look and feel once even if it occurs 
                // multiple times in the configuration.
                if (!lookAndFeelMap_.containsKey(info.getName()))
                {    
                    UIManager.installLookAndFeel(
                        info.getName(), 
                        info.getClassName());
                }
                
                lookAndFeelMap_.put(info.getName(), info);
                lookAndFeels_.add(info);
            }
        }
        catch (Exception ioe)
        {
            logger_.error(ioe);
        }
        finally
        {
            StreamUtil.close(is);
        }
    }
}