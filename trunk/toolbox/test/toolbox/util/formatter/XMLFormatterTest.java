package toolbox.util.formatter;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import nu.xom.Element;

import org.apache.log4j.Logger;

import toolbox.util.StringUtil;
import toolbox.util.io.StringInputStream;
import toolbox.util.io.StringOutputStream;

/**
 * Unit test for XMLFormatter.
 * 
 * @see toolbox.util.formatter.XMLFormatter
 */
public class XMLFormatterTest extends TestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(XMLFormatterTest.class);
    
    //--------------------------------------------------------------------------
    // Test Data Constants
    //--------------------------------------------------------------------------
    
    private static final String XML_TEST1_IN  = "<oneElement/>";
    private static final String XML_TEST1_OUT = "<oneElement/>";
    private static final String XML_TEST2_IN  = "<one><two></two></one>";
    private static final String XML_TEST2_OUT = "<one>\n  <two/>\n</one>";
    
    private static final String XML_TEST3_IN = 
        "<one a=\"1\" b=\"3\"><two>blah</two></one>";
    
    private static final String XML_TEST3_OUT =
        "<one a=\"1\" b=\"3\">\n" 
        +"  <two>blah</two>\n"
        +"</one>";

    
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    public static void main(String[] args)
    {
        TestRunner.run(XMLFormatterTest.class);
    }
    
    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    public void testFormatStrings() throws Exception
    {
        logger_.info("Running testFormatStrings...");
        
        Formatter f = new XMLFormatter();
        
        String t1 = f.format(XML_TEST1_IN).trim();
        assertEquals(XML_TEST1_OUT, t1);
        logger_.info(StringUtil.banner(t1));
        
        String t2 = f.format(XML_TEST2_IN).trim();
        logger_.info(StringUtil.banner(t2));
        assertEquals(XML_TEST2_OUT, t2);
        
        String t3 = f.format(XML_TEST3_IN).trim();
        logger_.info(StringUtil.banner(t3));
        assertEquals(XML_TEST3_OUT, t3);
    }
        

    public void testFormatStreams() throws Exception
    {
        logger_.info("Running testFormatStreams...");
        
        Formatter f = new XMLFormatter();
        StringOutputStream sos = null;
        StringInputStream sis = null;
        String out = null;
        
        sis = new StringInputStream(XML_TEST1_IN);
        sos = new StringOutputStream();
        f.format(sis, sos);
        out = sos.toString().trim();
        assertEquals(XML_TEST1_OUT, out);
        logger_.info(StringUtil.banner(out));
        
        sis = new StringInputStream(XML_TEST2_IN);
        sos = new StringOutputStream();
        f.format(sis, sos);
        out = sos.toString().trim();
        logger_.info(StringUtil.banner(out));
        assertEquals(XML_TEST2_OUT, out);

        sis = new StringInputStream(XML_TEST3_IN);
        sos = new StringOutputStream();
        f.format(sis, sos);
        out = sos.toString().trim();
        logger_.info(StringUtil.banner(out));
        assertEquals(XML_TEST3_OUT, out);
    }
    
    
    public void testPreferenced() throws Exception
    {
        logger_.info("Running testPreferenced...");
        
        XMLFormatter f = new XMLFormatter();
        f.setIndent(10);
        f.setLineWidth(200);
        f.setOmitDeclaration(true);
        
        Element prefs = new Element("root");
        f.savePrefs(prefs);
        
        XMLFormatter f2 = new XMLFormatter();
        f2.applyPrefs(prefs);
        
        assertEquals(f.getIndent(), f2.getIndent());
        assertEquals(f.getLineWidth(), f2.getLineWidth());
        assertEquals(f.isOmitDeclaration(), f2.isOmitDeclaration());
    }
}
