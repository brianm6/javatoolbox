package toolbox.jedit;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.apache.log4j.Logger;

import toolbox.util.ui.JPopupListener;

/**
 * Popup menu with commonly used functionality for JEditTextArea subclasses.
 */
public class JEditPopupMenu extends JPopupMenu
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
     * Default constructor 
     */
    public JEditPopupMenu()
    {
    }
    
    /**
     * Creates a JEditTextAreaPopupMenu for the given textarea
     * 
     * @param  textArea  JEditTextArea to add popup to
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
     * @param  area  Textarea
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
        add(new JMenuItem(new JEditActions.CopyAction(textArea_)));
        add(new JMenuItem(new JEditActions.CutAction(textArea_)));
        add(new JMenuItem(new JEditActions.PasteAction(textArea_)));
        add(new JMenuItem(new JEditActions.SelectAllAction(textArea_)));
        addSeparator();
        add(new JMenuItem(new JEditActions.SetFontAction(textArea_)));
        add(new JMenuItem(new JEditActions.FindAction(textArea_)));
        add(new JMenuItem(new JEditActions.InsertFileAction(textArea_)));
        add(new JMenuItem(new JEditActions.SaveAsAction(textArea_)));
        
        textArea_.addMouseListener(new JPopupListener(this));
    }
}