package toolbox.util.ui;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.KeyEvent;

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
 *  <li>Esc will dispose of the dialog.
 *  <li>Size and location can be saved/restored via the IPreferenced interface
 * </ul>
 */
public class JSmartDialog extends JDialog implements IPreferenced
{
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
        // Bind ESC to the dispose action.
        getRootPane()
            .getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), KEY_ESC);

        getRootPane().getActionMap().put(KEY_ESC, new DisposeAction(this));
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