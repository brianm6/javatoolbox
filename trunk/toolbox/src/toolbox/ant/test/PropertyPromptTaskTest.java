package toolbox.ant.test;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.tools.ant.Main;

import toolbox.junit.StandaloneTestCase;

/**
 * Unit test for PropertyPromptTask. This test is marked as standalone 
 * because the call into Ant results in call to System.exit() on completion.
 */
public class PropertyPromptTaskTest extends TestCase 
                                    implements StandaloneTestCase 
{
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    /**
     * Entry point.
     * 
     * @param args None recognized
     */
    public static void main(String[] args)
    {
        TestRunner.run(PropertyPromptTaskTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests property prompt task.
     * 
     * @throws Exception on error.
     */
    public void testPropertyPrompt() throws Exception
    {
        Main.main(new String[]{
            "-debug", 
            "-verbose", 
            "-f", 
            "c:\\workspaces\\workspace-toolbox\\toolbox\\src\\toolbox\\ant\\test\\PropertyPromptTaskTest.xml"
        });
    }
}