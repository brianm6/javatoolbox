package toolbox.plugin.statcvs;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import nu.xom.Element;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import toolbox.util.BeanUtil;
import toolbox.util.StringUtil;
import toolbox.util.formatter.XMLFormatter;

/**
 * Unit test for {@link toolbox.plugin.statcvs.CVSProject}.
 */
public class CVSProjectTest extends TestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(CVSProjectTest.class);
    
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    public static void main(String[] args)
    {
        TestRunner.run(CVSProjectTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    public void testCompareTo()
    {
    }


    public void testApplyPrefs() throws Exception
    {
        logger_.info("Running testApplyPrefs...");
        
        CVSProject p = new CVSProject(
            "project",
            "module", 
            "cvsRoot",
            "password",
            "checkoutDir",
            true, 
            "launchURL",
            "engine");
        
        Element root = new Element("root");
        p.savePrefs(root);
        
        logger_.debug(StringUtil.banner(
            new XMLFormatter().format(root.toXML())));

        Element cp = root.getFirstChildElement(CVSProject.NODE_CVSPROJECT);

        assertEquals("cvsRoot", cp.getAttributeValue(CVSProject.PROP_CVSROOT));
        assertEquals("true", cp.getAttributeValue(CVSProject.PROP_DEBUG));
        assertEquals("engine", cp.getAttributeValue(CVSProject.PROP_ENGINE));
        assertEquals("module", cp.getAttributeValue(CVSProject.PROP_MODULE));
        assertEquals("project", cp.getAttributeValue(CVSProject.PROP_PROJECT));
        
        assertEquals("checkoutDir", 
            cp.getAttributeValue(CVSProject.PROP_CHECKOUT_DIR));
        
        assertEquals("launchURL", 
            cp.getAttributeValue(CVSProject.PROP_LAUNCH_URL));
        
        assertEquals(new String(Base64.encodeBase64("password".getBytes())), 
            cp.getAttributeValue(CVSProject.PROP_PASSWORD));
        
        CVSProject q = new CVSProject();
        q.applyPrefs(root);
     
        logger_.debug(StringUtil.banner(BeanUtil.toString(q)));
        assertTrue(0 == p.compareTo(q));
    }


    public void testSavePrefs()
    {
        logger_.debug("Running testSavePrefs...");
    }
}