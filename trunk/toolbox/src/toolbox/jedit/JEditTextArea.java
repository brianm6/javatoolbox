package toolbox.jedit;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.text.PlainDocument;

import org.apache.log4j.Logger;
import org.jedit.syntax.TextAreaDefaults;
import org.jedit.syntax.TokenMarker;

import toolbox.util.SwingUtil;

/**
 * Modified JEditTextArea that supports a mouse wheel, tab size, and font
 */
public class JEditTextArea extends org.jedit.syntax.JEditTextArea
    implements MouseWheelListener
{
    private static final Logger logger_ = 
        Logger.getLogger(JEditTextArea.class);
        
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
            "C+A", new JEditTextAreaPopupMenu.SelectAllAction(this));
            
        getInputHandler().addKeyBinding(
            "C+V", new JEditTextAreaPopupMenu.PasteAction(this));
            
        getInputHandler().addKeyBinding(
            "C+C", new JEditTextAreaPopupMenu.CopyAction(this));

        getInputHandler().addKeyBinding(
            "C+X", new JEditTextAreaPopupMenu.CutAction(this));
            
        getInputHandler().addKeyBinding(
            "C+O", new JEditTextAreaPopupMenu.InsertFileAction(this));
            
        getInputHandler().addKeyBinding(
            "C+S", new JEditTextAreaPopupMenu.SaveAsAction(this));
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
    // Interface java.awt.event.MouseWheelListener 
    //--------------------------------------------------------------------------
    
    /**
     * Handle wheel
     */
    public void mouseWheelMoved(MouseWheelEvent mouseWheelEvent)
    {
        if (mouseWheelEvent.getScrollAmount() == 0)
            return;
            
        vertical.setValue(
            vertical.getValue() + mouseWheelUnit_ * 
                mouseWheelEvent.getWheelRotation());
    }
}