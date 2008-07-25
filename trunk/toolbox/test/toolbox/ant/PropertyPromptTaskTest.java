package toolbox.ant;

import java.io.File;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;
import org.apache.tools.ant.Main;

import toolbox.junit.testcase.StandaloneTestCase;
import toolbox.util.FileUtil;
import toolbox.util.ResourceUtil;

/**
 * Unit test for {@link toolbox.ant.PropertyPromptTask}. This test is marked as
 * standalone because the call into Ant results in call to System.exit() on
 * completion.
 */
public class PropertyPromptTaskTest extends TestCase implements StandaloneTestCase {

    private static final Logger log = Logger.getLogger(PropertyPromptTaskTest.class);

    // --------------------------------------------------------------------------
    // Main
    // --------------------------------------------------------------------------

    public static void main(String[] args) {
        TestRunner.run(PropertyPromptTaskTest.class);
    }

    // --------------------------------------------------------------------------
    // Unit Tests
    // --------------------------------------------------------------------------

    public void testPropertyPrompt() throws Exception {
        File f = ResourceUtil.getResourceAsTempFile("test/toolbox/ant/PropertyPromptTaskTest.xml");
        log.debug("Ant file = " + f.getAbsolutePath());
        Main.main(new String[] { "-debug", "-verbose", "-f", f.getCanonicalPath() });
        FileUtil.deleteQuietly(f);
    }
}