package toolbox.util.ui.flippane.test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.SwingUtil;
import toolbox.util.ui.JFileExplorer;
import toolbox.util.ui.JSmartButton;
import toolbox.util.ui.JSmartLabel;
import toolbox.util.ui.flippane.FlipPaneListener;
import toolbox.util.ui.flippane.JFlipPane;

/**
 * Unit test for JFlipPane
 */
public class JFlipPaneTest extends TestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(JFlipPaneTest.class);

    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    /**
     * Entry point
     * 
     * @param args None
     * @throws Exception on error
     */    
    public static void main(String[] args) throws Exception
    {
        SwingUtil.setPreferredLAF();
        TestRunner.run(JFlipPaneTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    public void testJFlipPane()
    {
        logger_.info("Running testJFlipPane...");
        
        Tester tester = new Tester();
        tester.setSize(800, 600);
        SwingUtil.centerWindow(tester);
        tester.setVisible(true);
    }
    
    //--------------------------------------------------------------------------
    // Inner Classes
    //--------------------------------------------------------------------------
        
    class Tester extends JDialog
    {
        public Tester()
        {
            super(new JFrame(), "testJFlipPane", true);
            setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            Container c = getContentPane();
            
            c.setLayout(new BorderLayout());
            
            c.add(BorderLayout.WEST, createFlipPane(JFlipPane.LEFT));
            c.add(BorderLayout.EAST, createFlipPane(JFlipPane.RIGHT));
            c.add(BorderLayout.NORTH, createFlipPane(JFlipPane.TOP));
            c.add(BorderLayout.SOUTH, createFlipPane(JFlipPane.BOTTOM));
            
            JLabel label = new JSmartLabel("Filler");
            label.setFont(label.getFont().deriveFont((float)50.0));
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setBackground(Color.white);
            label.setForeground(Color.blue);
            c.add(label, BorderLayout.CENTER);
        }
    
        protected JFlipPane createFlipPane(String pos)
        {
            JFlipPane fp = new JFlipPane(pos);
            JLabel card1 = new JSmartLabel("Label");
            fp.addFlipper(card1.getText(), card1);
            
            JButton card2 = new JSmartButton("Button");
            card2.setPreferredSize(new Dimension(100,100));
            card2.setMinimumSize(new Dimension(50,50));
            fp.addFlipper(card2.getText(), card2);
            
            JFileExplorer explorer = new JFileExplorer(false);
            fp.addFlipper("File Explorer", explorer);
            
            fp.addFlipPaneListener(new FlipPaneListener()
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
}