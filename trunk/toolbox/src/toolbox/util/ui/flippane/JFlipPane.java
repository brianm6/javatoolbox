package toolbox.util.ui.flippane;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import nu.xom.Element;

import org.apache.log4j.Logger;

import toolbox.util.ArrayUtil;
import toolbox.util.PreferencedUtil;
import toolbox.util.SwingUtil;
import toolbox.util.XOMUtil;
import toolbox.util.ui.CompoundIcon;
import toolbox.util.ui.layout.StackLayout;
import toolbox.workspace.IPreferenced;

/**
 * JFlipPane is basically a collapsable tabpanel with a built in slider to 
 * separate it from its attached compartment. A JFlipPane can have the following
 * positions:
 * <ul>
 *   <li>Top
 *   <li>Bottom
 *   <li>Left
 *   <li>Right
 * </ul>
 * For the left and right positions, the icon and text located on the tab are
 * rotated accordingly to save space.
 */
public class JFlipPane extends JPanel implements IPreferenced
{
    // TODO: Add property change listener support for java been props
    
    private static final Logger logger_ = Logger.getLogger(JFlipPane.class);

    //--------------------------------------------------------------------------
    // Javabean Property Constants
    //--------------------------------------------------------------------------
    
    /**
     * Javabean property for the collapsed state of the flippane.
     */
    private static final String PROP_COLLAPSED = "collapsed";

    /**
     * Javabean property for the height/width of the flippane.
     */
    private static final String PROP_DIMENSION = "dimension";

    /**
     * Javabean property for the currently selected flipper.
     */
    private static final String PROP_ACTIVE_FLIPPER = "activeFlipper";

    //--------------------------------------------------------------------------
    // IPreferenced Constants
    //--------------------------------------------------------------------------

    /**
     * Root node for preferences.
     */
    private static final String NODE_JFLIPPANE = "JFlipPane";

    /**
     * List of javabean properties saved by the IPreferenced interface impl.
     */
    private static final String[] SAVED_PROPS = {
        PROP_COLLAPSED,
        PROP_DIMENSION,
        PROP_ACTIVE_FLIPPER
    };

    //--------------------------------------------------------------------------
    // Directional Constants
    //--------------------------------------------------------------------------

    /**
     * Flippane attached to the top wall.
     */
    public static final String TOP = "top";

    /**
     * Flippane attached to the left wall.
     */
    public static final String LEFT = "left";

    /**
     * Flippane attached to the bottom wall.
     */
    public static final String BOTTOM = "bottom";

    /**
     * Flippane attached to the right wall.
     */
    public static final String RIGHT = "right";

    /**
     * Draggable splitpane like splitter bar width.
     */
    public static final int SPLITTER_WIDTH = 10;

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * The wall of the enclosing panel that the flippane is attached to. Values
     * include left, right, top, and bottom.
     */
    private String position_;

    /**
     * The dimension_ is the width of the flippane if the position_ is left or
     * right. Conversely, the dimension_ is the height of the flippane when the
     * position is top or bottom.
     */
    private int dimension_;

    /**
     * Houses the buttons that expand/collapse flippers.
     */
    private JPanel buttonPanel_;

    /**
     * Button group used to ensure that only one flipper can be active at a
     * time.
     */
    private ButtonGroup buttonGroup_;

    /**
     * Internal layout used by the flippane to switch between flippers.
     */
    private FlipCardPanel flipCardPanel_;

    /**
     * Currently selected or active flipper.
     */
    private JComponent current_;

    /**
     * Listeners interested in flippane generated events.
     */
    private List listeners_;

    /**
     * Maps a name (button text) to a flippane component.
     */
    private Map flippers_;

