package toolbox.jedit;

import java.awt.Font;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;

import org.apache.log4j.Logger;

import toolbox.util.ExceptionUtil;
import toolbox.util.FileUtil;
import toolbox.util.SwingUtil;
import toolbox.util.ui.JFindDialog;
import toolbox.util.ui.JPopupListener;
import toolbox.util.ui.font.FontChooserException;
import toolbox.util.ui.font.IFontChooserDialogListener;
import toolbox.util.ui.font.JFontChooser;
import toolbox.util.ui.font.JFontChooserDialog;

/**
 * Popup menu with commonly used functionality for JEditTextArea subclasses.
 */
public class JEditTextAreaPopupMenu extends JPopupMenu
{
    private static final Logger logger_ =
        Logger.getLogger(JEditTextAreaPopupMenu.class); 
        
    /** Text component to associate this popup menu with */
    private JEditTextArea textArea_;

    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------

    /**
     * Default constructor 
     */
    public JEditTextAreaPopupMenu()
    {
    }
    
    /**
     * Creates a JEditTextAreaPopupMenu for the given textarea
     * 
     * @param  textArea  JEditTextArea to add popup to
     */
    public JEditTextAreaPopupMenu(JEditTextArea textArea)
    {
        this("", textArea);
    }

    /**
     * Creates a JEditTextAreaPopupMenu for the given textarea
     * 
     * @param  label     Popupmenu label
     * @param  textArea  JEditTextArea to add popup to
     */
    public JEditTextAreaPopupMenu(String label, JEditTextArea textArea)
    {
        super(label);
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

    //--------------------------------------------------------------------------
    // Private
    //--------------------------------------------------------------------------

    /**
     * Builds popupmenu and adds mouse listener to listbox
     */
    public void buildView()
    {
        JEditActions a = new JEditActions();
        
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

