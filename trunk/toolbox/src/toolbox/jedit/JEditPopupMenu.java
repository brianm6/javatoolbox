package toolbox.jedit;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import nu.xom.Element;

import org.apache.log4j.Logger;

import toolbox.util.XOMUtil;
import toolbox.util.ui.JPopupListener;
import toolbox.util.ui.JSmartMenuItem;
import toolbox.util.ui.JSmartPopupMenu;
import toolbox.workspace.IPreferenced;

/**
 * Popup menu with commonly used functionality for JEditTextArea subclasses.
 */
public class JEditPopupMenu extends JSmartPopupMenu implements IPreferenced
{
    private static final Logger logger_ = 
        Logger.getLogger(JEditPopupMenu.class); 

    //--------------------------------------------------------------------------
    // IPreferenced Constants
    //--------------------------------------------------------------------------
    
    private static final String NODE_JEDITPOPUPMENU = "JEditPopupMenu";
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /** 
     * Text component to associate this popup menu with. 
     */
    private JEditTextArea textArea_;

    /**
     * Preferenced subcomponents to trickle save/applyPrefs to.
     */
    private List subComponents_;

    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a JEditPopupMenu.
     */
    public JEditPopupMenu()
    {
        subComponents_ = new ArrayList(0);
    }
    
    
    /**
     * Creates a JEditTextAreaPopupMenu for the given textarea.
     * 
     * @param textArea JEditTextArea to add popup to.
     */
    public JEditPopupMenu(JEditTextArea textArea)
    {
        subComponents_ = new ArrayList(2);
        setTextArea(textArea);
        buildView();
    }
    
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------

    /**
     * Sets the textarea.
     * 
     * @param area Textarea.
     */
    public void setTextArea(JEditTextArea area)
    {
        textArea_ = area;
    }

    
    /**
     * Builds popupmenu and adds mouse listener to listbox.
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
        add(new JSmartMenuItem(textArea_.getInsertFileAction()));
        add(new JSmartMenuItem(textArea_.getSaveAsAction()));
        
        //subComponents_.add(save);
        
        textArea_.addMouseListener(new JPopupListener(this));
    }
    
    //--------------------------------------------------------------------------
    // IPreferenced Interface
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.workspace.IPreferenced#applyPrefs(nu.xom.Element)
     */
    public void applyPrefs(Element prefs) throws Exception
    {
        Element root = XOMUtil.getFirstChildElement(prefs, NODE_JEDITPOPUPMENU,
            new Element(NODE_JEDITPOPUPMENU));
     
        for (Iterator iter = subComponents_.iterator(); iter.hasNext();)
            ((IPreferenced) iter.next()).applyPrefs(root);
    }


    /**
     * @see toolbox.workspace.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs) throws Exception
    {
        Element root = new Element(NODE_JEDITPOPUPMENU);
        for (Iterator iter = subComponents_.iterator(); iter.hasNext();)
            ((IPreferenced) iter.next()).savePrefs(root);
        XOMUtil.insertOrReplace(prefs, root);
    }
}