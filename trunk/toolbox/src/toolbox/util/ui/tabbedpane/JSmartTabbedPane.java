package toolbox.util.ui.tabbedpane;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

/***
 * A JTabbedPane which has an icon on each tab to close the tab.
 */
public class JSmartTabbedPane extends JTabbedPane
{
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a JSmartTabbedPane
     */
    public JSmartTabbedPane()
    {
        //setUI(new SmartTabbedPaneUI(SwingConstants.RIGHT));
        addMouseListener(new MouseListener());
    }

    //--------------------------------------------------------------------------
    // Overrides JTabbedPane
    //--------------------------------------------------------------------------

    /**
     * @see javax.swing.JTabbedPane#addTab(java.lang.String, java.awt.Component)
     */
    public void addTab(String title, Component component)
    {
        addTab(title, component, null);
    }
    
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /***
     * Adds a panel
     * 
     * @param pJPanel the panel
     * @param title the title
     */
    public void addPanel(JPanel pJPanel, String title)
    {
        add(pJPanel, title);
    }
    
    /***
     * Adds a tab
     * 
     * @param title title
     * @param component component
     * @param extraIcon icon
     */
    public void addTab(String title, Component component, Icon extraIcon)
    {
        super.addTab(title, new SmartTabbedPaneIcon(extraIcon), component);
        
        // TODO: move this somewhere where it is only executed once
        setUI(new SmartTabbedPaneUI(SwingConstants.LEFT));
    }

    //--------------------------------------------------------------------------
    // Inner Classes
    //--------------------------------------------------------------------------
    
    /**
     * Listens for a click in the close icon and then removes the tab.
     */    
    class MouseListener extends MouseAdapter
    {
        public void mouseClicked(MouseEvent e)
        {
            int tabNumber = getUI().tabForCoordinate(
                JSmartTabbedPane.this, e.getX(), e.getY());
        
            if (tabNumber >= 0)
            {
                Rectangle rect = ((SmartTabbedPaneIcon) 
                    getIconAt(tabNumber)).getBounds();
                
                if (rect.contains(e.getX(), e.getY()))
                   removeTabAt(tabNumber);
            }
        }
    }
}