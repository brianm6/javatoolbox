package toolbox.util.ui.flippane;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;

import nu.xom.Attribute;
import nu.xom.Element;

import org.apache.log4j.Logger;

import toolbox.util.ArrayUtil;
import toolbox.util.StringUtil;
import toolbox.util.XOMUtil;
import toolbox.util.ui.ImageCache;
import toolbox.util.ui.JSmartButton;
import toolbox.util.ui.JSmartMenuItem;
import toolbox.util.ui.JSmartPopupMenu;

/**
 * JFlipPane is a panel with flipper like behavior to hide a and show any
 * number of children.
 */
public class JFlipPane extends JPanel
{
    private static final Logger logger_ =
        Logger.getLogger(JFlipPane.class);

    /**
     * Root node for preferences
     */
    private static final String NODE_JFLIPPANE = "JFlipPane";

    /** 
     * Attribute for the collapsed state of the flippane
     */
    private static final String ATTR_COLLAPSED = "collapsed";
    
    /** 
     * Attribute for the height/width of the flippane 
     */
    private static final String ATTR_DIMENSION = "dimension";
    
    /** 
     * Attribute for the currently selected flipper 
     */
    private static final String ATTR_ACTIVE = "activeFlipper";
    
    /** 
     * Flippane attached to the top wall 
     */
    public static final String TOP = "top";
    
    /** 
     * Flippane attached to the left wall 
     */
    public static final String LEFT = "left";
    
    /** 
     * Flippane attached to the bottom wall 
     */
    public static final String BOTTOM = "bottom";
    
    /** 
     * Flippane attached to the right wall 
     */
    public static final String RIGHT  = "right";
    
    /** 
     * Draggable splitpane like splitter bar width 
     */
    public static final int SPLITTER_WIDTH = 10;

    /** 
     * The wall of the enclosing panel that the flippane is attached to 
     */
    private String position_;
    
    /**
     * The dimension_ is the width of the flippane if the position_ is left or
     * right. Conversely, the dimension_ is the height of the flippane when
     * the position is top or bottom. 
     */
    private int dimension_;
    
    /** 
     * Houses the buttons that expand/collapse a flipper 
     */
    private JPanel buttonPanel_;
    
    /** 
     * Button attached to every flippane used to collapse all flippers 
     */
    private JButton closeButton_;
    
    /**
     * Button group used to ensure that all flippane selections are mutually
     * exclusive
     */
    private ButtonGroup buttonGroup_;

    /** 
     * Internal layout used by the flippane to switch between flippers 
     */
    private FlipCardPanel flipCardPanel_;

    /** 
     * Currently selected/active flipper 
     */
    private JComponent current_;
    
    /** 
     * Interested listeners to flippane events 
     */
    private List listeners_;
    
    /** 
     * Maps a name (button text) to a flippane component 
     */
    private Map flippers_;

    // Not really used            
    private JButton         popupButton_;
    private JToggleButton   nullButton_;
    private JPopupMenu      popup_;
    
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
        position_  = position;
        dimension_ = 0;
        flippers_  = new HashMap();
        listeners_ = new ArrayList();
                        
