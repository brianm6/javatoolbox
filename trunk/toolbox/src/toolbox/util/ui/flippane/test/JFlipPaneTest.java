package toolbox.util.ui.flippane.test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.junit.UITestCase;
import toolbox.util.ui.JSmartButton;
import toolbox.util.ui.JSmartLabel;
import toolbox.util.ui.explorer.JFileExplorer;
import toolbox.util.ui.flippane.FlipPaneListener;
import toolbox.util.ui.flippane.JFlipPane;

/**
 * Unit test for JFlipPane.
 */
public class JFlipPaneTest extends UITestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(JFlipPaneTest.class);

    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    /**
     * Entry point.
     * 
     * @param args None recognized
     * @throws Exception on error
     */    
    public static void main(String[] args) throws Exception
    {
        TestRunner.run(JFlipPaneTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Shows multiple JFlipPanes in a window so it can be tested interactively.
     */
    public void testJFlipPane()
    {
        logger_.info("Running testJFlipPane...");
        
        JPanel p = new JPanel(new BorderLayout());

        p.add(BorderLayout.WEST, createFlipPane(JFlipPane.LEFT));
        p.add(BorderLayout.EAST, createFlipPane(JFlipPane.RIGHT));
        p.add(BorderLayout.NORTH, createFlipPane(JFlipPane.TOP));
        p.add(BorderLayout.SOUTH, createFlipPane(JFlipPane.BOTTOM));
        
        JLabel label = new JSmartLabel("Filler");
        label.setFont(label.getFont().deriveFont((float) 50.0));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBackground(Color.white);
        label.setForeground(Color.blue);
        p.add(label, BorderLayout.CENTER);
        
        launchInDialog(p, UITestCase.SCREEN_TWO_THIRDS);
    }

    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------

    /**
     * Creates a JFlipPane for the given position.
     * 
     * @param pos Position to place the flip pane. 
     *        JFlipPane.TOP|RIGHT|LEFT|RIGHT
     * @return JFlipPane
     */
    protected JFlipPane createFlipPane(String pos)
    {
        JFlipPane fp = new JFlipPane(pos);
        JLabel card1 = new JSmartLabel("Label");
        fp.addFlipper(card1.getText(), card1);
        
        JButton card2 = new JSmartButton("Button");
        card2.setPreferredSize(new Dimension(100, 100));
        card2.setMinimumSize(new Dimension(50, 50));
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