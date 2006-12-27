package toolbox.plugin.statcvs;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

import com.jgoodies.forms.builder.ButtonStackBuilder;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import org.netbeans.lib.cvsclient.commandLine.CVSCommand;

import toolbox.plugin.docviewer.DocumentViewer;
import toolbox.plugin.docviewer.WebWindowViewer;
import toolbox.util.ArrayUtil;
import toolbox.util.FileUtil;
import toolbox.util.StringUtil;
import toolbox.util.XOMUtil;
import toolbox.util.io.JTextAreaOutputStream;
import toolbox.util.io.StringOutputStream;
import toolbox.util.service.ServiceException;
import toolbox.util.service.ServiceTransition;
import toolbox.util.ui.ImageCache;
import toolbox.util.ui.JButtonGroup;
import toolbox.util.ui.JCollapsablePanel;
import toolbox.util.ui.JHeaderPanel;
import toolbox.util.ui.JSmartButton;
import toolbox.util.ui.JSmartCheckBox;
import toolbox.util.ui.JSmartComboBox;
import toolbox.util.ui.JSmartLabel;
import toolbox.util.ui.JSmartRadioButton;
import toolbox.util.ui.JSmartTextArea;
import toolbox.util.ui.JSmartTextField;
import toolbox.util.ui.SortedComboBoxModel;
import toolbox.util.ui.layout.ParagraphLayout;
import toolbox.util.ui.tabbedpane.JSmartTabbedPane;
import toolbox.workspace.AbstractPlugin;
import toolbox.workspace.IStatusBar;
import toolbox.workspace.PluginWorkspace;
import toolbox.workspace.PreferencedException;
import toolbox.workspace.WorkspaceAction;

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
public class StatcvsPlugin extends AbstractPlugin
{
    private static final Logger logger_ = Logger.getLogger(StatcvsPlugin.class);

    //--------------------------------------------------------------------------
    // IPreferenced Constants
    //--------------------------------------------------------------------------

    /**
     * Node that contains the StatCVS plugin's preferences.
     */
    private static final String NODE_STATCVS_PLUGIN = "StatCVSPlugin";

    /**
     * Node that holds a collection of CVSProjects.
     */
    private static final String NODE_CVSPROJECTS = "CVSProjects";

    /**
     * Attribute of NODE_CVSPROJECTS that saves the index of the currently
     * selected cvs project.
     */
    private static final String ATTR_SELECTED  = "selected";

    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------

    /**
     * Key of the client property that is attached to each of the report engine
     * radio buttons to identify which report engine it represents.
     */
    private static final String KEY_ENGINE =
        "toolbox.plugin.statcvs.StatcvsEngine";

    /**
     * Property that is the class name of the default statcvs report engine.
     */
    public static final String CLASS_STATCVS_ENGINE =
        "toolbox.plugin.statcvs.DefaultStatcvsEngine";

    /**
     * Property this is the class name of the statcvs-xml report engine.
     */
    public static final String CLASS_STATCVS_XML_ENGINE =
        "toolbox.plugin.statcvs.StatcvsXMLEngine";

    //--------------------------------------------------------------------------
    // UI Fields
    //--------------------------------------------------------------------------

    /**
     * View for this plugin.
     */
    private JComponent view_;
    
    /**
     * Collapsable panel that contains all the details of the cvs projects.
     */
    private JCollapsablePanel cvsProjectsView_;
    
    /**
     * Output text area for application activity.
     */
    private JSmartTextArea outputArea_;

    /**
     * Field for the project name (optional).
     */
    private JSmartComboBox projectCombo_;

    /**
     * Field for the cvs module name (required).
     */
    private JSmartTextField cvsModuleField_;

    /**
     * Field for the cvs root (required).
     */
    private JSmartTextField cvsRootField_;

    /**
     * Field for the cvs password (required but empty strings are OK).
     */
    private JPasswordField cvsPasswordField_;

