package toolbox.util.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import nu.xom.Element;

import org.apache.log4j.Logger;

import toolbox.util.PreferencedUtil;
import toolbox.util.XOMUtil;
import toolbox.workspace.IPreferenced;

/**
 * Extension of JHeaderPanel that allows the panels contents to be collapsed
 * using a button in the header.
 * <p>
 * Supports persistence of the following properties:
 * <ul>
 *   <li>Collapsed state
 * </ul>
 */
public class JCollapsablePanel extends JHeaderPanel implements IPreferenced
{
    private static final Logger logger_ = 
        Logger.getLogger(JCollapsablePanel.class);
    
    //--------------------------------------------------------------------------
    // Javabean Constants
    //--------------------------------------------------------------------------

    public static final String PROPERTY_COLLAPSED = "collapsed";
    
    //--------------------------------------------------------------------------
    // Preferenced Constants
    //--------------------------------------------------------------------------
    
    public static final String NODE_JCOLLAPSABLEPANEL = "JCollapsablePanel";
    
    public static final String[] SAVED_PROPERTIES = 
        new String[] {PROPERTY_COLLAPSED};
    
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
        makeCollapsable();        
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
        makeCollapsable();        
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
        makeCollapsable();        
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Returns true if the panel is collapsed, false otherwise.
     * 
     * @return boolean
     */
    public boolean isCollapsed()
    {
        return collapsed_;
    }
    
    
    /**
     * Sets the collapsed state of the panel.
     * 
     * @param collapsed True to collapse the panel, false to expand the panel.
     */
    public void setCollapsed(boolean collapsed)
    {
        if (collapsed != isCollapsed())
            toggle(); 
        
        firePropertyChange(PROPERTY_COLLAPSED, !collapsed, collapsed);
    }

    
    /**
     * Toggles the collapsed state of the panel.
     */
    public void toggle()
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

        collapsed_ = !collapsed_;
        toggleButton_.setIcon(getCollapsedIcon());
        toggleButton_.setRolloverIcon(getCollapsedIcon());

        revalidate();
        ((JComponent) getParent()).revalidate();
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
        
        JPanel titleBar = getGradientPanel();

        //
        // Wire a doubleclick on the title bar to toggle the panel
        //
        titleBar.addMouseListener(new MouseAdapter()
        {
            public void mouseClicked(MouseEvent e)
            {
                if (e.getClickCount() == 2)
                    toggle();
            }
        });
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
    // IPreferenced Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.workspace.IPreferenced#applyPrefs(nu.xom.Element)
     */
    public void applyPrefs(Element prefs) throws Exception
    {
        Element root = 
            XOMUtil.getFirstChildElement(
                prefs, 
                NODE_JCOLLAPSABLEPANEL,
                new Element(NODE_JCOLLAPSABLEPANEL));
     
        PreferencedUtil.readPreferences(this, root, SAVED_PROPERTIES);
    }


    /**
     * @see toolbox.workspace.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs) throws Exception
    {
        Element root = new Element(NODE_JCOLLAPSABLEPANEL);
        PreferencedUtil.writePreferences(this, root, SAVED_PROPERTIES);
        XOMUtil.insertOrReplace(prefs, root);
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
            toggle();
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