package toolbox.util.ui;

import javax.swing.Action;
import javax.swing.JMenuItem;

import toolbox.util.Assert;

/**
 * JMenu that works like a conveyor belt. New items get inserted at the top
 * of the menu and items get pushed off the bottom of the menu when the 
 * capacity is reached.
 */
public class JConveyorMenu extends JSmartMenu
{
    /** 
     * Max number of items that can be displayed by the menu. 
     */
    private int capacity_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a JConveyorMenu.
     * 
     * @param capacity Max number of menu items allowed in the menu before
     *        items at the bottom of the menu start getting pushed off.
     */
    public JConveyorMenu(int capacity)
    {
        this("", capacity);
    }

    
    /**
     * Creates a JConveyorMenu.
     * 
     * @param title Menu title
     * @param capacity Max number of menu items allowed in the menu before
     *        items at the bottom of the menu start getting pushed off.
     */
    public JConveyorMenu(String title, int capacity)
    {
        super(title);
        capacity_ = capacity;    
    }
    
    //--------------------------------------------------------------------------
    // Public 
    //--------------------------------------------------------------------------
    
    /**
     * Sets the max capacity of the popup menu.
     * 
     * @param capacity Capacity > 0
     */
    public void setCapacity(int capacity)
    {
        Assert.isTrue(capacity > 0, "Capacity must be > 0");
        capacity_ = capacity;    
    }
    
    //--------------------------------------------------------------------------
    // Overrides javax.swing.JMenu
    //--------------------------------------------------------------------------
        
    /**
     * @see javax.swing.JMenu#add(javax.swing.Action)
     */
    public JMenuItem add(Action action)
    {
        insert(new JSmartMenuItem(action), 0);
        
        if (getItemCount() > capacity_)
            remove(capacity_);
        
        return getItem(0);
    }
}