    /**
     * JDK 1.4 workaround.
     */
    private JToggleButton nullButton_;

    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a JFlipPane with the given position.
     * 
     * @param position Position (JFlipPane.[TOP|LEFT|BOTTOM|RIGHT])
     */
    public JFlipPane(String position)
    {
        position_ = position;
        dimension_ = 0;
        flippers_ = new HashMap();
        listeners_ = new ArrayList();

        buildView();
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------

    /**
     * Adds the given flipper to the JFlipPane.
     * 
     * @param name Text that appears on the tab that activates the flipper.
     * @param flipper Component that consumes the visible portion of the 
     *        flippane when activated.
     */
    public void addFlipper(String name, JComponent flipper)
    {
        addFlipper(null, name, flipper);
    }

    
    /**
     * Adds a flipper to this flippane.
     * 
     * @param icon Icon that appears on the tab that activates the flipper.
     * @param name Text that appears on the tab that activates the flipper.
     * @param flipper Component that consumes the visible portion of the 
     *        flippane when activated.
     */
    public void addFlipper(Icon icon, String name, JComponent flipper)
    {
        flipper.setName(name);
        flippers_.put(name, flipper);      // Map name -> component
        flipCardPanel_.add(name, flipper); // Add to card panel
        
        int rotation = getRotation();

        // Create the rotated button that makes up the tab portion
        JToggleButton button = 
            new JToggleButton(rotateFlipperTab(icon, name, rotation));
        
        button.setMargin(new Insets(0, 0, 0, 0));
        button.setRequestFocusEnabled(false);
        button.setActionCommand(name);
        button.addActionListener(new FlipperHandler());
        button.setName(name);

        // Add to button group and button panel
        buttonGroup_.add(button);
        
        if (rotation != FlipIcon.NONE)
            buttonPanel_.add("Top Wide Flush", button);
        else
            buttonPanel_.add("Left Tall Flush", button);
        
        // Make newly added flipper selected by default
        if (!isCollapsed())
            setActiveFlipper(flipper);

        revalidate();
    }

    
    /**
     * Removes the given flipper from the flipPane.
     * 
     * @param flipper Flipper to remove.
     */
    public void removeFlipper(JComponent flipper)
    {
        // Remove from buttons
        buttonPanel_.remove(getButtonFor(flipper));
        buttonGroup_.remove(getButtonFor(flipper));

        // Remove from card panel
        flipCardPanel_.remove(flipper);

        // Remove from internal table
        flippers_.remove(flipper.getName());

        revalidate();
    }


    /**
     * Sets the currently selected flipper.
     * 
     * @param flipper Flipper to select.
     */
    public void setActiveFlipper(JComponent flipper)
    {
        // Already selected
        if (current_ == flipper)
            return;

        // We didn't have a component previously, so create a border
        if (current_ == null)
            flipCardPanel_.setBorder(new FlipPaneBorder(position_));

        if (flipper != null)
        {
            current_ = flipper;
            flipCardPanel_.showCard(flipper.getName());
            getButtonFor(flipper).setSelected(true);
        }
        else
        {
            current_ = null;
            nullButton_.setSelected(true);
            // removing last component, so remove border
            flipCardPanel_.setBorder(null);
        }

        revalidate();
    }


    /**
     * Selects the active flipper by name.
     * 
     * @param name Name of the flipper to activate.
     */
    public void setActiveFlipper(String name)
    {
        setActiveFlipper((JComponent) flippers_.get(name));
    }


    /**
     * Returns the name of the currently active flipper or null if there is no
     * active flipper.
     * 
     * @return String
     */
    public String getActiveFlipper()
    {
        return current_ != null ? current_.getName() : null;
    }


    /**
     * Determines if a flipper is selected.
     * 
     * @param flipper Flipper to test if selected.
     * @return True if the given flipper is selected, false otherwise.
     */
    public boolean isFlipperActive(JComponent flipper)
    {
        return current_ == flipper;
    }


    /**
     * Toggles the flipper from its current state to the opposite state. Also
     * fires notifies to registered listeners.
     */
    public void toggleFlipper()
    {
        if (!isCollapsed())
        {
            //
            // Flipper is expanded so collapse it by removing the card panel
            //
            remove(flipCardPanel_);

            //
            // Invisible button steals the selected state so none show as
            // selected
            //
            nullButton_.setSelected(true);
            fireFlipperCollapsed();
        }
        else
        {
            //
            // Flipper is collapsed so expand it by adding back the card panel
            //
            add(BorderLayout.CENTER, flipCardPanel_);
            fireFlipperExpanded();
        }
    }

    
    /**
     * Sets the flippane to its collapsed state if b true, otherwise to its
     * expanded state.
     * 
     * @param b True to collapse, false to expand.
     */
    public void setCollapsed(boolean b)
    {
        setExpanded(!b);
    }

    
    /**
     * Sets the flippane to its expanded or collapsed state.
     * 
     * @param b True to expand, false to collapse.
     */
    public void setExpanded(boolean b)
    {
        if (b && isCollapsed())
        {
            toggleFlipper();
        }
        else if (!b && !isCollapsed())
        {
            toggleFlipper();
        }
    }

    //--------------------------------------------------------------------------
    // Overrides javax.swing.JComponent
    //--------------------------------------------------------------------------

    /**
     * Dimension that reflects the preferred size of the flip pane. The
     * preferred size varies based on whether the flip pane is expanded or
     * collapsed.
     * 
     * @see java.awt.Component#getPreferredSize()
     */
    public Dimension getPreferredSize()
    {
        Dimension pref;

        if (!isCollapsed())
        {
            int width = buttonPanel_.getPreferredSize().width
                + flipCardPanel_.getPreferredSize().width;

            int height = buttonPanel_.getPreferredSize().height
                + flipCardPanel_.getPreferredSize().height;

            pref = new Dimension(width, height);

            //logger_.info(method + "prefSize expanded = " + pref);
        }
        else
        {
            pref = buttonPanel_.getPreferredSize();

            //logger_.info(method + "prefSize collapsed = " + pref);
        }

        return pref;
    }


    //        public Dimension getMinimumSize()
    //        {
    //            if (isCollapsed())
    //                return buttonPanel_.getMinimumSize();
    //            else
    //                return new Dimension(200, 0);
    //        }
    //
    //
    //        public Dimension getMaximumSize()
    //        {
    //            return getPreferredSize();
    //        }

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
                prefs, NODE_JFLIPPANE, new Element(NODE_JFLIPPANE));

