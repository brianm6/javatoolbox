package toolbox.util.ui.plugin;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Map;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.apache.log4j.Logger;
import org.netbeans.lib.cvsclient.commandLine.CVSCommand;

import net.sf.statcvs.Main;
import net.sf.statcvs.output.CommandLineParser;

import toolbox.util.ArrayUtil;
import toolbox.util.FileUtil;
import toolbox.util.PropertiesUtil;
import toolbox.util.StringUtil;
import toolbox.util.io.JTextAreaOutputStream;
import toolbox.util.io.StringOutputStream;
import toolbox.util.ui.JSmartTextArea;
import toolbox.util.ui.NativeBrowser;
import toolbox.util.ui.layout.GridLayoutPlus;
import toolbox.util.ui.layout.ParagraphLayout;

/**
 * StatcvsPlugin is a GUI wrapper for the 
 * <a href=http://statcvs.sourceforge.net>StatCVS</a> command line application. 
 * The plugin also does all the upfront work that is not apparent in StatCVS to
 * generate a report. This includes checking out the module from cvs, 
 * generating a cvs log file and then running StatCVS to generate a HTML report. 
 * This is all 100% java and does not rely on a native cvs executable to run 
 * (thanks to the <a href=http://javacvs.netbeans.org/>javacvs</a> module from 
 * <a href=http://www.netbeans.org>Netbeans</a>).
 */
public class StatcvsPlugin extends JPanel implements IPlugin
{
    // TODO: Add nuke checkout dir
    // TODO: Add one button to do everything
    // TODO: Implement recent pattern
    
    private static final Logger logger_ = Logger.getLogger(StatcvsPlugin.class);
    
    /** Property key for the unique project name */
    private static final String PROP_PROJECT = "statcvs.plugin.project";
    
    /** Property key for the checkout directory */
    private static final String PROP_BASEDIR = "statcvs.plugin.co.directory";
    
    /** Property key for the debug flag for the cvslib.jar library */
    private static final String PROP_DEBUG = "statcvs.plugin.debug";
    
    /** Property key for the cvs module name */        
    private static final String PROP_CVSMODULE = "statcvs.plugin.cvsmodule";
    
    /** Property key for the cvs root */
    private static final String PROP_CVSROOT    = "statcvs.plugin.cvsroot";
    
    /** Property key for the cvs password */
    private static final String PROP_CVSPASSWORD= "statcvs.plugin.cvspassword";
    
    /** Reference to the workspace statusbar */
    private IStatusBar statusBar_;
    
    /** Output text area for application activity */
    private JSmartTextArea outputArea_;
    
    /** Field for the project name (optional) */
    private JTextField projectField_;
    
    /** Field for the cvs module name (required) */
    private JTextField cvsModuleField_;
    
    /** Field for the cvs root (required) */
    private JTextField cvsRootField_;
    
    /** Field for the cvs password (required by empty strings are OK) */
    private JTextField cvsPasswordField_;
    
    /** Field for the checkout directory (must already exist) */
    private JTextField checkoutDirField_;
    
    /** Checkbox to toggle the cvslib.jar debug flag */
    private JCheckBox  debugCheckBox_;
    
    /** Field that contains the URL to view the generated statcvs report */
    private JTextField launchURLField_;
    
    /** Saved user.dir before being overwritten (cvs commands require this) */
    private String      originalUserDir_;
    
    /** 
     * Saved System.out before being overwritten (hijacked to capture the
     * output of cvs log command).
     */
    private PrintStream originalSystemOut_;
    
    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------
    
    /**
     * Builds the GUI
     */        
    protected void buildView()
    {
        // Save original user directory since we're gonna muck w/ it
        originalUserDir_ = System.getProperty("user.dir");
        
        
        setLayout(new BorderLayout());
        add(BorderLayout.CENTER, buildOutputPanel());
        add(BorderLayout.NORTH, buildControlPanel());
        
        System.setOut(new PrintStream(new JTextAreaOutputStream(outputArea_)));
        System.setErr(new PrintStream(new JTextAreaOutputStream(outputArea_)));
        
        // Save original System.out 
        originalSystemOut_ = System.out;
    }
    
