package toolbox.plugin.jsourceview;

import java.io.IOException;

import junit.framework.TestCase;
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
import org.netbeans.jemmy.operators.JTextFieldOperator;
import org.netbeans.jemmy.util.NameComponentChooser;

import toolbox.util.FileUtil;
import toolbox.util.JemmyUtil;
import toolbox.util.ThreadUtil;
import toolbox.workspace.PluginMenu;
import toolbox.workspace.PluginWorkspace;
import toolbox.workspace.action.ExitAction;

/**
 * Unit test for {@link toolbox.plugin.jsourceview.JSourceViewPlugin}.
 */
public class JSourceViewPluginTest extends TestCase
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
    }

    //--------------------------------------------------------------------------
    // Overrides TestCase
    //--------------------------------------------------------------------------
    
    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        // TODO: SunAWTRobot does not let the JVM exit properly on linux so
        //       we need to make sure it isn't used on linux especially.
        //       Move behavior up to superclass on wich model to use based on
        //       operating system.
        
        //JemmyProperties.setCurrentDispatchingModel(
        //    JemmyProperties.ROBOT_MODEL_MASK);

        JemmyProperties.setCurrentDispatchingModel(
            JemmyProperties.QUEUE_MODEL_MASK);

        //JemmyProperties.setCurrentDispatchingModel(
        //    JemmyProperties.SHORTCUT_MODEL_MASK);

        JemmyProperties.setCurrentOutput(TestOut.getNullOutput());
        
        prefsFile_ = FileUtil.createTempFilename() + "-toolbox.xml";
        workspace_ = new PluginWorkspace(prefsFile_);
        window_ = new JFrameOperator(workspace_);
    }
    
    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Exercises the source view plugin to generate stats for source code in
     * a given directory and then finally displays a pie chart showing the 
     * source code categories.
     */
    public void testJSourceViewPlugin() throws IOException
    {
        logger_.info("Running testJSourceViewPlugin...");
     
        String saveFile = FileUtil.createTempFilename() + ".txt";
        String javaFile = FileUtil.createTempFilename() + ".java"; 
        
        try
        {
            JMenuBarOperator mbo = new JMenuBarOperator(window_);
            mbo.pushMenu(PluginMenu.LABEL);
            
            new JCheckBoxMenuItemOperator(
                window_, 
                JSourceViewPlugin.NAME).push();
            
            // Write a java file out to the system temp dir so we atleast have
            // one file that is picked up
            
            String javaSource = 
                  "public class JavaFile { \n" 
                + "  \n"
                + "  public static void main(String args[]) { \n"
                + "    // This is a comment \n"
                + "    System.out.println(\"Hello world!\"); \n"
                + "  } \n"
                + "} \n";
            
            FileUtil.setFileContents(javaFile, javaSource, false);
                
            // Start search in the system temp directory
            JemmyUtil.findTextField(window_, 
                JSourceView.NAME_DIRECTORY_FIELD).enterText(
                    FileUtil.getTempDir().getAbsolutePath());
                    
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
            
            new JTextFieldOperator(saveOp).enterText(saveFile);
            saveOp.waitClosed();
            
            // FIXME:
            // If this delay is not here, the output file is not ready to be
            // read for some reason on linux only...
            logger_.debug("Sleeping (linux workaround)...");
            ThreadUtil.sleep(5000);
            
            // Verify
            int len = FileUtil.getFileContents(saveFile).length();
            logger_.debug("Saved file is " + len + " bytes");
            assertTrue("Save file should not be empty", len > 0);
            
        }
        finally 
        {
            new ExitAction(workspace_).exitForTesting();
            FileUtil.deleteQuietly(saveFile);
            FileUtil.deleteQuietly(javaFile);
            FileUtil.deleteQuietly(prefsFile_);
        }
    }
}