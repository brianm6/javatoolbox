package toolbox.util.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.LineMetrics;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.metal.MetalLookAndFeel;

import org.apache.log4j.Logger;

import toolbox.util.ArrayUtil;
import toolbox.util.ResourceUtil;

/**
 * JFlipPane is a panel with flipper like behavior to hide a and show any
 * number of children.
 * 
 * <pre>
 * TODO: Add ability to save/restore preferences (selected flipper, open state)
 * </pre>
 */
public class JFlipPane extends JPanel
{
    /** Logger */
    private static final Logger logger_ =
        Logger.getLogger(JFlipPane.class);
    
    // Positions
    
    /** Top position */
    public static final String TOP = "top";
    
    /** Left position */
    public static final String LEFT = "left";
    
    /** Bottom position */
    public static final String BOTTOM = "bottom";
    
    /** Right position */
    public static final String RIGHT  = "right";
    
    /** Splitter bar width in pixels */
    public static final int SPLITTER_WIDTH = 10;

    // Instance variables

    private String          position_;
    private int             dimension_;
    
    private JPanel          buttonPanel_;
    private JButton         closeButton_;
    private JButton         popupButton_;
    private ButtonGroup     buttonGroup_;
    private JToggleButton   nullButton_;
    private JPopupMenu      popup_;
    private FlipCardPanel   flipCardPanel_;
    
