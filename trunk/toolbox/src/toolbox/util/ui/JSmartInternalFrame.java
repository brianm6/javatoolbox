package toolbox.util.ui;

import java.awt.Rectangle;

import javax.swing.JInternalFrame;

import nu.xom.Element;

import toolbox.util.PreferencedUtil;
import toolbox.util.XOMUtil;
import toolbox.workspace.IPreferenced;

/**
 * JSmartInternalFrame is an extension of JInternalFrame that supports
 * persistence of preferences including size, location, and iconified state.
 */
public class JSmartInternalFrame extends JInternalFrame implements IPreferenced
{
    //--------------------------------------------------------------------------
    // XML Constants
    //--------------------------------------------------------------------------

    private static final String NODE_JFRAME = "JInternalFrame";

    //--------------------------------------------------------------------------
    // Defaults Constants
    //--------------------------------------------------------------------------

    /**
     * Default size and location of frames which don't specify any bounds.
     */
    private static final Rectangle DEFAULT_BOUNDS = 
        new Rectangle(0, 0, 300, 200);
    
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
     * @param title Frame title.
     * @param resizable Frame can be resized.
     * @param closable Frame can be closed.
     * @param maximizable Frame can be maximized.
     * @param iconifiable Frame can be iconified.
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
     * Restores this frames size and location.
     * 
     * @see toolbox.workspace.IPreferenced#applyPrefs(nu.xom.Element)
     */
    public void applyPrefs(Element prefs) throws Exception
    {
        Element root = prefs.getFirstChildElement(NODE_JFRAME);
        PreferencedUtil.applyPrefs(root, this, DEFAULT_BOUNDS); 
    }


    /**
     * Saves this frames size and location.
     * 
     * @see toolbox.workspace.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs) throws Exception
    {
        Element root = new Element(NODE_JFRAME);
        PreferencedUtil.savePrefs(root, this);
        XOMUtil.insertOrReplace(prefs, root);
    }
}