    /**
     * Field for the checkout directory (must already exist).
     */
    private JSmartTextField checkoutDirField_;

    /**
     * Checkbox to toggle the cvslib.jar debug flag.
     */
    private JCheckBox debugCheckBox_;

    /**
     * Field that contains the URL to view the generated statcvs report.
     */
    private JSmartTextField launchURLField_;

    /**
     * Radio button that selects the report engine that comes with statcvs.
     */
    private JSmartRadioButton defaultEngine_;

    /**
     * Radio button that selects the report engine that comes with statcvs-xml.
     */
    private JSmartRadioButton xmlEngine_;

    /**
     * Mutual exclusion gruop for the report engine radio buttons.
     */
    private JButtonGroup engineGroup_;
    
    /**
     * Reference to the workspace statusbar.
     */
    private IStatusBar statusBar_;

    /**
     * HTML viewer for the generated statcvs report.
     */
    private DocumentViewer reportViewer_;
    
    //--------------------------------------------------------------------------
    // Non-UI Fields
    //--------------------------------------------------------------------------
    
    /**
     * CVS command's stdout redirected to the output text area.
     */
    private PrintStream cvsOut_;

    /**
     * CVS command's stderr redirected to the output text area.
     */
    private PrintStream cvsErr_;

    /**
     * The cvs lib likes to dump alot of info to stdout and stderr so the
     * streams are redirected temporarily to the output text area and then
     * restored with the original System.out. 
     */
    private PrintStream savedSystemOut_;
    
    /**
     * Ditto except for System.err.
     */
    private PrintStream savedSystemErr_;
    
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------

    /**
     * Adds a project to the existing list displayed in the combobox.
     *
     * @param project CVSProject to add.
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
    // UI Construction
    //--------------------------------------------------------------------------

    /**
     * Constructs the user interface.
     */
    protected void buildView()
    {
        view_ = new JPanel(new BorderLayout());
//        view_.add(BorderLayout.CENTER, buildOutputConsoleView());
        view_.add(BorderLayout.CENTER, buildOutputTabPanel());
        view_.add(BorderLayout.NORTH, cvsProjectsView_ = buildCVSProjectsView());
    }


