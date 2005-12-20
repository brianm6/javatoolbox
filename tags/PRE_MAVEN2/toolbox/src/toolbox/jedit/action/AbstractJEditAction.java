package toolbox.jedit.action;

import javax.swing.AbstractAction;
import javax.swing.Icon;

import toolbox.jedit.JEditTextArea;

/**
 * Abstract base class for all JEdit actions. 
 */
public abstract class AbstractJEditAction extends AbstractAction
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Text area that is the target of this action. 
     */
    protected JEditTextArea area_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates an AbstractJEditAction.
     * 
     * @param area Target text area.
     */
    public AbstractJEditAction(JEditTextArea area)
    {
        area_ = area;
    }
    
    
    /**
     * Creates an AbstractJEditAction.
     * 
     * @param label Action label.
     * @param area Target text area.
     */
    public AbstractJEditAction(String label, JEditTextArea area)
    {
        super(label);
        area_ = area;
    }
    
    
    /**
     * Creates an AbstractJEditAction.
     * 
     * @param label Action label.
     * @param icon Action's icon.
     * @param area Target text area.
     */
    public AbstractJEditAction(String label, Icon icon, JEditTextArea area)
    {
        super(label, icon);
        area_ = area;
    }
}