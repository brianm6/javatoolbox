package toolbox.util.ui.test;

import java.awt.BorderLayout;
import java.awt.Container;
import java.io.StringReader;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import nu.xom.Builder;
import nu.xom.Element;

import org.apache.log4j.Logger;

import toolbox.util.SwingUtil;
import toolbox.util.XOMUtil;
import toolbox.util.ui.JSmartTextArea;

/**
 * Unit test for JSmartTextArea
 */
public class JSmartTextAreaTest extends TestCase
{
    private static final Logger logger_ =
        Logger.getLogger(JSmartTextAreaTest.class);
        
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    /** 
     * Entry point
     * 
     * @param  args  None recognized
     * @throws Exception on error
     */
    public static void main(String[] args) throws Exception
    {
        SwingUtil.setPreferredLAF();
        TestRunner.run(JSmartTextAreaTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests 
    //--------------------------------------------------------------------------
    
    /**
     * Tests autoscroll feature
     */
    public void testAutoScroll()
    {
        logger_.info("Running testAutoScroll...");
        
        JDialog dialog = new JDialog(new JFrame(), "testAutoScroll", true);        
        Container cp = dialog.getContentPane();
        cp.setLayout(new BorderLayout());
        cp.add(new JScrollPane(new JSmartTextArea("hello")));
        dialog.setSize(150,150);
        SwingUtil.centerWindow(dialog);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);
    }
    
    /**
     * Tests savePrefs() and applyPrefs()
     * 
     * @throws Exception on error
     */
    public void testSaveApplyPrefs() throws Exception
    {
        logger_.info("Running testSaveApplyPrefs...");
        
        JSmartTextArea before = new JSmartTextArea("", true, true);
        before.setFont(SwingUtil.getPreferredMonoFont());
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
        assertEquals(before.isAntiAlias(), after.isAntiAlias());
        assertEquals(before.isAutoScroll(), after.isAutoScroll());
        assertEquals(before.getCapacity(), after.getCapacity());
        assertEquals(before.getPruneFactor(), after.getPruneFactor());
        assertEquals(before.getFont().getName(), after.getFont().getName());
        assertEquals(before.getFont().getSize(), after.getFont().getSize());
        assertEquals(before.getFont().getStyle(), after.getFont().getStyle());
    }
}