    /**
     * Builds top collapsable panel that contains all the CVS project
     * information and the buttons the execute the statcvs process.
     *
     * @return JCollapsablePanel
     */
    protected JCollapsablePanel buildCVSProjectsView()
    {
        JPanel p = new JPanel(new ParagraphLayout(10, 10, 5, 5, 5, 5));

        p.add(new JSmartLabel("Project"), ParagraphLayout.NEW_PARAGRAPH);
        p.add(projectCombo_ = new JSmartComboBox(new SortedComboBoxModel()));
        projectCombo_.setEditable(true);
        projectCombo_.setAction(new ProjectChangedAction());

        JToolBar tb = new JToolBar();
        tb.setFloatable(false);
        tb.setBorderPainted(false);
        tb.add(new SaveAction());
        tb.add(new DeleteAction());
        p.add(tb);

        p.add(new JSmartLabel("CVS Module"), ParagraphLayout.NEW_PARAGRAPH);
        p.add(cvsModuleField_ = new JSmartTextField(20));
        cvsModuleField_.setName("CVS module");

        p.add(new JSmartLabel("CVS Root"), ParagraphLayout.NEW_PARAGRAPH);
        p.add(cvsRootField_ = new JSmartTextField(50));
        cvsRootField_.setName("CVS root");

        p.add(new JSmartLabel("CVS Password"), ParagraphLayout.NEW_PARAGRAPH);
        p.add(cvsPasswordField_ = new JPasswordField(20));

        p.add(new JSmartLabel("Checkout Directory"),
                ParagraphLayout.NEW_PARAGRAPH);
        p.add(checkoutDirField_ = new JSmartTextField(30));
        checkoutDirField_.setName("Check out directory");

        p.add(new JSmartLabel("Debug output"), ParagraphLayout.NEW_PARAGRAPH);
        p.add(debugCheckBox_ = new JSmartCheckBox());

        p.add(new JSmartLabel("Launch URL"), ParagraphLayout.NEW_PARAGRAPH);
        p.add(launchURLField_ = new JSmartTextField(30));

        p.add(new JSmartLabel("Report Style"), ParagraphLayout.NEW_PARAGRAPH);

        engineGroup_ = new JButtonGroup();
        defaultEngine_ = new JSmartRadioButton("StatCVS");
        defaultEngine_.putClientProperty(KEY_ENGINE, CLASS_STATCVS_ENGINE);
        xmlEngine_ = new JSmartRadioButton("StatCVS-XML");
        xmlEngine_.putClientProperty(KEY_ENGINE, CLASS_STATCVS_XML_ENGINE);
        engineGroup_.add(defaultEngine_);
        engineGroup_.add(xmlEngine_);
        p.add(defaultEngine_);
        p.add(xmlEngine_);
        defaultEngine_.setSelected(true);

        launchURLField_.setEditable(false);

        //
        // Button stack
        //
        
        JButton[] buttons = new JButton[] {
            new JSmartButton(new EverythingAction()),
            new JSmartButton(new LoginAction()),
            new JSmartButton(new CheckoutAction()),
            new JSmartButton(new LogAction()),
            new JSmartButton(new GenerateStatsAction()),
            new JSmartButton(new LaunchAction())
        };

        ButtonStackBuilder builder = new ButtonStackBuilder();
        builder.addGlue();
        builder.addButtons(buttons);
        builder.addGlue();

        JPanel base = new JPanel(new BorderLayout());
        base.add(BorderLayout.WEST, p);
        base.add(BorderLayout.CENTER, builder.getPanel());
        
        JCollapsablePanel wrapper = new JCollapsablePanel("CVS Projects");
        wrapper.setContent(base);
        return wrapper;
    }

    
    /**
     * Builds the tab panel that houses the output console and report viewer.
     * 
     * @return JComponent
     */
    protected JComponent buildOutputTabPanel()
    {
        JSmartTabbedPane tabPanel = new JSmartTabbedPane();
        tabPanel.addTab("Output", buildOutputConsoleView());
        tabPanel.addTab("Report", buildReportView());
        return tabPanel;
    }

    
    /**
     * Builds the output panel.
     *
     * @return JComponent
     */
    protected JComponent buildOutputConsoleView()
    {
        outputArea_ = new JSmartTextArea(true, false);

        JHeaderPanel hp =
            new JHeaderPanel(
                "Output",
                JHeaderPanel.createToolBar(outputArea_),
                new JScrollPane(outputArea_));

        return hp;
    }
    

    /**
     * Builds the html component used to view the generated reports.
     */
    protected JComponent buildReportView()
    {
        reportViewer_ = new WebWindowViewer();
        return reportViewer_.getComponent();
    }

    //--------------------------------------------------------------------------
    // Input validation
    //--------------------------------------------------------------------------

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
     * Verifies that all the fields pass simple verification checks.
     */
    protected void verify()
    {
        checkEmpty(cvsModuleField_);
        checkEmpty(cvsRootField_);
        checkEmpty(checkoutDirField_);
        checkTrailer(checkoutDirField_);
    }


    /**
     * Checks to make sure that the given field contains some data.
     *
     * @param field Field to check.
     * @throws IllegalArgumentException if the field is blank.
     */
    protected void checkEmpty(JSmartTextField field)
    {
        String text = field.getText();

        if (StringUtils.isBlank(text))
            throw new IllegalArgumentException(
                "Field '" + field.getName() + "' must have a value.");
    }


    /**
     * Checks the trailing character on directory fields to make sure then have
     * a terminating File.separator.
     *
     * @param field Field to check.
     */
    protected void checkTrailer(JSmartTextField field)
    {
        field.setText(FileUtil.trailWithSeparator(field.getText()));
    }

