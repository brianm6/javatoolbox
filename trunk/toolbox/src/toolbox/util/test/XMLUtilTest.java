package toolbox.util.test;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.XMLUtil;

/**
 * Unit test for XMLUtil
 */
public class XMLUtilTest extends TestCase
{
    private static final Logger logger_ =
        Logger.getLogger(XMLUtilTest.class);

    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
            
    /**
     * Entrypoint
     * 
     * @param args None recognized
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
     * @throws Exception on error
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
     * @throws Exception on error
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
}