package toolbox.util.ui.flipper;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.metal.MetalLookAndFeel;


// FlipPaneBorder class
public class FlipPaneBorder implements Border
{
    private static final int SPLITTER_WIDTH = 10;
    
    String position;
    Insets insets;
    Color color1;
    Color color2;
    Color color3;

    // FlipPaneBorder constructor
    FlipPaneBorder(String position)
    {
        this.position = position;
        insets = new Insets(
            position.equals(JFlipPane.BOTTOM)
                ? SPLITTER_WIDTH : 0,
            position.equals(JFlipPane.RIGHT)
                ? SPLITTER_WIDTH : 0,
            position.equals(JFlipPane.TOP)
                ? SPLITTER_WIDTH : 0,
            position.equals(JFlipPane.LEFT)
                ? SPLITTER_WIDTH : 0);
    } 

    // paintBorder() method
    public void paintBorder(Component c, Graphics g,
        int x, int y, int width, int height)
    {
        updateColors();

        if(color1 == null || color2 == null || color3 == null)
            return;

        if(position.equals(JFlipPane.BOTTOM))
            paintHorizBorder(g,x,y,width);
        else if(position.equals(JFlipPane.RIGHT))
            paintVertBorder(g,x,y,height);
        else if(position.equals(JFlipPane.TOP))
        {
            paintHorizBorder(g,x,y + height - SPLITTER_WIDTH,width);
        }
        else if(position.equals(JFlipPane.LEFT))
        {
            paintVertBorder(g,x + width - SPLITTER_WIDTH,y,height);
        }
    } 

    // getBorderInsets() method
    public Insets getBorderInsets(Component c)
    {
        return insets;
    } 

    // isBorderOpaque() method
    public boolean isBorderOpaque()
    {
        return false;
    } 

    // paintHorizBorder() method
    private void paintHorizBorder(Graphics g, int x, int y, int width)
    {
        g.setColor(color3);
        g.fillRect(x,y,width,SPLITTER_WIDTH);

        for(int i = 0; i < width / 4 - 1; i++)
        {
            g.setColor(color1);
            g.drawLine(x + i * 4 + 2,y + 3,
                x + i * 4 + 2,y + 3);
            g.setColor(color2);
            g.drawLine(x + i * 4 + 3,y + 4,
                x + i * 4 + 3,y + 4);
            g.setColor(color1);
            g.drawLine(x + i * 4 + 4,y + 5,
                x + i * 4 + 4,y + 5);
            g.setColor(color2);
            g.drawLine(x + i * 4 + 5,y + 6,
                x + i * 4 + 5,y + 6);
        }
    } 

    // paintVertBorder() method
    private void paintVertBorder(Graphics g, int x, int y, int height)
    {
        g.setColor(color3);
        g.fillRect(x,y,SPLITTER_WIDTH,height);

        for(int i = 0; i < height / 4 - 1; i++)
        {
            g.setColor(color1);
            g.drawLine(x + 3,y + i * 4 + 2,
                x + 3,y + i * 4 + 2);
            g.setColor(color2);
            g.drawLine(x + 4,y + i * 4 + 3,
                x + 4,y + i * 4 + 3);
            g.setColor(color1);
            g.drawLine(x + 5,y + i * 4 + 4,
                x + 5,y + i * 4 + 4);
            g.setColor(color2);
            g.drawLine(x + 6,y + i * 4 + 5,
                x + 6,y + i * 4 + 5);
        }
    } 

    // updateColors() method
    private void updateColors()
    {
        if(UIManager.getLookAndFeel() instanceof MetalLookAndFeel)
        {
            color1 = MetalLookAndFeel.getControlHighlight();
            color2 = MetalLookAndFeel.getControlDarkShadow();
            color3 = MetalLookAndFeel.getControl();
        }
        else
        {
            color1 = color2 = color3 = null;
        }
    } 
}