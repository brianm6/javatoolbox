package toolbox.util.ui.flipper;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.apache.log4j.Category;

import toolbox.util.ui.JFileExplorer;

/**
 * 
 */
public class FlipPaneTest extends JFrame
{
    private static final Category logger_ = 
        Category.getInstance(FlipPaneTest.class);

    
    public static void main(String[] args)
    {
        JFrame frame = new FlipPaneTest();
        frame.setSize(400,400);
        frame.setVisible(true);
    }

    /**
     * Constructor for FlipPaneWindow.
     */
    public FlipPaneTest()
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
        
        JFlipPane fp = new JFlipPane(JFlipPane.LEFT);
        JLabel card1 = new JLabel("Label");
        fp.addFlipper(card1.getText(), card1);
        
        JButton card2 = new JButton("Button");
        card2.setPreferredSize(new Dimension(100,100));
        card2.setMinimumSize(new Dimension(50,50));
        fp.addFlipper(card2.getText(), card2);
        
        JFileExplorer explorer = new JFileExplorer(false);
        fp.addFlipper("File Explorer", explorer);
        
        c.add(BorderLayout.WEST, fp);
        
        JLabel label = new JLabel("Filler");
        label.setBackground(Color.white);
        label.setForeground(Color.blue);
        c.add(BorderLayout.CENTER, label);
        
        
        fp.addFlipPaneListener(new IFlipPaneListener()
        {
            /**
             * @see toolbox.util.ui.flipper.IFlipPaneListener#flipperCollapsed(JFlipPane)
             */
            public void flipperCollapsed(JFlipPane flipPane)
            {
                logger_.info("Flipper collapsed");
            }

            /**
             * @see toolbox.util.ui.flipper.IFlipPaneListener#flipperExpanded(JFlipPane)
             */
            public void flipperExpanded(JFlipPane flipPane)
            {
                logger_.info("Flipper expanded");
            }
        });
    }
}