    /**
     * Builds the control panel
     */
    protected JComponent buildControlPanel()
    {
        JPanel p = new JPanel(new ParagraphLayout(5,5,5,5,5,5));
 
        p.add(new JLabel("Project"), ParagraphLayout.NEW_PARAGRAPH);
        p.add(projectField_ = new JTextField(20));
        p.add(new JLabel("CVS Module"), ParagraphLayout.NEW_PARAGRAPH);
        p.add(cvsModuleField_ = new JTextField(20));
        p.add(new JLabel("CVS Root"), ParagraphLayout.NEW_PARAGRAPH);
        p.add(cvsRootField_ = new JTextField(50));
        p.add(new JLabel("CVS Password"), ParagraphLayout.NEW_PARAGRAPH);
        p.add(cvsPasswordField_ = new JTextField(20));
        p.add(new JLabel("Checkout Directory"), ParagraphLayout.NEW_PARAGRAPH);
        p.add(checkoutDirField_ = new JTextField(30));
        p.add(new JLabel("Debug output"), ParagraphLayout.NEW_PARAGRAPH);
        p.add(debugCheckBox_ = new JCheckBox());
        p.add(new JLabel("Launch URL"), ParagraphLayout.NEW_PARAGRAPH);        
        p.add(launchURLField_ = new JTextField(30));

        launchURLField_.setEditable(false);
                
        JPanel b = new JPanel(new GridLayoutPlus(4,1,5,5,5,5));
        
        b.add(new JButton(new LoginAction()));
        b.add(new JButton(new CheckoutAction()));
        b.add(new JButton(new LogAction()));
        b.add(new JButton(new StatcvsAction()));
        b.add(new JButton(new LaunchAction()));
        b.add(new JButton(outputArea_.createClearAction()));

        JPanel base = new JPanel(new BorderLayout());
        base.add(BorderLayout.WEST, p);
        base.add(BorderLayout.CENTER, b);
        
        return base;
    }

    /**
     * Builds the output panel
     */
    protected JComponent buildOutputPanel()
    {
        outputArea_ = new JSmartTextArea(true, false);
        return new JScrollPane(outputArea_);
    }

    /**
     * Sets debug flags for the external 3rd party cvslib.jar based on the 
     * debugCheckBox's selected state.
     */
    protected void setDebug()
    {    
        // Debug flag for netbeans cvs client
        if (debugCheckBox_.isSelected())
        {
            System.setProperty("cvsClientLog", "system");
            org.netbeans.lib.cvsclient.util.Logger.setLogging("system");
        }
        else
        {
            System.getProperties().remove("cvsClientLog");
            org.netbeans.lib.cvsclient.util.Logger.setLogging(null);
        }
    }

    /**
     * Sets the system property user.dir to the given value
     * 
     * @param dir Directory
     */    
    protected void setUserDir(String dir)
    {
        System.setProperty("user.dir", dir);
    }

    /**
     * Restores the value of system property user.dir
     */    
    protected void restoreUserDir()
    {
        System.setProperty("user.dir", originalUserDir_);
    }

    /**
     * Sets System.out to the given stream
     * 
     * @param os  Stream to set to System.out
     */
    protected void setSystemOut(OutputStream os)
    {
        System.setOut(new PrintStream(os));
    }
    
    /**
     * Restores System.out to the value before it was changed
     */
    protected void restoreSystemOut()
    {
        System.setOut(originalSystemOut_);
    }
    
    /**
     * Verifies that all the fields pass simple verification checks.
     * 
     * @throws Exception if one or more of the fields did not pass verification.
     */
    protected void verify() throws Exception
    {
        checkEmpty(cvsModuleField_, "CVS module");
        checkEmpty(cvsRootField_, "CVS root");
        checkEmpty(checkoutDirField_, "Check out directory");
        checkTrailer(checkoutDirField_);
    }
    
    /**
     * Checks to make sure that the given field contains some data.
     * 
     * @param  field  Field to check
     * @param  name   Name to use in throw exception is field is blank
     * @throws IllegalArgumentException if field is blank
     */
    protected void checkEmpty(JTextField field, String name)
    {
        String text = field.getText();
        
        if (StringUtil.isNullOrBlank(text))
            throw new IllegalArgumentException(
                "Field '" + name + "' must have a value.");        
    }
    
    /**
     * Checks the trailing character on directory fields to make sure then
     * have a terminating File.separator.
     * 
     * @param field  Field to check
     */
    protected void checkTrailer(JTextField field)
    {
        String text = field.getText();
        field.setText(FileUtil.trailWithSeparator(text));    
    }

    /**
     * Returns a log file name based on the cvs module. Takes into account that
     * the module name may be a hierarchy (a/b/c/b) and replaces file separators
     * with periods.
     * 
     * @param   module  Module to use for creating the log file name.
     * @return  Log file name 
     */
    protected String moduleToLogFile(String module)
    {
        return StringUtil.replace(
            FileUtil.matchPlatformSeparator(cvsModuleField_.getText()),
            File.separator,
            ".") + ".log";
    }
    
