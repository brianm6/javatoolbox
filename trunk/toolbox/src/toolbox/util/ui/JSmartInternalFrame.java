package toolbox.util.ui;

import javax.swing.JInternalFrame;

import nu.xom.Attribute;
import nu.xom.Element;

import toolbox.util.XOMUtil;
import toolbox.workspace.IPreferenced;

/**
 * An extension of JInternalFrame that supports persistence of preferences 
 * including size, location, and iconified state.
 */
public class JSmartInternalFrame extends JInternalFrame implements IPreferenced
{
    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------

    // Preferences.
    private static final String NODE_JFRAME    = "JInternalFrame";
    private static final String ATTR_MAXIMIZED = "maximized";
    private static final String ATTR_WIDTH     = "w";
    private static final String ATTR_HEIGHT    = "h";
    private static final String ATTR_X         = "x";
    private static final String ATTR_Y         = "y";

    // Defaults for select preferences.
    private static final boolean DEFAULT_MAXIMIZED = false;
    private static final int DEFAULT_WIDTH = 300;
    private static final int DEFAULT_HEIGHT = 200;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a JSmartInternalFrame.
     */
    public JSmartInternalFrame()
    {
    }


    /**
     * Creates a JSmartInternalFrame.
     *
     * @param title Frame title.
     */
    public JSmartInternalFrame(String title)
    {
        super(title);
    }

    
    /**
     * Creates a JSmartInternalFrame.
     * 
     * @param title
     * @param resizable
     * @param closable
     * @param maximizable
     * @param iconifiable
     */
    public JSmartInternalFrame(
        String title, 
        boolean resizable,
        boolean closable, 
        boolean maximizable, 
        boolean iconifiable)
    {
        super(title, resizable, closable, maximizable, iconifiable);
    }
    
    //--------------------------------------------------------------------------
    // IPreferenced Interface
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.workspace.IPreferenced#applyPrefs(nu.xom.Element)
     */
    public void applyPrefs(Element prefs) throws Exception
    {
        Element root = prefs.getFirstChildElement(NODE_JFRAME);

        setBounds(
            XOMUtil.getIntegerAttribute(root, ATTR_X, 0),
            XOMUtil.getIntegerAttribute(root, ATTR_Y, 0),
            XOMUtil.getIntegerAttribute(root, ATTR_WIDTH, DEFAULT_WIDTH),
            XOMUtil.getIntegerAttribute(root, ATTR_HEIGHT, DEFAULT_HEIGHT));

        boolean maximized =
            XOMUtil.getBooleanAttribute(
                root, ATTR_MAXIMIZED, DEFAULT_MAXIMIZED);
    }


    /**
     * @see toolbox.workspace.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs) throws Exception
    {
        Element root = new Element(NODE_JFRAME);

        boolean maximized = false;
        
        // (getMgetExtendedState() & Frame.MAXIMIZED_BOTH) ==;
        // Frame.MAXIMIZED_BOTH;

        root.addAttribute(new Attribute(ATTR_MAXIMIZED, maximized + ""));

        if (!maximized)
        {
            root.addAttribute(new Attribute(ATTR_X, getLocation().x + ""));
            root.addAttribute(new Attribute(ATTR_Y, getLocation().y + ""));
            root.addAttribute(new Attribute(ATTR_WIDTH, getSize().width + ""));
            root.addAttribute(new Attribute(ATTR_HEIGHT,
                getSize().height + ""));
        }

        XOMUtil.insertOrReplace(prefs, root);
    }
}