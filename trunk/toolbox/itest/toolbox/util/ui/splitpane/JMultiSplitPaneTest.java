package toolbox.util.ui.splitpane;

import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.junit.testcase.UITestCase;
import toolbox.util.ui.JSmartLabel;

/**
 * Unit test for {@link toolbox.util.ui.splitpane.JMultiSplitPane}.
 */
public class JMultiSplitPaneTest extends UITestCase
{
    // TODO: This test needs more interactive ways to test.
    
    private static final Logger logger_ =
        Logger.getLogger(JMultiSplitPaneTest.class);
        
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    public static void main(String[] args) 
    {
        TestRunner.run(JMultiSplitPaneTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests 
    //--------------------------------------------------------------------------
    
    /**
     * Tests autoscroll feature.
     * 
     * @throws Exception on error.
     */
    public void testDistributeEvenly() throws Exception
    {
        logger_.info("Running testDistributeEvenly...");
        
        final JMultiSplitPane jms = new JMultiSplitPane(3, SplitOrientation.VERTICAL);
        
        jms.setComponent(0, new JSmartLabel("Pane 0"));
        jms.setComponent(1, new JSmartLabel("Pane 1"));
        jms.setComponent(2, new JSmartLabel("Pane 2"));

//        new Thread(new Runnable()
//        {
//            public void run()
//            {
//                ThreadUtil.sleep(4000);
//                jms.distributeEvenly();
//            }
//        }).start();
        
        jms.distributeEvenly();
        
        //jms.setDividerLocation(0, 200);
        //jms.setDividerLocation(1, 200);
        
        launchInDialog(jms, UITestCase.SCREEN_TWO_THIRDS);
    }
}