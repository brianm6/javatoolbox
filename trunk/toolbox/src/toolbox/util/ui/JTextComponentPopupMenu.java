package toolbox.util.ui;

import javax.swing.text.JTextComponent;
import javax.swing.JMenuItem;
import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;

import java.awt.event.ActionEvent;

/**
 * Popup menu with commonly used functionality for JTextComponent subclasses.
 */
public class JTextComponentPopupMenu extends JPopupMenu
{
    /**
     * Text component to associate this popup menu with
     */
    private JTextComponent textComponent_;

    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Constructor for JTextComponentPopupMenu.
     */
    public JTextComponentPopupMenu()
    {
    }
    
    /**
     * Constructor for JTextComponentPopupMenu.
     * 
     * @param  textComponent  JTextComponent to add popup to
     */
    public JTextComponentPopupMenu(JTextComponent textComponent)
    {
        this("", textComponent);
    }

    /**
     * Constructor for JTextComponentPopupMenu.
     * 
     * @param label             Popupmenu label
     * @param textComponent     JTextComponent to add popup to
     */
    public JTextComponentPopupMenu(String label, JTextComponent textComponent)
    {
        super(label);
        textComponent_ = textComponent;
        buildView();
    }

    //--------------------------------------------------------------------------
    //  Private
    //--------------------------------------------------------------------------

    /**
     * Builds popupmenu and adds mouse listener to listbox
     */
    protected void buildView()
    {
        add(new JMenuItem(new CopyAction()));
        add(new JMenuItem(new PasteAction()));
        add(new JMenuItem(new SelectAllAction()));
        textComponent_.addMouseListener(new JPopupListener(this));
    }
    
    //--------------------------------------------------------------------------
    //  Action Inner Classes
    //--------------------------------------------------------------------------

    /**
     * Copies the contents of the currently selected indices to the clipboard
     */    
    protected class CopyAction extends AbstractAction
    {
        public CopyAction()
        {
            super("Copy");
        }
        
        public void actionPerformed(ActionEvent e)
        {
            textComponent_.copy();
        }
    }


    /**
     * Pastes the contents of the clipboard into the text component
     */    
    protected class PasteAction extends AbstractAction
    {
        public PasteAction()
        {
            super("Paste");
        }
        
        public void actionPerformed(ActionEvent e)
        {
            textComponent_.paste();
        }
    }
    
    
    /**
     * Selects all items in the list box 
     */
    protected class SelectAllAction extends AbstractAction
    {
        public SelectAllAction()
        {
            super("Select All");
        }
        
        public void actionPerformed(ActionEvent e)
        {
            textComponent_.selectAll();
        }
    }
}