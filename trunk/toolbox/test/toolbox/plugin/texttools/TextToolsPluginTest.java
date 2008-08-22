package toolbox.plugin.texttools;

import java.awt.HeadlessException;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import nu.xom.Element;

import org.apache.commons.collections.MapUtils;
import org.apache.log4j.Logger;

import toolbox.plugin.texttools.TextToolsPlugin.UnquoteAction;
import toolbox.util.StringUtil;
import toolbox.util.formatter.XMLFormatter;

/**
 * Unit test for {@link toolbox.plugin.texttools.TextToolsPlugin}.
 */
public class TextToolsPluginTest extends TestCase
{
    private static final Logger log = Logger.getLogger(TextToolsPluginTest.class);
    
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
        log.info("Running testPreferenced...");
        
        try {
            Element prefs = new Element("testPreferenced");
            
            TextToolsPlugin plugin = new TextToolsPlugin();
            plugin.initialize(MapUtils.EMPTY_MAP);
            plugin.buildView();
            plugin.savePrefs(prefs);
            plugin.destroy();
            String toXML = prefs.toXML();
    		log.debug(StringUtil.banner(new XMLFormatter().format(toXML)));
    		assertNotNull(toXML);
    		
            TextToolsPlugin plugin2 = new TextToolsPlugin();
            plugin2.initialize(MapUtils.EMPTY_MAP);
            plugin2.buildView();
            plugin2.applyPrefs(prefs);
            plugin2.destroy();
            toXML = prefs.toXML();
            log.debug(StringUtil.banner(new XMLFormatter().format(toXML)));
            assertNotNull(toXML);
        }
        catch (HeadlessException he) {
            log.info("Skipping test because: " + he.getMessage());
        }
    }
    
    public void testUnquoteAction_SingleLine() 
    {
    	TextToolsPlugin plugin = new TextToolsPlugin();
    	UnquoteAction action = plugin.new UnquoteAction();
    	assertEquals("Hello", action.unquote("\"Hello\""));
    	assertEquals("Hello", action.unquote("\"Hello\";"));
    	assertEquals("Hello", action.unquote("  \"Hello\"  ;"));
    	assertEquals(" Hello ", action.unquote("  \" Hello \"  ; "));
    }
    
    public void testUnquoteAction_EmptyOrBlankString() 
    {
    	TextToolsPlugin plugin = new TextToolsPlugin();
    	UnquoteAction action = plugin.new UnquoteAction();
        assertEquals("", action.unquote("\"\""));
        assertEquals("  ", action.unquote("\"  \""));
    }

    
    public void testUnquoteAction_MultiLine() 
    {
    	TextToolsPlugin plugin = new TextToolsPlugin();
    	UnquoteAction action = plugin.new UnquoteAction();
    	assertEquals("Hello\nWorld", action.unquote("\"Hello\" + \n\"World\";"));
    	assertEquals("Hello\nWorld", action.unquote("\"Hello\"\n  +  \"World\";"));
    	assertEquals("Hello \n World", action.unquote("\"Hello \"\n  +  \" World\";"));
    }
    
//    public void testUnquoteAction_EmbeddedNewLines() 
//    {
//    	TextToolsPlugin plugin = new TextToolsPlugin();
//    	UnquoteAction action = plugin.new UnquoteAction();
//    	assertEquals("Hello\nWorld", action.unquote("\"Hello\" + \n\"World\";"));
//    }
}
