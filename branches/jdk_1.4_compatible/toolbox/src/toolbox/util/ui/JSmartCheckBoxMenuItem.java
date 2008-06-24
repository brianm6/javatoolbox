package toolbox.util.ui;

import java.awt.Graphics;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;

import toolbox.util.SwingUtil;

/**
 * JSmartCheckBoxMenuItem adds the following behavior.
 * <p>
 * <ul>
 *   <li>Support for antialised text
 * </ul>
 */
public class JSmartCheckBoxMenuItem extends JCheckBoxMenuItem
    implements AntiAliased
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Antialiased flag.
     */
    private boolean antiAliased_ = SwingUtil.getDefaultAntiAlias();

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a JSmartCheckBoxMenuItem.
     */
    public JSmartCheckBoxMenuItem()
    {
    }


    /**
     * Creates a JSmartCheckBoxMenuItem.
     * 
     * @param text Checkbox text.
     */
    public JSmartCheckBoxMenuItem(String text)
    {
        super(text);
    }


    /**
     * Creates a JSmartCheckBoxMenuItem.
     * 
     * @param text Checkbox text.
     * @param b Checked state.
     */
    public JSmartCheckBoxMenuItem(String text, boolean b)
    {
        super(text, b);
    }


    /**
     * Creates a JSmartCheckedBoxMenuItem.
     * 
     * @param a Action activated when the check box is toggled.
     */
    public JSmartCheckBoxMenuItem(Action a)
    {
        super(a);
    }


    /**
     * Creates a JSmartCheckBoxMenuItem.
     * 
     * @param icon Checkbox icon.
     */
    public JSmartCheckBoxMenuItem(Icon icon)
    {
        super(icon);
    }


    /**
     * Creates a JSmartCheckBoxMenuItem.
     * 
     * @param text Checkbox text.
     * @param icon Checkbox icon.
     */
    public JSmartCheckBoxMenuItem(String text, Icon icon)
    {
        super(text, icon);
    }


    /**
     * Creates a JSmartCheckBoxMenuItem.
     * 
     * @param text Checkbox text.
     * @param icon Checkbox icon.
     * @param b Checked state.
     */
    public JSmartCheckBoxMenuItem(String text, Icon icon, boolean b)
    {
        super(text, icon, b);
    }

    
    /**
     * Creates a JSmartCheckedBoxMenuItem.
     * 
     * @param action Action activated when the check box is toggled.
     * @param propertyChangeEventSource The source of the property change event
     *        that can toggle this check box.
     * @param property The name of the property from the property change event
     *        source that can toggle this check box.
     */
    public JSmartCheckBoxMenuItem(
        Action action, 
        JComponent propertyChangeEventSource,
        String property)
    {
        super(action);
        toggleOnProperty(propertyChangeEventSource, property);
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Toggles the state of the checkbox based on a property change event
     * originating from a component.
     * 
     * @param comp Component generating the property change event.
     * @param property Property name to listen for.
     */
    public void toggleOnProperty(JComponent comp, String property)
    {
        comp.addPropertyChangeListener(property, new PropertyChangeListener() 
        {
            public void propertyChange(PropertyChangeEvent evt)
            {
                setSelected(((Boolean) evt.getNewValue()).booleanValue());
            }
        });
    }
    
    //--------------------------------------------------------------------------
    // AntiAliased Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.ui.AntiAliased#isAntiAliased()
     */
    public boolean isAntiAliased()
    {
        return antiAliased_;
    }

    
    /**
     * @see toolbox.util.ui.AntiAliased#setAntiAliased(boolean)
     */
    public void setAntiAliased(boolean b)
    {
        antiAliased_ = b;
    }
    
    //--------------------------------------------------------------------------
    // Overrides JComponent
    //--------------------------------------------------------------------------

    /**
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    public void paintComponent(Graphics gc)
    {
        SwingUtil.makeAntiAliased(gc, isAntiAliased());
        super.paintComponent(gc);
    }
}