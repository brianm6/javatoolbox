package toolbox.util.test;

import java.io.LineNumberReader;
import java.io.StringReader;
import java.util.ArrayList;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.StringUtil;

/**
 * StringUtilTest
 */
public class StringUtilTest extends TestCase
{
    /** Logger **/
    private static Logger logger = 
        Logger.getLogger(StringUtilTest.class);

    /**
     * Entrypoint
     * 
     * @param  args  Args
     */
    public static void main(String[] args)
    {
        TestRunner.run(StringUtilTest.class);
    }

    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
    
    /**
     * StringUtilTest constructor comment.
     * 
     * @param name java.lang.String
     */
    public StringUtilTest(String name)
    {
        super(name);
    }

    //--------------------------------------------------------------------------
    //  Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests right() for proper truncation behavior
     */
    public void testRightForTruncation()
    {
        String s = "HELLO";

        String a1 = "   HELLO";
        String a2 = "333HELLO";
        String a3 = "HELLO";
        String a4 = "HELL";
        String b1 = StringUtil.right( s, 8, ' ' );
        String b2 = StringUtil.right( s, 8, '3' );
        String b3 = StringUtil.right( s, 4, ' ' );
        String b4 = StringUtil.right( s, 4, ' ', true );
  
        if( !a1.equals( b1 )) 
        {
            fail( "ERROR RIGHT 1" );
        }

        if( !a2.equals( b2 )) 
        {
            fail( "ERROR RIGHT 2" );
        }

        if( !a3.equals( b3 )) 
        {
            fail( "ERROR RIGHT 3" );
        }

        if( !a4.equals( b4 )) 
        {
            fail( "ERROR RIGHT 4" );
        }
    }

    /**
     * Tests left() for proper truncation behavior
     */
    public void testLeftForTruncation()
    {
        String s = "HELLO";

        String a1 = "HELLO   ";
        String a2 = "HELLO333";
        String a3 = "HELLO";
        String a4 = "HELL";
        String b1 = StringUtil.left( s, 8, ' ' );
        String b2 = StringUtil.left( s, 8, '3' );
        String b3 = StringUtil.left( s, 4, ' ' );
        String b4 = StringUtil.left( s, 4, ' ', true );

        if( !a1.equals( b1 )) 
        {
            fail( "ERROR LEFT 1" );
        }

        if( !a2.equals( b2 )) 
        {
            fail( "ERROR LEFT 2" );
        }

        if( !a3.equals( b3 )) 
        {
            fail( "ERROR LEFT 3" );
        }

        if( !a4.equals( b4 )) 
        {
            fail( "ERROR LEFT 4" );
        }
    }

    /**
     * Tests static toString( List )
     */
    public void testToStringForList()
    {
        ArrayList theList = new ArrayList();
        theList.add( "ELEM_ONE" );
        theList.add( "ELEM_TWO" );
        theList.add( "ELEM_THREE" );

        String result = StringUtil.toString( theList );
        String expected = " [0] = ELEM_ONE [1] = ELEM_TWO [2] = ELEM_THREE";

        if( ! expected.equals( result ))
        {
            fail( " Result(" + result + ") does not match " +
               "expected(" + expected + ")" );
        }
    }
    
    /**
     * Tests isNullOrEmpty()
     */
    public void testIsNullOrEmpty()
    {
        assertTrue("pass in null should return true", 
            StringUtil.isNullOrEmpty(null));
            
        assertTrue("pass in empty shoudl return true", 
            StringUtil.isNullOrEmpty(""));
            
        assertTrue("pass in space should return false", 
            !StringUtil.isNullOrEmpty(" "));
            
        assertTrue("pass in string should return false", 
            !StringUtil.isNullOrEmpty("turbo"));
    }
    
    /**
     * Tests getStringRuler()
     * 
     * @throws Exception on error
     */
    public void testGetStringRuler() throws Exception
    {
        /* one digit length test */
        {
            String s  = "abcd";
            String sr = StringUtil.getStringRuler(s);
            
            logger.debug("\n" +sr);
            
            assertTrue("ruler incorrect", sr.startsWith(s));
            assertTrue("ruler incorrect", sr.endsWith("1234"));
        }
    
        /* two digit length test */
        {
            String s  = "abcdefghijklmnopqrstuvwxyz";
            String sr = StringUtil.getStringRuler(s);
            
            logger.debug("\n" +sr);
            
            
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
        //======================================
        //      STRING LENGTH = 0 
        //======================================
         
        /* test empty string with zero */
        {
            String s = StringUtil.truncate("", 0);
            assertEquals(s, "");
        }

        /* test empty string with one */
        {
            String s = StringUtil.truncate("", 1);
            assertEquals(s, "");
        }
        
        /* test empty string with many */
        {
            String s = StringUtil.truncate("", 50);
            assertEquals(s, "");
        }
        
        //======================================
        //      STRING LENGTH = 1 
        //======================================
         
        /* test 1 char string with zero */
        {
            String t = "x";
            String s = StringUtil.truncate(t, 0);
            assertEquals(s, "");
        }

        /* test 1 char string with one */
        {
            String t = "x";
            String s = StringUtil.truncate(t, 1);
            assertEquals(s, t);
        }
        
        /* test 1 char string with many */
        {
            String t = "x";
            String s = StringUtil.truncate(t, 50);
            assertEquals(s, t);
        }
        
        //======================================
        //      STRING LENGTH > 1
        //======================================
        
        /* test non-empty string with zero */
        {
            String t = "hello";
            String s = StringUtil.truncate(t, 0);
            assertEquals(s, "");
        }

        /* test non-empty string with one */
        {
            String t = "hello";
            String s = StringUtil.truncate(t, 1);
            assertEquals(s, "h");
        }
        
        /* test non-empty string with many */
        {
            String t = "hello";
            String s = StringUtil.truncate(t, 4);
            assertEquals(s, "hell");
        }
        
        /* test non-empty string with exact length */
        {
            String t = "hello";
            String s = StringUtil.truncate(t, t.length());
            assertEquals(s, t);
        }

        /* test non-empty string with length greater than string */
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
        assertEquals("trimmed empty string incorrect",
            "", StringUtil.trim("",'x'));
    }
    
    /**
     * Tests trim() for a single char string
     */
    public void testTrimOne()
    {
        String s = "x";
        assertEquals("trimmed incorrect", "", StringUtil.trim(s,'x'));
        assertEquals("trimmed incorrect", s, StringUtil.trim(s,' '));
    }

    /**
     * Tests trim() for larger string
     */
    public void testTrimMany()
    {
        String s = "..abcdefg..x..";
        assertEquals("trim incorrect", "abcdefg..x", StringUtil.trim(s,'.'));
        assertEquals("trim incorrect", s, StringUtil.trim(s, 'z'));
    }
 
 
    /**
     * Tests trim() for trimming entire string
     */
    public void testTrimEntire()
    {
        String s = "aaaaaaaaaaaaaaaaaaaaaaaa";
        assertEquals("trim incorrect", "", StringUtil.trim(s, 'a'));            
    }
    
    /**
     * Tests trim() for prefix trimming only
     */
    public void testTrimPrefixOnly()
    {
        String s = ".......aaaaaa";
        assertEquals("trim incorrect", "aaaaaa", StringUtil.trim(s, '.'));            
    }

    /**
     * Tests trim() for suffix trimming only
     */
    public void testTrimSuffixOnly()
    {
        String s = "aaaaaa........";
        assertEquals("trim incorrect", "aaaaaa", StringUtil.trim(s, '.'));            
    }
}
