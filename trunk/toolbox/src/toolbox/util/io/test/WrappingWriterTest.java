package toolbox.util.io.test;

import java.io.IOException;
import java.io.StringWriter;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.StringUtil;
import toolbox.util.io.WrappingWriter;

/**
 * Unit test for WrappingWriter
 */
public class WrappingWriterTest extends TestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(WrappingWriterTest.class);
        
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
        
    /**
     * Entrypoint
     * 
     * @param  args none recognized
     */    
    public static void main(String[] args)
    {
        TestRunner.run(WrappingWriterTest.class);
    }

    //--------------------------------------------------------------------------
    //  Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests WrappingWriter
     * 
     * @throws IOException on error
     */
    public void testWrappingWriter() throws IOException
    {
        logger_.info("Running testWrappingWriter...");
        
        StringWriter sw = new StringWriter();
        WrappingWriter w = new WrappingWriter(sw, 10, "", "");
        
        w.write("0123456789");
        w.write("9876543210");
        w.write("01234567899876543210");
        w.write("11111111112222222222333333333344444444445555555555");
        w.flush();
        w.close();
        logger_.info("\n" + sw.toString());
    }
    
    /**
     * Tests WrappingWriter
     * 
     * @throws IOException on error
     */
    public void testWrappingWriterWithPrePost() throws IOException
    {
        logger_.info("Running testWrappingWriterWithPrePost...");
        
        StringWriter sw = new StringWriter();
        WrappingWriter w = new WrappingWriter(sw, 10, "[", "]");
        
        w.write("12345678");
        w.write("87654321");
        w.write("1234567887654321");
        w.write("111111112222222233333333444444445555");
        w.flush();
        w.close();
        logger_.info("\n" + sw.toString());
    }
    
    
    /**
     * Tests writer with zero characters
     * 
     * @throws IOException on error
     */
    public void testEmpty() throws IOException
    {
        logger_.info("Running testEmpty...");
        
        StringWriter sw = new StringWriter(); 
        WrappingWriter ww = new WrappingWriter(sw);
        ww.write("");
        ww.close();
        String s = sw.toString();
        String compare = "";
        logger_.info("\n" + StringUtil.getStringRuler(s));
        logger_.info("\n" + StringUtil.getStringRuler(compare));
        assertEquals("Strings don't match", compare, s);
    }


    /**
     * Tests writer with one character
     * 
     * @throws IOException on error
     */
    public void testOne() throws IOException
    {
        logger_.info("Running testOne...");
        
        String one = "a";
        StringWriter sw = new StringWriter(); 
        WrappingWriter ww = new WrappingWriter(sw);
        ww.write(one);
        ww.close();
        String s = sw.toString();
        String compare = "a"; 
        logger_.info("\n" + StringUtil.getStringRuler(s));
        assertEquals("Strings don't match", compare, s);
    }
    
    
    /**
     * Tests exactly one line
     * 
     * @throws Exception on error
     */
    public void testOneLine() throws Exception
    {
        logger_.info("Running testOneLine...");
        
        String expected = "12345";
        
        StringWriter sw = new StringWriter(); 
        WrappingWriter ww = new WrappingWriter(sw, 7);
        ww.write("12345");
        ww.close();
        String s = sw.toString();
        logger_.info(StringUtil.getStringRuler(s));
        assertEquals("strings don't match", expected, s);
    }    


    /**
     * Tests exactly one line and one char
     * 
     * @throws Exception on error
     */
    public void testOneLineOneChar() throws Exception
    {
        logger_.info("Running testOneLineOneChar...");
        
        String expected = "12345\n6";
        
        StringWriter sw = new StringWriter(); 
        WrappingWriter ww = new WrappingWriter(sw, 5);
        ww.write("123456");
        ww.close();
        String s = sw.toString();
        logger_.info(s);
        assertEquals("strings don't match", expected, s);
    }
    
    
    /**
     * Tests exactly one line and two char
     * 
     * @throws Exception on error
     */
    public void testOneLineTwoChar() throws Exception
    {
        logger_.info("Running testOneLineTwoChar...");
        
        String expected = "12345\n67";
        
        StringWriter sw = new StringWriter(); 
        WrappingWriter ww = new WrappingWriter(sw, 5);
        ww.write("1234567");
        ww.close();
        String s = sw.toString();
        logger_.info(s);
        assertEquals("strings don't match", expected, s);
    }    
    
    
    /**
     * Tests wrapper for a width of 1
     * 
     * @throws Exception on error
     */
    public void testSmallWidth() throws Exception
    {
        logger_.info("Running testSmallWitdth...");
        
        String expected = "1\n2\n3\n4\n5";
        
        StringWriter sw = new StringWriter(); 
        WrappingWriter ww = new WrappingWriter(sw, 1);
        ww.write("12345");
        ww.close();
        String s = sw.toString();
        logger_.info(s);
        assertEquals("strings don't match", expected, s);
    }
    

    /**
     * Tests writer with zero characters
     * 
     * @throws IOException on error
     */
    public void testEmptyDecorated() throws IOException
    {
        logger_.info("Running testEmptyDecorated...");
        
        StringWriter sw = new StringWriter(); 
        WrappingWriter ww = new WrappingWriter(sw, 80, "[", "]");
        ww.write("");
        ww.close();
        String s = sw.toString();
        String compare = "";
        logger_.info("\n" + StringUtil.getStringRuler(s));
        assertEquals("Strings don't match", compare, s);
    }


    /**
     * Tests writer with one character
     * 
     * @throws IOException on error
     */
    public void testOneDecorated() throws IOException
    {
        logger_.info("Running testOneDecorated...");        
        
        String one = "a";
        
        StringWriter sw = new StringWriter(); 
        WrappingWriter ww = new WrappingWriter(sw, 80,"[", "]");
        ww.write(one);
        ww.close();
        String s = sw.toString();
        String compare = "[a]"; 
        logger_.info("\n" + StringUtil.getStringRuler(s));
        assertEquals("Strings don't match", compare, s);
    }
    
    
    /**
     * Tests exactly one line
     * 
     * @throws Exception on error
     */
    public void testOneLineDecorated() throws Exception
    {
        logger_.info("Running testOneLineDecorated...");        
        
        String expected = "[12345]";

        StringWriter sw = new StringWriter(); 
        WrappingWriter ww = new WrappingWriter(sw, 7, "[", "]");
        ww.write("12345");
        ww.close();
        String s = sw.toString();
        logger_.info("\n" + StringUtil.getStringRuler(s));
        assertEquals("strings don't match", expected, s);
    }    


    /**
     * Tests exactly one line and one char
     * 
     * @throws Exception on error
     */
    public void testOneLineOneCharDecorated() throws Exception
    {
        logger_.info("Running testOneLineOneCharDecorated...");        
        
        String expected = "[12345]\n[6]";
        
        StringWriter sw = new StringWriter(); 
        WrappingWriter ww = new WrappingWriter(sw, 7, "[","]");
        ww.write("123456");
        ww.close();
        String s = sw.toString();
        logger_.info(s);
        assertEquals("strings don't match", expected, s);
    }

    
    /**
     * Tests exactly one line and two char
     * 
     * @throws Exception on error
     */
    public void testOneLineTwoCharDecorated() throws Exception
    {
        logger_.info("Running testOneLineTwoCharDecorated...");        
        
        String expected = "[12345]\n[67]";
        
        StringWriter sw = new StringWriter(); 
        WrappingWriter ww = new WrappingWriter(sw, 7, "[", "]");
        ww.write("1234567");
        ww.close();
        String s = sw.toString();
        logger_.info(s);
        assertEquals("strings don't match", expected, s);
    }    
    
    
    /**
     * Tests wrapper for a width of 1
     * 
     * @throws Exception on error
     */
    public void testSmallWidthDecorated() throws Exception
    {
        logger_.info("Running testSmallWitdthDecorated...");
        
        String expected = "[1]\n[2]\n[3]\n[4]\n[5]";
        
        StringWriter sw = new StringWriter(); 
        WrappingWriter ww = new WrappingWriter(sw, 3, "[", "]");
        ww.write("12345");
        ww.close();
        String s = sw.toString();
        logger_.info(s);
        assertEquals("strings don't match", expected, s);
    }
    
    /**
     * Tests toString()
     * 
     * @throws Exception on error
     */
    public void testToString() throws Exception
    {
        logger_.info("Running testToString...");
        
        StringWriter sw = new StringWriter(); 
        WrappingWriter ww = new WrappingWriter(sw, 3, "[", "]");
        ww.write("we're testing toSTring");
        logger_.info(ww.toString());        
        ww.close();
    }
}