    //--------------------------------------------------------------------------
    // Helpers
    //--------------------------------------------------------------------------

    /**
     * Creates an instance of the appropriate report engine.
     *
     * @return StatcvsEngine
     * @throws Exception on instantiation error.
     */
    protected StatcvsEngine getStatcvsEngine() throws Exception
    {
        JComponent selected = engineGroup_.getSelected();
        String engineClazz = (String) selected.getClientProperty(KEY_ENGINE);

        StatcvsEngine engine =
            (StatcvsEngine) Class.forName(engineClazz).newInstance();

        engine.setPlugin(this);
        return engine;
    }


    /**
     * Returns a log file name based on the cvs module. Takes into account that
     * the module name may be a hierarchy (a/b/c/b) and replaces file separators
     * with periods.
     *
     * @param module Module to use for creating the log file name.
     * @return Log file name.
     */
    protected String moduleToLogFile(String module)
    {
        return StringUtils.replace(
            FilenameUtils.separatorsToSystem(module),
                File.separator,
                ".")
            + ".log";
    }


    /**
     * Returns the absolute path to the cvs generated log file.
     *
     * @return Path to log file.
     */
    protected String getCVSLogFile()
    {
        return checkoutDirField_.getText() +
               cvsModuleField_.getText() +
               File.separator +
               moduleToLogFile(cvsModuleField_.getText());
    }


    /**
     * Returns the base CVS checkout directory.
     *
     * @return Base CVS checkout directory.
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
     * @param contents Log file contents.
     * @return Fixed Log file contents.
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

        if (StringUtils.isBlank(lines[0]))
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
            contents = contents.substring(i + 1);
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
    // Initializable Interface
    //--------------------------------------------------------------------------
    
    /*
     * @see toolbox.util.service.Initializable#initialize(java.util.Map)
     */
    public void initialize(Map params) throws ServiceException
    {
        checkTransition(ServiceTransition.INITIALIZE);
        
        if (params != null)
            statusBar_ = (IStatusBar)
                params.get(PluginWorkspace.KEY_STATUSBAR);

        buildView();

        cvsOut_ = new PrintStream(new JTextAreaOutputStream(outputArea_));
        cvsErr_ = new PrintStream(new JTextAreaOutputStream(outputArea_));
        
        savedSystemOut_ = System.out;
        savedSystemErr_ = System.err;
        
        transition(ServiceTransition.INITIALIZE);
    }
    
    //--------------------------------------------------------------------------
    // IPlugin Interface
    //--------------------------------------------------------------------------

    /*
     * @see toolbox.workspace.IPlugin#getPluginName()
     */
    public String getPluginName()
    {
        return "StatCVS";
    }


    /*
     * @see toolbox.workspace.IPlugin#getView()
     */
    public JComponent getView()
    {
        return view_;
    }


    /*
     * @see toolbox.workspace.IPlugin#getDescription()
     */
    public String getDescription()
    {
        return "Runs Statcvs on a CVS module";
    }

    //--------------------------------------------------------------------------
    // Destroyable Interface
    //--------------------------------------------------------------------------
    
    /*
     * @see toolbox.util.service.Destroyable#destroy()
     */
    public void destroy() throws ServiceException
    {
        transition(ServiceTransition.DESTROY);
    }

    //--------------------------------------------------------------------------
    // IPreferenced Interface
    //--------------------------------------------------------------------------

