package toolbox.util.test;

import java.io.LineNumberReader;
import java.io.StringReader;
import java.util.ArrayList;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.log4j.SmartLogger;
import toolbox.util.StringUtil;
import toolbox.util.Stringz;

/**
 * Unit test for StringUtil
 */
public class StringUtilTest extends TestCase implements Stringz
{
    private static Logger logger_ = 
        Logger.getLogger(StringUtilTest.class);

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
        TestRunner.run(StringUtilTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests right() for proper truncation behavior
     */
    public void testRightForTruncation()
    {
        logger_.info("Running testRightForTruncation...");

        String s = "HELLO";

        String a1 = "   HELLO";
        String a2 = "333HELLO";
        String a3 = "HELLO";
        String a4 = "HELL";
        String b1 = StringUtil.right(s, 8, ' ');
        String b2 = StringUtil.right(s, 8, '3');
        String b3 = StringUtil.right(s, 4, ' ');
        String b4 = StringUtil.right(s, 4, ' ', true);
  
        if (!a1.equals(b1))
            fail("ERROR RIGHT 1");

        if (!a2.equals(b2))
            fail("ERROR RIGHT 2");

        if (!a3.equals(b3))
            fail("ERROR RIGHT 3");

        if (!a4.equals(b4))
            fail("ERROR RIGHT 4");
    }

    /**
     * Tests right(int, width)
     */
    public void testRightForInt()
    {
        logger_.info("Running testRightForInt...");
        
        assertEquals("  345", StringUtil.right(345, 5));
        assertEquals("345", StringUtil.right(345, 3));
        assertEquals("345", StringUtil.right(345, 2));        
    }

    /**
     * Tests right(String, width)
     */
    public void testRightForString()
    {
        logger_.info("Running testRightForString...");
        
        assertEquals("  345", StringUtil.right("345", 5));
        assertEquals("345", StringUtil.right("345", 3));
        assertEquals("345", StringUtil.right("345", 2));        
    }

    /**
     * Tests left() for proper truncation behavior
     */
    public void testLeftForTruncation()
    {
        logger_.info("Running testLeftForTruncation...");

        String s = "HELLO";

        String a1 = "HELLO   ";
        String a2 = "HELLO333";
        String a3 = "HELLO";
        String a4 = "HELL";
        String b1 = StringUtil.left(s, 8, ' ');
        String b2 = StringUtil.left(s, 8, '3');
        String b3 = StringUtil.left(s, 4, ' ');
        String b4 = StringUtil.left(s, 4, ' ', true);

        if (!a1.equals(b1))
            fail("ERROR LEFT 1");

        if (!a2.equals(b2))
            fail("ERROR LEFT 2");

        if (!a3.equals(b3))
            fail("ERROR LEFT 3");

        if (!a4.equals(b4))
            fail("ERROR LEFT 4");
    }

    /**
     * Tests left(int, width)
     */
    public void testLeftForInt()
    {
        logger_.info("Running testLeftForInt...");
        
        assertEquals("345  ", StringUtil.left(345, 5));
        assertEquals("345", StringUtil.left(345, 3));
        assertEquals("345", StringUtil.left(345, 2));        
    }

    /**
     * Tests static toString( List )
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
     * Tests isNullOrBlank()
     */
    public void testIsNullOrBlank()
    {
        logger_.info("Running testIsNullOrBlank...");
        
        assertTrue("pass in null should return true", 
            StringUtil.isNullOrEmpty(null));
            
        assertTrue("pass in empty should return true", 
            StringUtil.isNullOrEmpty(""));
            
        assertTrue("pass in space should return true", 
            !StringUtil.isNullOrEmpty(" "));

        assertTrue("pass in lotsa spaces should return true", 
            !StringUtil.isNullOrEmpty("                   "));
            
        assertTrue("pass in string should return false", 
            !StringUtil.isNullOrEmpty("howdy"));
    }

    /**
     * Tests isNullOrEmpty()
     */
    public void testIsNullOrEmpty()
    {
        logger_.info("Running testIsNullOrEmpty...");
        
        assertTrue("pass in null should return true", 
            StringUtil.isNullOrEmpty(null));
            
        assertTrue("pass in empty shoudl return true", 
            StringUtil.isNullOrEmpty(""));
            
        assertTrue("pass in space should return false", 
            !StringUtil.isNullOrEmpty(" "));
            
        assertTrue("pass in string should return false", 
            !StringUtil.isNullOrEmpty("howdy"));
    }
    
    /**
     * Tests getStringRuler()
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
     * Tests truncate()
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
     * Tests trim() for an empty string
     */
    public void testTrimEmpty()
    {
        logger_.info("Running testTrimEmpty...");
        
        assertEquals("trimmed empty string incorrect",
            "", StringUtil.trim("",'x'));
    }
    
    /**
     * Tests trim() for a single char string
     */
    public void testTrimOne()
    {
        logger_.info("Running testTrimOne...");
        
        String s = "x";
        assertEquals("trimmed incorrect", "", StringUtil.trim(s,'x'));
        assertEquals("trimmed incorrect", s, StringUtil.trim(s,' '));
    }

    /**
     * Tests trim() for larger string
     */
    public void testTrimMany()
    {
        logger_.info("Running testTrimMany...");
        
        String s = "..abcdefg..x..";
        assertEquals("trim incorrect", "abcdefg..x", StringUtil.trim(s,'.'));
        assertEquals("trim incorrect", s, StringUtil.trim(s, 'z'));
    }
 
    /**
     * Tests trim() for trimming entire string
     */
    public void testTrimEntire()
    {
        logger_.info("Running testTrimEntire...");
        
        String s = "aaaaaaaaaaaaaaaaaaaaaaaa";
        assertEquals("trim incorrect", "", StringUtil.trim(s, 'a'));            
    }
    
    /**
     * Tests trim() for prefix trimming only
     */
    public void testTrimPrefixOnly()
    {
        logger_.info("Running testTrimPrefixOnly...");
        
        String s = ".......aaaaaa";
        assertEquals("trim incorrect", "aaaaaa", StringUtil.trim(s, '.'));
    }

    /**
     * Tests trim() for suffix trimming only
     */
    public void testTrimSuffixOnly()
    {
        logger_.info("Running testTrimSuffixOnly...");
        
        String s = "aaaaaa........";
        assertEquals("trim incorrect", "aaaaaa", StringUtil.trim(s, '.'));
    }
    
    /**
     * Tests wrap()
     */
    public void testWrap()
    {
        logger_.info("Running testWrap...");
        
        String s = "0123456789";
        
        logger_.info(StringUtil.wrap(s));
        logger_.info(NL + StringUtil.wrap(s, 5));
        logger_.info(StringUtil.wrap(s, 5, true));
        logger_.info(StringUtil.wrap(s, 5, false));
    }

    /**
     * Tests replace() for zero replacements
     */
    public void testReplaceNone()
    {
        logger_.info("Running testReplaceNone...");
        
        String a = "howdy";
        String b = StringUtil.replace(a, "whatever", "blah");
        
        assertEquals(a, b);
    }
    
    /**
     * Tests replace() for a single replacement
     */
    public void testReplaceOne()
    {
        logger_.info("Running testReplaceOne...");
        
        String a = "howdy";
        String b = StringUtil.replace(a, "howdy", "mister");
        
        assertEquals("mister", b);
    }
    
    /**
     * Tests replace() with max number of replacements
     */
    public void testReplaceMax()
    {
        logger_.info("Running testReplaceMax...");
        
        String a = "me me me";
        String b = StringUtil.replace(a, "me", "ho", 2);
        
        assertEquals("ho ho me", b);
    }
    
    /**
     * Tests isMultiline()
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
     * Tests tokenize()
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
            assertEquals(5,       StringUtil.tokenize(m, "Z", true).length);
            assertEquals("one",   StringUtil.tokenize(m, "Z", true)[0]);
            assertEquals("Z",     StringUtil.tokenize(m, "Z", true)[1]);
            assertEquals("two",   StringUtil.tokenize(m, "Z", true)[2]);
            assertEquals("Z",     StringUtil.tokenize(m, "Z", true)[3]);            
            assertEquals("three", StringUtil.tokenize(m, "Z", true)[4]);
    }
    
    /**
     * Tests getLine()
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
        assertEquals("", StringUtil.getLine(NL, 0));
        assertNull(StringUtil.getLine(NL, 1));
        assertNull(StringUtil.getLine(NL, 500));
        
        // String with one char + newline
        s = "a" + NL;
        assertEquals("a", StringUtil.getLine(s, 0));
        assertNull(StringUtil.getLine(s, 1));
        assertNull(StringUtil.getLine(s, 500));

        // String with multiple chars + newline
        s = "abc" + NL;
        assertEquals("abc", StringUtil.getLine(s, 0));
        assertNull(StringUtil.getLine(s, 1));
        assertNull(StringUtil.getLine(s, 500));
        
        // String with multiple newlines
        int cnt = 10;
        s = StringUtil.repeat(NL, cnt);
        for (int i=0; i<cnt; i++)
            assertEquals("", StringUtil.getLine(s, i));
        assertNull(StringUtil.getLine(s, 500));

        // String with multiple newlines w/ single chars
        s = "a" + NL + "b" + NL + "c" + NL + "d" + NL;
        assertEquals("a", StringUtil.getLine(s, 0));
        assertEquals("b", StringUtil.getLine(s, 1));
        assertEquals("c", StringUtil.getLine(s, 2));
        assertEquals("d", StringUtil.getLine(s, 3));                        
        assertNull(StringUtil.getLine(s, 500));
        
        // String with multiple newlines w/ multiple chars
        s = "abc" + NL + "def" + NL + "ghi" + NL + "jkl" + NL;
        assertEquals("abc", StringUtil.getLine(s, 0));
        assertEquals("def", StringUtil.getLine(s, 1));
        assertEquals("ghi", StringUtil.getLine(s, 2));
        assertEquals("jkl", StringUtil.getLine(s, 3));                        
        assertNull(StringUtil.getLine(s, 500));
        
        // String with multiple newlines w/ multiple chars. The last line does
        // not have a terminating newline
        s = "abc" + NL + "def" + NL + "ghi" + NL + "jkl";
        assertEquals("abc", StringUtil.getLine(s, 0));
        assertEquals("def", StringUtil.getLine(s, 1));
        assertEquals("ghi", StringUtil.getLine(s, 2));
        assertEquals("jkl", StringUtil.getLine(s, 3));                        
        assertNull(StringUtil.getLine(s, 500));
    }
    
    /**
     * Tests reverse()
     */
    public void testReverse()
    {
        logger_.info("Running testReverse...");
        
        // Zero
        assertEquals("", StringUtil.reverse(""));
        
        // One
        assertEquals("x", StringUtil.reverse("x"));
        
        // Many
        assertEquals("ydwoh", StringUtil.reverse("howdy"));
        
        // Reverse x 2 = original
        assertEquals("original", 
            StringUtil.reverse(StringUtil.reverse("original")));
    }
}