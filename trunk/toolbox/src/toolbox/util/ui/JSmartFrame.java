package toolbox.util.ui;

import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import nu.xom.Attribute;
import nu.xom.Element;

import toolbox.util.XOMUtil;
import toolbox.workspace.IPreferenced;

/**
 * JSmartFrame is an extension of JFrame that supports persistence of
 * preferences including size, location, and maximized state.
 */
public class JSmartFrame extends JFrame implements IPreferenced
{
	// TODO: Needs to be tested.

    // Preferences.
    private static final String NODE_JFRAME    = "JFrame";
    private static final String ATTR_MAXIMIZED = "maximized";
    private static final String ATTR_WIDTH     = "w";
    private static final String ATTR_HEIGHT    = "h";
    private static final String ATTR_X         = "x";
    private static final String ATTR_Y         = "y";
    
    // Defaults for select preferences.
    private static final boolean DEFAULT_MAXIMIZED = false;
    private static final int DEFAULT_WIDTH = 800;
    private static final int DEFAULT_HEIGHT = 600;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a JSmartFrame.
     * 
     * @throws HeadlessException on headless error. 
     */
    public JSmartFrame() throws HeadlessException
    {
    }

    
    /**
     * Creates a JSmartFrame.
     * 
     * @param gc Graphics configuration.
     */
    public JSmartFrame(GraphicsConfiguration gc)
    {
        super(gc);
    }

    
    /**
     * Creates a JSmartFrame.
     * 
     * @param title Frame title.
     * @throws HeadlessException on headless error.
     */
    public JSmartFrame(String title) throws HeadlessException
    {
        super(title);
    }

    
    /**
     * Creates a JSmartFrame.
     * 
     * @param title Frame title.
     * @param gc Graphics configuration.
     */
    public JSmartFrame(String title, GraphicsConfiguration gc)
    {
        super(title, gc);
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
        
        setLocation(
            XOMUtil.getIntegerAttribute(root, ATTR_X, 0),
            XOMUtil.getIntegerAttribute(root, ATTR_Y, 0));
        
        setSize(
            XOMUtil.getIntegerAttribute(root, ATTR_WIDTH, DEFAULT_WIDTH),
            XOMUtil.getIntegerAttribute(root, ATTR_HEIGHT, DEFAULT_HEIGHT));
        
        boolean maximized = 
            XOMUtil.getBooleanAttribute(
                root, ATTR_MAXIMIZED, DEFAULT_MAXIMIZED);
        
        // Frame has to be visible before it can be maximized so just queue
        // this bad boy up on the event queue 
        if (maximized)
        {
            SwingUtilities.invokeLater(new Runnable()
            {
                public void run()
                {
                    setExtendedState(Frame.MAXIMIZED_BOTH);
                }
            });
        }
    }
    
    
    /**
     * @see toolbox.workspace.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs) throws Exception
    {
        Element root = new Element(NODE_JFRAME);
        
        boolean maximized = (getExtendedState() & Frame.MAXIMIZED_BOTH) == 
            Frame.MAXIMIZED_BOTH;

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