package toolbox.junit;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import toolbox.util.ArrayUtil;
import toolbox.util.ClassUtil;
import toolbox.util.StringUtil;
import toolbox.util.SwingUtil;
import toolbox.util.ui.JListPopupMenu;
import toolbox.util.ui.JTextAreaAppender;
import toolbox.util.ui.RegexListModelFilter;
import toolbox.util.ui.plugin.IPlugin;
import toolbox.util.ui.plugin.IStatusBar;

/**
 * Simple plugin that allows running of JUnit tests by package
 */
public class JUnitPlugin extends JPanel implements  IPlugin
{
    public static final Logger logger_ =
        Logger.getLogger(JUnitPlugin.class);

    private static final String PROP_FILTER = "junit.plugin.filter";

    private Action getPackagesAction_;
    private Action testPackagesAction_;
    
    private JTextField              filterField_;
    private JList                   packageList_;
    private DefaultListModel        packageModel_;
    private RegexListModelFilter    filterModel_;
    private IStatusBar              statusBar_;
           
    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------

    /**
     * Default constructor
     */
    public JUnitPlugin()
    {
    }

    //--------------------------------------------------------------------------
    //  Private
    //--------------------------------------------------------------------------

    /**
     * Builds the GUI
     */
    protected void buildView()
    {
        // the selection panel
        JPanel selectPanel = new JPanel(new BorderLayout());
        
        packageModel_  = new DefaultListModel();        
        filterModel_ = new RegexListModelFilter(packageModel_);
        selectPanel.add(BorderLayout.NORTH, filterField_ = new JTextField(12));
        
        selectPanel.add(BorderLayout.CENTER, 
            new JScrollPane(packageList_ = new JList(filterModel_)));
            
        packageList_.setFont(SwingUtil.getPreferredMonoFont());
        new JListPopupMenu(packageList_);
        filterField_.setFont(SwingUtil.getPreferredMonoFont());
        filterField_.addKeyListener(new FilterKeyListener());

        // Output panel
        JPanel outputPanel = new JPanel(new BorderLayout());
        JTextAreaAppender appender = new JTextAreaAppender();
        Logger.getRootLogger().addAppender(appender);        
        appender.setThreshold(Priority.DEBUG);
        
        JTextArea area = appender.getTextArea();
        outputPanel.add(BorderLayout.CENTER, new JScrollPane(area));
        area.setFont(SwingUtil.getPreferredMonoFont());
        
        // build button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBorder(BorderFactory.createEtchedBorder());
        
        buttonPanel.add(
            new JButton(getPackagesAction_ = new GetPackageListAction()));
            
        buttonPanel.add(
            new JButton(testPackagesAction_ = new TestPackagesAction()));

        // configure the root panel        
        setLayout(new BorderLayout());
        add(BorderLayout.WEST, selectPanel);
        add(BorderLayout.CENTER, outputPanel);
        add(BorderLayout.SOUTH, buttonPanel);        
    }     

    /**
     * Updates the package list as a result of a change in the filter criteria
     */
    protected void updatePackageList()
    {
        // Have to do this cuz the filter model is immutable
        packageList_.setModel(new DefaultListModel());
        filterModel_.setRegex(filterField_.getText());
        packageList_.setModel(filterModel_);
        packageList_.scrollRectToVisible(new Rectangle(0,0,0,0));
    }

    /**
     * Sets the status text
     * 
     * @param  status  Status text
     */
    protected void setStatus(String status)
    {
        if (statusBar_ != null)
            statusBar_.setStatus(status);
    }
       
    //--------------------------------------------------------------------------
    //  IPlugin interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.ui.plugin.IPlugin#init()
     */
    public void init()
    {
        buildView();        
    }

    /**
     * @see toolbox.util.ui.plugin.IPlugin#getName()
     */
    public String getName()
    {
        return "JUnit";
    }

    /**
     * @see toolbox.util.ui.plugin.IPlugin#getComponent()
     */
    public Component getComponent()
    {
        return this;
    }

    /**
     * @see toolbox.util.ui.plugin.IPlugin#getDescription()
     */
    public String getDescription()
    {
        return "JUnit add-on that enables running of test cases by package.";
    }

    /**
     * @see toolbox.util.ui.plugin.IPlugin#savePrefs(Properties)
     */
    public void savePrefs(Properties prefs)
    {
        if (!StringUtil.isNullOrEmpty(filterField_.getText()))
            prefs.setProperty(PROP_FILTER, filterField_.getText());
    }

    /**
     * @see toolbox.util.ui.plugin.IPlugin#applyPrefs(Properties)
     */
    public void applyPrefs(Properties prefs)
    {
        // Set default to test packages
        filterField_.setText(prefs.getProperty(PROP_FILTER, ".*test"));
    }

    /**
     * @see toolbox.util.ui.plugin.IPlugin#setStatusBar(IStatusBar)
     */
    public void setStatusBar(IStatusBar statusBar)
    {
        statusBar_ = statusBar;
    }

    /**
     * @see toolbox.util.ui.plugin.IPlugin#shutdown()
     */
    public void shutdown()
    {
    }

    //--------------------------------------------------------------------------
    //  GUI Actions
    //--------------------------------------------------------------------------
    
    /**
     * Gets the list of packages on the classpath and populates the package
     * list box
     */
    class GetPackageListAction extends AbstractAction
    {
        public GetPackageListAction()
        {
            super("Get Packages");
            putValue(MNEMONIC_KEY, new Integer('G'));
            putValue(SHORT_DESCRIPTION, 
                "Retrives the list of all know packages on the classpath");
        }
        
        public void actionPerformed(ActionEvent e)
        {
            String[] packages = ClassUtil.getPackagesInClasspath();

            packageList_.setModel(new DefaultListModel());
            packageModel_ = new DefaultListModel();
                        
            for (int i=0; i<packages.length; i++)
                packageModel_.addElement(packages[i]);
            
            filterModel_ = new RegexListModelFilter(packageModel_);
            packageList_.setModel(filterModel_);
            
            updatePackageList();
        }
    }

    /**
     * Runs the unit tests in the selected packges in the package list box
     */
    class TestPackagesAction extends AbstractAction
    {
        public TestPackagesAction()
        {
            super("Test Packages");
            putValue(MNEMONIC_KEY, new Integer('T'));
            putValue(SHORT_DESCRIPTION, 
                "Runs JUNit tests in the selected packages");
        }
        
        public void actionPerformed(ActionEvent e)
        {
            Object[] selected = packageList_.getSelectedValues();
            PackageTester pt = new PackageTester();
            
            for(int i=0; i<selected.length; i++)
                pt.addPackage(selected[i].toString());    
                
            logger_.info("Running tests on: " + ArrayUtil.toString(selected));
            
            pt.run();
        }
    }
    
    //--------------------------------------------------------------------------
    //  Inner Classes
    //--------------------------------------------------------------------------

    /**
     * Enabled dynamic filtering  of regex as it is typed
     */    
    class FilterKeyListener extends KeyAdapter
    {
        private String oldValue_ = "";
        
        public void keyReleased(KeyEvent e)
        {
            super.keyReleased(e);
            
            String newValue = filterField_.getText().trim() + e.getKeyChar();
 
            // Only refresh if the filter has changed           
            if (!newValue.equals(oldValue_))
            {                
                oldValue_ = newValue;
                updatePackageList();            
            }
        }
    }
}