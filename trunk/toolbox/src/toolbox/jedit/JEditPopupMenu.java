package toolbox.jedit;

import org.apache.log4j.Logger;

import toolbox.util.ui.JPopupListener;
import toolbox.util.ui.JSmartMenuItem;
import toolbox.util.ui.JSmartPopupMenu;

/**
 * Popup menu with commonly used functionality for JEditTextArea subclasses.
 */
public class JEditPopupMenu extends JSmartPopupMenu
{
    private static final Logger logger_ =
        Logger.getLogger(JEditPopupMenu.class); 
        
    /** 
     * Text component to associate this popup menu with 
     */
    private JEditTextArea textArea_;

    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a JEditPopupMenu 
     */
    public JEditPopupMenu()
    {
    }
    
    /**
     * Creates a JEditTextAreaPopupMenu for the given textarea
     * 
     * @param textArea JEditTextArea to add popup to
     */
    public JEditPopupMenu(JEditTextArea textArea)
    {
        textArea_ = textArea;
        buildView();
        
    }
    
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------

    /**
     * Sets the textarea
     * 
     * @param area Textarea
     */
    public void setTextArea(JEditTextArea area)
    {
        textArea_ = area;
    }

    /**
     * Builds popupmenu and adds mouse listener to listbox
     */
    public void buildView()
    {
        add(new JSmartMenuItem(new JEditActions.CopyAction(textArea_)));
        add(new JSmartMenuItem(new JEditActions.CutAction(textArea_)));
        add(new JSmartMenuItem(new JEditActions.PasteAction(textArea_)));
        add(new JSmartMenuItem(new JEditActions.SelectAllAction(textArea_)));
        addSeparator();
        add(new JSmartMenuItem(new JEditActions.SetFontAction(textArea_)));
        add(new JSmartMenuItem(new JEditActions.FindAction(textArea_)));
        add(new JSmartMenuItem(new JEditActions.InsertFileAction(textArea_)));
        add(new JSmartMenuItem(new JEditActions.SaveAsAction(textArea_)));
        
        textArea_.addMouseListener(new JPopupListener(this));
    }
}