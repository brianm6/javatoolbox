package toolbox.util.ui.table;

import java.awt.Graphics;

import javax.swing.table.DefaultTableCellRenderer;

import toolbox.util.SwingUtil;
import toolbox.util.ui.AntiAliased;

/**
 * 
 */
public class SmartDefaultTableCellRenderer extends DefaultTableCellRenderer
    implements AntiAliased
{

    /**
     * 
     */
    public SmartDefaultTableCellRenderer()
    {
        super();
    }

    //--------------------------------------------------------------------------
    // AntiAliased Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.ui.AntiAliased#isAntiAlias()
     */
    public boolean isAntiAliased()
    {
        return SwingUtil.isAntiAliased();
    }

    /**
     * @see toolbox.util.ui.AntiAliased#setAntiAlias(boolean)
     */
    public void setAntiAliased(boolean b)
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
        SwingUtil.makeAntiAliased(gc, isAntiAliased());
        super.paintComponent(gc);
    }
}
