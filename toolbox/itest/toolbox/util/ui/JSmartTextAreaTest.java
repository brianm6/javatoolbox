package toolbox.util.ui;

import java.io.StringReader;

import javax.swing.JScrollPane;

import junit.textui.TestRunner;

import nu.xom.Builder;
import nu.xom.Element;

import org.apache.log4j.Logger;

import toolbox.junit.testcase.UITestCase;
import toolbox.util.FontUtil;
import toolbox.util.XOMUtil;
import toolbox.util.ui.splitpane.JMultiSplitPane;
import toolbox.util.ui.splitpane.SplitOrientation;

/**
 * Unit test for {@link toolbox.util.ui.JSmartTextArea}.
 */
public class JSmartTextAreaTest extends UITestCase
{
    // TODO: This test needs more interactive ways to test.
    
    private static final Logger logger_ =
        Logger.getLogger(JSmartTextAreaTest.class);
        
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    public static void main(String[] args) 
    {
        TestRunner.run(JSmartTextAreaTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests 
    //--------------------------------------------------------------------------
    
    /**
     * Tests autoscroll feature.
     * 
     * @throws Exception on error.
     */
    public void testAutoScroll() throws Exception
    {
        logger_.info("Running testAutoScroll...");
        
        JMultiSplitPane jms = new JMultiSplitPane(3, SplitOrientation.VERTICAL);
        JSmartTextArea area = new JSmartTextArea("hello\nworld!");
        
        jms.setComponent(0, new JHeaderPanel(
            "Test Component", null, new JScrollPane(area)));
        
        jms.setComponent(1, createPropertySheet(area));
        jms.setComponent(2, createPropertyChangeConsole(area));

        jms.distributeEvenly();
        
        launchInDialog(jms, UITestCase.SCREEN_ONE_HALF);
    }
    
    
    /**
     * Tests savePrefs() and applyPrefs().
     * 
     * @throws Exception on error.
     */
    public void testSaveApplyPrefs() throws Exception
    {
        logger_.info("Running testSaveApplyPrefs...");
        
        JSmartTextArea before = new JSmartTextArea("", true, true);
        before.setFont(FontUtil.getPreferredMonoFont());
        before.setCapacity(10000);
        before.setPruningFactor(50);
        
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
        JSmartTextArea after = new JSmartTextArea();
        after.applyPrefs(
            new Builder().build(new StringReader(xml)).getRootElement());
        
        //
        // Compare the "after" properties to the "before" ones
        //
        assertEquals(before.isAntiAliased(), after.isAntiAliased());
        assertEquals(before.isAutoTail(), after.isAutoTail());
        assertEquals(before.getCapacity(), after.getCapacity());
        assertEquals(before.getPruningFactor(), after.getPruningFactor());
        assertEquals(before.getFont().getName(), after.getFont().getName());
        assertEquals(before.getFont().getSize(), after.getFont().getSize());
        assertEquals(before.getFont().getStyle(), after.getFont().getStyle());
    }
}