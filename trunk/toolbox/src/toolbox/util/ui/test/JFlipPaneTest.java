package toolbox.util.ui.test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.apache.log4j.Category;
import toolbox.util.ui.JFileExplorer;
import toolbox.util.ui.JFlipPane;

/**
 * Unit test for JFlipPane
 */
public class JFlipPaneTest extends JFrame
{
    /** Logger **/
    private static final Category logger_ = 
        Category.getInstance(JFlipPaneTest.class);


    /**
     * Entry point
     */    
    public static void main(String[] args)
    {
        JFrame frame = new JFlipPaneTest();
        frame.setSize(400,400);
        frame.setVisible(true);
    }

    /**
     * Constructor
     */
    public JFlipPaneTest()
    {
        init();
        buildView();
    }
    
    protected void init()
    {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    
    protected void buildView()
    {
        Container c = getContentPane();
        
        c.setLayout(new BorderLayout());
        
        c.add(BorderLayout.WEST, createFlipPane(JFlipPane.LEFT));
        c.add(BorderLayout.EAST, createFlipPane(JFlipPane.RIGHT));
        c.add(BorderLayout.NORTH, createFlipPane(JFlipPane.TOP));
        c.add(BorderLayout.SOUTH, createFlipPane(JFlipPane.BOTTOM));
        
        JLabel label = new JLabel("Filler");
        label.setBackground(Color.white);
        label.setForeground(Color.blue);
        c.add(BorderLayout.CENTER, label);
    }
    
    
    protected JFlipPane createFlipPane(String pos)
    {
        JFlipPane fp = new JFlipPane(pos);
        JLabel card1 = new JLabel("Label");
        fp.addFlipper(card1.getText(), card1);
        
        JButton card2 = new JButton("Button");
        card2.setPreferredSize(new Dimension(100,100));
        card2.setMinimumSize(new Dimension(50,50));
        fp.addFlipper(card2.getText(), card2);
        
        JFileExplorer explorer = new JFileExplorer(false);
        fp.addFlipper("File Explorer", explorer);
        
        fp.addFlipPaneListener(new JFlipPane.JFlipPaneListener()
        {
            public void collapsed(JFlipPane flipPane)
            {
                logger_.info("Flipper collapsed");
            }

            public void expanded(JFlipPane flipPane)
            {
                logger_.info("Flipper expanded");
            }
        });
        
        return fp;
    }    
}