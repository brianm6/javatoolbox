package toolbox.util.ui.list;

import java.awt.Graphics;
import java.util.Vector;

import javax.swing.JList;
import javax.swing.ListModel;

import toolbox.util.SwingUtil;
import toolbox.util.ui.AntiAliased;

/**
 * JSmartList extends JList by adding the following behavior.
 * <p>
 * <ul>
 *   <li>Antialised text
 *   <li>Ability to scroll to the top of the list - scrollToTop()
 *   <li>Ability to scroll to the bottom of the list - scrollToBottom()
 * </ul>
 */
public class JSmartList extends JList implements AntiAliased
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
     * Creates a JSmartList.
     */
    public JSmartList()
    {
    }


    /**
     * Creates a JSmartList.
     * 
     * @param listData List entries.
     */
    public JSmartList(Object[] listData)
    {
        super(listData);
    }


    /**
     * Creates a JSmartList.
     * 
     * @param listData List entries.
     */
    public JSmartList(Vector listData)
    {
        super(listData);
    }


    /**
     * Creates a JSmartList.
     * 
     * @param dataModel List model.
     */
    public JSmartList(ListModel dataModel)
    {
        super(dataModel);
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Scrolls to the top of the list.
     */
    public void scrollToTop()
    {
        ensureIndexIsVisible(0);
    }

    
    /**
     * Scrolls to the end of the list.
     */
    public void scrollToBottom()
    {
        ensureIndexIsVisible(getModel().getSize() - 1);
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
     * Activates antialiasing.
     * 
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    public void paintComponent(Graphics gc)
    {
        SwingUtil.makeAntiAliased(gc, isAntiAliased());
        super.paintComponent(gc);
    }
}