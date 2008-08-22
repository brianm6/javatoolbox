package toolbox.util.ui.plaf;

import java.awt.Frame;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import nu.xom.Builder;
import nu.xom.Element;
import nu.xom.Elements;

import org.apache.commons.collections.comparators.ReverseComparator;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import toolbox.util.ArrayUtil;
import toolbox.util.ResourceUtil;
import toolbox.util.StringUtil;
import toolbox.util.XOMUtil;
import toolbox.util.ui.JButtonGroup;
import toolbox.util.ui.JSmartCheckBoxMenuItem;
import toolbox.util.ui.JSmartDialog;
import toolbox.util.ui.JSmartMenu;

/**
 * LookAndFeelUtil manages the available Swing Look & Feels and provides
 * convenience methods to generate menus and switch between them.
 */
public final class LookAndFeelUtil
{
    // TODO: Fix frame decoration for LAFs that don't support it
    // TODO: Gracefully fail when toolbox-lookandfeel.jar is not on the claspath
       
    private static final Logger logger_ = 
        Logger.getLogger(LookAndFeelUtil.class);
    
    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------

    private static final String NODE_LOOKANDFEEL = "LookAndFeel";

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
     * Maps a LAFInfo to its corresponding JCheckBoxMenuItem.
     */
    private static Map menuItemMap_;

    /**
     * Maps look and feel name to LAFInfo.
     */
    private static Map lookAndFeelMap_;
    
    /**
     * List of all LAFInfos. 
     */
    private static List lookAndFeels_;

    /**
     * Checkbox group for the menu items.
     */
    private static JButtonGroup group_;

    /**
     * The set of look and feels provided with the JRE by default before we go
     * in and start polluting it.
     */
    private static UIManager.LookAndFeelInfo[] baseLAFs_;
    
    //--------------------------------------------------------------------------
    // Static Initializer
    //--------------------------------------------------------------------------
    