        PreferencedUtil.readPreferences(this, root, SAVED_PROPS);
        
        //int dim = XOMUtil.getIntegerAttribute(root, ATTR_DIMENSION, 100);

        // HACK BEGIN
        //dim += SPLITTER_WIDTH + 3;
        // HACK END

        //setDimension(dim);

        // TODO: Do we need this?
        repaint();
    }


    /**
     * @see toolbox.workspace.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs) throws Exception
    {
        Element flipPane = new Element(NODE_JFLIPPANE);
        PreferencedUtil.writePreferences(this, flipPane, SAVED_PROPS);
        XOMUtil.insertOrReplace(prefs, flipPane);
    }

    //--------------------------------------------------------------------------
    // Event Notification Support
    //--------------------------------------------------------------------------

    /**
     * Adds a flip pane listener.
     * 
     * @param listener Listener to add.
     */
    public void addFlipPaneListener(FlipPaneListener listener)
    {
        listeners_.add(listener);
    }


    /**
     * Removes a flip pane listener.
     * 
     * @param listener Listener to remove.
     */
    public void removeFlipPaneListener(FlipPaneListener listener)
    {
        listeners_.remove(listener);
    }


    /**
     * Fires notification that the flippane was expanded.
     */
    protected void fireFlipperExpanded()
    {
        Iterator i = listeners_.iterator();

        while (i.hasNext())
        {
            FlipPaneListener listener = (FlipPaneListener) i.next();
            listener.expanded(this);
        }
    }


    /**
     * Fires notification that the flippane was collapsed.
     */
    protected void fireFlipperCollapsed()
    {
        Iterator i = listeners_.iterator();

        while (i.hasNext())
        {
            FlipPaneListener listener = (FlipPaneListener) i.next();
            listener.collapsed(this);
        }
    }

    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------

    /**
     * Constructs the user interface.
     */
    protected void buildView()
    {
        // Determine which orientation of StackLayout we need to use
        
        int stackLayoutOrientation = StackLayout.HORIZONTAL;
        
        if (position_.equals(JFlipPane.RIGHT) ||
            position_.equals(JFlipPane.LEFT))
            stackLayoutOrientation = StackLayout.VERTICAL;
           
        buttonPanel_ = new JPanel(new StackLayout(stackLayoutOrientation));

        // Adds buttons to mutually exclusive button group
        buttonGroup_ = new ButtonGroup();

        // JDK 1.4 workaround
        buttonGroup_.add(nullButton_ = new JToggleButton());

        flipCardPanel_ = new FlipCardPanel(this);

        // Add to borderlayout
        setLayout(new BorderLayout());

        String bpos = null;

        if (position_.equals(LEFT))
            bpos = BorderLayout.WEST;
        else if (position_.equals(RIGHT))
            bpos = BorderLayout.EAST;
        else if (position_.equals(TOP))
            bpos = BorderLayout.NORTH;
        else if (position_.equals(BOTTOM))
            bpos = BorderLayout.SOUTH;

        add(bpos, buttonPanel_);
        add(BorderLayout.CENTER, flipCardPanel_);
    }


    /**
     * Mutator for the dimension.
     * 
     * @param dimension New dimension.
     */
    public void setDimension(int dimension)
    {
        // TODO: Figure out why dim has to be adjusted
        
        if (dimension != 0)
        {
            dimension_ = dimension - SPLITTER_WIDTH - 3;
            //logger_.debug("[setDim] dim = " + dimension +
            //                      " pref = " + getPreferredSize());
        }

    }


    /**
     * Returns flippane's collapsed state.
     * 
     * @return True if the flipPane is collapsed, false otherwise.
     */
    public boolean isCollapsed()
    {
        return !ArrayUtil.contains(getComponents(), flipCardPanel_);
    }


    /**
     * Returns the button wired to the given flipper.
     * 
     * @param flipper Flipper to find button for.
     * @return Button that activates the flipper.
     */
    protected JToggleButton getButtonFor(JComponent flipper)
    {
        //logger_.debug("Button count=" + buttonGroup_.getButtonCount());

        Enumeration e = buttonGroup_.getElements();

        while (e.hasMoreElements())
        {
            JComponent c = (JComponent) e.nextElement();
            if (c instanceof JToggleButton)
            {
                if (c.getName() != null) // skip over invisible button
                {
                    //logger_.debug("button " + c);
                    if (c.getName().equals(flipper.getName()))
                        return (JToggleButton) c;
                }
            }
        }

        return null;
    }


    /**
     * Determines the rotation for icons and text based on the position of this
     * JFlipPane. See FlipIcon.NONE|CW|CCW.
     * 
     * @return int
     */
    protected int getRotation()
    {
        int rotation;
        
        if (position_.equals(JFlipPane.TOP) || 
            position_.equals(JFlipPane.BOTTOM))
            rotation = FlipIcon.NONE;
        else if (position_.equals(JFlipPane.LEFT))
            rotation = FlipIcon.CCW;
        else if (position_.equals(JFlipPane.RIGHT))
            rotation = FlipIcon.CW;
        else
            throw new IllegalArgumentException("Invalid position " + position_);
        
        return rotation;
    }

    
    /**
     * Rotates the tab for a given flipper and returns the rendered icon 
     * according to the position of the flippane and its orientation.
     * 
     * @param icon Optional icon. Can be null.
     * @param name Text on the flipper tab.
     * @param rotation Rotation. See FlipIcon.CCW|CW
     * @return Icon
     */
    protected Icon rotateFlipperTab(Icon icon, String name, int rotation)
    {
        if (icon != null)
        {
            Icon textIcon = 
                new FlipIcon(
                    rotation, 
                    UIManager.getFont("Button.font"), 
                    name);
            
            Image iconImage = ((ImageIcon) icon).getImage();
            
            if (rotation != FlipIcon.NONE)
            {
                if (rotation == FlipIcon.CW)
                {
                    icon = new CompoundIcon(
                        new ImageIcon(SwingUtil.rotate(iconImage, 90)), 
                        textIcon,
                        SwingConstants.VERTICAL);
                }
                else
                {
                    icon = new CompoundIcon(
                        textIcon,
                        new ImageIcon(SwingUtil.rotate(iconImage, -90)),
                        SwingConstants.VERTICAL);
                }
            }
            else
            {
                icon = new CompoundIcon(
                    icon,
                    textIcon,
                    SwingConstants.HORIZONTAL);
            }
        }
        else
        {
            icon = new FlipIcon(
                rotation, 
                UIManager.getFont("Button.font"), 
                name);
        }
        return icon;
    }
    
    //--------------------------------------------------------------------------
    // Accessors
    //--------------------------------------------------------------------------

    /**
     * Returns the dimensions of this flippane. Dimension is defined as the 
     * width of the component if the position is left or right. Alternatively, 
     * the height of the component if the position is top or bottom.
     * 
     * @return int
     */
    public int getDimension()
    {
        return dimension_;
    }


    /**
     * Returns the position of this flippane. (left, right, top, bottom).
     * 
     * @return String
     */
    protected String getPosition()
    {
        return position_;
    }

    //--------------------------------------------------------------------------
    // FlipperHandler
    //--------------------------------------------------------------------------

    /**
     * Handles expanding/collapsing of a flipper.
     */
    class FlipperHandler implements ActionListener
    {
        /**
         * @see java.awt.event.ActionListener#actionPerformed(
         *      java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent evt)
        {
            JComponent button = (JComponent) evt.getSource();
            String name = button.getName();
            logger_.debug("Flipper " + name + " selected");
            JComponent flipper = (JComponent) flippers_.get(name);
    
            if (isFlipperActive(flipper))
            {
                //logger_.debug("Toggeling flipper");
                toggleFlipper();
            }
            else
            {
                //logger_.debug("Selecting flipper");
    
                // Must be expanded before flipper can be selected
                if (isCollapsed())
                    toggleFlipper();
    
                setActiveFlipper(flipper);
            }
    
            revalidate();
        }
    }
}