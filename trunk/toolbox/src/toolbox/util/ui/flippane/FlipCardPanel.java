package toolbox.util.ui.flippane;

import java.awt.CardLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;
import javax.swing.border.Border;

/**
 * Card like panel for use in JFlipPane that houses all the flippers
 */
public class FlipCardPanel extends JPanel
{
    /** Flippane encompassing this panel */
    private JFlipPane flipPane_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
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

    //--------------------------------------------------------------------------
    // Package
    //--------------------------------------------------------------------------
    
    /**
     * Shows the card with the given name
     * 
     * @param  name  Name of the card
     */
    void showCard(String name)
    {
        ((CardLayout)getLayout()).show(this,name);
    } 

    //--------------------------------------------------------------------------
    // Overrides javax.swing.JComponent
    //--------------------------------------------------------------------------
    
    /**
     * @return  Minimum size
     */
    public Dimension getMinimumSize()
    {
        return new Dimension(0,0);
    } 
    
    /**
     * Returns the preferred size of the flippane. Takes the width of the 
     * button bar and the current width of the flipper (assuming its LEFT or
     * RIGHT), adds them together and thats it.
     * 
     * @return  Preferred size of the flippane
     */
    public Dimension getPreferredSize()
    {
        Dimension pref;
        
        if (flipPane_ == null)
        {
            pref = new Dimension(0,0);
        }
        else
        {
            // Use pos to figure out if we're working on a height or a width
            int    dim = flipPane_.getDimension();
            String pos = flipPane_.getPosition();
                        
            if (flipPane_.getDimension()  <= 0)
            {
                int width = super.getPreferredSize().width;
                
                flipPane_.setDimension(
                    width - JFlipPane.SPLITTER_WIDTH - 3);
            }

            if (pos.equals(JFlipPane.TOP) || pos.equals(JFlipPane.BOTTOM))
            {
                pref = new Dimension(0,
                    dim + JFlipPane.SPLITTER_WIDTH + 3);
            }
            else
            {
                pref = new Dimension(dim + JFlipPane.SPLITTER_WIDTH + 3, 0);
            }
        }
        
        return pref;
    } 

    //--------------------------------------------------------------------------
    // Inner Classes
    //--------------------------------------------------------------------------
    
    /**
     * Mouse handler for resizing of the pane
     */
    class ResizeMouseHandler extends MouseAdapter implements MouseMotionListener
    {
        private boolean canDrag_;
        private int     dragStartDimension_;
        private Point   dragStart_;

        /**
         * Takes an image of the flip pane dimension (height or width) and 
         * the point that the mouse started to be dragged
         * 
         * @param  evt  Mouse pressed event
         */
        public void mousePressed(MouseEvent evt)
        {
            dragStartDimension_ = 
                flipPane_.getDimension() + JFlipPane.SPLITTER_WIDTH + 3;
                
            dragStart_ = evt.getPoint();
        } 
        
        /** 
         * Changes mouse cursor based on location over the draggable part of 
         * the border
         * 
         * @param  evt  Mouse moved event
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
                if (evt.getY() >= getHeight() - insets.bottom)
                {
                    cursor = Cursor.N_RESIZE_CURSOR;
                    canDrag_ = true;
                }
            } 
            // Left...
            else if (flipPane_.getPosition().equals(JFlipPane.LEFT))
            {
                if (evt.getX() >= getWidth() - insets.right)
                {
                    cursor = Cursor.W_RESIZE_CURSOR;
                    canDrag_ = true;
                }
            } 
            // Bottom...
            else if (flipPane_.getPosition().equals(JFlipPane.BOTTOM))
            {
                if (evt.getY() <= insets.top)
                {
                    cursor = Cursor.S_RESIZE_CURSOR;
                    canDrag_ = true;
                }
            } 
            // Right...
            else if (flipPane_.getPosition().equals(JFlipPane.RIGHT))
            {
                if (evt.getX() <= insets.left)
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
         * 
         * @param  evt  Mouse dragged event
         */
        public void mouseDragged(MouseEvent evt)
        {
            if (!canDrag_)
                return;

            if (dragStart_ == null) // can't happen?
                return;

            // Top...
            if (flipPane_.getPosition().equals(JFlipPane.TOP))
            {
                flipPane_.setDimension(
                    evt.getY() + dragStartDimension_ - dragStart_.y);
            } 
            // Left...
            else if (flipPane_.getPosition().equals(JFlipPane.LEFT))
            {
                flipPane_.setDimension(
                    evt.getX() + dragStartDimension_ - dragStart_.x);
            } 
            // Bottom...
            else if (flipPane_.getPosition().equals(JFlipPane.BOTTOM))
            {
                flipPane_.setDimension(
                    flipPane_.getDimension() + (dragStart_.y - evt.getY()));
            } 
            // Right...
            else if (flipPane_.getPosition().equals(JFlipPane.RIGHT))
            {
                flipPane_.setDimension(
                    flipPane_.getDimension() + dragStart_.x - evt.getX());
            } 

            if (flipPane_.getDimension() <= 0)
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
         * Reset the mouse cursor to the normal cursor once dragging is
         * is completed.
         * 
         * @param  evet  Mouse exited event 
         */
        public void mouseExited(MouseEvent evt)
        {
            setCursor(Cursor.getPredefinedCursor(
                Cursor.DEFAULT_CURSOR));
        } 
    } 
}