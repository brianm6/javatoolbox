package toolbox.util.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JToggleButton;
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
     * Since the content of the panel is removed when collapsed, it is saved
     * here so that it is available when expanded again.
     */
    private Component savedContent_;
    
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
    // Protected
    //--------------------------------------------------------------------------
    
    /**
     * Makes the panel collapsable.
     */
    protected void makeCollapsable()
    {
        JToggleButton toggle = 
            createToggleButton(
                ImageCache.getIcon(ImageCache.IMAGE_TRIANGLE),
                "Collapse", 
                new CollapseAction());
        
        JToolBar tb = createToolBar();
        tb.add(toggle);
        setToolBar(tb);
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
         * @see java.awt.event.ActionListener#actionPerformed(
         *      java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e)
        {
            if (getContent() instanceof NullComponent)
            {
                logger_.debug("Expanding...");
                setContent(savedContent_);
            }
            else
            {
                logger_.debug("Collapsing...");
                savedContent_ = getContent();
                setContent(new NullComponent());
            }
            
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