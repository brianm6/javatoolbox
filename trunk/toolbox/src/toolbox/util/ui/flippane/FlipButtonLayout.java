package toolbox.util.ui.flippane;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;

/**
 * Button layout for the buttons in the JFlipPane.
 */
public class FlipButtonLayout implements LayoutManager
{
    /** 
     * Flippane to layout. 
     */
    private JFlipPane flipPane_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a button layout.
     * 
     * @param flipPane Enclosing flip pane
     */    
    public FlipButtonLayout(JFlipPane flipPane)
    {
        flipPane_ = flipPane;
    }
    
    //--------------------------------------------------------------------------
    // LayoutManager Interface
    //--------------------------------------------------------------------------
    
    /**
     * Adds component to be layed out.
     * 
     * @param name Name of component
     * @param comp Component to layout
     */
    public void addLayoutComponent(String name, Component comp) 
    {
    } 


    /**
     * Removes component to be layed out.
     * 
     * @param comp Component to remove from the layout
     */
    public void removeLayoutComponent(Component comp) 
    {
    } 

    
    /**
     * Gets preferred layout size.
     * 
     * @param parent Parent container
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
     * Retrieves min layout size.
     * 
     * @param parent Parent container
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
     * Lays out the container.
     * 
     * @param parent Container to layout
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

                    if (noMore || pos + size.height > parentSize.height - 
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