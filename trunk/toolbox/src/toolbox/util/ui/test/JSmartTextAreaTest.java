package toolbox.util.ui.test;

import java.awt.BorderLayout;
import java.io.StringReader;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import junit.textui.TestRunner;

import nu.xom.Builder;
import nu.xom.Element;

import org.apache.log4j.Logger;

import toolbox.junit.UITestCase;
import toolbox.util.FontUtil;
import toolbox.util.XOMUtil;
import toolbox.util.ui.JSmartTextArea;

/**
 * Unit test for JSmartTextArea.
 */
public class JSmartTextAreaTest extends UITestCase
{
    // TODO: This test needs more interactive ways to test.
    
    private static final Logger logger_ =
        Logger.getLogger(JSmartTextAreaTest.class);
        
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    /** 
     * Entry point.
     * 
     * @param args None recognized
     */
    public static void main(String[] args) 
    {
        TestRunner.run(JSmartTextAreaTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests 
    //--------------------------------------------------------------------------
    
    /**
     * Tests autoscroll feature.
     */
    public void testAutoScroll()
    {
        logger_.info("Running testAutoScroll...");
        
        JPanel cp = new JPanel(new BorderLayout());
        cp.add(new JScrollPane(new JSmartTextArea("hello")));
        launchInDialog(cp, SCREEN_ONE_THIRD);
    }
    
    
    /**
     * Tests savePrefs() and applyPrefs().
     * 
     * @throws Exception on error
     */
    public void testSaveApplyPrefs() throws Exception
    {
        logger_.info("Running testSaveApplyPrefs...");
        
        JSmartTextArea before = new JSmartTextArea("", true, true);
        before.setFont(FontUtil.getPreferredMonoFont());
        before.setCapacity(10000);
        before.setPruneFactor(50);
        
        //
        // Serialize to XML
        //
        Element root = new Element("root");
        before.savePrefs(root);
        String xml = XOMUtil.toXML(root);
        logger_.info("\n" + xml);
        
        // 
        // Hydrate from XML
        //
        JSmartTextArea after = new JSmartTextArea();
        after.applyPrefs(
            new Builder().build(new StringReader(xml)).getRootElement());
        
        //
        // Compare the "after" properties to the "before" ones
        //
        assertEquals(before.isAntiAliased(), after.isAntiAliased());
        assertEquals(before.isAutoScroll(), after.isAutoScroll());
        assertEquals(before.getCapacity(), after.getCapacity());
        assertEquals(before.getPruneFactor(), after.getPruneFactor());
        assertEquals(before.getFont().getName(), after.getFont().getName());
        assertEquals(before.getFont().getSize(), after.getFont().getSize());
        assertEquals(before.getFont().getStyle(), after.getFont().getStyle());
    }
}