package toolbox.findclass;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import net.sf.jode.decompiler.Decompiler;

import org.apache.log4j.Logger;
import org.apache.regexp.RESyntaxException;

import org.jedit.syntax.JavaTokenMarker;
import org.jedit.syntax.TextAreaDefaults;

import toolbox.jedit.JEditPopupMenu;
import toolbox.jedit.JEditTextArea;
import toolbox.jedit.JavaDefaults;
import toolbox.util.ClassUtil;
import toolbox.util.DateTimeUtil;
import toolbox.util.ExceptionUtil;
import toolbox.util.FileUtil;
import toolbox.util.MathUtil;
import toolbox.util.PropertiesUtil;
import toolbox.util.StringUtil;
import toolbox.util.SwingUtil;
import toolbox.util.ThreadUtil;
import toolbox.util.io.NullWriter;
import toolbox.util.ui.JFileExplorer;
import toolbox.util.ui.JFileExplorerAdapter;
import toolbox.util.ui.JPopupListener;
import toolbox.util.ui.JStatusBar;
import toolbox.util.ui.ThreadSafeTableModel;
import toolbox.util.ui.flippane.JFlipPane;
import toolbox.util.ui.plugin.IPreferenced;
import toolbox.util.ui.plugin.IStatusBar;
import toolbox.util.ui.plugin.WorkspaceAction;
import toolbox.util.ui.table.TableSorter;

/**
 * GUI for finding class files by regular expression from the classpath
 * or any arbitrary java archive or directory.
 */
public class JFindClass extends JFrame implements IPreferenced
{
    /*
     * TODO: Update tablecell renderer to highlight the matching substring
     * TODO: Add sorting to table
     * TODO: Add button to search for all duplicates
     */

    private static final Logger logger_ = 
        Logger.getLogger(JFindClass.class);

    // Search    
    private JTextField           searchField_;
    private JButton              searchButton_;
    private JButton              dupesButton_;
    private JCheckBox            ignoreCaseCheckBox_;
    private JCheckBox            showPathCheckBox_;
   
    // Left flip pane            
    private JFlipPane            leftFlipPane_;
    private JFileExplorer        fileExplorer_;
    
    // Top flip pane
    private JFlipPane            topFlipPane_;
    private JList                searchList_;
    private DefaultListModel     searchListModel_;
    private JPopupMenu           searchPopupMenu_;
    private JEditTextArea        sourceArea_;
    
    // Results    
    private JTable               resultTable_;
    private ThreadSafeTableModel resultTableModel_;
    private TableSorter          resultTableSorter_;    
    private JScrollPane          resultPane_;
    private int                  resultCount_;
    private FindClass            findClass_;

    // Status
    private IStatusBar statusBar_ = new JStatusBar();
    
    /** Result table column headers */    
    private String[] resultColumns_ = new String[] 
    {
        "Num", 
        "Source", 
        "Class File",
        "Size", 
        "Timestamp"
    };

    private static final int COL_NUM       = 0;
    private static final int COL_SOURCE    = 1;
    private static final int COL_CLASS     = 2;
    private static final int COL_SIZE      = 3;
    private static final int COL_TIMESTAMP = 4;

    /** Prefix tacked onto the beginning of all properties assoc w/ JTail */
    private static final String PROP_PREFIX = "jfindclass.plugin";
    
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
        
    /**
     * Entrypoint
     * 
     * @param  args  None recognized
     * @throws Exception on error
     */    
    public static void main(String[] args) throws Exception
    {
        SwingUtil.setPreferredLAF();           
        JFindClass jfc = new JFindClass();
        jfc.init();
        jfc.setSize(800,600);
        SwingUtil.centerWindow(jfc);        
        jfc.setVisible(true);
    }

    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------

    /**
     * Default Constructor
     */
    public JFindClass()
    {
        super("JFindClass");
    }
    
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------

    /**
     * Sets the status bar
     * 
     * @param  statusBar  Status bar
     */
    public void setStatusBar(IStatusBar statusBar)
    {
        statusBar_ = statusBar;
    }