    /**
     * Returns the absolute path to the cvs generated log file
     * 
     * @return Path to log file
     */
    protected String getCVSLogFile()
    {
        return checkoutDirField_.getText() + 
               cvsModuleField_.getText() + 
               File.separator +
               moduleToLogFile(cvsModuleField_.getText());
    }

    /**
     * Returns the base CVS checkout directory
     * 
     * @return Base CVS checkout directory
     */
    protected String getCVSBaseDir()
    {
        return checkoutDirField_.getText() + 
               cvsModuleField_.getText() + 
               File.separator;
    }
    
    /**
     * Examines the cvs log and makes sure the first few lines don't contain
     * junk that will throw off the statcvs log file parser.
     * 
     * @param   contents  Log file contents
     * @return  Fixed Log file contents
     */
    protected String fixLogFile(String contents)
    {
        boolean firstBlank = false;
        boolean firstRCS   = false;
        boolean secondWhat = false;
        boolean secondRCS = false;
        boolean tossFirst = false;
        boolean addFirst  = false;
        
        
        String[] lines = new String[2];
        lines[0] = StringUtil.getLine(contents, 0);
        lines[1] = StringUtil.getLine(contents, 1);  
        
        logger_.debug("lines: " + ArrayUtil.toString(lines, true));    
            
        if (StringUtil.isNullOrBlank(lines[0]))
            firstBlank = true;
        else if (lines[0].startsWith("RCS"))
            firstRCS = true;
                        
        if (lines[1].startsWith("?"))
            secondWhat = true;
        else if (lines[1].startsWith("RCS"))
            secondRCS = true;

        if (firstBlank && secondWhat)
            tossFirst = true;
        else if (firstRCS)
            addFirst = true;
        
        if (tossFirst)
        {
            int i = contents.indexOf("\n");
            contents = contents.substring(i+1);    
            logger_.debug("Tossed first line from log file");
        }
        else if (addFirst)
        {
            contents = "\n" + contents;
            logger_.debug("Added blank like to log file");
        }
        else
            logger_.debug("No fixes made to log file");
                                
        return contents;    
    }
    
    //--------------------------------------------------------------------------
    // IPlugin Interface
    //--------------------------------------------------------------------------
    
    public String getName()
    {
        return "StatCVS";    
    }
    
    public Component getComponent()
    {
        return this;
    }

    public String getDescription()
    {
        return "Runs Statcvs on a CVS module";
    }

    public void startup(Map params)
    {
        if (params != null)
            statusBar_= (IStatusBar) params.get(PluginWorkspace.PROP_STATUSBAR);
            
        buildView();
    }

    public void shutdown()
    {
    }

    //--------------------------------------------------------------------------
    // IPreferenced Interface
    //--------------------------------------------------------------------------
    
    public void savePrefs(Properties prefs)
    {
        prefs.setProperty(PROP_BASEDIR,  checkoutDirField_.getText());
        prefs.setProperty(PROP_CVSROOT,  cvsRootField_.getText());
        prefs.setProperty(PROP_CVSMODULE,   cvsModuleField_.getText());
        prefs.setProperty(PROP_CVSPASSWORD, cvsPasswordField_.getText());
        prefs.setProperty(PROP_PROJECT,  projectField_.getText());
        
        PropertiesUtil.setBoolean(prefs, 
            PROP_DEBUG, debugCheckBox_.isSelected());
    }

    public void applyPrefs(Properties prefs)
    {
        try
        {
            checkoutDirField_.setText(prefs.getProperty(
                PROP_BASEDIR, FileUtil.getTempDir().getCanonicalPath()));
        }
        catch (IOException e)
        {
            logger_.error(e);
        }
        
        cvsRootField_.setText(prefs.getProperty(
            PROP_CVSROOT, ":pserver:user@host:/some/path"));
            
        cvsModuleField_.setText(prefs.getProperty(PROP_CVSMODULE, ""));
        cvsPasswordField_.setText(prefs.getProperty(PROP_CVSPASSWORD, ""));
        projectField_.setText(prefs.getProperty(PROP_PROJECT, ""));
        
        debugCheckBox_.setSelected(
            PropertiesUtil.getBoolean(prefs, PROP_DEBUG, false));
    }
    
