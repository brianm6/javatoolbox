package toolbox.util.db;

import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.junit.testcase.UITestCase;

/**
 * Unit test for {@link toolbox.util.db.SQLFormatterView}.
 */
public class SQLFormatterViewTest extends UITestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(SQLFormatterViewTest.class);
    
    public static void main(String[] args)
    {
        TestRunner.run(SQLFormatterViewTest.class);
    }
    
    
    public void testView() throws Exception
    {
        launchInFrame(new SQLFormatterView(new SQLFormatter()));
    }
}