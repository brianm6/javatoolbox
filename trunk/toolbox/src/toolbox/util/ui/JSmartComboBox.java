package toolbox.util.ui;

import java.awt.Graphics;
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;

import toolbox.util.SwingUtil;
import toolbox.util.ui.list.SmartListCellRenderer;

/**
 * JSmartComboBox adds the following behavior.
 * <p>
 * <ul>
 *   <li>Support for antialised text
 * </ul>
 * 
 * @see SmartListCellRenderer
 */
public class JSmartComboBox extends JComboBox implements AntiAliased
{
    /**
     * Antialiased flag
     */
    private boolean antiAliased_ = SwingUtil.getDefaultAntiAlias();

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a JSmartComboBox.
     */
    public JSmartComboBox()
    {
        init();
    }


    /**
     * Creates a JSmartComboBox.
     * 
     * @param items Dropdown items
     */
    public JSmartComboBox(Object[] items)
    {
        super(items);
        init();
    }


    /**
     * Creates a JSmartComboBox.
     * 
     * @param items Dropdown items
     */
    public JSmartComboBox(Vector items)
    {
        super(items);
        init();
    }


    /**
     * Creates a JSmartComboBox.
     * 
     * @param aModel Combobox model
     */
    public JSmartComboBox(ComboBoxModel aModel)
    {
        super(aModel);
        init();
    }

    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------
    
    /**
     * Initializes the combobox by setting the list cell renderer to a
     * SmartListCellRenderer.
     */
    protected void init()
    {
        setRenderer(new SmartListCellRenderer());
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
     * @see toolbox.util.ui.AntiAliased#setAntiAlias(boolean)
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