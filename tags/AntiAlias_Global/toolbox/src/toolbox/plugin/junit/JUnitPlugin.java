package toolbox.junit;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import nu.xom.Attribute;
import nu.xom.Element;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import toolbox.log4j.JTextAreaAppender;
import toolbox.util.ArrayUtil;
import toolbox.util.ClassUtil;
import toolbox.util.SwingUtil;
import toolbox.util.XOMUtil;
import toolbox.util.ui.JListPopupMenu;
import toolbox.util.ui.JSmartButton;
import toolbox.util.ui.JSmartList;
import toolbox.util.ui.JSmartTextField;
import toolbox.util.ui.RegexListModelFilter;
import toolbox.util.ui.plugin.IPlugin;
import toolbox.util.ui.plugin.IStatusBar;
import toolbox.util.ui.plugin.PluginWorkspace;

/**
 * Simple plugin that allows running of JUnit tests by package
 */
public class JUnitPlugin extends JPanel implements IPlugin
{
    public static final Logger logger_ =
        Logger.getLogger(JUnitPlugin.class);

    private static final String NODE_JUNIT_PLUGIN = "JUnitPlugin";
    private static final String ATTR_FILTER       = "filter";

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
     * Creates a JUnitPlugin
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
        
        selectPanel.add(BorderLayout.NORTH, 
            filterField_ = new JSmartTextField(12));
        
        selectPanel.add(BorderLayout.CENTER, 
            new JScrollPane(packageList_ = new JSmartList(filterModel_)));
            
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
            new JSmartButton(getPackagesAction_ = new GetPackageListAction()));
            
        buttonPanel.add(
            new JSmartButton(testPackagesAction_ = new TestPackagesAction()));

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

    //--------------------------------------------------------------------------
    //  IPlugin interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.ui.plugin.IPlugin#startup(Map)
     */
    public void startup(Map params)
    {
        if (params != null)
            statusBar_= (IStatusBar) params.get(PluginWorkspace.PROP_STATUSBAR);

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
    public JComponent getComponent()
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
     * @see toolbox.util.ui.plugin.IPreferenced#applyPrefs(nu.xom.Element)
     */
    public void applyPrefs(Element prefs) throws Exception
    {
        Element root = XOMUtil.getFirstChildElement(
            prefs, NODE_JUNIT_PLUGIN, new Element(NODE_JUNIT_PLUGIN));
        
        filterField_.setText(
            XOMUtil.getStringAttribute(root, ATTR_FILTER, ".*test"));
    }
    
    /**
     * @see toolbox.util.ui.plugin.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs)
    {
        Element root = new Element(NODE_JUNIT_PLUGIN);
        root.addAttribute(new Attribute(ATTR_FILTER, filterField_.getText()));
        XOMUtil.insertOrReplace(prefs, root);    
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