    /*
     * @see toolbox.workspace.IPreferenced#applyPrefs(nu.xom.Element)
     */
    public void applyPrefs(Element prefs) throws PreferencedException
    {
        Element root =
            XOMUtil.getFirstChildElement(
                prefs,
                NODE_STATCVS_PLUGIN,
                new Element(NODE_STATCVS_PLUGIN));

        Element projects =
            XOMUtil.getFirstChildElement(
                root,
                NODE_CVSPROJECTS,
                new Element(NODE_CVSPROJECTS));

        if (projects.getChildCount() == 0)
        {
            try
            {
                projectCombo_
                    .addItem(new CVSProject(
                        "Sourceforge",
                        "<module>",
                        ":pserver:anonymous@cvs.sourceforge.net:2401/cvsroot/<module>",
                        "", FileUtil.getTempDir().getCanonicalPath(), false,
                        "", CLASS_STATCVS_ENGINE));

                projectCombo_.addItem(new CVSProject("java.dev.net",
                    "<module>",
                    ":pserver:<username>@cvs.dev.java.net:2401/cvs", "",
                    FileUtil.getTempDir().getCanonicalPath(), false, "",
                    CLASS_STATCVS_ENGINE));

                projectCombo_.addItem(new CVSProject("Apache", "<module>",
                    ":pserver:anoncvs@cvs.apache.org:2401/home/cvspublic", "",
                    FileUtil.getTempDir().getCanonicalPath(), false, "",
                    CLASS_STATCVS_ENGINE));

                projectCombo_
                    .addItem(new CVSProject(
                        "Statcvs",
                        "statcvs",
                        ":pserver:anonymous@cvs.sourceforge.net:2401/cvsroot/statcvs",
                        "", FileUtil.getTempDir().getCanonicalPath(), false,
                        "", CLASS_STATCVS_ENGINE));
            }
            catch (IOException e)
            {
                throw new PreferencedException(e);
            }
        }
        else
        {
            Elements projectList =
                projects.getChildElements(CVSProject.NODE_CVSPROJECT);

            for (int i = 0, n = projectList.size(); i < n; i++)
            {
                try
                {
                    Element projectNode = projectList.get(i);
                    CVSProject project = new CVSProject(projectNode.toXML());
                    addProject(project);
                }
                catch (Exception e)
                {
                    throw new PreferencedException(e);
                }
            }

            projectCombo_.setSelectedIndex(
                XOMUtil.getIntegerAttribute(projects, ATTR_SELECTED, 0));
        }

        outputArea_.applyPrefs(root);
        cvsProjectsView_.applyPrefs(root);
    }


    /*
     * @see toolbox.workspace.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs) throws PreferencedException
    {
        Element root = new Element(NODE_STATCVS_PLUGIN);
        Element projects = new Element(NODE_CVSPROJECTS);

        projects.addAttribute(
            new Attribute(
                ATTR_SELECTED,
                projectCombo_.getSelectedIndex() + ""));

        for (int i = 0, n = projectCombo_.getItemCount(); i < n; i++)
        {
            CVSProject project = (CVSProject) projectCombo_.getItemAt(i);
            Element wrapper = new Element("wrapper");
            project.savePrefs(wrapper);
            
            Element cvsProject = 
                wrapper.getFirstChildElement(CVSProject.NODE_CVSPROJECT);
            
            cvsProject.detach();
            projects.appendChild(cvsProject);
        }

        root.appendChild(projects);
        outputArea_.savePrefs(root);
        cvsProjectsView_.savePrefs(root);
        XOMUtil.insertOrReplace(prefs, root);
    }


    //--------------------------------------------------------------------------
    // StatcvsAction
    //--------------------------------------------------------------------------

    /**
     * Abstract base class for all actions that takes care of settin/restoring
     * the stdout and stderr streams before and after the action completes
     * execution.
     */
    abstract class StatcvsAction extends WorkspaceAction
    {
        /**
         * Creates a StatcvsAction.
         *
         * @param name Name of the action.
         * @param async Is the action to be executed asynchronously?
         * @param scope Is the scope of the action limited to a single component
         * @param statusBar Status bar to update.
         */
        StatcvsAction(
            String name,
            boolean async,
            JComponent scope,
            IStatusBar statusBar)
        {
            super(name, true, async, scope, statusBar);

            
            addPreAction(new AbstractAction()
            {
                public void actionPerformed(ActionEvent e)
                {
                    System.setOut(cvsOut_);
                    System.setErr(cvsErr_);
                }
            });
            
            addFinallyAction(new AbstractAction()
            {
                public void actionPerformed(ActionEvent e)
                {
                    cvsOut_.flush();
                    cvsErr_.flush();
                    
                    System.setOut(savedSystemOut_);
                    System.setErr(savedSystemErr_);
                }
            });
        }
    }

