package toolbox.util.io.test;

import java.io.IOException;
import java.io.StringWriter;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import toolbox.util.StringUtil;
import toolbox.util.io.WrappingWriter;

/**
 * Unit test for WrappingWriter
 */
public class WrappingWriterTest extends TestCase
{
    /**
     * Entrypoint
     * 
     * @param  args none recognized
     */    
    public static void main(String[] args)
    {
        TestRunner.run(WrappingWriterTest.class);
    }
    
    /**
     * Constructor for WrappingWriterTest.
     * 
     * @param  name  Name of test
     */
    public WrappingWriterTest(String name)
    {
        super(name);
    }

    /**
     * Setup
     */
    public void setUp()
    {
        System.out.println(StringUtil.repeat("-", 80));
    }

    /**
     * Tests WrappingWriter
     */
    public void testWrappingWriter() throws IOException
    {
        StringWriter sw = new StringWriter();
        WrappingWriter w = new WrappingWriter(sw, 10, "", "");
        
        w.write("0123456789");
        w.write("9876543210");
        w.write("01234567899876543210");
        w.write("11111111112222222222333333333344444444445555555555");
        w.flush();
        w.close();
        System.out.println(sw.toString());
    }
    
    /**
     * Tests WrappingWriter
     */
    public void testWrappingWriterWithPrePost() throws IOException
    {
        StringWriter sw = new StringWriter();
        WrappingWriter w = new WrappingWriter(sw, 10, "[", "]");
        
        w.write("12345678");
        w.write("87654321");
        w.write("1234567887654321");
        w.write("111111112222222233333333444444445555");
        w.flush();
        w.close();
        System.out.println(sw.toString());
    }
    
    
    /**
     * Tests writer with zero characters
     */
    public void testEmpty() throws IOException
    {
        StringWriter sw = new StringWriter(); 
        WrappingWriter ww = new WrappingWriter(sw);
        ww.write("");
        ww.close();
        String s = sw.toString();
        String compare = "";
        System.out.println(StringUtil.getStringRuler(s));
        System.out.println(StringUtil.getStringRuler(compare));
        assertEquals("Strings don't match", compare, s);
    }


    /**
     * Tests writer with one character
     */
    public void testOne() throws IOException
    {
        String one = "a";
        StringWriter sw = new StringWriter(); 
        WrappingWriter ww = new WrappingWriter(sw);
        ww.write(one);
        ww.close();
        String s = sw.toString();
        String compare = "a"; 
        System.out.println(StringUtil.getStringRuler(s));
        assertEquals("Strings don't match", compare, s);
    }
    
    
    /**
     * Tests exactly one line
     */
    public void testOneLine() throws Exception
    {
        String expected = "12345";
        
        StringWriter sw = new StringWriter(); 
        WrappingWriter ww = new WrappingWriter(sw, 7);
        ww.write("12345");
        ww.close();
        String s = sw.toString();
        System.out.println(StringUtil.getStringRuler(s));
        assertEquals("strings don't match", expected, s);
    }    


    /**
     * Tests exactly one line and one char
     */
    public void testOneLineOneChar() throws Exception
    {
        String expected = "12345\n6";
        
        StringWriter sw = new StringWriter(); 
        WrappingWriter ww = new WrappingWriter(sw, 5);
        ww.write("123456");
        ww.close();
        String s = sw.toString();
        System.out.println(s);
        assertEquals("strings don't match", expected, s);
    }
    
    
    /**
     * Tests exactly one line and two char
     */
    public void testOneLineTwoChar() throws Exception
    {
        String expected = "12345\n67";
        
        StringWriter sw = new StringWriter(); 
        WrappingWriter ww = new WrappingWriter(sw, 5);
        ww.write("1234567");
        ww.close();
        String s = sw.toString();
        System.out.println(s);
        assertEquals("strings don't match", expected, s);
    }    
    
    
    /**
     * Tests wrapper for a width of 1
     */
    public void testSmallWidth() throws Exception
    {
        String expected = "1\n2\n3\n4\n5";
        
        StringWriter sw = new StringWriter(); 
        WrappingWriter ww = new WrappingWriter(sw, 1);
        ww.write("12345");
        ww.close();
        String s = sw.toString();
        System.out.println(s);
        assertEquals("strings don't match", expected, s);
    }
    

    /**
     * Tests writer with zero characters
     */
    public void testEmptyDecorated() throws IOException
    {
        StringWriter sw = new StringWriter(); 
        WrappingWriter ww = new WrappingWriter(sw, 80, "[", "]");
        ww.write("");
        ww.close();
        String s = sw.toString();
        String compare = "";
        System.out.println(StringUtil.getStringRuler(s));
        assertEquals("Strings don't match", compare, s);
    }


    /**
     * Tests writer with one character
     */
    public void testOneDecorated() throws IOException
    {
        String one = "a";
        
        StringWriter sw = new StringWriter(); 
        WrappingWriter ww = new WrappingWriter(sw, 80,"[", "]");
        ww.write(one);
        ww.close();
        String s = sw.toString();
        String compare = "[a]"; 
        System.out.println(StringUtil.getStringRuler(s));
        assertEquals("Strings don't match", compare, s);
    }
    
    
    /**
     * Tests exactly one line
     */
    public void testOneLineDecorated() throws Exception
    {
        String expected = "[12345]";

        StringWriter sw = new StringWriter(); 
        WrappingWriter ww = new WrappingWriter(sw, 7, "[", "]");
        ww.write("12345");
        ww.close();
        String s = sw.toString();
        System.out.println(StringUtil.getStringRuler(s));
        assertEquals("strings don't match", expected, s);
    }    


    /**
     * Tests exactly one line and one char
     */
    public void testOneLineOneCharDecorated() throws Exception
    {
        String expected = "[12345]\n[6]";
        
        StringWriter sw = new StringWriter(); 
        WrappingWriter ww = new WrappingWriter(sw, 7, "[","]");
        ww.write("123456");
        ww.close();
        String s = sw.toString();
        System.out.println(s);
        assertEquals("strings don't match", expected, s);
    }

    
    /**
     * Tests exactly one line and two char
     */
    public void testOneLineTwoCharDecorated() throws Exception
    {
        String expected = "[12345]\n[67]";
        
        StringWriter sw = new StringWriter(); 
        WrappingWriter ww = new WrappingWriter(sw, 7, "[", "]");
        ww.write("1234567");
        ww.close();
        String s = sw.toString();
        System.out.println(s);
        assertEquals("strings don't match", expected, s);
    }    
    
    
    /**
     * Tests wrapper for a width of 1
     */
    public void testSmallWidthDecorated() throws Exception
    {
        String expected = "[1]\n[2]\n[3]\n[4]\n[5]";
        
        StringWriter sw = new StringWriter(); 
        WrappingWriter ww = new WrappingWriter(sw, 3, "[", "]");
        ww.write("12345");
        ww.close();
        String s = sw.toString();
        System.out.println(s);
        assertEquals("strings don't match", expected, s);
    }
}
