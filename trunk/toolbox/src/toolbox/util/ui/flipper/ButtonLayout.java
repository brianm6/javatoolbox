package toolbox.util.ui.flipper;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;


public class ButtonLayout implements LayoutManager
{
    private JFlipPane flipPane_;
    
    public ButtonLayout(JFlipPane flipPane)
    {
        flipPane_ = flipPane;
    }
    
    // addLayoutComponent() method
    public void addLayoutComponent(String name, Component comp) {} 

    // removeLayoutComponent() method
    public void removeLayoutComponent(Component comp) {} 

    // preferredLayoutSize() method
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
            if(flipPane_.getPosition().equals(JFlipPane.TOP)
                || flipPane_.getPosition().equals(JFlipPane.BOTTOM))
            {
                return new Dimension(0,comp[2].getPreferredSize().height);
            }
            else
            {
                return new Dimension(comp[2].getPreferredSize().width,0);
            }
        }
    } 

    // minimumLayoutSize() method
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
            if(flipPane_.getPosition().equals(JFlipPane.TOP)
                || flipPane_.getPosition().equals(JFlipPane.BOTTOM))
            {
                return new Dimension(0,comp[2].getMinimumSize().height);
            }
            else
            {
                return new Dimension(comp[2].getMinimumSize().width,0);
            }
        }
    } 

    // layoutContainer() method
    public void layoutContainer(Container parent)
    {
        Component[] comp = parent.getComponents();
        if(comp.length != 2)
        {
            boolean closeBoxSizeSet = false;
            boolean noMore = false;
            flipPane_.getPopupButton().setVisible(false);

            Dimension parentSize = parent.getSize();
            int pos = 0;
            for(int i = 2; i < comp.length; i++)
            {
                Dimension size = comp[i].getPreferredSize();
                if(flipPane_.getPosition().equals(JFlipPane.TOP)
                    || flipPane_.getPosition().equals(JFlipPane.BOTTOM))
                {
                    if(!closeBoxSizeSet)
                    {
                        flipPane_.getCloseButton().setBounds(0,0,size.height,size.height);
                        pos += size.height;
                        closeBoxSizeSet = true;
                    }

                    if(noMore || pos + size.width > parentSize.width
                        - (i == comp.length - 1
                        ? 0 : flipPane_.getCloseButton().getWidth()))
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
                    if(!closeBoxSizeSet)
                    {
                        flipPane_.getCloseButton().setBounds(0,0,size.width,size.width);
                        pos += size.width;
                        closeBoxSizeSet = true;
                    }

                    if(noMore || pos + size.height > parentSize.height
                        - (i == comp.length - 1
                        ? 0 : flipPane_.getCloseButton().getHeight()))
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