package toolbox.util;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import toolbox.util.XMLUtil;

/**
 * Unit test for {@link toolbox.util.XMLUtil}.
 */
public class XMLUtilTest extends TestCase
{
    private static final Logger logger_ = Logger.getLogger(XMLUtilTest.class);

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
        TestRunner.run(XMLUtilTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
        
    /**
     * Tests format()
     * 
     * @throws Exception on error.
     */
    public void testFormat() throws Exception
    {
        logger_.info("Running testFormat...");
        
        String xml =
            "<root>" +
            "<type unid=\"1awiysxq\">" +
            "<localName>" +
            "null</localName>" +
            "<isChanged>" +
            "false</isChanged>" +
            "<elideLabel>" +
            "false</elideLabel>" +
            "<sequence>" +
            "</sequence>" +
            "<validationSequence>" +
            "</validationSequence>" +
            "<readOnly name=\"domain\">" +
            "<localName>" +
            "domain</localName>" +
            "<isChanged>" +
            "false</isChanged>" +
            "<elideLabel>" +
            "false</elideLabel>" +
            "<valueDomain>" +
            "null</valueDomain>" +
            "<value>" +
            "</value>" +
            "</readOnly>" +
            "<list name=\"translationEntry\">" +
            "<localName>" +
            "translationEntry</localName>" +
            "<isChanged>" +
            "false</isChanged>" +
            "<elideLabel>" +
            "false</elideLabel>" +
            "<newAllowed>" +
            "true</newAllowed>" +
            "<deleteAllowed>" +
            "true</deleteAllowed>" +
            "</list>" +
            "<singleKeyword name=\"changeRequestApproval\">" +
            "<localName>" +
            "changeRequestApproval</localName>" +
            "<isChanged>" +
            "false</isChanged>" +
            "<elideLabel>" +
            "false</elideLabel>" +
            "<sequence>" +
            "</sequence>" +
            "<validationSequence>" +
            "#</validationSequence>" +
            "<editable>" +
            "true</editable>" +
            "<mandatory>" +
            "false</mandatory>" +
            "<domain>" +
            "APPROVAL</domain>" +
            "<cascaded>" +
            "false</cascaded>" +
            "<value>" +
            "</value>" +
            "</singleKeyword>" +
            "</type>" +
            "</root>";

        String formatted = XMLUtil.format(xml);
        
        assertNotNull(formatted);
        
        logger_.info("Formatted XML:\n\n" + formatted);
        
    }
    
    
    /**
     * Tests format() with XML containing a declaration. The formatter should
     * preserve the presence of the declaration.
     * 
     * @throws Exception on error.
     */
    public void testFormatWithDeclaration() throws Exception
    {
        logger_.info("Running testFormatWithDeclaration...");
        
        String xml =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<root>" +
            "<type unid=\"1awiysxq\">" +
            "<localName>null</localName>" +
            "</type>" +
            "</root>";
        
        String formatted = XMLUtil.format(xml);
        
        assertNotNull(formatted);
        
        logger_.info("Formatted XML:\n\n" + formatted);
    }
    
    
    /**
     * Tests toElement()
     * 
     * @throws Exception on error.
     */
    public void testToElement() throws Exception
    {
        logger_.info("Running testToElement...");
        
        String xml = new String("<root a=\"1\"><child>value</child></root>");
        Element node = XMLUtil.toElement(xml);
        
        assertEquals("root", node.getLocalName());
        assertEquals("1", node.getAttribute("a"));
        assertEquals(1, node.getChildNodes().getLength());
        
        Node child = node.getChildNodes().item(0);
        
        assertEquals("child", child.getLocalName());
        assertEquals("value", child.getChildNodes().item(0).getNodeValue());
    }
}