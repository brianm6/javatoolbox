package toolbox.util.test;

import java.io.IOException;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import nu.xom.Attribute;
import nu.xom.Element;

import org.apache.log4j.Logger;

import toolbox.util.XOMUtil;

/**
 * Unit test for XOMUtil.
 */
public class XOMUtilTest extends TestCase
{
    private static final Logger logger_ = Logger.getLogger(XOMUtilTest.class);
    
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
        TestRunner.run(XOMUtilTest.class);
    }
    
    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests getInteger() for positive, negative, and null cases.
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

        // Test invalid integer        
        node.removeChildren();
        node.appendChild("bad int");
        assertEquals(-1, XOMUtil.getInteger(node, -1));
    }

    
    /**
     * Tests getString()
     */
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

    
    /**
     * Tests getBoolean()
     */
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

    
    /**
     * Tests getIntegerAttribute()
     */
    public void testGetIntegerAttribute()
    {
        logger_.info("Running testGetIntegerAttribute...");
        
        Element node = new Element("Foo");
        node.addAttribute(new Attribute("value", "123"));
        node.addAttribute(new Attribute("badInt", "xxx"));
        assertEquals(123, XOMUtil.getIntegerAttribute(node, "value", -1));
        assertEquals(-1, XOMUtil.getIntegerAttribute(null, "value", -1));
        assertEquals(-1, XOMUtil.getIntegerAttribute(node, "blah", -1));
        assertEquals(-1, XOMUtil.getIntegerAttribute(null, null, -1));
        assertEquals(-1, XOMUtil.getIntegerAttribute(node, "badInt", -1));
    }

    
    /**
     * Tests getBooleanAttribute()
     */
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

    
    /**
     * Tests getStringAttribute()
     */
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

    
    /**
     * Tests insertOrReplace() for the insert scenario
     * 
     * @throws IOException on I/O error.
     */
    public void testInsertOrReplaceAsInsert() throws IOException
    {
        logger_.info("Running testInsertOrReplaceAsInsert...");
        
        Element parent = new Element("parent");
        Element child = new Element("child");
        XOMUtil.insertOrReplace(parent, child);
        assertEquals(1, parent.getChildCount());
        assertEquals(child, parent.getFirstChildElement("child"));
        assertEquals(parent, child.getParent());
        //logger_.info(XOMUtil.toXML(parent));
    }

    
    /**
     * Tests insertOrReplace() for the replace scenario.
     * 
     * @throws IOException on I/O error.
     */
    public void testInsertOrReplaceAsReplace() throws IOException
    {
        logger_.info("Running testInsertOrReplaceAsReplace...");
        
        Element parent = new Element("parent");
        Element origChild = new Element("child");
        origChild.addAttribute(new Attribute("id", "old"));
        parent.appendChild(origChild);
        // logger_.info("\n" + XOMUtil.toXML(parent));
                
        Element newChild = new Element("child");
        newChild.addAttribute(new Attribute("id","new"));
        
        XOMUtil.insertOrReplace(parent, newChild);
        assertEquals(1, parent.getChildCount());
        assertEquals(newChild, parent.getFirstChildElement("child"));
        assertEquals(parent, newChild.getParent());
        // logger_.info("\n" + XOMUtil.toXML(parent));
    }

    
    /**
     * Tests insertOrReplace() failure when 2 identical child nodes already
     * exist.
     * 
     * @throws IOException on I/O error.
     */
    public void testInsertOrReplaceFailure() throws IOException
    {
        logger_.info("Running testInsertOrReplaceFailure...");
        
        Element parent = new Element("parent");
        
        Element dupe1 = new Element("child");
        dupe1.addAttribute(new Attribute("id", "dupe1"));
        parent.appendChild(dupe1);
        
        Element dupe2 = new Element("child");
        dupe1.addAttribute(new Attribute("id", "dupe2"));
        parent.appendChild(dupe2);
        
        Element newChild = new Element("child");
        newChild.addAttribute(new Attribute("id","new"));
        
        try
        {
            XOMUtil.insertOrReplace(parent, newChild);
            fail("Failure expected.");
        }
        catch (IllegalArgumentException iae)
        {
            // Expected
            assertTrue(true);
        }
        catch (Exception e)
        {
            fail("Unexpected exception: " + e);
        }
    }
    
    
    /**
     * Tests getFirstChild()
     */
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
    
    
    /**
     * Tests toXML()
     * 
     * @throws IOException on I/O error.
     */
    public void testToXML() throws IOException
    {
        logger_.info("Running testToXML...");
        
        // Test get string from node
        Element node = new Element("Node");
        node.addAttribute(new Attribute("name", "value"));
        node.appendChild(new Element("Child"));
        String xml = XOMUtil.toXML(node);
        logger_.debug("\n" + xml);
    }
}