    //--------------------------------------------------------------------------
    // EverythingAction
    //--------------------------------------------------------------------------

    /**
     * Executes all steps necessary to produce the StatCVS report.
     */
    class EverythingAction extends StatcvsAction
    {
        /**
         * Creates a EverythingAction.
         */
        EverythingAction()
        {
            super("I'm feeling lucky!", true, null, null);
        }


        /**
         * @see toolbox.util.ui.SmartAction#runAction(
         *      java.awt.event.ActionEvent)
         */
        public void runAction(ActionEvent e) throws Exception
        {
            new LoginAction().runAction();
            new CheckoutAction().runAction();
            new LogAction().runAction(e);
            new GenerateStatsAction().runAction(e);
            new LaunchAction().runAction(e);
        }
    }

    //--------------------------------------------------------------------------
    // LoginAction
    //--------------------------------------------------------------------------

    /**
     * Logs into the cvs server.
     */
    class LoginAction extends StatcvsAction
    {
        /**
         * Creates a LoginAction.
         */
        LoginAction()
        {
            super("Login", true, null, statusBar_);
        }


        /*
         * @see toolbox.util.ui.SmartAction#runAction(java.awt.event.ActionEvent)
         */
        public void runAction(ActionEvent e) throws Exception
        {
            runAction();
        }


        /**
         * Non-event friendly way to invoke directly.
         */
        public void runAction()
        {
            verify();
            setDebug();

            statusBar_.setInfo("Logging in...");

            // Override cvs password for netbeans cvs lib
            System.setProperty(
                "cvs.password", 
                new String(cvsPasswordField_.getPassword()));

            // Delete password file. Netbeans cvs lib has problems overwriting
            // it if it already exists
            File cvspass = new File(
                FileUtil.trailWithSeparator(
                    System.getProperty("user.home")) + ".cvspass");

            if (cvspass.exists())
                cvspass.delete();

            String[] cvsArgs = new String[]
            {
                "-d",
                cvsRootField_.getText(),
                "login"
            };

            boolean success =
                CVSCommand.processCommand(
                    cvsArgs,
                    null,
                    checkoutDirField_.getText(),
                    cvsOut_,
                    cvsErr_);

            statusBar_.setInfo(success ? "Login completed" : "Login failed");
        }
    }

    //--------------------------------------------------------------------------
    // CheckoutAction
    //--------------------------------------------------------------------------

    /**
     * Checks out the module from the cvs server to the local filesystem.
     */
    class CheckoutAction extends StatcvsAction
    {
        /**
         * Creates a CheckoutAction.
         */
        CheckoutAction()
        {
            super("Checkout", true, null, statusBar_);
        }


        /*
         * @see toolbox.util.ui.SmartAction#runAction(java.awt.event.ActionEvent)
         */
        public void runAction(ActionEvent e) throws Exception
        {
            runAction();
        }


        /**
         * Non-event friendly way to invoke directly.
         */
        public void runAction()
        {
            verify();
            setDebug();

            statusBar_.setInfo(
                "Checking out " + cvsModuleField_.getText() +
                " module to " + checkoutDirField_.getText() + "...");

            String cvsArgs[] = new String[]
            {
                "-d",
                cvsRootField_.getText(),
                "checkout",
                cvsModuleField_.getText()
            };

            boolean success = CVSCommand.processCommand(
                cvsArgs,
                null,
                checkoutDirField_.getText(),
                cvsOut_,
                cvsErr_);

            statusBar_.setInfo(success ? "Checkout done" : "Checkout failed");
        }
    }

