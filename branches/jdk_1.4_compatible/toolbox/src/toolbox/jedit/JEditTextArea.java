package toolbox.jedit;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JPopupMenu;
import javax.swing.text.PlainDocument;

import nu.xom.Attribute;
import nu.xom.Element;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import org.jedit.syntax.TextAreaDefaults;
import org.jedit.syntax.TokenMarker;

import toolbox.jedit.action.InsertFileAction;
import toolbox.jedit.action.SaveAsAction;
import toolbox.util.FontUtil;
import toolbox.util.SwingUtil;
import toolbox.util.XOMUtil;
import toolbox.util.ui.AntiAliased;
import toolbox.workspace.IPreferenced;
import toolbox.workspace.PreferencedException;

/**
 * Modified JEditTextArea that supports a host of convenient features.
 * <p>
 * These features include:
 * <ul>
 *   <li>Mouse wheel support
 *   <li>Adjustable tab size
 *   <li>Adjustable font
 *   <li>Simple text search facility
 *   <li>Persistence of preferences
 *   <li>Automatically saves and restores the editor contents
 *   <li>Right mouse button activated popup menu with:
 *     <ul>
 *       <li>Copy, cut, paste
 *       <li>Save to file
 *       <li>Insert from file
 *       <li>Antialias text
 *     </ul>
 *   </li>
 * </ul>
 * <p>
 * Keyboard shortcuts added:
 * <ul>
 *   <li>Ctrl+A - Select All
 *   <li>Ctrl+V - Paste
 *   <li>Ctrl+C - Copy
 *   <li>Ctrl+X - Cut
 *   <li>Ctrl+O - Open file
 *   <li>Ctrl+S - Save file
 *   <li>Ctrl+F - Find
 * </ul>
 */