    //--------------------------------------------------------------------------
    // IPreferenced Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.ui.plugin.IPreferenced#savePrefs(Properties)
     */
    public void savePrefs(Properties prefs)
    {
        fileExplorer_.savePrefs(prefs, PROP_PREFIX);
        leftFlipPane_.savePrefs(prefs, PROP_PREFIX + ".left");
        topFlipPane_.savePrefs(prefs, PROP_PREFIX + ".top");
        
        PropertiesUtil.setBoolean(
            prefs,PROP_PREFIX + ".ignorecase",ignoreCaseCheckBox_.isSelected());
            
        PropertiesUtil.setBoolean(
            prefs,PROP_PREFIX + ".showpath", showPathCheckBox_.isSelected());
            
        prefs.setProperty(
            PROP_PREFIX + ".lastsearch", searchField_.getText().trim());
    }

    /**
     * @see toolbox.util.ui.plugin.IPreferenced#applyPrefs(Properties)
     */
    public void applyPrefs(Properties prefs)
    {
        fileExplorer_.applyPrefs(prefs, PROP_PREFIX);
        leftFlipPane_.applyPrefs(prefs, PROP_PREFIX + ".left");
        topFlipPane_.applyPrefs(prefs, PROP_PREFIX + ".top");

        ignoreCaseCheckBox_.setSelected(
            PropertiesUtil.getBoolean(prefs,PROP_PREFIX + ".ignorecase", true));
            
        showPathCheckBox_.setSelected(
            PropertiesUtil.getBoolean(prefs, PROP_PREFIX + ".showpath", true));
            
        searchField_.setText(prefs.getProperty(PROP_PREFIX + ".lastsearch","")); 
    }

    //--------------------------------------------------------------------------
    //  Protected
    //--------------------------------------------------------------------------
 
    /**
     * Initiailizes the GUI
     */
    protected void init()
    {
        buildView();
        findClass_ = new FindClass();
        //findClass_.addSearchListener(new SearchListener());        
        List targets = findClass_.getSearchTargets();
        
        for (Iterator i = targets.iterator(); i.hasNext(); 
            searchListModel_.addElement(i.next()));
    }

