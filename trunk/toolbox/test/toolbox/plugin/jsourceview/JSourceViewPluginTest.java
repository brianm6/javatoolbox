package toolbox.plugin.jsourceview;

import java.io.IOException;

import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.TestOut;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JCheckBoxMenuItemOperator;
import org.netbeans.jemmy.operators.JDialogOperator;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.JLabelOperator;
import org.netbeans.jemmy.operators.JMenuBarOperator;
import org.netbeans.jemmy.operators.JPopupMenuOperator;
import org.netbeans.jemmy.operators.JTextFieldOperator;
import org.netbeans.jemmy.util.NameComponentChooser;

import toolbox.junit.testcase.UITestCase;
import toolbox.util.FileUtil;
import toolbox.util.JemmyUtil;
import toolbox.workspace.PluginMenu;
import toolbox.workspace.PluginWorkspace;

/**
 * Unit test for JSourceViewPlugin.
 * 
 * @see toolbox.plugin.jsourceview.JSourceViewPlugin
 */
public class JSourceViewPluginTest extends UITestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(JSourceViewPluginTest.class);
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    private JFrameOperator window_;
    private PluginWorkspace workspace_;
    private String prefsFile_;

    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
            
    /**
     * Entry point.
     * 
     * @param args None recognized.
     * @throws Exception on LAF error.
     */
    public static void main(String[] args) throws Exception
    {
        TestRunner.run(JSourceViewPluginTest.class);
        //System.exit(0); 
    }

    //--------------------------------------------------------------------------
    // Overrides TestCase
    //--------------------------------------------------------------------------
    
    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        JemmyProperties.setCurrentDispatchingModel(
            JemmyProperties.ROBOT_MODEL_MASK);

        JemmyProperties.setCurrentOutput(TestOut.getNullOutput());
        
        prefsFile_ = FileUtil.createTempFilename() + "-toolbox.xml";
        workspace_ = new PluginWorkspace(prefsFile_);
        window_ = new JFrameOperator(workspace_);
    }
   
    
    /**
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {
        window_.close();
        FileUtil.delete(prefsFile_);
    }
    
    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    public void testJSourceViewPlugin() throws IOException
    {
        logger_.info("Running testJSourceViewPlugin...");
        
        JMenuBarOperator mbo = new JMenuBarOperator(window_);
        mbo.pushMenu(PluginMenu.LABEL);
        JPopupMenuOperator pmo = new JPopupMenuOperator(window_);
        new JCheckBoxMenuItemOperator(window_, JSourceViewPlugin.NAME).push();
        
        // Start search
        JemmyUtil.findTextField(window_, JSourceView.NAME_DIRECTORY_FIELD)
            .enterText(FileUtil.getTempDir().getAbsolutePath());
                
        //"J:\\tmp\\wsad\\workspace\\toolbox\\src\\toolbox\\findclass");

        // Wait for search to complete
        JLabelOperator.waitJLabel(workspace_, "Elapsed", false, false);
        
        // Show chart in dialog box
        new JButtonOperator(window_, 
            new NameComponentChooser(
                JSourceView.NAME_CHART_BUTTON)).pushNoBlock();
        
        // Dismiss dialog box
        JDialogOperator.waitJDialog("Message", true, true);
        new JButtonOperator(new JDialogOperator("Message"), "OK").push();
        
        // Save to file 
        new JButtonOperator(window_, 
            new NameComponentChooser(
                JSourceView.NAME_SAVE_BUTTON)).pushNoBlock();
        
        // Set file name and save
        JDialogOperator saveOp = new JDialogOperator("Save to file");
        String saveFile = FileUtil.createTempFilename() + ".txt";
        new JTextFieldOperator(saveOp).enterText(saveFile);
        saveOp.waitClosed();
        
        // Verify
        int len = FileUtil.getFileContents(saveFile).length();
        logger_.debug("Saved file is " + len + " bytes");
        assertTrue(len > 0);
        FileUtil.delete(saveFile);
    }
}