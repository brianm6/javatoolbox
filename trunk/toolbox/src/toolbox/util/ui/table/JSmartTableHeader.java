package toolbox.util.ui.table;

import java.awt.Graphics;

import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

import toolbox.util.SwingUtil;

/**
 * 
 */
public class JSmartTableHeader extends JTableHeader
{

    /**
     * 
     */
    public JSmartTableHeader()
    {
        super();
    }

    /**
     * @param cm
     */
    public JSmartTableHeader(TableColumnModel cm)
    {
        super(cm);
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
