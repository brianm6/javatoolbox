package toolbox.util.ui;

import javax.swing.Action;
import javax.swing.JMenuItem;

import org.apache.commons.lang.Validate;

/**
 * JMenu that works like a conveyor belt. New items get inserted at the top of
 * the menu and items get pushed off the bottom of the menu when the capacity
 * is reached.
 */
public class JConveyorMenu extends JSmartMenu
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /** 
     * Max number of items that can be displayed by the menu. Must be a positive
     * integer greater than zero. 
     */
    private int capacity_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a JConveyorMenu.
     * 
     * @param capacity Max number of menu items allowed in the menu before
     *        items at the bottom of the menu start getting bumped off.
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
        setCapacity(capacity);    
    }
    
    //--------------------------------------------------------------------------
    // Public 
    //--------------------------------------------------------------------------
    
    /**
     * Sets the max capacity of the popup menu.
     * 
     * @param capacity Max number of items the menu can hold.
     */
    public void setCapacity(int capacity)
    {
        Validate.isTrue(capacity > 0, "Capacity must be > 0");
        capacity_ = capacity;    
    }
    
    //--------------------------------------------------------------------------
    // Overrides javax.swing.JMenu
    //--------------------------------------------------------------------------
        
    /**
     * Wraps the given Action in a menuitem and adds via add(JMenuItem).
     * 
     * @see #add(JMenuItem)
     * @see javax.swing.JMenu#add(javax.swing.Action)
     */
    public JMenuItem add(Action action)
    {
        return add(new JSmartMenuItem(action));
    }
    
    
    /**
     * Bump the oldest menu item off if a new one is added and we're at 
     * capacity.
     * 
     * @see javax.swing.JMenu#add(javax.swing.JMenuItem)
     */
    public JMenuItem add(JMenuItem menuItem)
    {
        insert(menuItem, 0);
        
        if (getItemCount() > capacity_)
            remove(capacity_);
        
        return getItem(0);
    }
}