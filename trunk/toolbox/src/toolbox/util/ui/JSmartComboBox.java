package toolbox.util.ui;

import java.awt.Graphics;
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;

import toolbox.util.SwingUtil;

/**
 * 
 */
public class JSmartComboBox extends JComboBox implements AntiAliased
{

    /**
     * 
     */
    public JSmartComboBox()
    {
        super();
    }

    /**
     * @param items
     */
    public JSmartComboBox(Object[] items)
    {
        super(items);
    }

    /**
     * @param items
     */
    public JSmartComboBox(Vector items)
    {
        super(items);
    }

    /**
     * @param aModel
     */
    public JSmartComboBox(ComboBoxModel aModel)
    {
        super(aModel);
    }

    //--------------------------------------------------------------------------
    // AntiAliased Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.ui.AntiAliased#isAntiAlias()
     */
    public boolean isAntiAlias()
    {
        return SwingUtil.isAntiAliasGlobal();
    }

    /**
     * @see toolbox.util.ui.AntiAliased#setAntiAlias(boolean)
     */
    public void setAntiAlias(boolean b)
    {
    }
    
    //--------------------------------------------------------------------------
    // Overrides JComponent
    //--------------------------------------------------------------------------

    /**
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    public void paintComponent(Graphics gc)
    {
        SwingUtil.setAntiAlias(gc, isAntiAlias());
        super.paintComponent(gc);
    }
}
