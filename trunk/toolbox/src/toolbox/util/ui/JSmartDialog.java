package toolbox.util.ui;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.KeyStroke;

import nu.xom.Element;

import org.apache.log4j.Logger;

import toolbox.util.PreferencedUtil;
import toolbox.util.SwingUtil;
import toolbox.util.XOMUtil;
import toolbox.util.ui.action.DisposeAction;
import toolbox.workspace.IPreferenced;

/**
 * JSmartDialog is an extension of JDialog. Features:
 * <ul>
 *  <li>Pressing the escape key (ESC) will dismiss the dialog.
 *  <li>Size and location can be saved/restored via the IPreferenced interface
 *  <li>A list of all active JSmartDialog instances can obtained (useful when
 *      changing look and feel without restarting)
 * </ul>
 */
public class JSmartDialog extends JDialog implements IPreferenced
{
    // TODO: Write unit test to make sure initDialog() and dispose() are 
    //       maintaining a proper list of active dialogs.

    private static final Logger logger_ = Logger.getLogger(JSmartDialog.class);
    
    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------
    
    /**
     * Map key for the esc key stroke and its associated action in the 
     * rootpane's input and action maps.
     */
    private static final String KEY_ESC = "escPressed";

    /**
     * XML element name.
     */
    private static final String NODE_JDIALOG = "JDialog";

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * Store a ref to each active instance.
     */
    private static List instances_ = new ArrayList();
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a JSmartDialog.
     * 
     * @throws java.awt.HeadlessException
     */
    public JSmartDialog() throws HeadlessException
    {
        super();
        initDialog();
    }


    /**
     * Creates a JSmartDialog.
     * 
     * @param owner
     * @throws java.awt.HeadlessException
     */
    public JSmartDialog(Dialog owner) throws HeadlessException
    {
        super(owner);
        initDialog();
    }


    /**
     * Creates a JSmartDialog.
     * 
     * @param owner
     * @param modal
     * @throws java.awt.HeadlessException
     */
    public JSmartDialog(Dialog owner, boolean modal) throws HeadlessException
    {
        super(owner, modal);
        initDialog();
    }


    /**
     * Creates a JSmartDialog.
     * 
     * @param owner
     * @throws java.awt.HeadlessException
     */
    public JSmartDialog(Frame owner) throws HeadlessException
    {
        super(owner);
        initDialog();
    }


    /**
     * Creates a JSmartDialog.
     * 
     * @param owner
     * @param modal
     * @throws java.awt.HeadlessException
     */
    public JSmartDialog(Frame owner, boolean modal) throws HeadlessException
    {
        super(owner, modal);
        initDialog();
    }


    /**
     * Creates a JSmartDialog.
     * 
     * @param owner
     * @param title
     * @throws java.awt.HeadlessException
     */
    public JSmartDialog(Dialog owner, String title) throws HeadlessException
    {
        super(owner, title);
        initDialog();
    }


    /**
     * Creates a JSmartDialog.
     * 
     * @param owner
     * @param title
     * @param modal
     * @throws java.awt.HeadlessException
     */
    public JSmartDialog(Dialog owner, String title, boolean modal)
        throws HeadlessException
    {
        super(owner, title, modal);
        initDialog();
    }


    /**
     * Creates a JSmartDialog.
     * 
     * @param owner
     * @param title
     * @throws java.awt.HeadlessException
     */
    public JSmartDialog(Frame owner, String title) throws HeadlessException
    {
        super(owner, title);
        initDialog();
    }


    /**
     * Creates a JSmartDialog.
     * 
     * @param owner
     * @param title
     * @param modal
     * @throws java.awt.HeadlessException
     */
    public JSmartDialog(Frame owner, String title, boolean modal)
        throws HeadlessException
    {
        super(owner, title, modal);
        initDialog();
    }
    
    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------
    
    /**
     * Initializes this instance of JSmartDialog. 
     */
    protected void initDialog()
    {
        instances_.add(this);
     
        addWindowListener(new InternalListener());
        
        // ESC key should dismiss the dialog...
        SwingUtil.bindKey(
            getRootPane(), 
            new DisposeAction(this),    
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW);
    }
    
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Returns an array of all known active JSmartDialogs.
     * 
     * @return JDialog[]
     */
    public static JDialog[] getDialogs() 
    {
        return (JDialog[]) instances_.toArray(new JDialog[0]);
    }
    
    //--------------------------------------------------------------------------
    // Overrides java.awt.Dialog
    //--------------------------------------------------------------------------
    
    /**
     * Removes the store references that was saved during #initDialog().
     * 
     * @see java.awt.Dialog#dispose()
     */
    public void dispose()
    {
        instances_.remove(this);
        super.dispose();
    }
    
    
    //--------------------------------------------------------------------------
    // IPreferenced Interface
    //--------------------------------------------------------------------------
    
    /**
     * Restores this dialogs location and size.
     * 
     * @see toolbox.workspace.IPreferenced#applyPrefs(nu.xom.Element)
     */
    public void applyPrefs(Element prefs) throws Exception
    {
        Element root = prefs.getFirstChildElement(NODE_JDIALOG);
        PreferencedUtil.applyPrefs(root, this);
    }


    /**
     * Saves this dialogs location and size.
     * 
     * @see toolbox.workspace.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs) throws Exception
    {
        Element root = new Element(NODE_JDIALOG);
        PreferencedUtil.savePrefs(root, this);
        XOMUtil.insertOrReplace(prefs, root);
    }    
    
    //--------------------------------------------------------------------------
    // InternalListener
    //--------------------------------------------------------------------------
    
    class InternalListener extends WindowAdapter
    {
        public void windowClosing(WindowEvent e)
        {
            logger_.debug("windowClosing");
            
            String key = getName();
            
            if (key != null) 
            {
                Element prefs = new Element(NODE_JDIALOG);
                
                try
                {
                    savePrefs(prefs);
                }
                catch (Exception e1)
                {
                    logger_.error("savePrefs", e1);
                }
                
                checkIn(key, prefs);
            }
            
            super.windowClosing(e);
        }

        public void windowClosed(WindowEvent e)
        {
            logger_.debug("windowClosed");
        }
        
        
        public void windowOpened(WindowEvent e)
        {
            String key = getName();
            
            if (key != null)
            {
                Element prefs = checkOut(key);
                
                if (prefs != null)
                {
                    try
                    {
                        applyPrefs(prefs);
                    }
                    catch (Exception e1)
                    {
                        logger_.error("applyPrefs", e1);
                    }
                }
            }
            
            super.windowOpened(e);
        }
    }
    
    //--------------------------------------------------------------------------
    // Static 
    //--------------------------------------------------------------------------
    
    private static Map prefs_ = new HashMap();
    
    private static void checkIn(String key, Element prefs)
    {
        prefs_.put(key, prefs);
    }
    
    private static Element checkOut(String key)
    {
        return (Element) prefs_.get(key);
    }
}    