    static
    {
        lookAndFeelMap_ = new HashMap();
        menuItemMap_ = new HashMap();
        lookAndFeels_ = new ArrayList();
        baseLAFs_ = UIManager.getInstalledLookAndFeels();
        initLookAndFeels();
    }
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Prevent construction of this static singleton.
     */
    private LookAndFeelUtil()
    {
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Selects the given look and feel on the look and feel menu.
     * 
     * @param info Look And Feel that maps to a menu item.
     */ 
    public static void selectOnMenu(LAFInfo info)
    {
        JCheckBoxMenuItem cbmi = (JCheckBoxMenuItem) menuItemMap_.get(info);

        if (cbmi != null)
        {
            cbmi.setSelected(true);
            logger_.debug("Successlly selected laf: " + info.getName());
        }
        else
        {
            StringBuffer sb = new StringBuffer();
            sb.append("Could not find lafinfo in menu map.\n" + info + "\n");
            
            sb.append(ArrayUtil.toString(
                menuItemMap_.keySet().toArray(), true));
            
            logger_.debug(StringUtil.banner(sb.toString()));
        }
    }


    /**
     * Sets the look and feel given a DOM.
     * 
     * @param prefs XML tree to read the look and feel preferences from.
     * @throws Exception on error.
     */
    public static void setLookAndFeel(Element prefs) throws Exception
    {
        Element lafNode = 
            XOMUtil.getFirstChildElement(prefs, NODE_LOOKANDFEEL, null);

        if (lafNode == null)
            logger_.info("Look and Feel not set. DOM is null");
        else
            setLookAndFeel(new LAFInfo(lafNode));
    }

    
    /**
     * Sets the look and feel.
     * 
     * @param info Look and feel info.
     * @throws Exception on error.
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
        Map submenus = new HashMap();
        
        // Create the menu whilst also groups similar look and feels together
        // on the same submenu.
        
        for (Iterator i = lookAndFeels_.iterator(); i.hasNext();)
        {   
            LAFInfo info = (LAFInfo) i.next();        
            
            try
            {
                // Group look and feels by property group.name. If group.name
                // is not available, then just use the look and feels name
                // instead.
                
                String clazz = info.getProperty("group.name");
                if (clazz == null)
                    clazz = info.getName();

                JMenu submenu = (JMenu) submenus.get(clazz);

                if (submenu == null)
                {
                    submenu = new JSmartMenu(clazz);
                    submenus.put(clazz, submenu);
                }

                Class actionClass = Class.forName(info.getAction());
                
                LookAndFeelActivator activator =
                    (LookAndFeelActivator) actionClass.newInstance();
                
                activator.setLookAndFeelInfo(info);

                JCheckBoxMenuItem cb =
                    new JSmartCheckBoxMenuItem((Action) activator);

                group_.add(cb);
                submenu.add(cb);
                menuItemMap_.put(info, cb);
            }
            catch (Throwable t)
            {
                logger_.error(t);
            }
        }

        // Sort entries in the menu
        List sorted = new ArrayList(submenus.keySet());
        Collections.sort(sorted, new ReverseComparator());

        // Add the resulting submenus to the top level menu. Groups that contain
        // only one item are placed on the toplevel menu instead of having their
        // own submenu.
        
        for (Iterator i = sorted.iterator(); i.hasNext();)
        {
            JMenu submenu = (JMenu) submenus.get(i.next());
            
            if (submenu.getMenuComponentCount() == 1)
                menu.add(submenu.getMenuComponent(0));
            else
                menu.insert(submenu, 0);
        }
        
        // Select the active look and feel if it is available on the menu.
        
        LAFInfo current = (LAFInfo) 
            UIManager.getLookAndFeel().getDefaults().get(
                LAFInfo.PROP_HIDDEN_KEY);

        if (current != null)
            selectOnMenu(current);
        else
            logger_.debug(StringUtil.banner(
                "Hidden key not found for LAF " + UIManager.getLookAndFeel()));
        
        return menu;
    }


    /**
     * Saves currently active look and feel to a node in the given element.
     *
     * @param workspace Node in which to save look and feel info.
     * @throws Exception on error.
     */
    public static void savePrefs(Element workspace) throws Exception
    {
        AbstractButton button = group_.getSelected();
        
        if (button != null) // No look and feel selected, skip...
        {    
            logger_.info("Selected button: " + button);
            
            Action action = button.getAction();
            
            logger_.info("Selected action: " + action);
            
            LookAndFeelActivator activator = 
                (LookAndFeelActivator) group_.getSelected().getAction();
            
            LAFInfo info = activator.getLookAndFeelInfo();
            info.savePrefs(workspace);
        }
        else
        {
            logger_.debug(
                "No look and feel selected. Not saving LookAndFeelUtil prefs.");
        }
    }

    
    /**
     * Propagates the change in LookAndFeel selection to all known frames and
     * dialogs. Appropriate to be called immediately after 
     * UIManager.setLookAndFeel().
     */
    public static void propagateChangeInLAF()
    {
        Frame[] frames = Frame.getFrames();
        for (int i = 0; i < frames.length; i++)
            SwingUtilities.updateComponentTreeUI(frames[i]);
        
        JDialog[] dialogs = JSmartDialog.getDialogs();
        for (int i = 0; i < dialogs.length; i++)
            SwingUtilities.updateComponentTreeUI(dialogs[i]);
    }

    
    /**
     * Sets the Look and Feel to Metal.
     * 
     * @throws Exception on error.
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
     * @throws Exception on error.
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
     * @throws Exception on error.
     */
    public static void setSkinLAF() throws Exception
    { 
        UIManager.setLookAndFeel("com.l2fprod.gui.plaf.skin.SkinLookAndFeel");
        propagateChangeInLAF();
    }

    
    /**
     * Sets the preferred Look and Feel.
     * 
     * @throws Exception on error.
     */
    public static void setPreferredLAF() throws Exception
    { 
        UIManager.setLookAndFeel("com.jgoodies.looks.plastic.PlasticXPLookAndFeel");
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
    
    
    /**
     * Shows look and feel properties in a JTable.
     */
    public static void showUIProperties()
    {
        JFrame f = new JFrame("UI Properties");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        Set defaults = UIManager.getLookAndFeelDefaults().entrySet();
        
        Set ts = new TreeSet(new Comparator()
        {
            public int compare(Object a, Object b)
            {
                Map.Entry ea = (Map.Entry) a;
                Map.Entry eb = (Map.Entry) b;
                return ((String)ea.getKey()).compareTo(((String) eb.getKey()));
            }
        });
        
        ts.addAll(defaults);
        Object[][] nvPairs = new Object[defaults.size()][2];
        Object[] columnNames = new Object[]{"Key", "Value"};
        int row = 0;
        
        for (Iterator i = ts.iterator(); i.hasNext();)
        {
            Object o = i.next();
            Map.Entry entry = (Map.Entry) o;
            nvPairs[row][0] = entry.getKey();
            nvPairs[row][1] = entry.getValue();
            row++;
        }
        
        JTable table = new JTable(nvPairs, columnNames);
        JScrollPane sp = new JScrollPane(table);
        f.getContentPane().add(sp);
        f.pack();
        f.setVisible(true);
    }

    
    /**
     * Initializes and installs the look and feels read from lookandfeel.xml.
     */
    public static void initLookAndFeels() 
    {
        InputStream is = null;
        
        try
        {
            is = ResourceUtil.getResource(FILE_LOOKANDFEEL_CONFIG);
            Element root = new Builder().build(is).getRootElement();
            Elements lafs = root.getChildElements(NODE_LOOKANDFEEL);
            
            //
            // Append LAFs from lookandfeel.xml
            //
            
            for (int i = 0; i < lafs.size(); i++)
            {    
                LAFInfo info = new LAFInfo(lafs.get(i));
                
                // Only install a look and feel once even if it occurs 
                // multiple times in the configuration.
                
                if (!lookAndFeelMap_.containsKey(info.getName()))
                {
                    logger_.debug("Installed " + info.getName());
                    
                    UIManager.installLookAndFeel(
                        info.getName(), 
                        info.getClassName());
                }
                
                lookAndFeelMap_.put(info.getName(), info);
                lookAndFeels_.add(info);
            }
            
            //
            // Append LAFs that are already installed by the JRE
            //
            
            for (int i = 0; i < baseLAFs_.length; i++)
            {    
                LAFInfo info = new LAFInfo();
                info.setAction(ActivateLookAndFeelAction.class.getName());
                info.setClassName(baseLAFs_[i].getClassName());
                info.setName(baseLAFs_[i].getName());
                info.getProperties().put("group.name", "Sun");
                
                if (!lookAndFeelMap_.containsKey(info.getName()))
                {
                    logger_.debug("Installed " + info.getName());
                    
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
            IOUtils.closeQuietly(is);
        }
    }
}