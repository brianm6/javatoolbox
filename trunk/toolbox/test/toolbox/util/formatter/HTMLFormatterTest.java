package toolbox.util.formatter;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import nu.xom.Element;

import org.apache.log4j.Logger;

import toolbox.util.StringUtil;
import toolbox.util.formatter.Formatter;
import toolbox.util.formatter.HTMLFormatter;
import toolbox.util.io.StringInputStream;
import toolbox.util.io.StringOutputStream;

/**
 * Unit test for HTMLFormatter.
 * 
 * @see toolbox.util.formatter.HTMLFormatter
 */
public class HTMLFormatterTest extends TestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(HTMLFormatterTest.class);
    
    //--------------------------------------------------------------------------
    // Test Data
    //--------------------------------------------------------------------------
    
    /**
     * Ugly html to feed to the formatter from Google.
     */
    private static final String HTML_OBFUSCATED = 
        "<html><head><meta http-equiv=\"content-type\" content=\"text/ht"
        + "ml; charset=UTF-8\"><title>Google</title><style><!--body,td,a"
        + ",p,.h{font-family:arial,sans-serif;}.h{font-size: 20px;}.q{c"
        + "olor:#0000cc;}//--></style><script><!--function sf(){documen"
        + "t.f.q.focus();}// --></script></head><body bgcolor=#ffffff t"
        + "ext=#000000 link=#0000cc vlink=#551a8b alink=#ff0000 onLoad="
        + "sf()><center><table border=0 cellspacing=0 cellpadding=0><tr"
        + "><td><img src=\"/images/logo.gif\" width=276 height=110 alt=\"G"
        + "oogle\"></td></tr></table><br><form action=\"/search\" name=f><"
        + "script><!--function qs(el) {if (window.RegExp && window.enco"
        + "deURIComponent) {var qe=encodeURIComponent(document.f.q.valu"
        + "e);if (el.href.indexOf(\"q=\")!=-1) {el.href=el.href.replace(n"
        + "ew RegExp(\"q=[^&$]*\"),\"q=\"+qe);} else {el.href+=\"&q=\"+qe;}}r"
        + "eturn 1;}// --></script><table border=0 cellspacing=0 cellpa"
        + "dding=4><tr><td nowrap class=q><font size=-1><b><font color="
        + "#000000>Web</font></b>&nbsp;&nbsp;&nbsp;&nbsp;<a id=1a class"
        + "=q href=\"/imghp?hl=en&tab=wi\" onClick=\"return qs(this);\">Ima"
        + "ges</a>&nbsp;&nbsp;&nbsp;&nbsp;<a id=2a class=q href=\"/grphp"
        + "?hl=en&tab=wg\" onClick=\"return qs(this);\">Groups</a>&nbsp;&n"
        + "bsp;&nbsp;&nbsp;<a id=4a class=q href=\"/nwshp?hl=en&tab=wn\" "
        + "onClick=\"return qs(this);\">News</a>&nbsp;&nbsp;&nbsp;&nbsp;<"
        + "a id=5a class=q href=\"/froogle?hl=en&tab=wf\" onClick=\"return"
        + " qs(this);\">Froogle</a>&nbsp;&nbsp;&nbsp;&nbsp;<b><a href=\"/"
        + "options/index.html\" class=q>more&nbsp;&raquo;</a></b></font>"
        + "</td></tr></table>  <table cellspacing=0 cellpadding=0><tr><"
        + "td width=25%>&nbsp;</td><td align=center><input type=hidden "
        + "name=hl value=en><span id=hf></span><input type=hidden name="
        + "ie value=\"UTF-8\"><input maxLength=256 size=55 name=q value=\""
        + "\"><br><input type=submit value=\"Google Search\" name=btnG><in"
        + "put type=submit value=\"I'm Feeling Lucky\" name=btnI></td><td"
        + " valign=top nowrap width=25%><font size=-2>&nbsp;&nbsp;<a hr"
        + "ef=/advanced_search?hl=en>Advanced&nbsp;Search</a><br>&nbsp;"
        + "&nbsp;<a href=/preferences?hl=en>Preferences</a><br>&nbsp;&n"
        + "bsp;<a href=/language_tools?hl=en>Language Tools</a></font><"
        + "/td></tr></table></form><br><br><font size=-1><a href=\"/ads/"
        + "\">Advertising&nbsp;Programs</a> - <a href=\"/services/\">Busin"
        + "ess&nbsp;Solutions</a> - <a href=/about.html>About Google</a"
        + "></font><p><font size=-2>&copy;2004 Google - Searching 4,285"
        + ",199,774 web pages</font></p></center></body></html>";

    
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    public static void main(String[] args)
    {
        TestRunner.run(HTMLFormatterTest.class);
    }
    
    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests String format(String) 
     */
    public void testFormatStrings() throws Exception
    {
        logger_.info("Running testFormatStrings...");
        
        Formatter f = new HTMLFormatter();
        String t = f.format(HTML_OBFUSCATED);
        assertNotNull(t);
        logger_.info(StringUtil.banner(t));
        assertNotSame(HTML_OBFUSCATED, t);
    }
        

    /**
     * Tests format(InputStream, OutputStream)
     */
    public void testFormatStreams() throws Exception
    {
        logger_.info("Running testFormatStreams...");
        
        Formatter f = new HTMLFormatter();
        StringOutputStream sos = null;
        StringInputStream sis = null;
        String out = null;
        
        sis = new StringInputStream(HTML_OBFUSCATED);
        sos = new StringOutputStream();
        f.format(sis, sos);
        out = sos.toString().trim();
        assertNotNull(out);
        logger_.info(StringUtil.banner(out));
        assertNotSame(HTML_OBFUSCATED, out);
    }

    
    public void testPreferenced() throws Exception
    {
        logger_.info("Running testPreferenced...");
        
        HTMLFormatter f = new HTMLFormatter();
        f.setIndent(10);
        f.setWrapLength(200);
        
        Element prefs = new Element("root");
        f.savePrefs(prefs);
        
        logger_.debug(StringUtil.banner(prefs.toXML()));
        
        HTMLFormatter f2 = new HTMLFormatter();
        f2.applyPrefs(prefs);
        
        assertEquals(f.getIndent(), f2.getIndent());
        assertEquals(f.getWrapLength(), f2.getWrapLength());
    }
}