        buildView();
    } 

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------

    /**
     * Adds the given flipper to the JFlipPane.
     * 
     * @param name Name of the flipper
     * @param flipper Flipper to add
     */
    public void addFlipper(String name, JComponent flipper)
    {
        flipper.setName(name);   
           
        // Add to internal map
        flippers_.put(name, flipper);
        
        // Add to card panel
        flipCardPanel_.add(name, flipper);
        
        // Figure out rotation of text for button
        int rotation;
        if (position_.equals(JFlipPane.TOP) ||
            position_.equals(JFlipPane.BOTTOM))
            rotation = FlipIcon.NONE;
        else if (position_.equals(JFlipPane.LEFT))
            rotation = FlipIcon.CCW;
        else if (position_.equals(JFlipPane.RIGHT))
            rotation = FlipIcon.CW;
        else
            throw new IllegalArgumentException("Invalid position: "+ position_);

        // Create the button
        JToggleButton button = new JToggleButton();
        button.setMargin(new Insets(0, 0, 0, 0));
        button.setRequestFocusEnabled(false);
        button.setIcon(new FlipIcon(rotation, button.getFont(), name));
        button.setActionCommand(name);
        button.addActionListener(new FlipperHandler());
        button.setName(name);

        // Add to button group and button panel
        buttonGroup_.add(button);
        buttonPanel_.add(button);

        // Add mouse listener
        button.addMouseListener(new PopupHandler());

        // Create menu item
        JMenuItem menuItem = new JSmartMenuItem(name);

        menuItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {
                logger_.warn("FIX ME!!! menuItem actionlistener");
                //showDockableWindow(entry.getName());
            }
        }); 

        popup_.add(menuItem);

        // Make newly added flipper selected by default
        if (!isCollapsed())
            setActiveFlipper(flipper);
        
        // TODO: find proper way to do this
        revalidate();
        repaint();
    } 


    /**
     * Removes the given flipper from the flipPane.
     * 
     * @param flipper Flipper to remove
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

        // TODO: find proper way to do this               
        revalidate();
    } 


    /**
     * Sets the currently selected flipper.
     * 
     * @param flipper Flipper to select
     */
    public void setActiveFlipper(JComponent flipper)
    {
        // Already selected
        if (current_ == flipper)
            return;

        // we didn't have a component previously, so create a border
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

        // TODO: find proper way to do this
        revalidate();
        flipCardPanel_.repaint();
    } 


    /**
     * Sets the active flipper by name.
     * 
     * @param name Name of the flipper to activate
     */
    public void setActiveFlipper(String name)
    {
       setActiveFlipper((JComponent) flippers_.get(name)); 
    }


    /**
     * Returns the currently active flipper.
     * 
     * @return Currently active flipper
     */
    public JComponent getActiveFlipper()
    {
        return current_;    
    }


    /**
     * Determines if a flipper is selected.
     * 
     * @param flipper Flipper to test if selected
     * @return True if the given flipper is selected, false otherwise
     */
    public boolean isFlipperActive(JComponent flipper)
    {
        return current_ == flipper;
    } 


    /**
     * Toggles the flipper from its current state to the opposite state.
     * Also notifies all FlipPaneListeners.
     */
    public void toggleFlipper()
    {
        if (!isCollapsed())
        {
            // Flipper is expanded so collapse it by removing the card panel
            remove(flipCardPanel_);
            
            // Invisible button steals the selected state so none show as
            // selected
            nullButton_.setSelected(true);
            fireFlipperCollapsed();
        }
        else
        {
            // Flipper is collapsed so expand it by adding back the card panel
            add(BorderLayout.CENTER, flipCardPanel_);
            fireFlipperExpanded();
        }
    }


    /**
     * Sets the flip pane to its expanded state.
     * 
     * @param b True to expand, false to collapse
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
     * Preferred size.
     * 
     * @return Dimension that reflects the preferred size of the flip pane.
     *         The preferred size varies based on whether the flip pane is
     *         expanded or collapsed.
     */   
    public Dimension getPreferredSize()
    {
        Dimension pref;
        
        if (!isCollapsed())
        {
            int width = buttonPanel_.getPreferredSize().width +
                        flipCardPanel_.getPreferredSize().width;
                        
            int height = buttonPanel_.getPreferredSize().height + 
                         flipCardPanel_.getPreferredSize().height;
                        
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
    public void applyPrefs(Element prefs)
    {
        Element root = XOMUtil.getFirstChildElement(
            prefs, NODE_JFLIPPANE, new Element(NODE_JFLIPPANE));

        int dim = XOMUtil.getIntegerAttribute(root, ATTR_DIMENSION, 100);
        
        // HACK BEGIN
        dim += SPLITTER_WIDTH + 3;
        // HACK END
        
        setDimension(dim);
        
        boolean collapsed = 
            XOMUtil.getBooleanAttribute(root, ATTR_COLLAPSED, false);  
 
        if (collapsed != isCollapsed())
            toggleFlipper();
        
        String flipper = XOMUtil.getStringAttribute(root, ATTR_ACTIVE,"");
        
        if (!StringUtil.isNullOrEmpty(flipper))
            setActiveFlipper(flipper);
            
        repaint();
    }


    /**
     * @see toolbox.workspace.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs)
    {
        Element flipPane = new Element(NODE_JFLIPPANE);
        flipPane.addAttribute(new Attribute(ATTR_COLLAPSED, isCollapsed()+""));
        flipPane.addAttribute(new Attribute(ATTR_DIMENSION, getDimension()+""));
        
        JComponent flipper = getActiveFlipper();
        
        if (flipper != null)
            flipPane.addAttribute(
                new Attribute(ATTR_ACTIVE, flipper.getName()));

        XOMUtil.insertOrReplace(prefs, flipPane);
    }

    //--------------------------------------------------------------------------
    // Event Notification Support
    //--------------------------------------------------------------------------

    /**
     * Adds a flip pane listener.
     * 
     * @param listener Listener to add
     */
    public void addFlipPaneListener(FlipPaneListener listener)
    {
        listeners_.add(listener);
    }


    /**
     * Removes a flip pane listener.
     * 
     * @param listener Listener to remove
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
     * Builds the GUI.
     */
    protected void buildView()
    {
        buttonPanel_ = new JPanel(new FlipButtonLayout(this));
        buttonPanel_.addMouseListener(new PopupHandler());
        
        closeButton_ = new JSmartButton(
            ImageCache.getIcon(ImageCache.IMAGE_CROSS));
                
        closeButton_.setToolTipText("Close");
        
        int left;
        if (position_.equals(JFlipPane.RIGHT) || 
            position_.equals(JFlipPane.LEFT))
            left = 1;
        else
            left = 0;

        closeButton_.setMargin(new Insets(0,left,0,0));
        buttonPanel_.add(closeButton_);
        closeButton_.addActionListener(new FlipperHandler());

        // Popup button
        popupButton_ = 
            new JSmartButton(ImageCache.getIcon(ImageCache.IMAGE_TRIANGLE));
            
        popupButton_.setRequestFocusEnabled(false);
        popupButton_.setToolTipText("Popup menu");
        popupButton_.addMouseListener(new PopupHandler());
        buttonPanel_.add(popupButton_);        
                
        // Adds buttons to mutually exclusive button group
        popup_ = new JSmartPopupMenu();
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
     * @param dimension New dimension
     */
    protected void setDimension(int dimension)
    {
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
     * @return True if the flipPane is collapsed, false otherwise
     */    
    protected boolean isCollapsed()
    {
        return !ArrayUtil.contains(getComponents(), flipCardPanel_);
    }


    /**
     * Returns the button wired to the given flipper.
     * 
     * @param flipper Flipper to find button for
     * @return Button that activates the flipper 
     */
    protected JToggleButton getButtonFor(JComponent flipper)
    {
        //logger_.debug("Button count=" + buttonGroup_.getButtonCount());
        
        Enumeration e = buttonGroup_.getElements();
        
        while(e.hasMoreElements())
        {
            JComponent c = (JComponent) e.nextElement();
            if (c instanceof JToggleButton)
            {
                if (c.getName() != null)  // skip over invisible button
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
     * Returns if the specified event is the popup trigger event. This 
     * implements precisely defined behavior, as opposed to 
     * MouseEvent.isPopupTrigger().
     * 
     * @param evt Event
     * @return True if popup trigger, false otherwise
     */
    protected boolean isPopupTrigger(MouseEvent evt)
    {
        return ((evt.getModifiers() & InputEvent.BUTTON3_MASK) != 0);
    } 


    /**
     * Shows the specified popup menu, ensuring it is displayed within the 
     * bounds of the screen.
     * 
     * @param popup Popup menu
     * @param comp Component to show it for
     * @param x X coordinate
     * @param y Y coordinate
     */
    protected void showPopupMenu(
        JPopupMenu popup, 
        Component comp, 
        int x, 
        int y)
    {
        Point p = new Point(x,y);
        SwingUtilities.convertPointToScreen(p,comp);

        Dimension size   = popup.getPreferredSize();
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

        boolean horiz = false;
        boolean vert = false;

        // might need later
        int origX = x;

        if (p.x + size.width > screen.width && size.width < screen.width)
        {
            x += (screen.width - p.x - size.width);
            horiz = true;
        }

        if (p.y + size.height > screen.height && size.height < screen.height)
        {
            y += (screen.height - p.y - size.height);
            vert = true;
        }

        // If popup needed to be moved both horizontally and vertically, the 
        // mouse pointer might end up over a menu item, which will be invoked 
        // when the mouse is released. This is bad, so move popup to a different
        // location.
        
        if (horiz && vert)
            x = origX - size.width - 2;

        popup.show(comp,x,y);
    } 

    //--------------------------------------------------------------------------
    // Accessors
    //--------------------------------------------------------------------------
    
    /**
     * Returns flippane dimension. 
     * 
     * @return Dimension (width if position is left/right  or height if 
     *         position is top/bottom)
     */
    protected int getDimension()
    {
        return dimension_;
    }


    /**
     * Return the position (left, right, top, bottom).
     * 
     * @return String
     */            
    protected String getPosition()
    {
        return position_;
    }            


    /**
     * Returns the Popup button.
     * 
     * @return JButton
     */
    protected JButton getPopupButton()
    {
        return popupButton_;
    }


    /**
     * Returns the close button.
     * 
     * @return JButton
     */
    protected JButton getCloseButton()
    {
        return closeButton_;
    }
    
    //--------------------------------------------------------------------------
    //  Inner Classes
    //--------------------------------------------------------------------------
    
    /**
     * Handles expanding/collapsing of a flipper.
     */
    class FlipperHandler implements ActionListener
    {
        public void actionPerformed(ActionEvent evt)
        {
            if (evt.getSource() == closeButton_)
            {
                setActiveFlipper( (JComponent) null);
            }
            else
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
     
                // TODO: find correct way to do this           
                revalidate();
                repaint();
            }
        }
    } 


    /**
     * Mouse handler to show popup menu.
     */
    class PopupHandler extends MouseAdapter
    {
        public void mousePressed(MouseEvent evt)
        {
            if (evt.getSource() == popupButton_ || isPopupTrigger(evt))
            {
                if (popup_.isVisible())
                    popup_.setVisible(false);
                else
                {
                    showPopupMenu(
                        popup_, 
                        (Component)evt.getSource(),
                        evt.getX(),
                        evt.getY());
                }
            }
        }
    } 
}

/*
Revision History before before change of package name
================================================================================
revision 1.19  2003/04/16 02:12:47  analogue  Added saving/restoring of 
                                              preferences. Still need to verify.
revision 1.18  2003/04/15 11:41:52  analogue  Removed method name debugs
revision 1.17  2003/04/14 01:42:01  analogue  Added T O D O for saved/restore
revision 1.16  2003/04/08 23:09:07  analogue  Coding standard updates
revision 1.15  2003/03/28 08:38:34  analogue  Removed unused code
revision 1.14  2003/03/27 04:14:27  analogue  Axed loadIcon() and updated to 
                                              load gifs via ResourceUtil
revision 1.13  2003/03/23 05:03:56  analogue  Removed tabs
revision 1.12  2003/03/15 03:59:52  analogue  Checkstyle updates
revision 1.11  2002/12/24 06:26:56  analogue  None
revision 1.10  2002/12/09 09:02:11  analogue  Checkstyle updates
revision 1.9   2002/11/02 02:23:27  analogue  Updated log4j to 1.2.7
revision 1.8   2002/11/02 01:38:37  analogue  Updated imports
revision 1.7   2002/10/23 02:12:48  analogue  Updated imports`
revision 1.6   2002/09/06 05:29:09  analogue  Added method names to debug
revision 1.5   2002/09/04 03:32:56  analogue  Less logging
revision 1.4   2002/08/24 05:18:56  analogue  Javadoc updates
revision 1.3   2002/08/23 02:51:39  analogue  Removed getCurrent()
revision 1.2   2002/08/22 05:35:01  analogue  Debug
revision 1.1   2002/08/21 03:41:30  analogue  Initial version
================================================================================
*/