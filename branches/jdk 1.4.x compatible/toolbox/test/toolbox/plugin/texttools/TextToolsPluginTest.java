package toolbox.plugin.texttools;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import nu.xom.Element;

import org.apache.commons.collections.MapUtils;
import org.apache.log4j.Logger;

import toolbox.util.StringUtil;
import toolbox.util.formatter.XMLFormatter;

/**
 * Unit test for {@link toolbox.plugin.texttools.TextToolsPlugin}.
 */
public class TextToolsPluginTest extends TestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(TextToolsPluginTest.class);
    
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    public static void main(String[] args)
    {
        TestRunner.run(TextToolsPluginTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    public void testPreferenced() throws Exception
    {
        logger_.info("Running testPreferenced...");
        
        Element prefs = new Element("testPreferenced");
        
        TextToolsPlugin plugin = new TextToolsPlugin();
        plugin.initialize(MapUtils.EMPTY_MAP);
        plugin.buildView();
        plugin.savePrefs(prefs);
        plugin.destroy();
        String toXML = prefs.toXML();
		logger_.debug(StringUtil.banner(new XMLFormatter().format(toXML)));
		assertNotNull(toXML);
		
        TextToolsPlugin plugin2 = new TextToolsPlugin();
        plugin2.initialize(MapUtils.EMPTY_MAP);
        plugin2.buildView();
        plugin2.applyPrefs(prefs);
        plugin2.destroy();
        toXML = prefs.toXML();
        logger_.debug(StringUtil.banner(new XMLFormatter().format(toXML)));
        assertNotNull(toXML);
    }
}
