package toolbox.util.ui.test;

import java.awt.BorderLayout;
import java.awt.Container;
import java.io.StringReader;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Serializer;

import toolbox.util.SwingUtil;
import toolbox.util.io.StringOutputStream;
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
        
        JFrame frame = new JFrame("testAutoScroll");        
        Container cp = frame.getContentPane();
        cp.setLayout(new BorderLayout());
        cp.add(new JScrollPane(new JSmartTextArea("hello")));
        frame.pack();
        frame.setVisible(true);
        SwingUtil.centerWindow(frame);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    /**
     * Tests savePrefs() and applyPrefs()
     */
    public void testSaveApplyPrefs() throws Exception
    {
        logger_.info("Running testSaveApplyPrefs...");
        
        JSmartTextArea before = new JSmartTextArea("", true, true);
        before.setFont(SwingUtil.getPreferredMonoFont());
        
        //
        // Serialize to XML
        //
        Element root = new Element("root");
        before.savePrefs(root);
        StringOutputStream sos = new StringOutputStream();
        Serializer serializer = new Serializer(sos, "UTF-8");
        serializer.setIndent(4);
        serializer.write(new Document(root));
        String xml = sos.toString();
        logger_.info("\n" + xml);
        
        // 
        // Hydrate from XML
        //
        Builder builder = new Builder();
        Element prefs = builder.build(new StringReader(xml)).getRootElement();
        JSmartTextArea after = new JSmartTextArea();
        after.applyPrefs(prefs);
        
        //
        // Compare the "after" properties to the "before" ones
        //
        assertEquals(before.isAntiAlias(), after.isAntiAlias());
        assertEquals(before.isAutoScroll(), after.isAutoScroll());
        assertEquals(before.getFont().getName(), after.getFont().getName());
        assertEquals(before.getFont().getSize(), after.getFont().getSize());
        assertEquals(before.getFont().getStyle(), after.getFont().getStyle());
    }
}
