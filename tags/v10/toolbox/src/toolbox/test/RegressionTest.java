package toolbox.test;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import org.apache.log4j.BasicConfigurator;
import toolbox.util.io.test.StringInputStreamTest;
import toolbox.util.io.test.StringOutputStreamTest;
import toolbox.util.test.ArrayUtilTest;
import toolbox.util.test.AssertTest;
import toolbox.util.test.FileUtilTest;
import toolbox.util.test.ResourceUtilTest;
import toolbox.util.test.StreamUtilTest;
import toolbox.util.test.StringUtilTest;
import toolbox.util.test.ThreadUtilTest;

/**
 * Regression test suite for toolbox library
 */
public class RegressionTest
{
    /**
     * Launches the tests in text mode
     */
    public static void main(String[] args)
    {
        junit.swingui.TestRunner.run(RegressionTest.class);
    }
    
    /**
     * Provides master list of all test cases
     * 
     * @return  Test   Suite containing all test cases
     */
    public static Test suite()
    {
        BasicConfigurator.configure();
                
        TestSuite suite = new TestSuite();
         
        // util
        suite.addTest(new TestSuite(ArrayUtilTest.class));
        suite.addTest(new TestSuite(ResourceUtilTest.class));
        suite.addTest(new TestSuite(ThreadUtilTest.class));
        suite.addTest(new TestSuite(StreamUtilTest.class));
        suite.addTest(new TestSuite(FileUtilTest.class));
        suite.addTest(new TestSuite(AssertTest.class));
        suite.addTest(new TestSuite(StringUtilTest.class));
        
        // util.io
        suite.addTest(new TestSuite(StringInputStreamTest.class));
        suite.addTest(new TestSuite(StringOutputStreamTest.class));        
        
        return suite;
    }
}