package toolbox.ant.test;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.tools.ant.Main;

/**
 * Unit test for PropertyPromptTask.
 */
public class PropertyPromptTaskTest extends TestCase
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
     * @throws Exception
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