package toolbox.util.test;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import nu.xom.Attribute;
import nu.xom.Element;

import toolbox.util.XOMUtil;

/**
 * Unit test for XOMUtil
 */
public class XOMUtilTest extends TestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(XOMUtilTest.class);
    
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
        
    /**
     * Entrypoint
     *
     * @param args None
     */
    public static void main(String[] args)
    {
        TestRunner.run(XOMUtilTest.class);
    }
    
    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests getInteger() for positive, negative, and null cases
     */
    public void testGetInteger()
    {
        logger_.info("Running testGetInteger...");
        
        // Test get int from node
        Element node = new Element("IntNode");
        node.appendChild("123");
        int i = XOMUtil.getInteger(node, -1);
        assertEquals(123, i);

        // Test node doesn't have an int
        node.removeChildren();
        assertEquals(-1, XOMUtil.getInteger(node, -1));

        // Test node is null
        assertEquals(-1, XOMUtil.getInteger(null, -1));
    }

    public void testGetString()
    {
        logger_.info("Running testGetString...");
        
        // Test get string from node
        Element node = new Element("StringNode");
        node.appendChild("xyz");
        
        assertEquals("xyz", XOMUtil.getString(node, "default"));

        // Test node doesn't have a string
        node.removeChildren();
        assertEquals("default", XOMUtil.getString(node, "default"));

        // Test node is null
        assertEquals("default", XOMUtil.getString(null, "default"));
    }

    public void testGetBoolean()
    {
        logger_.info("Running testGetBoolean...");
        
        // Test get boolean from node
        Element node = new Element("BooleanNode");
        node.appendChild("true");
        
        assertEquals(true, XOMUtil.getBoolean(node, false));

        // Test node doesn't have a boolean
        node.removeChildren();
        assertEquals(false, XOMUtil.getBoolean(node, false));

        // Test node is null
        assertEquals(false, XOMUtil.getBoolean(null, false));
    }

    public void testGetIntegerAttribute()
    {
        logger_.info("Running testGetIntegerAttribute...");
        
        Element node = new Element("Foo");
        node.addAttribute(new Attribute("value", "123"));
        
        assertEquals(123, XOMUtil.getIntegerAttribute(node, "value", -1));
        assertEquals(-1, XOMUtil.getIntegerAttribute(null, "value", -1));
        assertEquals(-1, XOMUtil.getIntegerAttribute(node, "blah", -1));
        assertEquals(-1, XOMUtil.getIntegerAttribute(null, null, -1));
    }

    public void testGetBooleanAttribute()
    {
        logger_.info("Running testGetBooleanAttribute...");
        
        Element node = new Element("Foo");
        node.addAttribute(new Attribute("value", "true"));
        
        assertTrue(XOMUtil.getBooleanAttribute(node, "value", false));
        assertFalse(XOMUtil.getBooleanAttribute(null, "value", false));
        assertFalse(XOMUtil.getBooleanAttribute(node, "blah", false));
        assertFalse(XOMUtil.getBooleanAttribute(null, null, false));
    }

    public void testGetStringAttribute()
    {
        logger_.info("Running testGetStringAttribute...");
        
        Element node = new Element("Foo");
        node.addAttribute(new Attribute("value", "xyz"));
        
        assertEquals("xyz", XOMUtil.getStringAttribute(node, "value", "abc"));
        assertEquals("abc", XOMUtil.getStringAttribute(null, "value", "abc"));
        assertEquals("abc", XOMUtil.getStringAttribute(node, "blah", "abc"));
        assertEquals("abc", XOMUtil.getStringAttribute(null, null, "abc"));
    }

    public void testInsertOrReplace()
    {
        logger_.info("Running testInsertOrReplace...");
        
        // TODO: testInsertOrReplace
    }

    public void testGetFirstChild()
    {
        logger_.info("Running testGetFirstChild...");
        
        Element parent = new Element("parent");
        
        Element child1 = new Element("child");
        child1.addAttribute(new Attribute("seq", "1"));
        parent.appendChild(child1);
        
        Element child2 = new Element("child");
        child2.addAttribute(new Attribute("seq", "2"));
        parent.appendChild(child2);

        Element result = 
            XOMUtil.getFirstChildElement(parent,"child",new Element("default"));        
        
        assertEquals("1", result.getAttributeValue("seq"));
        
        assertEquals("default", XOMUtil.getFirstChildElement(
            parent, "blah", new Element("default")).getLocalName());

        assertEquals("default", XOMUtil.getFirstChildElement(
            new Element("woo"), "blah", new Element("default")).getLocalName());
            
        assertEquals("default", XOMUtil.getFirstChildElement(
            null, "blah", new Element("default")).getLocalName());

        assertEquals("default", XOMUtil.getFirstChildElement(
            parent, null, new Element("default")).getLocalName());
            
    }
}
