package toolbox.util.ui;

import javax.swing.text.JTextComponent;

import org.apache.log4j.Logger;

import toolbox.util.ui.action.SetFontAction;
import toolbox.util.ui.textarea.action.CopyAction;
import toolbox.util.ui.textarea.action.FindAction;
import toolbox.util.ui.textarea.action.InsertFileAction;
import toolbox.util.ui.textarea.action.PasteAction;
import toolbox.util.ui.textarea.action.SaveAsAction;
import toolbox.util.ui.textarea.action.SelectAllAction;

/**
 * Popup menu with commonly used functionality for JTextComponent subclasses.
 */
public class JTextComponentPopupMenu extends JSmartPopupMenu
{
    private static final Logger logger_ =
        Logger.getLogger(JTextComponentPopupMenu.class); 
        
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /** 
     * Text component to associate this popup menu with.
     */
    private JTextComponent textComponent_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a JTextComponentPopupMenu.
     */
    public JTextComponentPopupMenu()
    {
    }
    
    
    /**
     * Creates a JTextComponentPopupMenu with an associated text component.
     * 
     * @param textComponent JTextComponent to add popup to.
     */
    public JTextComponentPopupMenu(JTextComponent textComponent)
    {
        textComponent_ = textComponent;
        buildView();
    }

    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------

    /**
     * Builds the popupmenu and adds a mouse listener.
     */
    protected void buildView()
    {
        add(new JSmartMenuItem(new CopyAction(textComponent_)));
        add(new JSmartMenuItem(new PasteAction(textComponent_)));
        add(new JSmartMenuItem(new SelectAllAction(textComponent_)));
        addSeparator();
        add(new JSmartMenuItem(new SetFontAction(textComponent_)));
        add(new JSmartMenuItem(new FindAction(textComponent_)));
        add(new JSmartMenuItem(new InsertFileAction(textComponent_)));
        add(new JSmartMenuItem(new SaveAsAction(textComponent_)));
        
        textComponent_.addMouseListener(new JPopupListener(this));
    }
}