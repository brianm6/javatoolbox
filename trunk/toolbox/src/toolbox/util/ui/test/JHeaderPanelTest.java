package toolbox.util.ui.test;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.junit.UITestCase;
import toolbox.util.ui.JCollapsablePanel;
import toolbox.util.ui.JHeaderPanel;
import toolbox.util.ui.JSmartLabel;

/**
 * Unit test for JHeaderPanel.
 */
public class JHeaderPanelTest extends UITestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(JHeaderPanelTest.class);
        
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    /** 
     * Entry point.
     * 
     * @param args None recognized.
     */
    public static void main(String[] args) 
    {
        TestRunner.run(JHeaderPanelTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests 
    //--------------------------------------------------------------------------
    
    /**
     * Tests autoscroll feature.
     * 
     * @throws Exception on error.
     */
    public void testHeaderPanel() throws Exception
    {
        logger_.info("Running testHeaderPanel...");
        
        JPanel cp = new JPanel(new BorderLayout());
        JHeaderPanel hp = new JCollapsablePanel("Header Panel 1");
        hp.setContent(new JSmartLabel("Header Panel Content"));
        cp.add(hp, BorderLayout.NORTH);
        cp.add(new JScrollPane(createPropertySheet(hp)), BorderLayout.CENTER);
        launchInDialog(cp, SCREEN_ONE_THIRD);
    }
}