package toolbox.util;

import java.io.LineNumberReader;
import java.io.StringReader;
import java.util.ArrayList;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import toolbox.log4j.SmartLogger;

/**
 * Unit test for {@link toolbox.util.StringUtil}.
 */
public class StringUtilTest extends TestCase
{
    private static Logger logger_ = Logger.getLogger(StringUtilTest.class);

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
        TestRunner.run(StringUtilTest.class);
    }

    //--------------------------------------------------------------------------
    // right() Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests right() for proper truncation behavior.
     */
    public void testRightForTruncation()
    {
        logger_.info("Running testRightForTruncation...");

        String input    = "hello";
        String expected = "hell";
        String result   = StringUtil.right(input, 4, ' ', true);
        assertEquals(expected, result);
    }
    
    //--------------------------------------------------------------------------
    // left() Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests left() for proper truncation behavior.
     */
    public void testLeftForTruncation()
    {
        logger_.info("Running testLeftForTruncation...");

        String input    = "hello";
        String expected = "hell";
        String result   = StringUtil.left(input, 4, ' ', true);
        assertEquals(expected, result);
    }

    //--------------------------------------------------------------------------
    // wrap() Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests wrap().
     */
    public void testWrap()
    {
        logger_.info("Running testWrap...");
        
        String s = "0123456789";
        
        logger_.debug(StringUtil.wrap(s));
        logger_.debug(StringUtil.NL + StringUtil.wrap(s, 5));
        logger_.debug(StringUtil.wrap(s, 5, true));
        logger_.debug(StringUtil.wrap(s, 5, false));
    }
    
    //--------------------------------------------------------------------------
    // indent() Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests indent(s, indentString)
     */
    public void testIndentStringString() 
    {
        logger_.info("Running testIndentStringString...");
        
        // Null
        assertEquals("***", StringUtil.indent(null, "***"));
        
        // Empty string
        assertEquals("***", StringUtil.indent("", "***"));
        
        // Blank string
        assertEquals("***   ", StringUtil.indent("   ", "***"));
        
        // Single line
        assertEquals("***howdy", StringUtil.indent("howdy", "***"));
        
        // New line
        assertEquals("***\n", StringUtil.indent("\n", "***"));
        
        // Consecutive new lines
        assertEquals("*\n*\n*\n", StringUtil.indent("\n\n\n", "*"));
        
        // Multiple lines
        assertEquals(
            "*line1\n*line2\n*line3", 
            StringUtil.indent("line1\nline2\nline3", "*"));
            
        // Alternating newlines
        assertEquals(
            "*line1\n*\n*line2\n*\n*line3\n*\n", 
            StringUtil.indent("line1\n\nline2\n\nline3\n\n", "*"));
    }

    
    /**
     * Tests indent(s, numChars)
     */
    public void testIndentInt() 
    {
        logger_.info("Running testIndentInt...");
        
        // Zero
        assertEquals("int", StringUtil.indent("int", 0));
        
        // One
        assertEquals(
            StringUtils.repeat(StringUtil.DEFAULT_INDENT_CHAR, 1) + "int",
            StringUtil.indent("int", 1));
            
        // Many
        assertEquals(
            StringUtils.repeat(StringUtil.DEFAULT_INDENT_CHAR, 4) + "int",
            StringUtil.indent("int", 4));
    }
    

    /**
     * Tests indent(s, numChars, indentChar)
     */
    public void testIndentIntString() 
    {
        logger_.info("Running testIndentIntString...");
        
        // Zero empty
        assertEquals("int", StringUtil.indent("int", 0, ""));

        // Zero non-empty
        assertEquals("int", StringUtil.indent("int", 0, "nonempty"));
        
        // One empty
        assertEquals("int", StringUtil.indent("int", 1, ""));

        // One non-empty
        assertEquals("1int", StringUtil.indent("int", 1, "1"));
            
        // Many empty
        assertEquals("int", StringUtil.indent("int", 4, ""));
        
        // Many non-empty
        assertEquals("4444int", StringUtil.indent("int", 4, "4"));
    }

    
    /**
     * Tests indent(s)
     */
    public void testIndentString() 
    {
        
        logger_.info("Running testIndentString...");
        
        // Empty
        assertEquals(
            StringUtils.repeat(
                StringUtil.DEFAULT_INDENT_CHAR,
                StringUtil.DEFAULT_INDENT_LENGTH),
            StringUtil.indent(""));
        
        // Non-Empty
        assertEquals(
            StringUtils.repeat(
                StringUtil.DEFAULT_INDENT_CHAR,
                StringUtil.DEFAULT_INDENT_LENGTH) + "abc",
            StringUtil.indent("abc"));
    }
    
    //--------------------------------------------------------------------------
    // Misc Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests static toString(List).
     */
    public void testToStringForList()
    {
        logger_.info("Running testToStringForList...");

        ArrayList theList = new ArrayList();
        theList.add("ELEM_ONE");
        theList.add("ELEM_TWO");
        theList.add("ELEM_THREE");

        String result = StringUtil.toString(theList);
        String expected = " [0] = ELEM_ONE [1] = ELEM_TWO [2] = ELEM_THREE";

        if (!expected.equals(result))
        {
            fail(
                " Result("
                    + result
                    + ") does not match "
                    + "expected("
                    + expected
                    + ")");
        }
    }
    
    
    /**
     * Tests getStringRuler().
     * 
     * @throws Exception on error
     */
    public void testGetStringRuler() throws Exception
    {
        logger_.info("Running testGetStringRuler...");
        
        // one digit length test
        {
            String s  = "abcd";
            String sr = StringUtil.getStringRuler(s);
            
            SmartLogger.info(logger_, sr);
            
            assertTrue("ruler incorrect", sr.startsWith(s));
            assertTrue("ruler incorrect", sr.endsWith("1234"));
        }
    
        // two digit length test
        {
            String s  = "abcdefghijklmnopqrstuvwxyz";
            String sr = StringUtil.getStringRuler(s);
            
            SmartLogger.info(logger_, sr);
            
            LineNumberReader lnr = new LineNumberReader(new StringReader(sr));
    
            assertTrue("ruler line 1 incorrect", lnr.readLine().equals(s));
                    
            assertTrue("ruler line 2 incorrect", 
                lnr.readLine().equals("12345678911111111112222222"));
                                       
            assertTrue("ruler line 3 incorrect",
                lnr.readLine().equals("         01234567890123456"));
        }       
    }
    
    
    /**
     * Tests truncate().
     */ 
    public void testTruncate() 
    {
        logger_.info("Running testTruncate...");
        
        //======================================
        //      STRING LENGTH = 0 
        //======================================
         
        // test empty string with zero 
        {
            String s = StringUtil.truncate("", 0);
            assertEquals(s, "");
        }

        // test empty string with one 
        {
            String s = StringUtil.truncate("", 1);
            assertEquals(s, "");
        }
        
        // test empty string with many 
        {
            String s = StringUtil.truncate("", 50);
            assertEquals(s, "");
        }
        
        //======================================
        //      STRING LENGTH = 1 
        //======================================
         
        // test 1 char string with zero
        {
            String t = "x";
            String s = StringUtil.truncate(t, 0);
            assertEquals(s, "");
        }

        // test 1 char string with one 
        {
            String t = "x";
            String s = StringUtil.truncate(t, 1);
            assertEquals(s, t);
        }
        
        // test 1 char string with many 
        {
            String t = "x";
            String s = StringUtil.truncate(t, 50);
            assertEquals(s, t);
        }
        
        //======================================
        //      STRING LENGTH > 1
        //======================================
        
        // test non-empty string with zero 
        {
            String t = "hello";
            String s = StringUtil.truncate(t, 0);
            assertEquals(s, "");
        }

        // test non-empty string with one
        {
            String t = "hello";
            String s = StringUtil.truncate(t, 1);
            assertEquals(s, "h");
        }
        
        // test non-empty string with many 
        {
            String t = "hello";
            String s = StringUtil.truncate(t, 4);
            assertEquals(s, "hell");
        }
        
        // test non-empty string with exact length 
        {
            String t = "hello";
            String s = StringUtil.truncate(t, t.length());
            assertEquals(s, t);
        }

        // test non-empty string with length greater than string 
        {
            String t = "hello";
            String s = StringUtil.truncate(t, 100);
            assertEquals(s, t);
        }
        
    }

    
    /**
     * Tests isMultiline().
     */
    public void testIsMultiline()
    {
        logger_.info("Running testIsMultiline...");
        
        assertEquals(false, StringUtil.isMultiline(""));
        assertEquals(false, StringUtil.isMultiline("a"));
        assertEquals(false, StringUtil.isMultiline("This is a sentence."));
        assertEquals(false, StringUtil.isMultiline("Embedded \t tab"));
        
        assertEquals(true, StringUtil.isMultiline("\n"));
        assertEquals(true, StringUtil.isMultiline("End of sentence\n"));
        assertEquals(true, StringUtil.isMultiline("\nBeginning of sentence"));
        assertEquals(true, StringUtil.isMultiline("Middle of \n sentence"));
        assertEquals(true, StringUtil.isMultiline("Multiple \n sentences \n!"));
        assertEquals(true, StringUtil.isMultiline("\na"));
        assertEquals(true, StringUtil.isMultiline("a\n"));
    }
    
    
    /**
     * Tests tokenize().
     */
    public void testTokenize()
    {
        logger_.info("Running testTokenize...");
        
        // Empty string
        assertEquals(0, StringUtil.tokenize("", "x").length);
        assertEquals(0, StringUtil.tokenize("", "x", true).length);
        
        // One lone token
        String s = "a";
        assertEquals(0, StringUtil.tokenize(s, "a").length);
        assertEquals(1, StringUtil.tokenize(s, "a", true).length);
        assertEquals("a", StringUtil.tokenize(s, "a", true)[0]);
        
        // Multiple tokens
        String m = "oneZtwoZthree";
        assertEquals(1, StringUtil.tokenize(m, "X").length);
        assertEquals(1, StringUtil.tokenize(m, "X", true).length);
            
        // Without delims
        assertEquals(3, StringUtil.tokenize(m, "Z").length);
        assertEquals("one", StringUtil.tokenize(m, "Z")[0]);
        assertEquals("two", StringUtil.tokenize(m, "Z")[1]);
        assertEquals("three", StringUtil.tokenize(m, "Z")[2]);

        // With delims
        assertEquals(5, StringUtil.tokenize(m, "Z", true).length);
        assertEquals("one", StringUtil.tokenize(m, "Z", true)[0]);
        assertEquals("Z", StringUtil.tokenize(m, "Z", true)[1]);
        assertEquals("two", StringUtil.tokenize(m, "Z", true)[2]);
        assertEquals("Z", StringUtil.tokenize(m, "Z", true)[3]);
        assertEquals("three", StringUtil.tokenize(m, "Z", true)[4]);
        
        // Delimiter > 1 character
        String n = "oneABtwoABthree";
        assertEquals(3, StringUtil.tokenize(n, "AB").length);
        assertEquals("one", StringUtil.tokenize(n, "AB")[0]);
        assertEquals("two", StringUtil.tokenize(n, "AB")[1]);
        assertEquals("three", StringUtil.tokenize(n, "AB")[2]);
    }
    
    
    /**
     * Tests getLine().
     */
    public void testGetLine()
    {
        logger_.info("Running testGetLine...");
        
        // Test string
        String s = null;
        
        // Empty string with no newline
        assertNull(StringUtil.getLine("", 0));
        assertNull(StringUtil.getLine("", 1));
        assertNull(StringUtil.getLine("", 500));
        
        // String with only newline
        assertEquals("", StringUtil.getLine(StringUtil.NL, 0));
        assertNull(StringUtil.getLine(StringUtil.NL, 1));
        assertNull(StringUtil.getLine(StringUtil.NL, 500));
        
        // String with one char + newline
        s = "a" + StringUtil.NL;
        assertEquals("a", StringUtil.getLine(s, 0));
        assertNull(StringUtil.getLine(s, 1));
        assertNull(StringUtil.getLine(s, 500));

        // String with multiple chars + newline
        s = "abc" + StringUtil.NL;
        assertEquals("abc", StringUtil.getLine(s, 0));
        assertNull(StringUtil.getLine(s, 1));
        assertNull(StringUtil.getLine(s, 500));
        
        // String with multiple newlines
        int cnt = 10;
        s = StringUtils.repeat(StringUtil.NL, cnt);
        
        for (int i = 0; i < cnt; i++)
            assertEquals("", StringUtil.getLine(s, i));
        
        assertNull(StringUtil.getLine(s, 500));

        // String with multiple newlines w/ single chars
        s = "a" + StringUtil.NL + 
            "b" + StringUtil.NL + 
            "c" + StringUtil.NL + 
            "d" + StringUtil.NL;
        
        assertEquals("a", StringUtil.getLine(s, 0));
        assertEquals("b", StringUtil.getLine(s, 1));
        assertEquals("c", StringUtil.getLine(s, 2));
        assertEquals("d", StringUtil.getLine(s, 3));                        
        assertNull(StringUtil.getLine(s, 500));
        
        // String with multiple newlines w/ multiple chars
        s = "abc" + StringUtil.NL + 
            "def" + StringUtil.NL + 
            "ghi" + StringUtil.NL + 
            "jkl" + StringUtil.NL;
        
        assertEquals("abc", StringUtil.getLine(s, 0));
        assertEquals("def", StringUtil.getLine(s, 1));
        assertEquals("ghi", StringUtil.getLine(s, 2));
        assertEquals("jkl", StringUtil.getLine(s, 3));                        
        assertNull(StringUtil.getLine(s, 500));
        
        // String with multiple newlines w/ multiple chars. The last line does
        // not have a terminating newline
        s = "abc" + StringUtil.NL + 
            "def" + StringUtil.NL + 
            "ghi" + StringUtil.NL + 
            "jkl";
        
        assertEquals("abc", StringUtil.getLine(s, 0));
        assertEquals("def", StringUtil.getLine(s, 1));
        assertEquals("ghi", StringUtil.getLine(s, 2));
        assertEquals("jkl", StringUtil.getLine(s, 3));                        
        assertNull(StringUtil.getLine(s, 500));
    }
    
    
    /**
     * Tests banner()
     */
    public void testBanner()
    {
        logger_.info("Running testBanner...");
        
        logger_.debug(StringUtil.banner(""));
        logger_.debug(StringUtil.banner("this is a single line"));
        logger_.debug(StringUtil.banner("this\nis a\nmulti-line"));
    }
    
    
    /**
     * testReplaceIgnoreCase
     */
    public void testReplaceIgnoreCase() 
    {
        logger_.info("Running testReplaceIgnoreCase...");
        
        // Empty Text ----------------------------------------------------------
        
        // Text is empty
        assertEquals("", StringUtil.replaceIgnoreCase("", "a", "b"));
        
        // Text length = 1 -----------------------------------------------------
        
        // Text is one char, no match
        assertEquals("a", StringUtil.replaceIgnoreCase("a", "b", "c"));
        assertEquals("a", StringUtil.replaceIgnoreCase("a", "B", "c"));
        
        // Text is one char, single match
        assertEquals("b", StringUtil.replaceIgnoreCase("a", "a", "b"));
        assertEquals("b", StringUtil.replaceIgnoreCase("a", "A", "b"));

        // Text is one char, single match no-op
        assertEquals("a", StringUtil.replaceIgnoreCase("a", "a", "a"));
        assertEquals("a", StringUtil.replaceIgnoreCase("a", "A", "a"));

        // Text is one char, single match, expansion
        assertEquals("123", StringUtil.replaceIgnoreCase("a", "a", "123"));
        assertEquals("123", StringUtil.replaceIgnoreCase("a", "A", "123"));

        // Text is one char, single match, expansion with duplicate
        assertEquals("aa", StringUtil.replaceIgnoreCase("a", "a", "aa"));
        assertEquals("aa", StringUtil.replaceIgnoreCase("a", "A", "aa"));

        // Text is one char, single match, contraction
        assertEquals("", StringUtil.replaceIgnoreCase("a", "a", ""));
        assertEquals("", StringUtil.replaceIgnoreCase("a", "A", ""));

        // Text length = n -----------------------------------------------------
        
        // Text is multiple chars, no match
        assertEquals("abc", StringUtil.replaceIgnoreCase("abc", "xyz", "123"));
        assertEquals("AbC", StringUtil.replaceIgnoreCase("AbC", "XyZ", "123"));
        
        // Text is multiple chars, single match
        assertEquals("xyz", StringUtil.replaceIgnoreCase("abc", "abc", "xyz"));
        assertEquals("xyz", StringUtil.replaceIgnoreCase("AbC", "aBc", "xyz"));

        // Text is multiple chars, single match, expansion
        assertEquals("abc123", 
            StringUtil.replaceIgnoreCase("abc", "abc", "abc123"));
        assertEquals("ABC123", 
            StringUtil.replaceIgnoreCase("AbC", "aBc", "ABC123"));

        // Text is multiple chars, single match, contraction
        assertEquals("z", StringUtil.replaceIgnoreCase("abc", "abc", "z"));
        assertEquals("z", StringUtil.replaceIgnoreCase("AbC", "aBc", "z"));

        // Text is multiple chars, multiple match
        assertEquals("xyzxyz", 
            StringUtil.replaceIgnoreCase("abcabc", "abc", "xyz"));
        assertEquals("xyzxyz", 
            StringUtil.replaceIgnoreCase("AbCaBC", "aBc", "xyz"));

        // Text is multiple chars, multiple match, expansion
        assertEquals("hellohello", 
            StringUtil.replaceIgnoreCase("abcabc", "abc", "hello"));
        assertEquals("hellohello", 
            StringUtil.replaceIgnoreCase("AbCaBC", "aBc", "hello"));

        // Text is multiple chars, multiple match, contraction
        assertEquals("", StringUtil.replaceIgnoreCase("abcabc", "abc", ""));
        assertEquals("", StringUtil.replaceIgnoreCase("AbCaBC", "aBc", ""));

        // Text is multiple char superset, no match
        assertEquals("1abc2", 
            StringUtil.replaceIgnoreCase("1abc2", "xyz", "123"));
        assertEquals("1AbC2", 
            StringUtil.replaceIgnoreCase("1AbC2", "XyZ", "123"));
        
        // Text is multiple char superset, single match
        assertEquals("1xyz2", 
            StringUtil.replaceIgnoreCase("1abc2", "abc", "xyz"));
        assertEquals("1xyz2", 
            StringUtil.replaceIgnoreCase("1AbC2", "aBc", "xyz"));
                    
        // Text is multiple char superset, single match, expansion
        assertEquals("1*abc*2", 
            StringUtil.replaceIgnoreCase("1abc2", "abc", "*abc*"));
        assertEquals("1*abc*2", 
            StringUtil.replaceIgnoreCase("1AbC2", "aBc", "*abc*"));
                        
        // Text is multiple char superset, single match, contraction
        assertEquals("12", StringUtil.replaceIgnoreCase("1abc2", "abc", ""));
        assertEquals("12", StringUtil.replaceIgnoreCase("1AbC2", "aBc", ""));

        // Text is multiple chars superset, multiple match
        assertEquals("xyz123xyz", 
            StringUtil.replaceIgnoreCase("abc123abc", "abc", "xyz"));
        assertEquals("xyz123xyz", 
            StringUtil.replaceIgnoreCase("AbC123aBC", "aBc", "xyz"));

        // Text is multiple chars superset, multiple match, expansion
        assertEquals("hello123hello", 
            StringUtil.replaceIgnoreCase("abc123abc", "abc", "hello"));
        assertEquals("hello123hello", 
            StringUtil.replaceIgnoreCase("AbC123aBC", "aBc", "hello"));

        // Text is multiple chars superset, multiple match, contraction
        assertEquals("123", 
            StringUtil.replaceIgnoreCase("abc123abc", "abc", ""));
        assertEquals("123", 
            StringUtil.replaceIgnoreCase("AbC123aBC", "aBc", ""));
    }
}