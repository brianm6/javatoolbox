package toolbox.util.ui.flipper;

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


// DockablePanel class
public class FlipCardPanel extends JPanel
{
    private JFlipPane flipPane_;
    
    
    // DockablePanel constructor
    FlipCardPanel(JFlipPane flipPane)
    {
        super(new CardLayout());

        flipPane_ = flipPane;
        
        ResizeMouseHandler resizeMouseHandler = new ResizeMouseHandler();
        addMouseListener(resizeMouseHandler);
        addMouseMotionListener(resizeMouseHandler);
    } 

    // showDockable() method
    void showDockable(String name)
    {
        ((CardLayout)getLayout()).show(this,name);
    } 

    // getMinimumSize() method
    public Dimension getMinimumSize()
    {
        return new Dimension(0,0);
    } 

    // getPreferredSize() method
    public Dimension getPreferredSize()
    {
        if(flipPane_ == null)
            return new Dimension(0,0);
        else
        {
            int    dim = flipPane_.getDimension();
            String pos = flipPane_.getPosition();
                        
            if(flipPane_.getDimension()  <= 0)
            {
                int width = super.getPreferredSize().width;
                flipPane_.setDimension(width - JFlipPane.SPLITTER_WIDTH - 3);
            }

            if(pos.equals(JFlipPane.TOP) || pos.equals(JFlipPane.BOTTOM))
            {
                return new Dimension(0,
                    dim + JFlipPane.SPLITTER_WIDTH + 3);
            }
            else
            {
                return new Dimension(dim + JFlipPane.SPLITTER_WIDTH + 3, 0);
            }
        }
    } 

    // ResizeMouseHandler class
    class ResizeMouseHandler extends MouseAdapter implements MouseMotionListener
    {
        boolean canDrag;
        int dragStartDimension;
        Point dragStart;

        // mousePressed() method
        public void mousePressed(MouseEvent evt)
        {
            dragStartDimension = flipPane_.getDimension();
            dragStart = evt.getPoint();
        } 

        // mouseMoved() method
        public void mouseMoved(MouseEvent evt)
        {
            Border border = getBorder();
            if(border == null)
            {
                // collapsed
                return;
            }

            Insets insets = border.getBorderInsets(FlipCardPanel.this);
            int cursor = Cursor.DEFAULT_CURSOR;
            canDrag = false;
            // Top...
            if(flipPane_.getPosition().equals(JFlipPane.TOP))
            {
                if(evt.getY() >= getHeight() - insets.bottom)
                {
                    cursor = Cursor.N_RESIZE_CURSOR;
                    canDrag = true;
                }
            } 
            // Left...
            else if(flipPane_.getPosition().equals(JFlipPane.LEFT))
            {
                if(evt.getX() >= getWidth() - insets.right)
                {
                    cursor = Cursor.W_RESIZE_CURSOR;
                    canDrag = true;
                }
            } 
            // Bottom...
            else if(flipPane_.getPosition().equals(JFlipPane.BOTTOM))
            {
                if(evt.getY() <= insets.top)
                {
                    cursor = Cursor.S_RESIZE_CURSOR;
                    canDrag = true;
                }
            } 
            // Right...
            else if(flipPane_.getPosition().equals(JFlipPane.RIGHT))
            {
                if(evt.getX() <= insets.left)
                {
                    cursor = Cursor.E_RESIZE_CURSOR;
                    canDrag = true;
                }
            } 

            setCursor(Cursor.getPredefinedCursor(cursor));
        } 

        // mouseDragged() method
        public void mouseDragged(MouseEvent evt)
        {
            if(!canDrag)
                return;

            if(dragStart == null) // can't happen?
                return;

            // Top...
            if(flipPane_.getPosition().equals(JFlipPane.TOP))
            {
                flipPane_.setDimension(evt.getY()
                    + dragStartDimension
                    - dragStart.y);
            } 
            // Left...
            else if(flipPane_.getPosition().equals(JFlipPane.LEFT))
            {
                flipPane_.setDimension(evt.getX()
                    + dragStartDimension
                    - dragStart.x);
            } 
            // Bottom...
            else if(flipPane_.getPosition().equals(JFlipPane.BOTTOM))
            {
                flipPane_.setDimension(flipPane_.getDimension() + (dragStart.y - evt.getY()));
            } 
            // Right...
            else if(flipPane_.getPosition().equals(JFlipPane.RIGHT))
            {
                flipPane_.setDimension(flipPane_.getDimension() + dragStart.x - evt.getX());
            } 

            if(flipPane_.getDimension() <= 0)
                flipPane_.setDimension(dragStartDimension);

            invalidate();
            validate();
        } 

        // mouseExited() method
        public void mouseExited(MouseEvent evt)
        {
            setCursor(Cursor.getPredefinedCursor(
                Cursor.DEFAULT_CURSOR));
        } 
    } 
} 
