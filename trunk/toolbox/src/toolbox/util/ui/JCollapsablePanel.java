package toolbox.util.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JToolBar;

import org.apache.log4j.Logger;

/**
 * Extension of JHeaderPanel that allows the panels contents to be collapsed
 * using a button in the header.
 */
public class JCollapsablePanel extends JHeaderPanel
{
    private static final Logger logger_ = 
        Logger.getLogger(JCollapsablePanel.class);
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Collapsed flag.
     */
    private boolean collapsed_;
    
    /**
     * Since the content of the panel is removed when collapsed, it is saved
     * here so that it is available when expanded again.
     */
    private Component savedContent_;
    
    /**
     * Two state button that expands/collapsed the content of the header panel.
     */
    private JButton toggleButton_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a JCollapsablePanel.
     * 
     * @param title Header title.
     */
    public JCollapsablePanel(String title)
    {
        super(title);
        makeCollapsable();
    }

    
    /**
     * Creates a JCollapsablePanel.
     * 
     * @param icon Header icon.
     * @param title Header title.
     */
    public JCollapsablePanel(Icon icon, String title)
    {
        super(icon, title);
    }


    /**
     * Creates a JCollapsablePanel.
     * 
     * @param title Header title.
     * @param bar Header toolbar.
     * @param content Header content.
     */
    public JCollapsablePanel(String title, JToolBar bar, JComponent content)
    {
        super(title, bar, content);
    }


    /**
     * Creates a JCollapsablePanel.
     * 
     * @param icon Header icon.
     * @param title Header title.
     * @param bar Header toolbar.
     * @param content Header content.
     */
    public JCollapsablePanel(Icon icon, String title, JToolBar bar,
        JComponent content)
    {
        super(icon, title, bar, content);
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Returns the collapsed.
     * 
     * @return boolean
     */
    public boolean isCollapsed()
    {
        return collapsed_;
    }
    
    
    /**
     * Sets the collapsed.
     * 
     * @param collapsed The collapsed to set.
     */
    public void setCollapsed(boolean collapsed)
    {
        collapsed_ = collapsed;
    }
    
    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------
    
    /**
     * Makes the panel collapsable.
     */
    protected void makeCollapsable()
    {
        toggleButton_ = createButton(new CollapseAction());
        
        JToolBar tb = createToolBar();
        tb.add(toggleButton_);
        setToolBar(tb);
        setCollapsed(false);
    }


    /**
     * Gets the appriate icon for the current collapsed state.
     * 
     * @return Icon
     */
    protected Icon getCollapsedIcon()
    {
        return 
            (collapsed_ ? 
                ImageCache.getIcon(ImageCache.IMAGE_DOUBLE_ARROW_DOWN) :
                ImageCache.getIcon(ImageCache.IMAGE_DOUBLE_ARROW_UP));
        
    }
    
    //--------------------------------------------------------------------------
    // CollapseAction
    //--------------------------------------------------------------------------
    
    /**
     * Collapses and expands the panel.
     */
    class CollapseAction extends AbstractAction
    {
        /**
         * Creates a CollapseAction.
         */
        public CollapseAction()
        {
            super(null, getCollapsedIcon());
        }
        
        
        /**
         * @see java.awt.event.ActionListener#actionPerformed(
         *      java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e)
        {
            if (collapsed_)
            {
                setContent(savedContent_);
            }
            else
            {
                savedContent_ = getContent();
                setContent(new NullComponent());
            }

            setCollapsed(!isCollapsed());
            toggleButton_.setIcon(getCollapsedIcon());
            toggleButton_.setRolloverIcon(getCollapsedIcon());

            revalidate();
            ((JComponent) getParent()).revalidate();
        }
    }

    //--------------------------------------------------------------------------
    // NullComponent
    //--------------------------------------------------------------------------
    
    /**
     * Empty component used as the content when the panel is collapsed.
     */
    class NullComponent extends Component
    {
    }
}