package toolbox.jedit;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.text.PlainDocument;

import nu.xom.Attribute;
import nu.xom.Element;

import org.apache.log4j.Logger;

import org.jedit.syntax.TextAreaDefaults;
import org.jedit.syntax.TokenMarker;

import toolbox.util.FontUtil;
import toolbox.util.SwingUtil;
import toolbox.util.XOMUtil;
import toolbox.util.ui.AntiAliased;
import toolbox.workspace.IPreferenced;

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
    private static final Logger logger_ = 
        Logger.getLogger(JEditTextArea.class);

    // Preferences
    private static final String NODE_JEDITTEXTAREA = "JEditTextArea";
    private static final String   ATTR_ANTIALIAS   = "antialias";
    private static final String   ATTR_WHEELUNIT   = "mousewheelunit";
    private static final String   ATTR_TABSIZE     = "tabsize";
    private static final String NODE_FONT          = "Font";
    
    /**
     * Number of lines to scroll per mouse wheel scroll.
     */    
    private int mouseWheelUnit_ = 3;
    
    /**
     * Antialias flag.
     */
    private boolean antiAlias_ = SwingUtil.getDefaultAntiAlias();

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
     * @param marker Token marker to use for syntax hiliting
     * @param defaults Text area defaults
     */
    public JEditTextArea(TokenMarker marker, TextAreaDefaults defaults)
    {
        super(defaults);
        
        if (marker != null)
            setTokenMarker(marker);
        
        addMouseWheelListener(this);
            
        // Some more useful keybindings...reuse actions from the popup menu            
        getInputHandler().addKeyBinding(
            "C+A", new JEditActions.SelectAllAction(this));
            
        getInputHandler().addKeyBinding(
            "C+V", new JEditActions.PasteAction(this));
            
        getInputHandler().addKeyBinding(
            "C+C", new JEditActions.CopyAction(this));

        getInputHandler().addKeyBinding(
            "C+X", new JEditActions.CutAction(this));
            
        getInputHandler().addKeyBinding(
            "C+O", new JEditActions.InsertFileAction(this));
            
        getInputHandler().addKeyBinding(
            "C+S", new JEditActions.SaveAsAction(this));
            
        getInputHandler().addKeyBinding(
            "C+F", new JEditActions.FindAction(this));
    }
    
    //--------------------------------------------------------------------------
    // Public 
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
     * @param mouseWheelUnit  Number of lines
     */
    public void setMouseWheelUnit(int mouseWheelUnit)
    {
        mouseWheelUnit_ = mouseWheelUnit;
    }

    
    /**
     * Sets the width of the tab character.
     * 
     * @param tabSize Tab width
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
    public void applyPrefs(Element prefs)
    {
        Element root = XOMUtil.getFirstChildElement(
            prefs, NODE_JEDITTEXTAREA, new Element(NODE_JEDITTEXTAREA));
                
        setAntiAliased(XOMUtil.getBooleanAttribute(root, ATTR_ANTIALIAS, true));
        setTabSize(XOMUtil.getIntegerAttribute(root, ATTR_TABSIZE, 4));
        setMouseWheelUnit(XOMUtil.getIntegerAttribute(root, ATTR_WHEELUNIT, 3));
                        
        getPainter().setFont(FontUtil.toFont(XOMUtil.getFirstChildElement(
            root, NODE_FONT, FontUtil.toElement(
                SwingUtil.getPreferredMonoFont()))));
    }
    
    
    /**
     * @see toolbox.workspace.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs)
    {
        Element root = new Element(NODE_JEDITTEXTAREA);
        root.addAttribute(new Attribute(ATTR_TABSIZE, getTabSize() + ""));
        root.addAttribute(new Attribute(ATTR_ANTIALIAS, isAntiAliased()+""));
        root.addAttribute(new Attribute(ATTR_WHEELUNIT, mouseWheelUnit_+""));
        root.appendChild(FontUtil.toElement(getPainter().getFont()));
        prefs.appendChild(root);
    }
}