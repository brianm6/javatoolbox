package toolbox.util.ui;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.KeyStroke;

import nu.xom.Element;

import org.apache.log4j.Logger;

import toolbox.util.PreferencedUtil;
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
        
        // Bind ESC to the dispose action.
        getRootPane()
            .getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), KEY_ESC);

        getRootPane().getActionMap().put(KEY_ESC, new DisposeAction(this));
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
}