    //--------------------------------------------------------------------------
    // LogAction
    //--------------------------------------------------------------------------

    /**
     * Generates a cvs log file which is later used as input to statcvs.
     */
    class LogAction extends StatcvsAction
    {
        /**
         * Creates a LogAction.
         */
        LogAction()
        {
            super("Generate CVS Log", true, null, statusBar_);
        }


        /* 
         * @see toolbox.util.ui.SmartAction#runAction(java.awt.event.ActionEvent)
         */
        public void runAction(ActionEvent e) throws Exception
        {
            statusBar_.setInfo("Generating log...");
            verify();
            setDebug();

            String[] cvsArgs = new String[]
            {
                "-d",
                cvsRootField_.getText(),
                "log"
            };

            logger_.debug("cvsLogFile = " + getCVSLogFile());

            StringOutputStream logCapture = new StringOutputStream();

            String cvsDir =
                FileUtil.trailWithSeparator(checkoutDirField_.getText()) +
                cvsModuleField_.getText();

            boolean success = CVSCommand.processCommand(
                cvsArgs,
                null,
                cvsDir,
                new PrintStream(logCapture),
                cvsErr_);


            String fixedLogFile = fixLogFile(logCapture.toString());
            FileUtil.setFileContents(getCVSLogFile(), fixedLogFile, false);

            statusBar_.setInfo(
                "Generated CVS log containing " +
                    fixedLogFile.length() + " bytes");
        }
    }

    //--------------------------------------------------------------------------
    // GenerateStatsAction
    //--------------------------------------------------------------------------

    /**
     * Runs statcvs against the generatted cvs log file to create a HTML report.
     */
    class GenerateStatsAction extends StatcvsAction
    {
        /**
         * Creates a GenerateStatsAction.
         */
        GenerateStatsAction()
        {
            super("Generate Stats", true, null, statusBar_);
        }


        /*
         * @see toolbox.util.ui.SmartAction#runAction(java.awt.event.ActionEvent)
         */
        public void runAction(ActionEvent e) throws Exception
        {
            statusBar_.setInfo("Generating stats...");
            verify();
            setDebug();
            StatcvsEngine engine = getStatcvsEngine();
            engine.generateStats();
            launchURLField_.setText(engine.getLaunchURL());
            statusBar_.setInfo("Generating stats done.");
        }
    }

    //--------------------------------------------------------------------------
    // LaunchAction
    //--------------------------------------------------------------------------

    /**
     * Launches web browser to view the generated Statcvs reports.
     */
    class LaunchAction extends StatcvsAction
    {
        /**
         * Creates a LaunchAction.
         */
        LaunchAction()
        {
            super("View stats report", false, null, null);
        }


        /*
         * @see toolbox.util.ui.SmartAction#runAction(java.awt.event.ActionEvent)
         */
        public void runAction(ActionEvent e) throws Exception
        {
            reportViewer_.view(new File(launchURLField_.getText()));
            //BrowserLauncher.openURL(launchURLField_.getText());
        }
    }

    //--------------------------------------------------------------------------
    // ProjectChangedAction
    //--------------------------------------------------------------------------

