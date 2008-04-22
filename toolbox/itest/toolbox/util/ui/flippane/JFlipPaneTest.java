package toolbox.util.ui.flippane;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.io.StringReader;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import junit.textui.TestRunner;

import nu.xom.Builder;
import nu.xom.Element;

import org.apache.log4j.Logger;

import toolbox.junit.testcase.UITestCase;
import toolbox.util.XOMUtil;
import toolbox.util.ui.ImageCache;
import toolbox.util.ui.JSmartButton;
import toolbox.util.ui.JSmartLabel;
import toolbox.util.ui.explorer.JFileExplorer;

/**
 * Unit test for {@link toolbox.util.ui.flippane.JFlipPane}.
 */
public class JFlipPaneTest extends UITestCase
{
    private static final Logger logger_ = Logger.getLogger(JFlipPaneTest.class);

    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
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

    
    /**
     * Tests savePrefs() and applyPrefs().
     * 
     * @throws Exception on error.
     */
    public void testSaveApplyPrefs() throws Exception
    {
        logger_.debug("Running testSaveApplyPrefs...");
        
        JFlipPane before = new JFlipPane(JFlipPane.LEFT);
        before.addFlipper("1", new JSmartLabel("Flipper1"));
        before.addFlipper("2", new JSmartLabel("Flipper2"));
        before.addFlipper("3", new JSmartLabel("Flipper3"));
        before.setCollapsed(false);
        before.setDimension(200);
        before.setActiveFlipper("1");
        
        //
        // Serialize to XML
        //
        Element root = new Element("root");
        before.savePrefs(root);
        String xml = XOMUtil.toXML(root);
        logger_.debug("\n" + xml);
        
        // 
        // Hydrate from XML
        //
        JFlipPane after = new JFlipPane(JFlipPane.LEFT);
        after.addFlipper("1", new JSmartLabel("Flipper1"));
        after.addFlipper("2", new JSmartLabel("Flipper2"));
        after.addFlipper("3", new JSmartLabel("Flipper3"));
        
        after.applyPrefs(new Builder().build(
            new StringReader(xml)).getRootElement());
        
        
        //
        // Compare the "after" properties to the "before" ones
        //
        assertEquals(before.isCollapsed(), after.isCollapsed());
        assertEquals(before.getDimension(), after.getDimension());
        assertEquals(before.getActiveFlipper(), after.getActiveFlipper());
    }
    
    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------

    /**
     * Creates a JFlipPane for the given position.
     * 
     * @param pos Position to place the flip pane.JFlipPane.TOP|RIGHT|LEFT|RIGHT
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
        
        fp.addFlipper(
            ImageCache.getIcon(ImageCache.IMAGE_DUKE), 
            "File Explorer", 
            explorer);
        
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