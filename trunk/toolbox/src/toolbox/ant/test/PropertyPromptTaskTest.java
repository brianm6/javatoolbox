package toolbox.ant.test;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.tools.ant.Main;

/**
 * 
 */
public class PropertyPromptTaskTest extends TestCase
{
    /**
     * 
     * @param args
     */
    public static void main(String[] args)
    {
        TestRunner.run(PropertyPromptTaskTest.class);
    }

    /**
     * Constructor for PropertyPromptTaskTest.
     * @param arg0
     */
    public PropertyPromptTaskTest(String arg0)
    {
        super(arg0);
    }
    
    public void testPropertyPrompt() throws Exception
    {
        Main.main(new String[]{"-debug", "-verbose", "-f", "c:\\workspaces\\workspace-toolbox\\toolbox\\src\\toolbox\\ant\\test\\PropertyPromptTaskTest.xml"});
        
    }
}