    /**
     * Updates the cvs project fields when the project selection changes.
     */
    class ProjectChangedAction extends AbstractAction
    {
        /*
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e)
        {
            Object obj = projectCombo_.getSelectedItem();

            if (obj instanceof CVSProject)
            {
                CVSProject project =
                    (CVSProject) projectCombo_.getSelectedItem();

                cvsModuleField_.setText(project.getCVSModule());
                cvsRootField_.setText(project.getCVSRoot());
                cvsPasswordField_.setText(project.getCVSPassword());
                checkoutDirField_.setText(project.getCheckoutDir());
                debugCheckBox_.setSelected(project.isDebug());
                launchURLField_.setText(project.getLaunchURL());
                
                String engin = project.getEngine();
                
                if (defaultEngine_.getClientProperty(KEY_ENGINE).equals(engin))
                    defaultEngine_.setSelected(true);
                else if (xmlEngine_.getClientProperty(KEY_ENGINE).equals(engin))
                    xmlEngine_.setSelected(true);
                else
                    throw new IllegalArgumentException(
                        "Unknown statcvs engine type '" + engin + "'");
            }
        }
    }

    //--------------------------------------------------------------------------
    // SaveAction
    //--------------------------------------------------------------------------

    /**
     * Saves the current cvs project. If the project does not already exist,
     * it is created.
     */
    class SaveAction extends StatcvsAction
    {
        /**
         * Creates a SaveAction.
         */
        SaveAction()
        {
            super("", false, null, null);
            putValue(SMALL_ICON, ImageCache.getIcon(ImageCache.IMAGE_SAVE));
            putValue(SHORT_DESCRIPTION, "Saves the project");
        }


        /*
         * @see toolbox.util.ui.SmartAction#runAction(java.awt.event.ActionEvent)
         */
        public void runAction(ActionEvent e) throws Exception
        {
            String current = projectCombo_.getEditor().getItem().toString();

            if (StringUtils.isBlank(current))
            {
                statusBar_.setWarning("Project name cannot be empty");
            }
            else
            {
                boolean found = false;

                for (int i = 0; i < projectCombo_.getItemCount(); i++)
                {
                    CVSProject project = (CVSProject) 
                        projectCombo_.getItemAt(i);

                    if (project.getProject().equals(current))
                    {
                        project.setCVSModule(cvsModuleField_.getText());
                        project.setCVSRoot(cvsRootField_.getText());
                        project.setCVSPassword(
                            new String(cvsPasswordField_.getPassword()));
                        project.setCheckoutDir(checkoutDirField_.getText());
                        project.setDebug(debugCheckBox_.isSelected());
                        project.setLaunchURL(launchURLField_.getText());
                        project.setEngine(
                            getStatcvsEngine().getClass().getName());
                        found |= true;
                        break;
                    }
                }

                if (!found)
                {
                    CVSProject project = null;

                    project = new CVSProject(
                        current,
                        cvsModuleField_.getText(),
                        cvsRootField_.getText(),
                        new String(cvsPasswordField_.getPassword()),
                        checkoutDirField_.getText(),
                        debugCheckBox_.isSelected(),
                        launchURLField_.getText(),
                        getStatcvsEngine().getClass().getName());

                    projectCombo_.addItem(project);
                    projectCombo_.setSelectedItem(project);
                }

                statusBar_.setInfo("Project " + current + " saved.");
            }
        }
    }

    //--------------------------------------------------------------------------
    // DeleteAction
    //--------------------------------------------------------------------------

    /**
     * Deletes the selected cvs project.
     */
    class DeleteAction extends AbstractAction
    {
        /**
         * Creates a DeleteAction.
         */
        DeleteAction()
        {
            super("", ImageCache.getIcon(ImageCache.IMAGE_DELETE));
            putValue(SHORT_DESCRIPTION, "Deletes the project");
        }


        /*
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e)
        {
            String current = projectCombo_.getEditor().getItem().toString();

            boolean found = false;

            for (int i = 0; i < projectCombo_.getItemCount(); i++)
            {
                CVSProject project = (CVSProject) projectCombo_.getItemAt(i);

                if (project.getProject().equals(current))
                {
                    logger_.debug("Removing " + i);
                    projectCombo_.removeItemAt(i);

                    if (projectCombo_.getItemCount() > 0)
                        projectCombo_.setSelectedIndex(0);

                    statusBar_.setInfo("Project " + current + " deleted.");
                    found |= true;
                    break;
                }
            }

            if (!found)
            {
                if (StringUtils.isBlank(current))
                    statusBar_.setInfo("Select a project to delete.");
                else
                    statusBar_.setWarning(
                        "Project " + current + " does not exist.");
            }
        }
    }
}