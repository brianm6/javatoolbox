package toolbox.util.test;

import junit.framework.TestCase;

import nu.xom.Element;

/**
 * 
 */
public class XOMUtilTest extends TestCase
{

    /**
     * Constructor for XOMUtilTest.
     * @param name
     */
    public XOMUtilTest(String name)
    {
        super(name);
    }

    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(XOMUtilTest.class);
    }

    public void testGetInteger()
    {
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
