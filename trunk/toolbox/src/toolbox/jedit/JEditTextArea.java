package toolbox.jedit;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.text.PlainDocument;

import org.jedit.syntax.TextAreaDefaults;

import toolbox.util.SwingUtil;

/**
 * Modified JEditTextArea that supports a mouse wheel, tab size, and font
 */
public class JEditTextArea extends org.jedit.syntax.JEditTextArea
    implements MouseWheelListener
{
    private int mouseWheelUnit_ = 3;
    private int tabSize_ = 4;

    //--------------------------------------------------------------------------
    // Constructors 
    //--------------------------------------------------------------------------
        
    /**
     * Default constructor
     */
    public JEditTextArea()
    {
        this(new TextAreaDefaults());
    }
    
    /**
     * Creates a JEditTextArea with the given defaults
     * 
     * @param textAreaDefaults  Defaults
     */
    public JEditTextArea(TextAreaDefaults textAreaDefaults)
    {
        super(textAreaDefaults);
        addMouseWheelListener(this);
        
        // Set the font
        getPainter().setFont(SwingUtil.getPreferredMonoFont());
        
        // Set the tab size
        getDocument().putProperty(
            PlainDocument.tabSizeAttribute, new Integer(tabSize_)); 
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