/*
 * @ 2003 Daniel C. Tofan daniel@danieltofan.org
 */
package toolbox.util.ui;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;

/**
 * Extends <code>javax.swing.ButtonGroup</code> to provide methods that allow
 * working with button references instead of button models.
 * 
 * @author Daniel Tofan
 * @version 1.0 April 2003
 * @see ButtonGroup
 */
public class JButtonGroup extends ButtonGroup
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Stores a reference to the currently selected button in the group.
     */
    private AbstractButton selectedButton_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates an empty <code>JButtonGroup</code>.
     */
    public JButtonGroup()
    {
    }

    
    /**
     * Creates a <code>JButtonGroup</code> object from an array of buttons
     * and adds the buttons to the group No button will be selected initially.
     * 
     * @param buttons Array of <code>AbstractButton</code>s
     */
    public JButtonGroup(AbstractButton[] buttons)
    {
        add(buttons);
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Adds a button to the group.
     * 
     * @param button <code>AbstractButton</code> reference.
     */
    public void add(AbstractButton button)
    {
        if (button == null || buttons.contains(button))
            return;
        
        super.add(button);
        
        if (getSelection() == button.getModel())
            selectedButton_ = button;
    }

    
    /**
     * Adds an array of buttons to the group.
     * 
     * @param buttons Array of <code>AbstractButton</code>s
     */
    public void add(AbstractButton[] buttons)
    {
        if (buttons == null)
            return;
        
        for (int i = 0; i < buttons.length; i++)
            add(buttons[i]);
    }

    
    /**
     * Removes a button from the group.
     * 
     * @param button Button to be removed
     */
    public void remove(AbstractButton button)
    {
        if (button != null)
        {
            if (selectedButton_ == button)
                selectedButton_ = null;
            
            super.remove(button);
        }
    }

    
    /**
     * Removes all the buttons in the array from the group.
     * 
     * @param buttons Array of <code>AbstractButton</code>s
     */
    public void remove(AbstractButton[] buttons)
    {
        if (buttons == null)
            return;
        
        for (int i = 0; i < buttons.length; i++)
            remove(buttons[i]);
    }

    
    /**
     * Sets the selected button in the group Only one button in the group can
     * be selected.
     * 
     * @param button <code>AbstractButton</code> reference.
     * @param selected <code>boolean</code> representing the selection state of 
     *        the button.
     */
    public void setSelected(AbstractButton button, boolean selected)
    {
        if (button != null) 
            setSelected(button.getModel(), selected);
    }
    
    
    /**
     * Sets the selected button model in the group.
     * 
     * @param model <code>ButtonModel</code> reference.
     * @param selected <code>boolean</code> representing the selection state of 
     *        the button
     */
    public void setSelected(ButtonModel model, boolean selected)
    {
        AbstractButton button = getButton(model);
        
        if (buttons.contains(button))
        {
            super.setSelected(model, selected);
            
            if (getSelection() == button.getModel())
                selectedButton_ = button;
        }
    }

    
    /**
     * Returns the <code>AbstractButton</code> whose <code>ButtonModel</code>
     * is given. If the model does not belong to a button in the group, returns
     * null.
     * 
     * @param model <code>ButtonModel</code> that should belong to a button in 
     *        the group
     * @return <code>AbstractButton</code> reference whose model is 
     *         <code>model</code> if the button belongs to the group, 
     *         <code>null</code> otherwise.
     */
    public AbstractButton getButton(ButtonModel model)
    {
        Iterator it = buttons.iterator();
        
        while (it.hasNext())
        {
            AbstractButton ab = (AbstractButton) it.next();
            
            if (ab.getModel() == model)
                return ab;
        }
        
        return null;
    }

    
    /**
     * Returns the selected button in the group.
     * 
     * @return Reference to the currently selected button in the group or
     *         <code>null</code> if no button is selected.
     */
    public AbstractButton getSelected()
    {
        return selectedButton_;
    }

    
    /**
     * Returns whether the button is selected.
     * 
     * @param button <code>AbstractButton</code> reference.
     * @return <code>true</code> if the button is selected, <code>false</code>
     *         otherwise.
     */
    public boolean isSelected(AbstractButton button)
    {
        return button == selectedButton_;
    }

    
    /**
     * Returns the buttons in the group as a <code>List</code>.
     * 
     * @return <code>List</code> containing the buttons in the group, in the 
     *         order they were added to the group.
     */
    public List getButtons()
    {
        return Collections.unmodifiableList(buttons);
    }

    
    /**
     * Checks whether the group contains the given button.
     * 
     * @param button Abstract button reference.
     * @return <code>True</code> if the button is contained in the group,
     *         <code>false</code> otherwise.
     */
    public boolean contains(AbstractButton button)
    {
        return buttons.contains(button);
    }
}