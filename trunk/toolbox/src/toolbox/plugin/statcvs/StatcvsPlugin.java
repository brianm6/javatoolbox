package toolbox.util.ui.plugin;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import org.apache.log4j.Logger;
import org.netbeans.lib.cvsclient.commandLine.CVSCommand;

import net.sf.statcvs.Main;
import net.sf.statcvs.output.CommandLineParser;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Element;
import nu.xom.Elements;

import toolbox.util.ArrayUtil;
import toolbox.util.FileUtil;
import toolbox.util.StringUtil;
import toolbox.util.XOMUtil;
import toolbox.util.io.JTextAreaOutputStream;
import toolbox.util.io.StringOutputStream;
import toolbox.util.ui.ImageCache;
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
    // TODO: Implement recent pattern
    // TODO: Add support for projects that span multiple modules
    
    private static final Logger logger_ = 
        Logger.getLogger(StatcvsPlugin.class);
    
    /**
     * XML: Node that holds a collection of CVSProjects
     */
    private static final String NODE_CVSPROJECTS = "CVSProjects";
    
    /**
     * XML: Node that contains the StatCVS plugin's preferences
     */
    private static final String NODE_STATCVS_PLUGIN = "StatCVSPlugin";
    
    /** 
     * Reference to the workspace statusbar 
     */
    private IStatusBar statusBar_;
    
    /** 
     * Output text area for application activity 
     */
    private JSmartTextArea outputArea_;
    
    /** 
     * Field for the project name (optional) 
     */
    private JComboBox projectCombo_;
    
    /** 
     * Field for the cvs module name (required) 
     */
    private JTextField cvsModuleField_;
    
    /** 
     * Field for the cvs root (required) 
     */
    private JTextField cvsRootField_;
    
    /** 
     * Field for the cvs password (required by empty strings are OK) 
     */
    private JTextField cvsPasswordField_;
    
    /** 
     * Field for the checkout directory (must already exist) 
     */
    private JTextField checkoutDirField_;
    
    /** 
     * Checkbox to toggle the cvslib.jar debug flag 
     */
    private JCheckBox debugCheckBox_;
    
    /** 
     * Field that contains the URL to view the generated statcvs report 
     */
    private JTextField launchURLField_;
    
    /** 
     * Saved user.dir before being overwritten (cvs commands require this) 
     */
    private String originalUserDir_;
    
    /** 
     * Saved System.out before being overwritten (hijacked to capture the
     * output of cvs log command).
     */
    private PrintStream originalSystemOut_;

    /**
     * Saved System.err
     */
    private PrintStream originalSystemErr_;
    
    /**
     * System.out redirected to the output text area
     */
    private PrintStream redirectedSystemOut_;
    
    /**
     * System.err redirected to the output text area
     */
    private PrintStream redirectedSystemErr_;

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Adds a project to the existing list displayed in the combobox
     * 
     * @param  project  CVSProject
     */
    public void addProject(CVSProject project)
    {
        DefaultComboBoxModel model = 
            (DefaultComboBoxModel) projectCombo_.getModel();
        
        if (model.getIndexOf(project) > 0)
            return;
        else
            projectCombo_.addItem(project);
    }

    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------
    
    /**
     * Builds the GUI
     */        
    protected void buildView()
    {
        setLayout(new BorderLayout());
        add(BorderLayout.CENTER, buildOutputPanel());
        add(BorderLayout.NORTH, buildControlPanel());
    }
    
    /**
     * Builds the control panel
     * 
     * @return JComponent
     */
    protected JComponent buildControlPanel()
    {
        JPanel p = new JPanel(new ParagraphLayout(5,5,5,5,5,5));
 
        p.add(new JLabel("Project"), ParagraphLayout.NEW_PARAGRAPH);
        p.add(projectCombo_ = new JComboBox());
        projectCombo_.setEditable(true);
        projectCombo_.setAction(new ProjectChangedAction());
        
        JToolBar tb = new JToolBar();
        tb.setFloatable(false);
        tb.setBorderPainted(false);
        tb.add(new SaveAction());
        tb.add(new DeleteAction());
        p.add(tb);
        
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
        
        b.add(new JButton(new EverythingAction()));
        b.add(new JButton(new LoginAction()));
        b.add(new JButton(new CheckoutAction()));
        b.add(new JButton(new LogAction()));
        b.add(new JButton(new GenerateStatsAction()));
        b.add(new JButton(new LaunchAction()));
        b.add(new JButton(outputArea_.new ClearAction()));
        
        JPanel base = new JPanel(new BorderLayout());
        base.add(BorderLayout.WEST, p);
        base.add(BorderLayout.CENTER, b);
        
        return base;
    }

    /**
     * Builds the output panel
     * 
     * @return JComponent
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
            FileUtil.matchPlatformSeparator(module),
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
    
    public JComponent getComponent()
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
        
        // Save original user directory since we're gonna muck w/ it
        originalUserDir_ = System.getProperty("user.dir");

        // Save original System.out 
        originalSystemOut_ = System.out;
        originalSystemErr_ = System.err;

        redirectedSystemOut_ = 
            new PrintStream(new JTextAreaOutputStream(outputArea_));
            
        redirectedSystemErr_ = 
            new PrintStream(new JTextAreaOutputStream(outputArea_));
    }

    public void shutdown()
    {
    }

    //--------------------------------------------------------------------------
    // IPreferenced Interface
    //--------------------------------------------------------------------------
 
    /**
     * @see toolbox.util.ui.plugin.IPreferenced#applyPrefs(nu.xom.Element)
     */
    public void applyPrefs(Element prefs) throws Exception
    {
        boolean useDefaults = false;

        if (prefs == null)
            useDefaults = true;
        else if (prefs.getFirstChildElement(NODE_STATCVS_PLUGIN) == null)
            useDefaults = true;
        else if (prefs.getFirstChildElement(
            NODE_STATCVS_PLUGIN).getFirstChildElement(NODE_CVSPROJECTS) == null)
            useDefaults = true;
        else if (prefs.getFirstChildElement(
            NODE_STATCVS_PLUGIN).getFirstChildElement(
                NODE_CVSPROJECTS).getChildCount() == 0)
            useDefaults = true;

        if (useDefaults)
        {
            projectCombo_.addItem(new CVSProject(
                "Sourceforge",
                "<module>",
                ":pserver:anonymous@cvs.sourceforge.net:/cvsroot/",
                "",
                FileUtil.getTempDir().getCanonicalPath(), 
                false,
                ""));

            projectCombo_.addItem(new CVSProject(
                "Apache",
                "<module>",
                ":pserver:anoncvs@cvs.apache.org:/home/cvspublic",
                "",
                FileUtil.getTempDir().getCanonicalPath(), 
                false,
                ""));
                
            projectCombo_.addItem(new CVSProject(
                "Statcvs",
                "statcvs",
                ":pserver:anonymous@cvs.sourceforge.net:/cvsroot/statcvs",
                "",
                FileUtil.getTempDir().getCanonicalPath(), 
                false,
                ""));
        }
        else
        {
            Element root = prefs.getFirstChildElement(NODE_STATCVS_PLUGIN);
            
            Elements projectList = 
                root.getFirstChildElement(NODE_CVSPROJECTS).
                    getChildElements(CVSProject.NODE_CVSPROJECT);
            
            for(int i=0; i<projectList.size(); i++)
            {
                Element projectNode = projectList.get(i);
                CVSProject project = new CVSProject(projectNode.toXML());
                addProject(project);
            }
            
            outputArea_.applyPrefs(root);            
        }
    }

    /**
     * @see toolbox.util.ui.plugin.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs)
    {
        Element root = new Element(NODE_STATCVS_PLUGIN);
        Element projects = new Element(NODE_CVSPROJECTS);
        
        for (int i=0, n=projectCombo_.getItemCount(); i<n; i++)
        {
            CVSProject project = (CVSProject) projectCombo_.getItemAt(i);
            projects.appendChild(project.toDOM());
        }
        
        root.appendChild(projects);
        outputArea_.savePrefs(root);
        
        XOMUtil.insertOrReplace(prefs, root);
    }
 
 
    //--------------------------------------------------------------------------
    // Inner Classes
    //--------------------------------------------------------------------------
    
    class CVSProject 
    {
        private static final String NODE_CVSPROJECT  = "CVSProject";
        private static final String ATTR_PROJECT     = "project";
        private static final String ATTR_MODULE      = "module";
        private static final String ATTR_CVSROOT     = "cvsroot";
        private static final String ATTR_PASSWORD    = "password";
        private static final String ATTR_CHECKOUTDIR = "checkoutdir";
        private static final String ATTR_DEBUG       = "debug";
        private static final String ATTR_LAUNCHURL   = "launchurl";
        
        /** Field for the project name (optional) */
        private String project_;
    
        /** Field for the cvs module name (required) */
        private String cvsModule_;
    
        /** Field for the cvs root (required) */
        private String cvsRoot_;
    
        /** Field for the cvs password (required by empty strings are OK) */
        private String cvsPassword_;
    
        /** Field for the checkout directory (must already exist) */
        private String checkoutDir_;
    
        /** Checkbox to toggle the cvslib.jar debug flag */
        private boolean debug_;
    
        /** Field that contains the URL to view the generated statcvs report */
        private String launchURL_;
        
        /**
         * Creates a CVSProject from XML
         *
         * @param  xml  String containing a valid XML persistence of CVSProject
         * @throws IOException on I/O error 
         */
        public CVSProject(String xml) throws Exception
        {
            Element project = 
                new Builder().build(new StringReader(xml)).getRootElement();
            
            setProject(
                XOMUtil.getStringAttribute(project, ATTR_PROJECT, "???"));
                
            setCVSModule(XOMUtil.getStringAttribute(project, ATTR_MODULE, ""));
            setCVSRoot(XOMUtil.getStringAttribute(project, ATTR_CVSROOT, ""));
            
            setCVSPassword(
                XOMUtil.getStringAttribute(project, ATTR_PASSWORD, ""));
                
            setCheckoutDir(
                XOMUtil.getStringAttribute(project, ATTR_CHECKOUTDIR, ""));
                
            setDebug(XOMUtil.getBooleanAttribute(project, ATTR_DEBUG, false));
            
            setLaunchURL(
                XOMUtil.getStringAttribute(project, ATTR_LAUNCHURL, ""));
        }
        
        /**
         * Arg constructor 
         * 
         * @param project      Project name
         * @param module       CVS module name
         * @param cvsRoot      CVS root url
         * @param password     Cleartext password
         * @param checkOutDir  Directory to checkout files to
         * @param debug        Print debug output
         * @param launchURL    Launch URL for viewing generated statistics
         */
        public CVSProject(String project, String module, String cvsRoot, 
            String password, String checkOutDir, boolean debug, 
            String launchURL)
        {
            setProject(project);
            setCVSModule(module);
            setCVSRoot(cvsRoot);
            setCVSPassword(password);
            setCheckoutDir(checkOutDir);
            setDebug(debug);
            setLaunchURL(launchURL);
        }

        /**
         * Returns an XML representation of the data contained in this project.
         * 
         * @return  XML string
         */
        public String toXML()
        {
            return toDOM().toString();
        }

        /**
         * Returns a DOM representation of the data contained in this project.
         * 
         * @return  DOM tree
         */    
        public Element toDOM()
        {
            Element project = new Element(NODE_CVSPROJECT);
            project.addAttribute(new Attribute(ATTR_PROJECT, getProject()));
            project.addAttribute(new Attribute(ATTR_MODULE, getCVSModule()));
            project.addAttribute(new Attribute(ATTR_CVSROOT, getCVSRoot()));
            
            project.addAttribute(
                new Attribute(ATTR_PASSWORD, getCVSPassword()));
                
            project.addAttribute(
                new Attribute(ATTR_CHECKOUTDIR, getCheckoutDir()));
                
            project.addAttribute(
                new Attribute(ATTR_DEBUG, isDebug() ? "true" : "false"));
                
            project.addAttribute(new Attribute(ATTR_LAUNCHURL, getLaunchURL()));
            return project;
        }

        /**
         * Returns directory that files will be checked out to.
         * 
         * @return Checkout directory 
         */
        public String getCheckoutDir()
        {
            return checkoutDir_;
        }

        /**
         * Returns the CVS module name that will be analyzed.
         * 
         * @return CVS module name
         */
        public String getCVSModule()
        {
            return cvsModule_;
        }

        /**
         * Returns the CVS password used for authentication.
         * 
         * @return CVS password
         */
        public String getCVSPassword()
        {
            return cvsPassword_;
        }

        /**
         * Returns the CVSROOT for the cvs module.
         * 
         * @return CVSROOT
         */
        public String getCVSRoot()
        {
            return cvsRoot_;
        }

        /**
         * Returns the debug flag for the CVS library.
         * 
         * @return Debug flag
         */
        public boolean isDebug()
        {
            return debug_;
        }

        /**
         * Returns the URL that points to the generated statistics in HTML.
         * 
         * @return URL to generated statistics
         */
        public String getLaunchURL()
        {
            return launchURL_;
        }

        /**
         * Returns the project name used to identify the set of configuration
         * values.
         * 
         * @return Project name
         */
        public String getProject()
        {
            return project_;
        }

        /**
         * @param string  Path to existing directory that cvs files will be
         *                checked out to.
         */
        public void setCheckoutDir(String string)
        {
            checkoutDir_ = string;
        }

        /**
         * @param string  CVS module name
         */
        public void setCVSModule(String string)
        {
            cvsModule_ = string;
        }

        /**
         * @param string  CVS authentication password
         */
        public void setCVSPassword(String string)
        {
            cvsPassword_ = string;
        }

        /**
         * @param string  CVSROOT 
         */
        public void setCVSRoot(String string)
        {
            cvsRoot_ = string;
        }

        /**
         * @param b  Debug flag
         */
        public void setDebug(boolean b)
        {
            debug_ = b;
        }

        /**
         * @param string  URL to the generated statistics
         */
        public void setLaunchURL(String string)
        {
            launchURL_ = string;
        }

        /**
         * @param string  Project name
         */
        public void setProject(String string)
        {
            project_ = string;
        }

        /**
         * Returns the project name so it is displayed by the renderer for
         * the comboxbox.
         * 
         * @see java.lang.Object#toString()
         */
        public String toString()
        {
            return getProject();
        }
    }
    
    //--------------------------------------------------------------------------
    // Actions
    //--------------------------------------------------------------------------

    /** 
     * Abstract base class for all actions that takes care of 
     * settin/restoring the stdout and stderr streams before and after
     * the action completes execution.
     */ 
    abstract class StatcvsAction extends WorkspaceAction
    {
        StatcvsAction(
            String name, 
            boolean async, 
            JComponent scope, 
            IStatusBar statusBar)
        {
            super(name, async, scope, statusBar);

            // Add an action to run before the main action to set the system
            // out and err stream to the text area.            
            addPreAction(new AbstractAction() 
            {
                public void actionPerformed(ActionEvent e)
                {
                    System.setOut(redirectedSystemOut_);
                    System.setErr(redirectedSystemErr_);
                }
            });

            // This will restore the system out and err to their original 
            // values when the main action has completed execution.
            addFinallyAction(new AbstractAction() 
            {
                public void actionPerformed(ActionEvent e)
                {
                    System.setOut(originalSystemOut_);
                    System.setErr(originalSystemErr_);
                }
            });
        }        
    }


    /**
     * Executes all steps necessary to produce the StatCVS report
     */
    class EverythingAction extends StatcvsAction
    {
        public EverythingAction()
        {
            super("I'm feeling lucky!", true, null, null);
        }
        
        public void runAction(ActionEvent e) throws Exception
        {
            new LoginAction().runAction(e);
            new CheckoutAction().runAction(e);
            new LogAction().runAction(e);
            new GenerateStatsAction().runAction(e);
            new LaunchAction().runAction(e);
        }
    }
    
    /**
     * Logs into the cvs server
     */
    class LoginAction extends StatcvsAction
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
    class CheckoutAction extends StatcvsAction
    {
        public CheckoutAction()
        {
            super("Checkout", true, null, statusBar_);
        }
        
        public void runAction(ActionEvent e) throws Exception
        {
            verify();
            setDebug();
            
            statusBar_.setStatus("Checking out " + cvsModuleField_.getText() + 
                " module to " + checkoutDirField_.getText() + "...");

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
    class LogAction extends StatcvsAction
    {
        public LogAction()
        {
            super("Generate CVS Log", true, null, statusBar_);
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
            PrintStream before = System.out;
            
            try
            {
                System.setOut(new PrintStream(sos));
                CVSCommand.main(args);
                String fixedLogFile = fixLogFile(sos.toString());
                FileUtil.setFileContents(getCVSLogFile(), fixedLogFile, false);
                
                statusBar_.setStatus(
                    "Generated CVS log containing " + 
                        fixedLogFile.length() + " bytes");
            }
            finally
            {
                System.setOut(before);
                restoreUserDir();
            }
        }
    }

    /**
     * Runs statcvs against the generatted cvs log file to create a HTML report
     */    
    class GenerateStatsAction extends StatcvsAction
    {
        public GenerateStatsAction()
        {
            super("Generate Stats", true, null, statusBar_);
        }
        
        public void runAction(ActionEvent e) throws Exception
        {
            statusBar_.setStatus("Generating stats...");
            
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
                //"-title " + cvsModuleField_.getText(),
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
            
            statusBar_.setStatus("Generating stats done.");
        }
    }
    
    /**
     * Launches web browser to view the generated Statcvs reports
     */
    class LaunchAction extends StatcvsAction
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
    
    /** 
     * Updates the cvs project fields when the project selection changes.
     */
    class ProjectChangedAction extends AbstractAction
    {
        public void actionPerformed(ActionEvent e)
        { 
            Object obj = projectCombo_.getSelectedItem();
            
            if (obj instanceof CVSProject)
            {
                CVSProject project=(CVSProject) projectCombo_.getSelectedItem();
                
                cvsModuleField_.setText(project.getCVSModule());
                cvsRootField_.setText(project.getCVSRoot());
                cvsPasswordField_.setText(project.getCVSPassword());
                checkoutDirField_.setText(project.getCheckoutDir());
                debugCheckBox_.setSelected(project.isDebug());
                launchURLField_.setText(project.getLaunchURL());
            }
        }
    }

    /**
     * Saves the current cvs project. If the project does not already exist,
     * it is created.
     */
    class SaveAction extends AbstractAction
    {
        SaveAction()
        {
            super("", ImageCache.getIcon("/toolbox/util/ui/images/Save.gif"));
            putValue(SHORT_DESCRIPTION, "Saves the project");
            
        }
        
        public void actionPerformed(ActionEvent e)
        {
            String current = projectCombo_.getEditor().getItem().toString();
            
            if (StringUtil.isNullOrBlank(current))
            {
                statusBar_.setStatus("Project name cannot be empty");
            }
            else
            {
                boolean found = false;
                
                for (int i=0; i< projectCombo_.getItemCount(); i++)
                {
                    CVSProject project = (CVSProject) projectCombo_.getItemAt(i);
                    
                    if (project.getProject().equals(current))
                    {
                        project.setCVSModule(cvsModuleField_.getText());
                        project.setCVSRoot(cvsRootField_.getText());
                        project.setCVSPassword(cvsPasswordField_.getText());
                        project.setCheckoutDir(checkoutDirField_.getText());
                        project.setDebug(debugCheckBox_.isSelected());
                        project.setLaunchURL(launchURLField_.getText());
                        found |= true;
                        break;
                    }
                }
                
                if (!found)
                {
                    CVSProject project = new CVSProject(
                        current,
                        cvsModuleField_.getText(),
                        cvsRootField_.getText(),
                        cvsPasswordField_.getText(),
                        checkoutDirField_.getText(),
                        debugCheckBox_.isSelected(),
                        launchURLField_.getText());
                        
                    projectCombo_.addItem(project);
                    projectCombo_.setSelectedItem(project);    
                }
                
                statusBar_.setStatus("Project " + current + " saved.");
            }               
        }
    }

    /**
     * Deletes the selected cvs project
     */
    class DeleteAction extends AbstractAction
    {
        public DeleteAction()  
        {
            super("", ImageCache.getIcon("/toolbox/util/ui/images/Delete.gif"));
            putValue(SHORT_DESCRIPTION, "Deletes the project");
        }
    
        public void actionPerformed(ActionEvent e)
        {
            String current = projectCombo_.getEditor().getItem().toString();
            
            boolean found = false;
            
            for (int i=0; i< projectCombo_.getItemCount(); i++)
            {
                CVSProject project = (CVSProject) projectCombo_.getItemAt(i);
                
                if (project.getProject().equals(current))
                {
                    logger_.debug("Removing " + i);
                    projectCombo_.removeItemAt(i);                    
                    
                    if (projectCombo_.getItemCount() > 0)
                        projectCombo_.setSelectedIndex(0);
                    
                    statusBar_.setStatus("Project " + current + " deleted.");    
                    found |= true;
                    break;
                }
            }
            
            if (!found)
            {
                if (StringUtil.isNullOrBlank(current))
                    statusBar_.setStatus("Select a project to delete.");
                else
                    statusBar_.setStatus(
                        "Project " + current + " does not exist.");    
            }   
        }
    }    
}