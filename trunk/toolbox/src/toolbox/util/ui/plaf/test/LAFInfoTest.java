package toolbox.util.ui.plaf.test;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import nu.xom.Element;

import org.apache.log4j.Logger;

import toolbox.util.ElapsedTime;
import toolbox.util.StringUtil;
import toolbox.util.XOMUtil;
import toolbox.util.ui.plaf.LAFInfo;

/**
 * Unit test for LAFInfoTest.
 */
public class LAFInfoTest extends TestCase
{
    private static final Logger logger_ =
        Logger.getLogger(LAFInfoTest.class);
    
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    /**
     * Entrypoint.
     * 
     * @param args None recognized.
     */
    public static void main(String[] args)
    {
        TestRunner.run(LAFInfoTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests the default constructor.
     * 
     * @throws Exception on error.
     */
    public void testConstructorDefault() throws Exception
    {
        logger_.info("Running testConstructorDefault...");
        
        LAFInfo info = new LAFInfo();
        assertNotNull(info);
    }

    
    /**
     * Tests the Element constructor.
     * 
     * @throws Exception on error. 
     */
    public void testConstructorElement() throws Exception
    {
        logger_.info("Running testConstructorElement...");
        
        String xml = 
            "<LookAndFeel name=\"name\" class=\"class\" action=\"action\"/>";

        Element lafNode = XOMUtil.toElement(xml);
        LAFInfo info = new LAFInfo(lafNode);
        
        assertEquals("name", info.getName());
        assertEquals("class", info.getClassName());
        assertEquals("action", info.getAction());
    }
    
    
    /**
     * Tests deserialization of the object from XML.
     * 
     * @throws Exception on error.
     */
    public void testApplyPrefs() throws Exception
    {
        logger_.info("Running testApplyPrefs...");

        String xml = 
              "<LookAndFeel name=\"name\" class=\"class\" action=\"action\">"
            + "  <Property name=\"key1\" value=\"value1\"/>"
            + "  <Property name=\"key2\" value=\"value2\"/>"
            + "</LookAndFeel>";
        
        Element root = XOMUtil.toElement(xml);
        LAFInfo info = new LAFInfo();
        info.applyPrefs(root);

        logger_.info("toString:" + info);
        
        assertEquals("name", info.getName());
        assertEquals("class", info.getClassName());
        assertEquals("action", info.getAction());
        assertEquals("value1", info.getProperty("key1"));
        assertEquals("value2", info.getProperty("key2"));
    }

    
    /**
     * Tests serialization of the object to XML.
     * 
     * @throws Exception on error.
     */
    public void testSavePrefs() throws Exception
    {
        logger_.info("Running testSavePrefs...");
        
        LAFInfo info = new LAFInfo();
        info.setName("name");
        info.setClassName("className");
        info.setAction("action");
        
        // Serialize with no properties
        Element root1 = new Element("root");
        info.savePrefs(root1);
        String xml1 = XOMUtil.toXML(root1);
        logger_.debug(StringUtil.addBars(xml1));

        // Serialize with properties
        info.getProperties().put("key1", "value1");
        info.getProperties().put("key2", "value2");
        
        Element root2 = new Element("root");
        info.savePrefs(root2);
        String xml2 = XOMUtil.toXML(root2);
        logger_.debug(StringUtil.addBars(xml2));        
    }
    
    
    /**
     * Tests the equals() method.
     */
    public void xxtestEqualsPerformance()
    {
        logger_.info("Running testEqualsPerformance...");
        
        ElapsedTime timer = new ElapsedTime();
        
        for (int i=0; i<100000; i++)
        {    
            // Equality to self
            LAFInfo info = new LAFInfo();
            info.setName("Foo");
            info.setClassName("com.foo.Bar");
            info.setAction("com.foo.Action");
            info.getProperties().put("key", "value");
            info.getProperties().put("xxx", "yyy");
            assertTrue(info.equals(info));
            
            
            // Equality by content
            LAFInfo replica = new LAFInfo();
            replica.setName("Foo");
            replica.setClassName("com.foo.Bar");
            replica.setAction("com.foo.Action");
            replica.getProperties().put("key", "value");
            replica.getProperties().put("xxx", "yyy");
            assertTrue(info.equals(replica));
    
            
            // Equality by content but with props out of order
            LAFInfo outOfOrder = new LAFInfo();
            outOfOrder.setName("Foo");
            outOfOrder.setClassName("com.foo.Bar");
            outOfOrder.setAction("com.foo.Action");
            outOfOrder.getProperties().put("xxx", "yyy");
            outOfOrder.getProperties().put("key", "value");
            assertTrue(info.equals(outOfOrder));
            
            // Inequality by property
            LAFInfo different = new LAFInfo();
            different.setName("Foo");
            different.setClassName("com.foo.Bar");
            different.setAction("com.foo.Action");
            different.getProperties().put("key", "value");
            different.getProperties().put("DIFFERENT", "PROPERTY");
            assertTrue(!info.equals(different));
        }
        
        timer.setEndTime();
        
        logger_.info("ElapsedTime: " + timer);
    }
}