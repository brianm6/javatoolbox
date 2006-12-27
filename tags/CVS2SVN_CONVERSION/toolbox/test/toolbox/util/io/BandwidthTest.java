package toolbox.util.io;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.StringUtil;

/**
 * Unit test for {@link toolbox.util.io.Bandwidth}.
 */
public class BandwidthTest extends TestCase
{
    private static final Logger logger_ = Logger.getLogger(BandwidthTest.class);
    
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    public static void main(String[] args)
    {
        TestRunner.run(BandwidthTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    public void testToString() 
    {
        logger_.info("Running testToString...");
        
        Bandwidth bw = new Bandwidth(1000, 1000, Bandwidth.TYPE_BOTH);
        
        logger_.debug(StringUtil.banner(bw.toString()));
    }
}