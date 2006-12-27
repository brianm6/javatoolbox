package toolbox.util.ui;

import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import nu.xom.Attribute;
import nu.xom.Element;

import toolbox.util.PreferencedUtil;
import toolbox.util.XOMUtil;
import toolbox.workspace.IPreferenced;
import toolbox.workspace.PreferencedException;

/**
 * JSmartFrame is an extension of JFrame that supports persistence of
 * preferences including size, location, and maximized state.
 */
public class JSmartFrame extends JFrame implements IPreferenced
{
    // TODO: Needs to be tested.

    //--------------------------------------------------------------------------
    // XML Constants
    //--------------------------------------------------------------------------

    private static final String NODE_JFRAME    = "JFrame";
    private static final String ATTR_MAXIMIZED = "maximized";

    //--------------------------------------------------------------------------
    // Defaults Constants
    //--------------------------------------------------------------------------

    /**
     * Not maximized by default.
     */
    private static final boolean DEFAULT_MAXIMIZED = false;

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
     * Restores this frames location and size or maximized state.
     * 
     * @see toolbox.workspace.IPreferenced#applyPrefs(nu.xom.Element)
     */
    public void applyPrefs(Element prefs) throws PreferencedException
    {
        Element root = 
            XOMUtil.getFirstChildElement(
                prefs, NODE_JFRAME, new Element(NODE_JFRAME)); 

        // Takes care of the bounds only
        PreferencedUtil.applyPrefs(root, this);
        
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
     * Saves this frames location and size or maximized state.
     * 
     * @see toolbox.workspace.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs) throws PreferencedException
    {
        Element root = new Element(NODE_JFRAME);

        boolean maximized = 
            (getExtendedState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH;

        root.addAttribute(new Attribute(ATTR_MAXIMIZED, maximized + ""));

        // Only if not maximized, save the bounds
        if (!maximized)
            PreferencedUtil.savePrefs(root, this);

        XOMUtil.insertOrReplace(prefs, root);
    }
}