package toolbox.workspace;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.TestOut;
import org.netbeans.jemmy.operators.JCheckBoxMenuItemOperator;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.JMenuBarOperator;
import org.netbeans.jemmy.operators.JPopupMenuOperator;

import toolbox.util.FileUtil;
import toolbox.workspace.action.ExitAction;

/**
 * Unit test for {@link toolbox.workspace.PluginWorkspace}.
 */
public class PluginWorkspaceTest extends TestCase
{
    private static final Logger logger_ =
        Logger.getLogger(PluginWorkspaceTest.class);

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    private JFrameOperator window_;
    private PluginWorkspace workspace_;

    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------

    public static void main(String[] args) throws Exception
    {
        TestRunner.run(PluginWorkspaceTest.class);
    }

    //--------------------------------------------------------------------------
    // Overrides TestCase
    //--------------------------------------------------------------------------

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        //JemmyProperties.setCurrentDispatchingModel(
        //    JemmyProperties.ROBOT_MODEL_MASK);

        JemmyProperties.setCurrentOutput(TestOut.getNullOutput());

        String prefsFile = FileUtil.createTempFilename() + "-toolbox.xml";
        workspace_ = new PluginWorkspace(prefsFile);
        window_ = new JFrameOperator(workspace_);
    }


    /**
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {
        new ExitAction(workspace_).exitForTesting();
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------

    /**
     * Invokes all plugins on the plugin menu. 
     */
    public void testPluginWorkspace()
    {
        logger_.info("Running testPluginWorkspace...");

        JMenuBarOperator mbo = new JMenuBarOperator(window_);

        // Push menu so menu items are accessible
        mbo.pushMenu("Plugins");

        JPopupMenuOperator pmo = new JPopupMenuOperator(window_);

        for (int i = 0; i < pmo.getComponentCount(); i++)
        {
            mbo.pushMenu("Plugins");
            new JCheckBoxMenuItemOperator(window_, i).push();
        }
    }
}