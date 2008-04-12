package toolbox.tivo;

import java.awt.Dimension;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

/**
 * Unit test for {@link toolbox.tivo.RatioFixer}.
 */
public class RatioFixerTest extends TestCase {

    private static final Logger logger_ = 
        Logger.getLogger(RatioFixerTest.class);
    
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    public static void main(String args[]) {
        TestRunner.run(RatioFixerTest.class);
    }
    
    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /*
     * Test method for 'toolbox.tivo.RatioFixer.calc()'
     */
    public void testCalc() {
        logger_.info("Running testCalc...");
        
        Dimension target = new Dimension(720, 480);
        Dimension source = new Dimension(416, 304);
     
        RatioFixer fixer = new RatioFixer(target, source);
        fixer.calc();
        
        logger_.debug(fixer.getWidth() + "x" + fixer.getHeight());
        logger_.debug("Pad left-right = " + fixer.getPadLeftRight());
        logger_.debug("Pad = " + fixer.getPad());
    }
}
