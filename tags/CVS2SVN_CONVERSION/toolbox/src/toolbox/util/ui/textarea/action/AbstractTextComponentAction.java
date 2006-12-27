package toolbox.util.ui.textarea.action;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.text.JTextComponent;

/**
 * Abstract base class for textcomponent actions. Just provides a convenient
 * setter/getter for the textcomponent at this point.
 */
public abstract class AbstractTextComponentAction extends AbstractAction
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Target text component.
     */
    private JTextComponent textComponent_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a AbstractTextComponentAction.
     * 
     * @param textComponent Text component that is the target of this action.
     */
    public AbstractTextComponentAction(JTextComponent textComponent)
    {
        setTextComponent(textComponent);
    }
    
    
    /**
     * Creates a AbstractTextComponentAction.
     * 
     * @param textComponent Text component.
     * @param name Label of the action target.
     */
    public AbstractTextComponentAction(
        JTextComponent textComponent, 
        String name)
    {
        super(name);
        setTextComponent(textComponent);
    }

    
    /**
     * Creates a AbstractTextComponentAction.
     * 
     * @param textComponent Text component.
     * @param name Label of the action target.
     * @param icon Icon.
     */
    public AbstractTextComponentAction(
        JTextComponent textComponent,
        String name, 
        Icon icon)
    {
        super(name, icon);
        setTextComponent(textComponent);
    }

    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------
    
    /**
     * Returns the textComponent.
     * 
     * @return JTextComponent
     */
    protected JTextComponent getTextComponent()
    {
        return textComponent_;
    }
    
    //--------------------------------------------------------------------------
    // Private
    //--------------------------------------------------------------------------
    
    /**
     * Sets the textComponent.
     * 
     * @param textComponent The textComponent to set.
     */
    private void setTextComponent(JTextComponent textComponent)
    {
        textComponent_ = textComponent;
    }
}