package toolbox.util;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;

import javax.swing.JPanel;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import nu.xom.Element;

import org.apache.log4j.Logger;

import toolbox.util.FontUtil;
import toolbox.util.XOMUtil;

/**
 * Unit test for {@link toolbox.util.FontUtil}.
 */
public class FontUtilTest extends TestCase
{
    private static final Logger logger_ = Logger.getLogger(FontUtilTest.class);
    
    //--------------------------------------------------------------------------
    // Main 
    //--------------------------------------------------------------------------

    public static void main(String[] args)
    {
        TestRunner.run(FontUtilTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests 
    //--------------------------------------------------------------------------

    public void testToElementToFont() throws Exception
    {
        logger_.info("Running testToElement...");
        
        try {
            Element forward = FontUtil.toElement(FontUtil.getPreferredMonoFont());
            String xml = XOMUtil.toXML(forward);
    
            logger_.debug("\n" + xml);
    
            Font reverse = FontUtil.toFont(forward);
            assertNotNull(reverse);
        }
        catch (HeadlessException he) {
            logger_.info("Skipping test because: " + he.getMessage());
        }
    }


    public void testSetBold()
    {
        logger_.info("Running testSetBold...");
        
        Font f = new Font("Dialog", Font.PLAIN, 10);
        JPanel p = new JPanel();
        p.setFont(f);
        FontUtil.setBold(p);
        assertTrue(p.getFont().isBold());
    }

    
    public void testGetPreferredMonoFont()
    {
        logger_.info("Running testGetPreferredMonoFont...");
        
        Font f = FontUtil.getPreferredMonoFont();
        assertNotNull(f);
        logger_.debug("Preferred mono font: " + f.getName());
    }


    public void testGetPreferredSerifFont()
    {
        logger_.info("Running testGetPreferredSerifFont...");
        
        Font f = FontUtil.getPreferredSerifFont();
        assertNotNull(f);
        logger_.debug("Preferred serif font: " + f.getName());
    }

    
    public void testGrow()
    {
        logger_.info("Running testGrow...");
        
        Font f = FontUtil.getPreferredMonoFont();
        Font g = FontUtil.grow(f, 8);
        assertEquals(f.getSize() + 8, g.getSize());
    }
    
    
    public void testShrink()
    {
        logger_.info("Running testShrink...");
        
        Font f = FontUtil.getPreferredMonoFont();
        Font g = FontUtil.shrink(f, 4);
        assertEquals(f.getSize() - 4, g.getSize());
    }
    
    
    public void testSetSize()
    {
        logger_.info("Running testSetSize...");
        
        int expected = 48;
        Font before = FontUtil.getPreferredSerifFont();
        Font after  = FontUtil.setSize(before, expected);
        assertEquals(expected, after.getSize());
    }
    
    
    public void testIsMonospacedTrue() throws Exception
    {
        logger_.info("Running testIsMonospacedTrue...");
        
        Font f = FontUtil.getPreferredMonoFont();
        assertTrue(
            f.getName() + " should be a monospaced font", 
            FontUtil.isMonospaced(f));
    }

    
    public void testIsMonospacedFalse() throws Exception
    {
        logger_.info("Running testIsMonospacedFalse...");
        
        Font f = FontUtil.getPreferredSerifFont();
        assertFalse(
            f.getName() + " should not be monspaced", 
            FontUtil.isMonospaced(f));
    }
    
    
    public void testIsMonospacedAll()
    {
        logger_.info("Running testIsMonospacedAll...");
        
        Font[] fonts = 
            GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
        
        for (int i = 0; i < fonts.length; i++)
            if (FontUtil.isMonospaced(fonts[i]))
                logger_.debug(fonts[i].getName() + " is monospaced"); 
    }
    
    
    public void testPreferenced() throws Exception
    {
        logger_.info("Running testPreferenced...");
        
        // Induce initialization
        FontUtil.getPreferredMonoFont();
        FontUtil.getPreferredSerifFont();
        
        Element root = new Element("root");
        FontUtil.getInstance().savePrefs(root);
        
        logger_.debug(StringUtil.banner(XMLUtil.format(root.toXML())));
 
        assertEquals(
            FontUtil.getPreferredMonoFont().getName(),
            root.getFirstChildElement(FontUtil.NODE_FONTUTIL)
                .getFirstChildElement(FontUtil.NODE_DEFAULT_MONO)
                .getFirstChildElement(FontUtil.NODE_FONT)
                .getAttributeValue(FontUtil.ATTR_NAME));
        
        assertEquals(
            FontUtil.getPreferredSerifFont().getName(),
            root.getFirstChildElement(FontUtil.NODE_FONTUTIL)
                .getFirstChildElement(FontUtil.NODE_DEFAULT_SERIF)
                .getFirstChildElement(FontUtil.NODE_FONT)
                .getAttributeValue(FontUtil.ATTR_NAME));
        
        // Increase font size to 20, read back in, and verify
        root.getFirstChildElement(FontUtil.NODE_FONTUTIL)
            .getFirstChildElement(FontUtil.NODE_DEFAULT_MONO)
            .getFirstChildElement(FontUtil.NODE_FONT)
            .getAttribute(FontUtil.ATTR_SIZE)
            .setValue("20");
        
        root.getFirstChildElement(FontUtil.NODE_FONTUTIL)
            .getFirstChildElement(FontUtil.NODE_DEFAULT_SERIF)
            .getFirstChildElement(FontUtil.NODE_FONT)
            .getAttribute(FontUtil.ATTR_SIZE)
            .setValue("20");
        
        logger_.debug(StringUtil.banner(XMLUtil.format(root.toXML())));
        
        FontUtil.getInstance().applyPrefs(root);
        assertEquals(20, FontUtil.getPreferredMonoFont().getSize());
        assertEquals(20, FontUtil.getPreferredSerifFont().getSize());
    }
}