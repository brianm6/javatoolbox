package toolbox.util.ui.tabbedpane;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import org.apache.log4j.Logger;

import toolbox.util.ArrayUtil;
import toolbox.util.SwingUtil;
import toolbox.util.ui.AntiAliased;
import toolbox.util.ui.ImageCache;

/**
 * JSmartTabbedPane adds the following behavior to the default JTabbedPane.
 * <p>
 * <ul>
 *   <li>Support for antialiased text.
 *   <li>Icon on the tab itself that removes the tab.
 *   <li>Notification the tab is about to removed.
 * </ul>
 * 
 * @see SmartTabbedPaneListener
 */
public class JSmartTabbedPane extends JTabbedPane implements AntiAliased
{
    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------
    
    private static final Logger logger_ =
        Logger.getLogger(JSmartTabbedPane.class);
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * Antialiased flag.
     */
    private boolean antiAliased_ = SwingUtil.getDefaultAntiAlias();

    /**
     * Array of listeners interested in JSmartTabbedPane generated events.
     */
    private SmartTabbedPaneListener[] listeners_;

    /**
     * If true, an icon will appear on each tab allowing the tab to be removed.
     */
    private boolean closeable_;

    /**
     * X icon that shows up on the tabs if the tab is removable.
     */
    private Icon closeIcon_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a JSmartTabbedPane whose tabs are not removable via an icon on
     * the tab.
     */
    public JSmartTabbedPane()
    {
        this(false);
    }

    
    /**
     * Creates a JSmartTabbedPane.
     *
     * @param closeable True to make tabs removable via an icon on the tab
     *        itself or false otherwise.
     */
    public JSmartTabbedPane(boolean closeable)
    {
        this(JTabbedPane.TOP, closeable);
    }

    
    /**
     * Creates a JSmartTabbedPane.
     * 
     * @param tabPlacement Tab placement.
     */
    public JSmartTabbedPane(int tabPlacement)
    {
        this(tabPlacement, false);
    }


    /**
     * Creates a JSmartTabbedPane.
     * 
     * @param tabPlacement Tab placement.
     * @param closeable True to make tabs removable via an icon on the tab
     *        itself or false otherwise.
     */
    public JSmartTabbedPane(int tabPlacement, boolean closeable)
    {
        super(tabPlacement);
        setUI(new SmartTabbedPaneUI(SwingConstants.LEFT));
        listeners_ = new SmartTabbedPaneListener[0];
        
        closeable_ = closeable;
        
        if (closeable_)
        {    
            addMouseListener(new MouseListener());
            closeIcon_ = ImageCache.getIcon(ImageCache.IMAGE_CROSS);
        }
    }

    
    /**
     * Creates a JSmartTabbedPane.
     * 
     * @param tabPlacement Tab placement.
     * @param tabLayoutPolicy Tab layout policy.
     */
    public JSmartTabbedPane(int tabPlacement, int tabLayoutPolicy)
    {
        super(tabPlacement, tabLayoutPolicy);
        setUI(new SmartTabbedPaneUI(SwingConstants.LEFT));
        //addMouseListener(new MouseListener());
        listeners_ = new SmartTabbedPaneListener[0];
    }

    //--------------------------------------------------------------------------
    // Overrides JTabbedPane
    //--------------------------------------------------------------------------

    /**
     * @see javax.swing.JTabbedPane#addTab(java.lang.String, java.awt.Component)
     */
    public void addTab(String title, Component component)
    {
        addTab(title, component, closeIcon_);
    }
    
    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------
    
    /***
     * Adds a tab to the last position in the tabbed pane.
     * 
     * @param title Tab title.
     * @param component Component to embed in the tab panel.
     * @param extraIcon Icon for display on the tab that will close the tab.
     */
    protected void addTab(String title, Component component, Icon extraIcon)
    {
        super.addTab(
            title, 
            (extraIcon == null ? null : new SmartTabbedPaneIcon(extraIcon)), 
            component);
    }

    //--------------------------------------------------------------------------
    // Event Support
    //--------------------------------------------------------------------------

    /**
     * Adds a tabbed pane listener.
     * 
     * @param listener Listener to add.
     */
    public void addSmartTabbedPaneListener(SmartTabbedPaneListener listener)
    {
        listeners_ = 
            (SmartTabbedPaneListener[]) 
                ArrayUtil.add(listeners_, listener);
    }


    /**
     * Removes a tabbed pane listener.
     * 
     * @param listener Listener to remove.
     */
    public void removeSmartTabbedPaneListener(SmartTabbedPaneListener listener)
    {
        listeners_ = 
            (SmartTabbedPaneListener[]) 
                ArrayUtil.remove(listeners_, listener);
    }


    /**
     * Fires notification to all listeners that a tab is about to be close
     * via the close icon on the tab.
     * 
     * @param tabIndex Zero based index of tab being closed.
     */
    protected void fireTabClosing(int tabIndex)
    {
        for (int i = 0; 
             i < listeners_.length; 
             listeners_[i++].tabClosing(this, tabIndex));        
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
    
    //--------------------------------------------------------------------------
    // MouseListener
    //--------------------------------------------------------------------------
    
    /**
     * Listens for a click in the close icon and then removes the tab.
     */    
    class MouseListener extends MouseAdapter
    {
        /**
         * @see java.awt.event.MouseListener#mouseClicked(
         *      java.awt.event.MouseEvent)
         */
        public void mouseClicked(MouseEvent e)
        {
            int tabNumber = getUI().tabForCoordinate(
                JSmartTabbedPane.this, e.getX(), e.getY());
        
            if (tabNumber >= 0)
            {
                String title = getTitleAt(tabNumber);
                
                Rectangle rect = ((SmartTabbedPaneIcon) 
                    getIconAt(tabNumber)).getBounds();
                
                if (rect.contains(e.getX(), e.getY()))
                {
                    fireTabClosing(tabNumber);
                    
                    // if the tab was removed by the listener, make sure we
                    // don't try to remove it again. first make sure the tab
                    // index is still valid and then double that the title is
                    // still the same as it was before fireTabClosing()
                    
                    if (getTabCount() > tabNumber)
                        if (getTitleAt(tabNumber).equals(title))
                            removeTabAt(tabNumber);
                }
            }
        }
    }
}