    private JComponent      current_;
    private Hashtable       flippers_;
    private List            listeners_;
    
    
    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a JFlipPane with the given position
     * 
     * @param  position  Position (JSplitPane.[TOP|LEFT|BOTTOM|RIGHT]
     */
    public JFlipPane(String position)
    {
        position_  = position;
        dimension_ = 0;
        flippers_  = new Hashtable();
        listeners_ = new ArrayList();
                        
        buildView();
    } 

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------

    /**
     * Adds the given flipper to the JFlipPane
     * 
     * @param  name     Name of the flipper
     * @param  flipper  Flipper to add
     */
    public void addFlipper(String name, JComponent flipper)
    {
        // TODO: put this as a client property instead
        flipper.setName(name);   
           
        // Add to internal map
        flippers_.put(name, flipper);
        
        // Add to card panel
        flipCardPanel_.add(name, flipper);
        
        // Figure out rotation of text for button
        int rotation;
        if (position_.equals(JFlipPane.TOP) ||
            position_.equals(JFlipPane.BOTTOM))
            rotation = RotatedTextIcon.NONE;
        else if (position_.equals(JFlipPane.LEFT))
            rotation = RotatedTextIcon.CCW;
        else if (position_.equals(JFlipPane.RIGHT))
            rotation = RotatedTextIcon.CW;
        else
            throw new InternalError("Invalid position: " + position_);

        // Create the button
        JToggleButton button = new JToggleButton();
        button.setMargin(new Insets(0, 0, 0, 0));
        button.setRequestFocusEnabled(false);
        button.setIcon(new RotatedTextIcon(rotation,button.getFont(), name));
        button.setActionCommand(name);
        button.addActionListener(new ActionHandler());
        
        // TODO: put in client property
        button.setName(name);

        // Add to button group and button panel
        buttonGroup_.add(button);
        buttonPanel_.add(button);

        // Add mouse listener
        button.addMouseListener(new MouseHandler());

        // Create menu item
        JMenuItem menuItem = new JMenuItem(name);

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
            setSelectedFlipper(flipper);
        
        // TODO: find proper way to do this
        revalidate();
        repaint();
    } 

    /**
     * Removes the given flipper from the flipPane
     * 
     * @param  flipper  Flipper to remove
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
     * Sets the currently selected flipper
     * 
     * @param  flipper  Flipper to select
     */
    public void setSelectedFlipper(JComponent flipper)
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
     * Returns the button wired to the given flipper
     * 
     * @param   flipper  Flipper to find button for
     * @return  Button that activates the flipper 
     */
    public JToggleButton getButtonFor(JComponent flipper)
    {
        //String method = "[butFor] ";
        
        //logger_.debug(method + 
        //  "Button count=" + buttonGroup_.getButtonCount());
        
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
     * @return Dimension that reflects the preferred size of the flip pane.
     *         The preferred size varies based on whether the flip pane is
     *         expanded or collapsed.
     */   
    public Dimension getPreferredSize()
    {
        //String method = "[prfSiz] ";
        
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


//    public Dimension getMinimumSize()
//    {
//        if (isCollapsed())
//            return buttonPanel_.getMinimumSize();
//        else
//            return new Dimension(200, 0);
//    }


//    public Dimension getMaximumSize()
//    {
//        return getPreferredSize();
//    }

    /**
     * Adds a flip pane listener
     * 
     * @param  l  Listener to add
     */
    public void addFlipPaneListener(JFlipPaneListener l)
    {
        listeners_.add(l);
    }

    /**
     * Removes a flip pane listener
     * 
     * @param  l  Listener to remove
     */
    public void removeFlipPaneListener(JFlipPaneListener l)
    {
        listeners_.remove(l);
    }

    /**
     * Determines if a flipper is selected
     * 
     * @param   flipper  Flipper to test if selected
     * @return  True if the given flipper is selected, false otherwise
     */
    public boolean isFlipperSelected(JComponent flipper)
    {
        return current_ == flipper;
    } 

    /**
     * Toggles the flipper from its current state to the opposite state.
     * Also notifies all IFlipPaneListeners.
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
     * Sets the flip pane to its expanded state
     * 
     * @param  b  True to expand, false to collapse
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

    /**
     * Returns if the specified event is the popup trigger event.
     * This implements precisely defined behavior, as opposed to
     * MouseEvent.isPopupTrigger().
     * 
     * @param   evt     Event
     * @return  True if popup trigger, false otherwise
     */
    public static boolean isPopupTrigger(MouseEvent evt)
    {
        return ((evt.getModifiers() & InputEvent.BUTTON3_MASK) != 0);
    } 


    /**
     * Shows the specified popup menu, ensuring it is displayed within the 
     * bounds of the screen.
     * 
     * @param popup  Popup menu
     * @param comp   Component to show it for
     * @param x      x co-ordinate
     * @param y      y co-ordinate
     */
    public static void showPopupMenu(JPopupMenu popup, Component comp,
        int x, int y)
    {
        Point p = new Point(x,y);
        SwingUtilities.convertPointToScreen(p,comp);

        Dimension size = popup.getPreferredSize();

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

        boolean horiz = false;
        boolean vert = false;

        // might need later
        int origX = x;

        if(p.x + size.width > screen.width && size.width < screen.width)
        {
            x += (screen.width - p.x - size.width);
            horiz = true;
        }

        if(p.y + size.height > screen.height && size.height < screen.height)
        {
            y += (screen.height - p.y - size.height);
            vert = true;
        }

        // If popup needed to be moved both horizontally and
        // vertically, the mouse pointer might end up over a
        // menu item, which will be invoked when the mouse is
        // released. This is bad, so move popup to a different
        // location.
        if(horiz && vert)
        {
            x = origX - size.width - 2;
        }

        popup.show(comp,x,y);
    } 



    //--------------------------------------------------------------------------
    //  Private
    //--------------------------------------------------------------------------

    /**
     * Builds the GUI
     */
    protected void buildView()
    {
        // Button panel
        buttonPanel_ = new JPanel(new FlipButtonLayout(this));
        buttonPanel_.addMouseListener(new MouseHandler());
        
        // Close button
        closeButton_ = new JButton(
            ResourceUtil.getResourceAsIcon(
                "toolbox/util/ui/images/Cross.gif"));
                
        closeButton_.setToolTipText("Close");
        
        int left;
        if (position_.equals(JFlipPane.RIGHT) || 
            position_.equals(JFlipPane.LEFT))
            left = 1;
        else
            left = 0;

        closeButton_.setMargin(new Insets(0,left,0,0));
        buttonPanel_.add(closeButton_);
        closeButton_.addActionListener(new ActionHandler());

        // Popup button
        popupButton_ = new JButton(
            ResourceUtil.getResourceAsIcon(
                "toolbox/util/ui/images/Triangle.gif"));
            
        popupButton_.setRequestFocusEnabled(false);
        popupButton_.setToolTipText("Popup menu");
        popupButton_.addMouseListener(new MouseHandler());
        buttonPanel_.add(popupButton_);        
                
        // Adds buttons to mutually exclusive button group
        popup_ = new JPopupMenu();
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
     * Fires notification that the flippane was expanded
     */
    protected void fireFlipperExpanded()
    {
        Iterator i = listeners_.iterator();
        
        while (i.hasNext())
        {
            JFlipPaneListener l = (JFlipPaneListener)i.next();
            l.expanded(this);
        }
    }
    
    /**
     * Fires notification that the flippane was collapsed
     */
    protected void fireFlipperCollapsed()
    {
        Iterator i = listeners_.iterator();
        
        while (i.hasNext())
        {
            JFlipPaneListener l = (JFlipPaneListener)i.next();
            l.collapsed(this);
        }
    }

    /**
     * Mutator for the dimension
     * 
     * @param  dimension  New dimension
     */
    protected void setDimension(int dimension)
    {
        if(dimension != 0)
        {
            dimension_ = dimension - SPLITTER_WIDTH - 3;
            //logger_.debug("[setDim] dim = " + dimension + 
            //                      " pref = " + getPreferredSize());
        }
            
    } 

    /**
     * @return  Dimension (width if position is left/right  or height if 
     *          position is top/bottom)
     */
    protected int getDimension()
    {
        return dimension_;
    }

    /**
     * @return  Position (left, right, top, bottom)
     */            
    protected String getPosition()
    {
        return position_;
    }            

    /**
     * @return Popup button
     */
    protected JButton getPopupButton()
    {
        return popupButton_;
    }

    /**
     * @return  Close button
     */
    protected JButton getCloseButton()
    {
        return closeButton_;
    }

    /**
     * @return  True if the flipPane is collapsed, false otherwise
     */    
    protected boolean isCollapsed()
    {
        return !ArrayUtil.contains(getComponents(), flipCardPanel_);
    }
    

    
    //--------------------------------------------------------------------------
    //  Inner Classes
    //--------------------------------------------------------------------------
    
    /**
     * Handles expanding/collapsing of a flipper
     */
    class ActionHandler implements ActionListener
    {
        public void actionPerformed(ActionEvent evt)
        {
            String method = "[actPrf] ";
            
            if(evt.getSource() == closeButton_)
                setSelectedFlipper(null);
            else
            {
                JComponent button = (JComponent)evt.getSource();
                String name = button.getName();
                logger_.debug(method + "Flipper " + name + " selected");
                JComponent flipper = (JComponent)flippers_.get(name);
                
                if (isFlipperSelected(flipper))
                {
                    logger_.debug(method + "Toggeling flipper");
                    toggleFlipper();
                    
                }
                else 
                {
                    logger_.debug(method + "Selecting flipper");
                    
                    // Must be expanded before flipper can be selected
                    if (isCollapsed())
                        toggleFlipper();
                        
                    setSelectedFlipper(flipper);
                }
     
                // TODO: find correct way to do this           
                revalidate();
                repaint();
            }
        }
    } 


    /**
     * Mouse handle to show popup menu
     */
    class MouseHandler extends MouseAdapter
    {
        public void mousePressed(MouseEvent evt)
        {
            if(evt.getSource() == popupButton_ || isPopupTrigger(evt))
            {
                if(popup_.isVisible())
                    popup_.setVisible(false);
                else
                {
                    showPopupMenu(popup_, (Component)evt.getSource(),
                        evt.getX(),evt.getY());
                }
            }
        }
    } 
    
    //--------------------------------------------------------------------------
    //  Interface JFlipPaneListener 
    //--------------------------------------------------------------------------
    
    /**
     * Interface for JFlippane generated events
     */
    public interface JFlipPaneListener
    {
        /**
         * Called when a flippane is collapsed
         * 
         * @param  flipPane  Flip pane that was collapsed
         */
        public void collapsed(JFlipPane flipPane);
        
        /**
         * Called when a flippane is expanded
         * 
         * @param  flipPane  Flip pane that was expanded
         */
        public void expanded(JFlipPane flipPane);
    }

    //--------------------------------------------------------------------------
    //  Class RotatedTextIcon 
    //--------------------------------------------------------------------------
    
    /**
     * RotatedTextIcon
     */
    public class RotatedTextIcon implements Icon
    {
        static final int NONE = 0;
        static final int CW = 1;
        static final int CCW = 2;
    
        private int rotate_;
        private Font font_;
        private GlyphVector glyphs_;
        private float width_;
        private float height_;
        private float ascent_;
        private RenderingHints renderHints_;
    
        /**
         * Creates a RotatedTextIcon
         * 
         * @param   rotate  [NONE|CW|CCW]
         * @param   font    Font to use for rendering
         * @param   text    Text of icon
         */
        public RotatedTextIcon(int rotate, Font font, String text)
        {
            rotate_ = rotate;
            font_ = font;
    
            FontRenderContext fontRenderContext = 
                new FontRenderContext(null,true,true);
                
            glyphs_ = font.createGlyphVector(fontRenderContext,text);
            width_ = (int)glyphs_.getLogicalBounds().getWidth() + 4;
            //height = (int)glyphs.getLogicalBounds().getHeight();
    
            LineMetrics lineMetrics = 
                font.getLineMetrics(text,fontRenderContext);
                
            ascent_ = lineMetrics.getAscent();
            height_ = (int)lineMetrics.getHeight();
    
            renderHints_ = new RenderingHints(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
                
            renderHints_.put(RenderingHints.KEY_FRACTIONALMETRICS,
                RenderingHints.VALUE_FRACTIONALMETRICS_ON);
                
            renderHints_.put(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        } 
    
        /**
         * @return Icon width
         */
        public int getIconWidth()
        {
            return (int)(rotate_ == RotatedTextIcon.CW || 
                         rotate_ == RotatedTextIcon.CCW ? height_ : width_);
        } 
    
        /**
         * @return  Icon height
         */
        public int getIconHeight()
        {
            return (int)(rotate_ == RotatedTextIcon.CW ||
                         rotate_ == RotatedTextIcon.CCW ? width_ : height_);
        } 
    
        /**
         * Renders the icon on the graphics
         * 
         * @param  c  Component
         * @param  g  Graphics
         * @param  x  X coord
         * @param  y  y coord
         */
        public void paintIcon(Component c, Graphics g, int x, int y)
        {
            Graphics2D g2d = (Graphics2D)g;
            g2d.setFont(font_);
            AffineTransform oldTransform = g2d.getTransform();
            RenderingHints oldHints = g2d.getRenderingHints();
    
            g2d.setRenderingHints(renderHints_);
            g2d.setColor(c.getForeground());
    
            
            if (rotate_ == RotatedTextIcon.NONE)
            {
                // No rotation
                g2d.drawGlyphVector(glyphs_,x + 2,y + ascent_);
            } 
            else if (rotate_ == RotatedTextIcon.CW)
            {
                // Clockwise rotation
                AffineTransform trans = new AffineTransform();
                trans.concatenate(oldTransform);
                trans.translate(x, y + 2);
                trans.rotate(Math.PI / 2, height_ / 2, width_ / 2);
                g2d.setTransform(trans);
                g2d.drawGlyphVector(glyphs_,(height_ - width_) / 2,
                    (width_ - height_) / 2 + ascent_);
            } 
            else if(rotate_ == RotatedTextIcon.CCW)
            {
                // Counterclockwise rotation
                AffineTransform trans = new AffineTransform();
                trans.concatenate(oldTransform);
                trans.translate(x,y - 2);
                trans.rotate(Math.PI * 3 / 2, height_ / 2, width_ / 2);
                g2d.setTransform(trans);
                g2d.drawGlyphVector(glyphs_,(height_ - width_) / 2,
                    (width_ - height_) / 2 + ascent_);
            } 
    
            g2d.setTransform(oldTransform);
            g2d.setRenderingHints(oldHints);
        } 
    }    
    
    //--------------------------------------------------------------------------
    //  Class FlipPaneBorder 
    //--------------------------------------------------------------------------
        
    /**
     * Custom border for JFlipPane
     */
    public class FlipPaneBorder implements Border
    {
        private String position_;
        private Insets insets_;
        private Color color1_;
        private Color color2_;
        private Color color3_;
    
        /**
         * Creates a FlipPaneBorder for the given position
         * 
         * @param  position  JFlipPane.[BOTTOM|RIGHT|TOP|LEFT]
         */
        FlipPaneBorder(String position)
        {
            position_ = position;
            insets_ = new Insets(
                position.equals(JFlipPane.BOTTOM)?JFlipPane.SPLITTER_WIDTH : 0,
                position.equals(JFlipPane.RIGHT) ?JFlipPane.SPLITTER_WIDTH : 0,
                position.equals(JFlipPane.TOP)   ?JFlipPane.SPLITTER_WIDTH : 0,
                position.equals(JFlipPane.LEFT)  ?JFlipPane.SPLITTER_WIDTH : 0);
        } 
    
        /**
         * Paints the border
         * 
         * @param  c        Component to paint
         * @param  g        Graphics device
         * @param  x        X coord
         * @param  y        Y coord
         * @param  width    Width
         * @param  height   Height
         */
        public void paintBorder(Component c, Graphics g, int x, int y, 
            int width, int height)
        {
            updateColors();
    
            if (color1_ == null || color2_ == null || color3_ == null)
                return;
    
            if (position_.equals(JFlipPane.BOTTOM))
                paintHorizBorder(g,x,y,width);
            else if (position_.equals(JFlipPane.RIGHT))
                paintVertBorder(g,x,y,height);
            else if (position_.equals(JFlipPane.TOP))
                paintHorizBorder(g,x,y + height - JFlipPane.SPLITTER_WIDTH,
                    width);
            else if (position_.equals(JFlipPane.LEFT))
                paintVertBorder(g,x + width - JFlipPane.SPLITTER_WIDTH,y,
                    height);
        } 
    
        /**
         * Retrieves border insets
         * 
         * @param   c   Component
         * @return  Border insets
         */
        public Insets getBorderInsets(Component c)
        {
            return insets_;
        } 
        
        /**
         * @return True if border is opaque, false otherwise
         */
        public boolean isBorderOpaque()
        {
            return false;
        } 
        
        /**
         * Paints horizontal border
         */
        private void paintHorizBorder(Graphics g, int x, int y, int width)
        {
            g.setColor(color3_);
            g.fillRect(x, y, width, JFlipPane.SPLITTER_WIDTH);
    
            for(int i = 0; i < width / 4 - 1; i++)
            {
                g.setColor(color1_);
                g.drawLine(x + i * 4 + 2,y + 3, x + i * 4 + 2,y + 3);
                g.setColor(color2_);
                g.drawLine(x + i * 4 + 3,y + 4, x + i * 4 + 3,y + 4);
                g.setColor(color1_);
                g.drawLine(x + i * 4 + 4,y + 5, x + i * 4 + 4,y + 5);
                g.setColor(color2_);
                g.drawLine(x + i * 4 + 5,y + 6, x + i * 4 + 5,y + 6);
            }
        } 
        
        /**
         * Paints vertical border
         */
        private void paintVertBorder(Graphics g, int x, int y, int height)
        {
            g.setColor(color3_);
            g.fillRect(x, y, JFlipPane.SPLITTER_WIDTH, height);
    
            for(int i = 0; i < height / 4 - 1; i++)
            {
                g.setColor(color1_);
                g.drawLine(x + 3,y + i * 4 + 2, x + 3,y + i * 4 + 2);
                g.setColor(color2_);
                g.drawLine(x + 4,y + i * 4 + 3, x + 4,y + i * 4 + 3);
                g.setColor(color1_);
                g.drawLine(x + 5,y + i * 4 + 4, x + 5,y + i * 4 + 4);
                g.setColor(color2_);
                g.drawLine(x + 6,y + i * 4 + 5, x + 6,y + i * 4 + 5);
            }
        } 
    
        /**
         * Updates colors
         */
        private void updateColors()
        {
            if(UIManager.getLookAndFeel() instanceof MetalLookAndFeel)
            {
                color1_ = MetalLookAndFeel.getControlHighlight();
                color2_ = MetalLookAndFeel.getControlDarkShadow();
                color3_ = MetalLookAndFeel.getControl();
            }
            else
            {
                color1_ = color2_ = color3_ = null;
            }
        } 
    }    
    
    //--------------------------------------------------------------------------
    //  Class FlipButtonLayout 
    //--------------------------------------------------------------------------
    
    /**
     * Button layout for the buttons in the JFlipPane
     */
    public class FlipButtonLayout implements LayoutManager
    {
        private JFlipPane flipPane_;
        
        /**
         * Creates a button layout
         * 
         * @param  flipPane  Enclosing flip pane
         */    
        public FlipButtonLayout(JFlipPane flipPane)
        {
            flipPane_ = flipPane;
        }
        
        /**
         * Adds component to be layed out
         * 
         * @param  name  Name of component
         * @param  comp  Component to layout
         */
        public void addLayoutComponent(String name, Component comp) 
        {
        } 
    
        /**
         * Removes component to be layed out
         * 
         * @param  comp  Component to remove from the layout
         */
        public void removeLayoutComponent(Component comp) 
        {
        } 
        
        /**
         * Gets preferred layout size
         * 
         * @param  parent  Parent container
         * @return Preferred layout size
         */
        public Dimension preferredLayoutSize(Container parent)
        {
            Component[] comp = parent.getComponents();
            
            if(comp.length == 2)
            {
                // nothing 'cept close box and popup button
                return new Dimension(0,0);
            }
            else
            {
                if (flipPane_.getPosition().equals(JFlipPane.TOP) || 
                    flipPane_.getPosition().equals(JFlipPane.BOTTOM))
                    return new Dimension(0,comp[2].getPreferredSize().height);
                else
                    return new Dimension(comp[2].getPreferredSize().width,0);
            }
        } 
        
        /**
         * Retrieves min layout size
         * 
         * @param  parent  Parent container
         * @return Minimum layout size
         */
        public Dimension minimumLayoutSize(Container parent)
        {
            Component[] comp = parent.getComponents();
            if(comp.length == 2)
            {
                // nothing 'cept close box and popup button
                return new Dimension(0,0);
            }
            else
            {
                if (flipPane_.getPosition().equals(JFlipPane.TOP) || 
                    flipPane_.getPosition().equals(JFlipPane.BOTTOM))
                    return new Dimension(0,comp[2].getMinimumSize().height);
                else
                    return new Dimension(comp[2].getMinimumSize().width,0);
            }
        } 
        
        /**
         * Lays out the container
         * 
         * @param  parent  Container to layout
         */
        public void layoutContainer(Container parent)
        {
            Component[] comp = parent.getComponents();
            
            if (comp.length != 2)
            {
                boolean closeBoxSizeSet = false;
                boolean noMore = false;
                flipPane_.getPopupButton().setVisible(false);
    
                Dimension parentSize = parent.getSize();
                int pos = 0;
                
                for (int i = 2; i < comp.length; i++)
                {
                    Dimension size = comp[i].getPreferredSize();
                    
                    if (flipPane_.getPosition().equals(JFlipPane.TOP) || 
                        flipPane_.getPosition().equals(JFlipPane.BOTTOM))
                    {
                        if (!closeBoxSizeSet)
                        {
                            flipPane_.getCloseButton().setBounds(
                                0,0,size.height,size.height);
                                
                            pos += size.height;
                            closeBoxSizeSet = true;
                        }
    
                        if (noMore || pos + size.width > parentSize.width - 
                            (i == comp.length - 1 ? 
                            0 : flipPane_.getCloseButton().getWidth()))
                        {
                            flipPane_.getPopupButton().setBounds(
                                parentSize.width - size.height,
                                0,size.height,size.height);
                                
                            flipPane_.getPopupButton().setVisible(true);
                            comp[i].setVisible(false);
                            noMore = true;
                        }
                        else
                        {
                            comp[i].setBounds(pos,0,size.width,size.height);
                            comp[i].setVisible(true);
                            pos += size.width;
                        }
                    }
                    else
                    {
                        if (!closeBoxSizeSet)
                        {
                            flipPane_.getCloseButton().setBounds(   
                                0,0,size.width,size.width);
                                
                            pos += size.width;
                            closeBoxSizeSet = true;
                        }
    
                        if(noMore || pos + size.height > parentSize.height - 
                            (i == comp.length - 1 ? 0 : 
                            flipPane_.getCloseButton().getHeight()))
                        {
                            flipPane_.getPopupButton().setBounds(
                                0,parentSize.height - size.width,
                                size.width,size.width);
                                
                            flipPane_.getPopupButton().setVisible(true);
                            comp[i].setVisible(false);
                            noMore = true;
                        }
                        else
                        {
                            comp[i].setBounds(0,pos,size.width,size.height);
                            comp[i].setVisible(true);
                            pos += size.height;
                        }
                    }
                }
            }
        } 
    }    

    //--------------------------------------------------------------------------
    //  Class FlipCardPanel 
    //--------------------------------------------------------------------------
    
    /**
     * Card like panel for use in JFlipPane that houses all the flippers
     */
    public static class FlipCardPanel extends JPanel
    {
        private JFlipPane flipPane_;
        
        /**
         * Creates a FlipCardPanel
         * 
         * @param  flipPane  Enclosing JFlipPane
         */
        public FlipCardPanel(JFlipPane flipPane)
        {
            super(new CardLayout());
            flipPane_ = flipPane;
            ResizeMouseHandler resizeMouseHandler = new ResizeMouseHandler();
            addMouseListener(resizeMouseHandler);
            addMouseMotionListener(resizeMouseHandler);
        } 
    
        /**
         * Shows the card with the given name
         * 
         * @param  name  Name of the card
         */
        void showCard(String name)
        {
            ((CardLayout)getLayout()).show(this,name);
        } 
    
        /**
         * @return  Minimum size
         */
        public Dimension getMinimumSize()
        {
            return new Dimension(0,0);
        } 
        
        /**
         * @return  Preferred size
         */
        public Dimension getPreferredSize()
        {
            //String method = "[prfSiz] ";
            
            Dimension pref;
            
            if(flipPane_ == null)
            {
                pref = new Dimension(0,0);
            }
            else
            {
                int    dim = flipPane_.getDimension();
                String pos = flipPane_.getPosition();
                            
                if(flipPane_.getDimension()  <= 0)
                {
                    int width = super.getPreferredSize().width;
                    
                    flipPane_.setDimension(
                        width - JFlipPane.SPLITTER_WIDTH - 3);
                }
    
                if(pos.equals(JFlipPane.TOP) || pos.equals(JFlipPane.BOTTOM))
                {
                    pref = new Dimension(0,
                        dim + JFlipPane.SPLITTER_WIDTH + 3);
                }
                else
                {
                    pref = new Dimension(dim + JFlipPane.SPLITTER_WIDTH + 3, 0);
                }
            }
            

            //fcpLogger_.info(method + "prefsize=" + pref);
            
            return pref;
        } 
    
        /**
         * Mouse handler for resizing of the pane
         */
        class ResizeMouseHandler extends MouseAdapter 
            implements MouseMotionListener
        {
            private boolean canDrag_;
            private int dragStartDimension_;
            private Point dragStart_;
    
            /**
             * Records start of mouse drag and dimension
             */
            public void mousePressed(MouseEvent evt)
            {
                dragStartDimension_ = flipPane_.getDimension();
                dragStart_ = evt.getPoint();
            } 
    
            
            /** 
             * Changes mouse cursor based on location over the draggable part
             * of the border
             */
            public void mouseMoved(MouseEvent evt)
            {
                Border border = getBorder();
                if (border == null)
                {
                    // collapsed
                    return;
                }
    
                Insets insets = border.getBorderInsets(FlipCardPanel.this);
                int cursor = Cursor.DEFAULT_CURSOR;
                canDrag_ = false;
                
                // Top...
                if (flipPane_.getPosition().equals(JFlipPane.TOP))
                {
                    if(evt.getY() >= getHeight() - insets.bottom)
                    {
                        cursor = Cursor.N_RESIZE_CURSOR;
                        canDrag_ = true;
                    }
                } 
                // Left...
                else if (flipPane_.getPosition().equals(JFlipPane.LEFT))
                {
                    if(evt.getX() >= getWidth() - insets.right)
                    {
                        cursor = Cursor.W_RESIZE_CURSOR;
                        canDrag_ = true;
                    }
                } 
                // Bottom...
                else if (flipPane_.getPosition().equals(JFlipPane.BOTTOM))
                {
                    if(evt.getY() <= insets.top)
                    {
                        cursor = Cursor.S_RESIZE_CURSOR;
                        canDrag_ = true;
                    }
                } 
                // Right...
                else if (flipPane_.getPosition().equals(JFlipPane.RIGHT))
                {
                    if(evt.getX() <= insets.left)
                    {
                        cursor = Cursor.E_RESIZE_CURSOR;
                        canDrag_ = true;
                    }
                } 
    
                setCursor(Cursor.getPredefinedCursor(cursor));
            } 
            
            /**
             * Sets dimension on flippane if the mouse is dragged. This causes
             * the flippane to resize dynamically with the drag
             */
            public void mouseDragged(MouseEvent evt)
            {
                if(!canDrag_)
                    return;
    
                if(dragStart_ == null) // can't happen?
                    return;
    
                // Top...
                if(flipPane_.getPosition().equals(JFlipPane.TOP))
                {
                    flipPane_.setDimension(evt.getY() + dragStartDimension_ - 
                        dragStart_.y);
                } 
                // Left...
                else if(flipPane_.getPosition().equals(JFlipPane.LEFT))
                {
                    flipPane_.setDimension(evt.getX() + dragStartDimension_ - 
                        dragStart_.x);
                } 
                // Bottom...
                else if(flipPane_.getPosition().equals(JFlipPane.BOTTOM))
                {
                    flipPane_.setDimension(
                        flipPane_.getDimension() + (dragStart_.y - evt.getY()));
                } 
                // Right...
                else if(flipPane_.getPosition().equals(JFlipPane.RIGHT))
                {
                    flipPane_.setDimension(
                        flipPane_.getDimension() + dragStart_.x - evt.getX());
                } 
    
                if(flipPane_.getDimension() <= 0)
                    flipPane_.setDimension(dragStartDimension_);
    
                // TODO: find out right way to do this
                flipPane_.revalidate();
                 
                //repaint();
                 
                //flipPane_.invalidate();
                //flipPane_.validate();
                //invalidate();
                //validate();
            } 
            
            /**
             * Reset the mouse cursor to the normal cursor 
             */
            public void mouseExited(MouseEvent evt)
            {
                setCursor(Cursor.getPredefinedCursor(
                    Cursor.DEFAULT_CURSOR));
            } 
        } 
    } 
}