    //--------------------------------------------------------------------------
    // Actions
    //--------------------------------------------------------------------------
    
    /**
     * Logs into the cvs server
     */
    class LoginAction extends WorkspaceAction
    {
        public LoginAction()
        {
            super("Login", true, null, statusBar_);
        }
        
        public void runAction(ActionEvent e) throws Exception
        {
            verify();
            setDebug();
            
            statusBar_.setStatus("Logging in...");

            // Override cvs password for netbeans cvs lib
            System.setProperty("cvs.password", cvsPasswordField_.getText());
            
            // Delete password file. Netbeans cvs lib has problems overwriting 
            // it if it already exists
            File cvspass = new File(
                FileUtil.trailWithSeparator(
                    System.getProperty("user.home")) + ".cvspass");
          
            if (cvspass.exists())  
                cvspass.delete();

            String[] args = 
                new String[] { "-d", cvsRootField_.getText(), "login"};            
            
            CVSCommand.main(args);
            
            statusBar_.setStatus("Login done");
        }
    }

    /**
     * Checks out the module from the cvs server to the local filesystem
     */    
    class CheckoutAction extends WorkspaceAction
    {
        public CheckoutAction()
        {
            super("Checkout", true, null, statusBar_);
        }
        
        public void runAction(ActionEvent e) throws Exception
        {
            verify();
            setDebug();
            
            statusBar_.setStatus(
                "Checkout out module " + cvsModuleField_.getText() + 
                    " to " + checkoutDirField_.getText() + "...");
            
            setDebug();

            setUserDir(checkoutDirField_.getText());

            String args[] = new String[] 
            { 
                "-d", cvsRootField_.getText(), 
                "checkout", cvsModuleField_.getText() 
            };

            try
            {            
                CVSCommand.main(args);
            }
            finally
            {
                restoreUserDir();                    
            }
            
            statusBar_.setStatus("Checkout done");
        }
    }

    /**
     * Generates a cvs log file which is later used as input to statcvs
     */    
    class LogAction extends WorkspaceAction
    {
        public LogAction()
        {
            super("Generate Log", true, null, statusBar_);
        }
        
        public void runAction(ActionEvent e) throws Exception
        {
            statusBar_.setStatus("Generating log...");
            verify();
            setDebug();
            
            setUserDir(
                FileUtil.trailWithSeparator(
                    checkoutDirField_.getText()) + cvsModuleField_.getText());

            String[] args = new String[] {"-d", cvsRootField_.getText(), "log"};
            
            
            logger_.debug("cvsLogFile = " + getCVSLogFile());
                    
            StringOutputStream sos = new StringOutputStream();
            
            try
            {
                setSystemOut(sos);
                CVSCommand.main(args);
            }
            finally
            {
                restoreSystemOut();
            }
            
            logger_.debug("Log file size = " + sos.getBuffer().length());
            
            String fixedLogFile = fixLogFile(sos.toString());
             
            FileUtil.setFileContents(getCVSLogFile(), fixedLogFile, false);
            
            statusBar_.setStatus("Generating log done.");            
        }
    }

    /**
     * Runs statcvs against the generatted cvs log file to create a HTML report
     */    
    class StatcvsAction extends WorkspaceAction
    {
        public StatcvsAction()
        {
            super("Generate stats", true, null, statusBar_);
        }
        
        public void runAction(ActionEvent e) throws Exception
        {
            statusBar_.setStatus("Running statcvs...");
            
            verify();
            setDebug();
                        
            String cvsBaseDir = 
                checkoutDirField_.getText() + 
                cvsModuleField_.getText() + 
                File.separator;

            String[] args = new String[] 
            {
                "-verbose",
                //"-debug",
                "-output-dir", cvsBaseDir + "statcvs",
                "-nocredits",
                cvsModuleField_.getText(),
                getCVSLogFile(),
                cvsBaseDir
            };

            new CommandLineParser(args).parse();
            Main.generateDefaultHTMLSuite();
            
            launchURLField_.setText(
                "file://" + 
                getCVSBaseDir() + 
                "statcvs" + 
                File.separator + 
                "index.html");
            
            statusBar_.setStatus("Running statcvs done.");
        }
    }
    
    /**
     * Launches web browser to view the generated Statcvs reports
     */
    class LaunchAction extends WorkspaceAction
    {
        public LaunchAction()
        {
            super("View stats report", false, null, null);
        }
        
        public void runAction(ActionEvent e) throws Exception
        {
            NativeBrowser.displayURL(launchURLField_.getText());
        }
    }
}