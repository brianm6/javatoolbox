package toolbox.util.test;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import nu.xom.Element;
import toolbox.util.XOMUtil;

/**
 * 
 */
public class XOMUtilTest extends TestCase
{

    /**
     * Entrypoint
     *
     * @param args None
     */
    public static void main(String[] args)
    {
        TestRunner.run(XOMUtilTest.class);
    }

    /**
     * Tests getInteger() for positive, negative, and null cases
     */
    public void testGetInteger()
    {
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
    }

    public void testGetBoolean()
    {
    }

    public void testGetIntegerAttribute()
    {
    }

    public void testGetBooleanAttribute()
    {
    }

    public void testGetStringAttribute()
    {
    }

    public void testInjectChild()
    {
    }

    public void testCrap()
    {
        Element node = new Element("root");
        node.appendChild("hello");
        node.appendChild("there");
        node.appendChild(new Element("child"));
        System.out.println(node.getChildCount());
        System.out.println(node.toXML());
        System.out.println(node.getValue());
    }
}
