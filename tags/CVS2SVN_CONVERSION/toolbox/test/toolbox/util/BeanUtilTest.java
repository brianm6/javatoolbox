package toolbox.util;

import javax.swing.JLabel;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Unit test for {@link toolbox.util.BeanUtil}.
 */
public class BeanUtilTest extends TestCase
{
    private static final Logger logger_ = Logger.getLogger(BeanUtilTest.class);
    
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    public static void main(String[] args)
    {
        TestRunner.run(BeanUtilTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests toString(javabean)
     */
    public void testToStringObject()
    {
        logger_.info("Running testToStringObject...");
        
        JLabel label = new JLabel("Whoopee!");
        
        logger_.debug(
            StringUtil.banner(
                StringUtils.center("JLabel BeanInfo", 80)
                + "\n"
                + StringUtil.BR 
                + "\n" 
                + BeanUtil.toString(label)));
    }
}