    /**
     * Builds the GUI and adds it to the contentPane
     */
    protected void buildView()
    {
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        // Search bar
        buildSearchPanel();
        
        // Top flip pane (classpath & decompiler)
        buildTopFlipPane();
        
        // SearchResults
        buildSearchResultsPanel();
        
        // Left flip pane with file explorer
        buildLeftFlipPane();

        // Post tweaks
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * Builds the search panel at the top of the GUI with the ignore case box.
     * Allows the user to enter a regular expression into the search field
     * and initiate a search.
     */    
    protected void buildSearchPanel()
    {
        // Search Panel
        
        // Action is stared by search textfield and button
        Action searchAction = new SearchAction();

        JLabel searchLabel = new JLabel("Find Class");
        
        searchField_ = new JTextField(20);
        searchField_.setAction(searchAction);
        searchField_.setToolTipText("I like Regular expressions!");
        
        searchButton_       = new JButton(searchAction);
        dupesButton_        = new JButton(new FindDupesAction());
        ignoreCaseCheckBox_ = new JCheckBox("Ignore Case", true);
        showPathCheckBox_   = new JCheckBox("Show Path", true);
        
        JPanel searchPanel = new JPanel(new FlowLayout());
        searchPanel.add(searchLabel);
        searchPanel.add(searchField_);
        searchPanel.add(searchButton_);
        searchPanel.add(dupesButton_);
        searchPanel.add(new JLabel("      "));
        searchPanel.add(ignoreCaseCheckBox_);
        searchPanel.add(showPathCheckBox_);
        
        getContentPane().add(searchPanel, BorderLayout.NORTH);
    }

    /**
     * Builds the Decompiler panel which shows a class file (search result) in 
     * decompiled form
     * 
     * @return Decompiler panel
     */
    protected JPanel buildDecompilerPanel()
    {
        TextAreaDefaults defaults = new JavaDefaults();
        
        sourceArea_ = 
            new JEditTextArea(new JavaTokenMarker(), defaults);

        // Hack for circular reference in popup menu            
        ((JEditPopupMenu) defaults.popup).setTextArea(sourceArea_);
        ((JEditPopupMenu) defaults.popup).buildView();
            
        JButton decompileButton = new JButton(new DecompileAction());
        JPanel decompilerPanel = new JPanel(new BorderLayout());
        decompilerPanel.add(BorderLayout.CENTER, sourceArea_);
        decompilerPanel.add(BorderLayout.SOUTH, decompileButton);
        decompilerPanel.setPreferredSize(new Dimension(100, 400));
        
        return decompilerPanel;
    }

    /**
     * Builds the Classpath panel which shows all paths/archives that have been
     * targeted for the current search
     * 
     * @return Classpath Panel
     */
    protected JPanel buildClasspathPanel()
    {
        searchListModel_ = new DefaultListModel(); 
        searchList_ = new JList(searchListModel_);
        
        // Create popup menu and wire it to the JList
        searchPopupMenu_ = new JPopupMenu();
        searchPopupMenu_.add(new JMenuItem(new ClearTargetsAction()));
        
        searchPopupMenu_.add(
            new JMenuItem(new AddClasspathTargetAction()));
            
        searchList_.addMouseListener(new JPopupListener(searchPopupMenu_));
        
        JPanel pathPanel = new JPanel(new BorderLayout());
        pathPanel.add(new JScrollPane(searchList_), BorderLayout.CENTER);
        pathPanel.setPreferredSize(new Dimension(100, 400));
        return pathPanel;
    }

    /**
     * Builds the flippane at the top of the application which contains the
     * Classpath and Decompiler panels
     * 
     * @return  Top flip pane
     */
    protected JPanel buildTopFlipPane()
    {
        JPanel pathPanel = buildClasspathPanel();        
        JPanel decompilerPanel = buildDecompilerPanel();
                
        // Top flip pane
        topFlipPane_ = new JFlipPane(JFlipPane.TOP);
        topFlipPane_.addFlipper("Classpath", pathPanel);
        topFlipPane_.addFlipper("Decompiler", decompilerPanel);
        topFlipPane_.setActiveFlipper(pathPanel);
        return topFlipPane_;
    }
    
    /**
     * Builds the Search Results panel which lists the results of the search in
     * a JTable. Once a result is selected, it can optionally be decompiled
     * and have the resulting source code appear in the Decompiler panel
     */
    protected void buildSearchResultsPanel()
    {
        // Search Results panel        
        JLabel resultLabel = new JLabel("Results");
        
        // Setup sortable table
//        tableModel_  = new ThreadSafeTableModel(colNames_, 0);
//        tableSorter_ = new TableSorter(tableModel_);
//        table_       = new JTable(tableSorter_);
//        tableSorter_.addMouseListenerToHeaderInTable(table_);
        
        resultTableModel_  = new ThreadSafeTableModel(resultColumns_,0);
        resultTableSorter_ = new TableSorter(resultTableModel_);
        resultTable_       = new JTable(resultTableSorter_);
        resultPane_        = new JScrollPane(resultTable_);
        resultTableSorter_.addMouseListenerToHeaderInTable(resultTable_);
        tweakTable();
        
        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.add(resultLabel, BorderLayout.NORTH);
        resultPanel.add(resultPane_, BorderLayout.CENTER);
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(BorderLayout.NORTH, buildTopFlipPane());
        centerPanel.add(BorderLayout.CENTER, resultPanel);
        
        getContentPane().add(BorderLayout.CENTER, centerPanel);
    }

    /**
     * Builds the left flip pane which contains the file explorer. The file
     * explorer can be used to add additional search targets to the Classpath
     * panel
     */
    protected void buildLeftFlipPane()
    {
        // Left flip pane - file explorer
        fileExplorer_ = new JFileExplorer(false);
        fileExplorer_.addJFileExplorerListener(new FileExplorerListener());
        leftFlipPane_ = new JFlipPane(JFlipPane.LEFT);
        leftFlipPane_.addFlipper("File Explorer", fileExplorer_);
        leftFlipPane_.setExpanded(false);
        getContentPane().add(leftFlipPane_, BorderLayout.WEST);
    }

    /**
     * Tweaks the table columns for width and extents
     */
    protected void tweakTable()
    {
        resultTable_.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        
        TableColumnModel columnModel = resultTable_.getColumnModel();

        // Tweak file number column
        TableColumn column = columnModel.getColumn(0);
        column.setMinWidth(50);
        column.setPreferredWidth(50);        
        column.setMaxWidth(100); 
        
        // Tweaks file size column
        column = columnModel.getColumn(3);
        column.setMinWidth(50);
        column.setPreferredWidth(50);
        column.setMaxWidth(130);
        
        // Tweak timestamp column
        column = columnModel.getColumn(4);
        column.setMinWidth(150);
        column.setPreferredWidth(150);
        column.setMaxWidth(300);

        // Set alternating row renderer
        for (int i=0; i < resultColumns_.length; i++)
            resultTable_.setDefaultRenderer(resultTable_.getColumnClass(i), 
                new AlternatingCellRenderer());
    }

    //--------------------------------------------------------------------------
    //  Inner Classes
    //--------------------------------------------------------------------------

    /**
     * Handler for events generated by JFileExplorer
     */
    class SearchListener extends FindClassAdapter
    {
        //----------------------------------------------------------------------
        // Overrides toolbox.findclass.FindClassAdapter
        //----------------------------------------------------------------------
            
        /**
         * When a class is found, add it to the result table
         * 
         * @param  searchResult  Info on class that was found
         */
        public void classFound(FindClassResult searchResult)
        {
            Vector row = new Vector();
            String offset = " ";
            
            row.add(offset + (++resultCount_));
            row.add(offset + searchResult.getClassLocation());
            row.add(offset + searchResult.getClassFQN());
            row.add(offset + searchResult.getFileSize()+"");
            row.add(offset + DateTimeUtil.format(searchResult.getTimestamp()));
            resultTableModel_.addRow(row);
        }
        
        /**
         * When a target is searched, update the status bar and hilight the
         * archive being search in the search list.
         * 
         * @param  target  Target that is being searched
         */
        public void searchingTarget(String target)
        {
            final String target2 = target;
            
            SwingUtilities.invokeLater(new Runnable()
            {
                public void run()
                {
                    statusBar_.setStatus("Searching " + target2 + " ...");
                    searchList_.setSelectedValue(target2, true);    
                }
            });
        }
        
        /**
         * When a search is cancelled, update the status bar
         */
        public void searchCancelled()
        {
            statusBar_.setStatus("Search canceled");
        }    
    }

    /**
     * Handler for events generated by JFileExplorer
     */
    class FindDupesListener extends FindClassAdapter
    {
        private Map dupes_;
        
        public FindDupesListener()
        {
            dupes_ = new HashMap();
        }
        
        protected void addResults(FindClassResult result)
        {        
            Vector row = new Vector(5);
            String offset = " ";
                
            row.add(offset + (++resultCount_));
                    
            if (showPathCheckBox_.isSelected())
                row.add(offset + result.getClassLocation());
            else
            {
                // TODO: handle non-jar targets
                row.add(offset + FileUtil.stripPath(result.getClassLocation()));
            }
                        
            row.add(offset + result.getClassFQN());
            row.add(offset + result.getFileSize()+"");
            row.add(offset + DateTimeUtil.format(result.getTimestamp()));
            resultTableModel_.addRow(row);
        }
                
        //----------------------------------------------------------------------
        // Overrides toolbox.findclass.FindClassAdapter
        //----------------------------------------------------------------------
            
        /**
         * When a class is found, add it to the result table
         * 
         * @param  searchResult  Info on class that was found
         */
        public void classFound(FindClassResult searchResult)
        {
            String className = searchResult.getClassFQN();
            
            if (dupes_.containsKey(className))
            {
                List dupesList = (List) dupes_.get(className);
                
                if (dupesList.size() == 1)
                    addResults( (FindClassResult) dupesList.get(0));
                 
                dupesList.add(searchResult);
                addResults(searchResult);
            }
            else
            {
                List dupesList = new ArrayList(1);
                dupesList.add(searchResult);
                dupes_.put(className, dupesList);
            }
        }
        
        /**
         * When a target is searched, update the status bar and hilight the
         * archive being search in the search list.
         * 
         * @param  target  Target that is being searched
         */
        public void searchingTarget(String target)
        {
            final String target2 = target;
            
            SwingUtilities.invokeLater(new Runnable()
            {
                public void run()
                {
                    statusBar_.setStatus("Searching " + target2 + " ...");
                    searchList_.setSelectedValue(target2, true);    
                }
            });
        }
        
        /**
         * When a search is cancelled, update the status bar
         */
        public void searchCancelled()
        {
            statusBar_.setStatus("Search canceled");
        }
        
        public void searchCompleted(String search)
        {
            logger_.debug("Search completed.");
            resultTableSorter_.setEnabled(true);
        }
    
    }
    
    /**
     * Handler class for the file explorer
     */
    class FileExplorerListener extends JFileExplorerAdapter
    {
        //----------------------------------------------------------------------
        // Overrides toolbox.util.ui.explorer.JFileExplorerAdapter
        //----------------------------------------------------------------------
        
        /**
         * Adds a directory to the path list
         * 
         * @param  folder  Directory to add
         */
        public void folderDoubleClicked(String folder)
        {
            List targets = findClass_.getArchivesInDir(new File(folder));
            
            statusBar_.setStatus(
                targets.size() + "archives added to the search list.");
            
            Iterator i = targets.iterator();
            
            while (i.hasNext())
            {
                String target = (String) i.next();
                searchListModel_.addElement(target);
            }
            
            searchListModel_.addElement(folder);
        }
 
        /**
         * Adds a file to the path list
         * 
         * @param  file  File to add
         */       
        public void fileDoubleClicked(String file)
        {
            if (ClassUtil.isArchive(file))
                searchListModel_.addElement(file);
            else
                statusBar_.setStatus(file + " is not a valid archive.");
        }
    }
 
    /**
     * Alternating color cell renderer to make the results table easier on
     * the eyes
     */   
    class AlternatingCellRenderer extends DefaultTableCellRenderer
    {
        //----------------------------------------------------------------------
        // Overrides javax.swing.table.DefaultTableCellRenderer
        //----------------------------------------------------------------------
        
        /**
         * Returns the default table cell renderer.
         *
         * @param   table       JTable
         * @param   value       Value to assign to the cell at [row, column]
         * @param   isSelected  True if the cell is selected
         * @param   hasFocus    True if cell has focus
         * @param   row         Row of the cell to render
         * @param   column      Column of the cell to render
         * 
         * @return  Default table cell renderer
         */
        public Component getTableCellRendererComponent(
            JTable  table,
            Object  value,
            boolean isSelected,
            boolean hasFocus,
            int     row,
            int     column)
        {

            if (isSelected)
            {
                setForeground(table.getSelectionForeground());
                setBackground(table.getSelectionBackground());
            }
            else
            {
                setForeground(table.getForeground());

                // Alternate row background colors colors
                                
                if (MathUtil.isEven(row))
                    setBackground(table.getBackground());
                else
                    setBackground(new Color(240,240,240));
                    
                
            }

            if (hasFocus)
            {
                setBorder(
                    UIManager.getBorder("Table.focusCellHighlightBorder"));
                    
                if (table.isCellEditable(row, column))
                {
                    setForeground(
                        UIManager.getColor("Table.focusCellForeground"));
                        
                    setBackground(
                        UIManager.getColor("Table.focusCellBackground"));
                }
            }
            else
                setBorder(noFocusBorder);

            setValue(value);

            return this;
        }
    }
    
    //--------------------------------------------------------------------------
    //  Actions
    //--------------------------------------------------------------------------

    /**
     * Searches for duplicate classes
     */
    protected class FindDupesAction extends WorkspaceAction
    {
        public FindDupesAction()
        {
            super("Find Duplicates", true, null, statusBar_);
            putValue(MNEMONIC_KEY, new Integer('F'));    
            putValue(SHORT_DESCRIPTION, "Searches for duplicates classes");
        }
        
        public void runAction(ActionEvent e) throws Exception
        {
            statusBar_.setInfo("Searching...");
            findDupes();
        }
        
        public void findDupes() throws RESyntaxException, IOException 
        {
            logger_.debug("Finding duplicates");
            
            // Empty results table
            resultTableModel_.setNumRows(0);
            resultCount_ = 0;
            resultTableSorter_.setEnabled(false);
            
            // Refresh search targets in case of change
            findClass_.removeSearchTargets();
            findClass_.removeSearchListeners();
            findClass_.addSearchListener(new FindDupesListener());
                        
            // Copy targets from GUI --> findClass_
            for (int i = 0, n = searchListModel_.size(); i<n; i++)
                findClass_.addSearchTarget(""+searchListModel_.elementAt(i));
            
            Object results[] = findClass_.findClass(".*", false);             
            
            statusBar_.setStatus(results.length + " duplicates found.");
            
        }
    }

    /**
     * Action to decompile the currently selected class file
     */
    private class DecompileAction extends AbstractAction
    {
        public DecompileAction()
        {
            super("Decompile");
            putValue(MNEMONIC_KEY, new Integer('D'));    
            putValue(SHORT_DESCRIPTION, "Decompiles class");
        }
        
        public void actionPerformed(ActionEvent e)
        {
            int idx = resultTable_.getSelectedRow();
            
            if (idx >=0)
            {
                // Jar or directory path
                String location = (String) 
                    resultTable_.getModel().getValueAt(idx, COL_SOURCE); 
                    
                // FQN of class
                String clazz  = (String) 
                    resultTable_.getModel().getValueAt(idx, COL_CLASS);
            
                location = location.trim();
                clazz  = clazz.trim();
            
                // Setup decompiler        
                Decompiler decompiler = new Decompiler();
                decompiler.setOption("style", "pascal");
                decompiler.setOption("tabwidth", "4");
                decompiler.setErr(new PrintWriter(new NullWriter()));
                decompiler.setClassPath(location);

                // Java source code will be dumped here                
                StringWriter writer = new StringWriter();
                                     
                try
                {
                    decompiler.decompile(clazz, writer, null);
                    
                    // Nuke the tabs                
                    String javaSource = StringUtil.replace(
                        writer.getBuffer().toString(), "\t", "    ");
                        
                    //logger_.debug("\n" + javaSource);    
                    
                    sourceArea_.setText(javaSource);
                    sourceArea_.setCaretPosition(0);
                }
                catch(IOException ioe)
                {
                    ExceptionUtil.handleUI(ioe, logger_);
                }
            }           
        }
    }
    
    /**
     * Searches for a class in the displayed targets
     */
    protected class SearchAction extends WorkspaceAction
    {
        private static final String SEARCH = "Search";
        private static final String CANCEL = "Cancel";
        
        private Thread searchThread_;
        private String search_;

        /**
         * Creates action that will run async
         */        
        public SearchAction()
        {
            super(SEARCH, true, null, statusBar_);
            putValue(MNEMONIC_KEY, new Integer('S'));    
            putValue(SHORT_DESCRIPTION, "Searches for a class");
        }
        
        /**
         * Branch on whether we're searching of canceling an existing search
         */
        public void runAction(ActionEvent e) throws Exception
        {
            if (getValue(NAME).equals(SEARCH))
            {
                search_ = searchField_.getText().trim();
                
                if (StringUtil.isNullOrEmpty(search_))
                    statusBar_.setWarning("Enter class to search for");
                else
                {
                    statusBar_.setInfo("Searching...");
                    searchThread_ = Thread.currentThread();                
                    doSearch();
                }
            }
            else
            {
                statusBar_.setStatus("Canceling search...");
                findClass_.cancelSearch();
                
                if (searchThread_!= null)
                    ThreadUtil.join(searchThread_, 60000);
                    
                putValue(NAME, SEARCH);
                putValue(MNEMONIC_KEY, new Integer('S'));                    
                statusBar_.setStatus("Search canceled");
            }
        }
        
        /**
         * Do the search 
         * 
         * @throws RESyntaxException 
         * @throws IOException
         */
        public void doSearch() throws RESyntaxException, IOException 
        {
            logger_.debug("Searching for: " + search_);
            
            // Flip title
            putValue(NAME, CANCEL);
            putValue(MNEMONIC_KEY, new Integer('C'));
            
            // Empty results table
            resultTableModel_.setNumRows(0);
            resultCount_ = 0;
            
            // Clear out from previous runs
            findClass_.removeSearchTargets();
            findClass_.removeSearchListeners();
            findClass_.addSearchListener(new SearchListener());
            
            // Copy 1-1 from the seach list model to FindClass
            for (int i=0, n=searchListModel_.size(); i<n; i++) 
                findClass_.addSearchTarget(""+searchListModel_.elementAt(i));
            
            // Execute the search
            Object results[]  = 
                findClass_.findClass(search_, ignoreCaseCheckBox_.isSelected());             
            
            statusBar_.setStatus(results.length + " matches found.");
            
            putValue(NAME, SEARCH);
            putValue(MNEMONIC_KEY, new Integer('S'));
        }
    }
    
    /**
     * Clears all entries in the search list
     */
    protected class ClearTargetsAction extends AbstractAction
    {
        public ClearTargetsAction()
        {
            super("Clear");
            putValue(MNEMONIC_KEY, new Integer('C'));
            putValue(SHORT_DESCRIPTION, "Clears search targets");
        }
        
        public void actionPerformed(ActionEvent e)
        {
            searchListModel_.removeAllElements();
        }
    }
    
    /**
     * Adds the current classpath to the search list
     */
    protected class AddClasspathTargetAction extends AbstractAction
    {
        public AddClasspathTargetAction()
        {
            super("Add Classpath");
            putValue(MNEMONIC_KEY, new Integer('A'));
            putValue(SHORT_DESCRIPTION, 
                "Adds the current classpath to the search list");
        }
        
        public void actionPerformed(ActionEvent e)
        {
            List targets = findClass_.getClassPathTargets();
            
            Iterator i = targets.iterator();
            while (i.hasNext())
            {
                String target = (String) i.next();
                searchListModel_.addElement(target);
            }
        }
    }
}