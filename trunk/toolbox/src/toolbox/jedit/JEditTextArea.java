package toolbox.jedit;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.text.PlainDocument;

import org.jedit.syntax.TextAreaDefaults;
import org.jedit.syntax.TokenMarker;

import toolbox.util.SwingUtil;

/**
 * Modified JEditTextArea that supports a host of convenient features. These
 * features include:
 * <ul>
 *   <li>Mouse wheel support
 *   <li>Adjustable tab size
 *   <li>Adjustable font
 *   <li>Simple text search facility
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
    implements MouseWheelListener
{
    //private static final Logger logger_ = 
    //    Logger.getLogger(JEditTextArea.class);
        
    private int mouseWheelUnit_ = 3;
    private int tabSize_ = 4;

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
     * Creates a JEditTextArea with the given defaults
     * 
     * @param marker   Token marker to use for syntax hiliting
     * @param defaults Text area defaults
     */
    public JEditTextArea(TokenMarker marker, TextAreaDefaults defaults)
    {
        super(defaults);
        
        if (marker != null)
            setTokenMarker(marker);
        
        addMouseWheelListener(this);
            
        // Set the font
        getPainter().setFont(SwingUtil.getPreferredMonoFont());
        
        // Set the tab size
        getDocument().putProperty(
            PlainDocument.tabSizeAttribute, new Integer(tabSize_));

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
     * @return Number of lines to scroll on mouse wheel activity
     */
    public int getMouseWheelUnit()
    {
        return mouseWheelUnit_;
    }
    
    /**
     * Sets the number of lines to scroll on mouse wheel activity
     * 
     * @param mouseWheelUnit  Number of lines
     */
    public void setMouseWheelUnit(int mouseWheelUnit)
    {
        mouseWheelUnit_ = mouseWheelUnit;
    }

    //--------------------------------------------------------------------------
    // MouseWheelListener Interface
    //--------------------------------------------------------------------------
    
    public void mouseWheelMoved(MouseWheelEvent mouseWheelEvent)
    {
        if (mouseWheelEvent.getScrollAmount() == 0)
            return;
            
        vertical.setValue(
            vertical.getValue() + mouseWheelUnit_ * 
                mouseWheelEvent.getWheelRotation());
    }
}