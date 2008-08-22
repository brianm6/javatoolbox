package toolbox.plugin.jdbc;

import java.awt.HeadlessException;
import java.util.Collections;

import junit.framework.TestCase;
import junit.textui.TestRunner;
import nu.xom.Element;

import org.apache.log4j.Logger;

import toolbox.util.StringUtil;
import toolbox.util.formatter.XMLFormatter;

/**
 * Unit test for {@link toolbox.plugin.jdbc.QueryPlugin}.
 */
public class QueryPluginTest extends TestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(QueryPluginTest.class);
    
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    public static void main(String[] args)
    {
        TestRunner.run(QueryPluginTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    public void testPreferenced() throws Exception
    {
        logger_.info("Running testPreferenced...");

        try {
            // Save ---------------------------------------------------------------
            
            QueryPlugin p = new QueryPlugin();
            p.initialize(Collections.EMPTY_MAP);
            
            p.setAutoScrollThreshold(100);
            p.setMaxHistory(99);
            p.setSqlTerminator("#");
            p.setSendErrorToConsole(false);
            
            Element root = new Element("root");
            p.savePrefs(root);
            
            logger_.debug(StringUtil.banner(
                new XMLFormatter().format(root.toXML())));
    
            // Apply ---------------------------------------------------------------
            
            QueryPlugin p2 = new QueryPlugin();
            p2.initialize(Collections.EMPTY_MAP);
            p2.applyPrefs(root);
            
            // Verify --------------------------------------------------------------
            
            assertEquals(p.getAutoScrollThreshold(), p2.getAutoScrollThreshold());
            assertEquals(p.getMaxHistory(), p2.getMaxHistory());
            assertEquals(p.getSqlTerminator(), p2.getSqlTerminator());
            assertEquals(p.isSendErrorToConsole(), p2.isSendErrorToConsole());
            assertEquals(p.isContinueOnError(), p2.isContinueOnError());
        }
        catch (HeadlessException he) {
            logger_.info("Skipping test because: " + he.getMessage());
        }
    }
}