public class JEditTextArea extends org.jedit.syntax.JEditTextArea
    implements MouseWheelListener, AntiAliased, IPreferenced
{
    private static final Logger logger_ = Logger.getLogger(JEditTextArea.class);

    //--------------------------------------------------------------------------
    // XML Constants 
    //--------------------------------------------------------------------------
    
    // Preferences
    public static final String NODE_JEDITTEXTAREA  = "JEditTextArea";
    public static final String   ATTR_ANTIALIAS    = AntiAliased.PROP_ANTIALIAS;
    public static final String   ATTR_WHEELUNIT    =   "mousewheelunit";
    public static final String   ATTR_TABSIZE      =   "tabsize";
    public static final String   ATTR_SAVE_CONTENTS=   "savecontents";
    public static final String   NODE_FONT         =   "Font";
    public static final String   NODE_CONTENTS     =   "Contents";
    
    //--------------------------------------------------------------------------
    // Fields 
    //--------------------------------------------------------------------------
    
    /**
     * Number of lines to scroll per mouse wheel scroll.
     */    
    private int mouseWheelUnit_;
    
    /**
     * Antialias flag.
     */
    private boolean antiAlias_ = SwingUtil.getDefaultAntiAlias();

    /**
     * Flag to save the contents on persistence/hydration of preferences.
     */
    private boolean saveContents_;

    /**
     * Action to save the contents of the text area to a file.
     */
    private SaveAsAction saveAsAction_;

    /**
     * Action to insert the contents of a file into the text area.
     */
    private InsertFileAction insertFileAction_;
    
    //--------------------------------------------------------------------------
    // Constructors 
    //--------------------------------------------------------------------------
        
    /**
     * Creates a JEditTextArea with the build in defaults and no token marker
     * for syntax hiliting.
     */
    public JEditTextArea()
    {
        this(null, TextAreaDefaults.getDefaults());
    }
    
    
    /**
     * Creates a JEditTextArea with the given defaults.
     * 
     * @param marker Token marker to use for syntax hiliting.
     * @param defaults Text area defaults.
     */
    public JEditTextArea(TokenMarker marker, TextAreaDefaults defaults)
    {
        super(defaults);
        
        setMouseWheelUnit(3);
        setSaveContents(false);
        
        if (marker != null)
            setTokenMarker(marker);
        
        addMouseWheelListener(this);
            
        //
        // Some more useful keybindings...reuse actions from the popup menu.
        //
        
        getInputHandler().addKeyBinding(
            "C+A", new JEditActions.SelectAllAction(this));
            
        getInputHandler().addKeyBinding(
            "C+V", new JEditActions.PasteAction(this));
            
        getInputHandler().addKeyBinding(
            "C+C", new JEditActions.CopyAction(this));

        getInputHandler().addKeyBinding(
            "C+X", new JEditActions.CutAction(this));
            
        getInputHandler().addKeyBinding(
            "C+O", insertFileAction_ = new InsertFileAction(this));
            
        getInputHandler().addKeyBinding(
            "C+S", saveAsAction_ = new SaveAsAction(this));
            
        getInputHandler().addKeyBinding(
            "C+F", new JEditActions.FindAction(this));
    }
    
    //--------------------------------------------------------------------------
    // Accessors/Mutators 
    //--------------------------------------------------------------------------
    
    /**
     * Returns number of lines to scroll on mouse wheel activity.
     * 
     * @return int
     */
    public int getMouseWheelUnit()
    {
        return mouseWheelUnit_;
    }
    
    
    /**
     * Sets the number of lines to scroll on mouse wheel activity.
     * 
     * @param mouseWheelUnit Number of lines.
     */
    public void setMouseWheelUnit(int mouseWheelUnit)
    {
        mouseWheelUnit_ = mouseWheelUnit;
    }

    
    /**
     * @return Returns the saveContents.
     */
    public boolean shouldSaveContents()
    {
        return saveContents_;
    }
    
    
    /**
     * @param saveContents The flag to save contents to set.
     */
    public void setSaveContents(boolean saveContents)
    {
        saveContents_ = saveContents;
    }
    
    //--------------------------------------------------------------------------
    // Convenience Methods
    //--------------------------------------------------------------------------
    
    /**
     * Sets the width of the tab character.
     * 
     * @param tabSize Tab width.
     */
    public void setTabSize(int tabSize)
    {
        getDocument().putProperty(
            PlainDocument.tabSizeAttribute, new Integer(tabSize));
    }

    
    /**
     * Returns the width of the tab character.
     * 
     * @return int
     */
    public int getTabSize()
    {
        return ((Integer) getDocument().getProperty(
                PlainDocument.tabSizeAttribute)).intValue();
    }

    //--------------------------------------------------------------------------
    // Provides access to protected fields in the superclass
    //--------------------------------------------------------------------------
    
    /**
     * Returns the popup menu for the text area.
     * 
     * @return JPopupMenu
     */
    public JPopupMenu getPopupMenu()
    {
        return popup;
    }
    
    
    /**
     * Sets the popup menu for the text area.
     * 
     * @param popupMenu Popup menu to set.
     */
    public void setPopupMenu(JPopupMenu popupMenu)
    {
        popup = popupMenu;
    }

    //--------------------------------------------------------------------------
    // MouseWheelListener Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see java.awt.event.MouseWheelListener#mouseWheelMoved(
     *          java.awt.event.MouseWheelEvent)
     */
    public void mouseWheelMoved(MouseWheelEvent mouseWheelEvent)
    {
        if (mouseWheelEvent.getScrollAmount() == 0)
            return;
            
        vertical.setValue(
            vertical.getValue() + mouseWheelUnit_ * 
                mouseWheelEvent.getWheelRotation());
    }
    
    //--------------------------------------------------------------------------
    // Antialiased Interface
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.util.ui.AntiAliased#setAntiAliased(boolean)
     */
    public void setAntiAliased(boolean b)
    {
        antiAlias_ = b;
    }

    
    /**
     * @see toolbox.util.ui.AntiAliased#isAntiAliased()
     */
    public boolean isAntiAliased()
    {
        // The modified TextAreaPainter in the 'debug' tree makes a callback to 
        // this method to toggle antialiasing in its paint() method.
        
        return antiAlias_;
    }
    
    //--------------------------------------------------------------------------
    // IPreferenced Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.workspace.IPreferenced#applyPrefs(nu.xom.Element)
     */
    public void applyPrefs(Element prefs) throws PreferencedException
    {
        Element root = XOMUtil.getFirstChildElement(
            prefs, NODE_JEDITTEXTAREA, new Element(NODE_JEDITTEXTAREA));
                
        setAntiAliased(XOMUtil.getBooleanAttribute(root, ATTR_ANTIALIAS, true));
        setTabSize(XOMUtil.getIntegerAttribute(root, ATTR_TABSIZE, 4));
        setMouseWheelUnit(XOMUtil.getIntegerAttribute(root, ATTR_WHEELUNIT, 3));

        setSaveContents(
            XOMUtil.getBooleanAttribute(root, ATTR_SAVE_CONTENTS, false));

        if (shouldSaveContents())
        {
            Element content = XOMUtil.getFirstChildElement(
                root, NODE_CONTENTS, new Element(NODE_CONTENTS));
            
            setText(new String(
                Base64.decodeBase64(XOMUtil.getString(content,"").getBytes())));
            
            scrollTo(0, 0);            
        }
        
        getPainter().setFont(FontUtil.toFont(XOMUtil.getFirstChildElement(
            root, NODE_FONT, FontUtil.toElement(
                FontUtil.getPreferredMonoFont()))));

        saveAsAction_.applyPrefs(root);
        insertFileAction_.applyPrefs(root);
    }
    
    
    /**
     * @see toolbox.workspace.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs) throws PreferencedException
    {
        Element root = new Element(NODE_JEDITTEXTAREA);
        root.addAttribute(new Attribute(ATTR_TABSIZE, getTabSize() + ""));
        root.addAttribute(new Attribute(ATTR_ANTIALIAS, isAntiAliased() + ""));
        
        root.addAttribute(
            new Attribute(ATTR_WHEELUNIT, getMouseWheelUnit() + ""));
        
        root.addAttribute(
            new Attribute(ATTR_SAVE_CONTENTS, shouldSaveContents() + ""));
        
        root.appendChild(FontUtil.toElement(getPainter().getFont()));
     
        if (shouldSaveContents())
        {
	        Element contents = new Element(NODE_CONTENTS);
	        
	        contents.appendChild(new String(
	            Base64.encodeBase64(getText().getBytes())));
	        
	        root.appendChild(contents);
        } 

        saveAsAction_.savePrefs(root);
        insertFileAction_.savePrefs(root);
        
        XOMUtil.insertOrReplace(prefs, root);
    }
    
    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------
    
    /**
     * Returns the SaveAsAction.
     * 
     * @return SaveAsAction
     */
    protected SaveAsAction getSaveAsAction()
    {
        return saveAsAction_;
    }
    
    
    /**
     * Returns the InsertFileAction.
     * 
     * @return InsertFileAction
     */
    protected InsertFileAction getInsertFileAction()
    {
        return insertFileAction_;
    }
}