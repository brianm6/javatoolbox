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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import nu.xom.Attribute;
import nu.xom.Element;

import org.apache.log4j.Logger;
import org.apache.regexp.RESyntaxException;

import toolbox.util.ClassUtil;
import toolbox.util.DateTimeUtil;
import toolbox.util.FileUtil;
import toolbox.util.MathUtil;
import toolbox.util.StringUtil;
import toolbox.util.ThreadUtil;
import toolbox.util.XOMUtil;
import toolbox.util.ui.JFileExplorer;
import toolbox.util.ui.JFileExplorerAdapter;
import toolbox.util.ui.JPopupListener;
import toolbox.util.ui.JSmartButton;
import toolbox.util.ui.JSmartCheckBox;
import toolbox.util.ui.JSmartLabel;
import toolbox.util.ui.JSmartMenuItem;
import toolbox.util.ui.JSmartPopupMenu;
import toolbox.util.ui.JSmartTextField;
import toolbox.util.ui.flippane.JFlipPane;
import toolbox.util.ui.list.JSmartList;
import toolbox.util.ui.table.JSmartTable;
import toolbox.util.ui.table.JSmartTableHeader;
import toolbox.util.ui.table.SmartTableCellRenderer;
import toolbox.util.ui.table.TableSorter;
import toolbox.workspace.IPreferenced;
import toolbox.workspace.IStatusBar;
import toolbox.workspace.PluginWorkspace;
import toolbox.workspace.WorkspaceAction;

/**
 * UI for finding class files by regular expression from the classpath or any 
 * arbitrary java archive or directory.
 */
public class JFindClass extends JFrame implements IPreferenced
{
    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------
    	
    private static final Logger logger_ = 
        Logger.getLogger(JFindClass.class);

    // XML stuff for preferences
    private static final String NODE_JFINDCLASS_PLUGIN = "JFindClassPlugin";
    private static final String   ATTR_IGNORECASE      = "ignorecase";
    private static final String   ATTR_SEARCH          = "search";
    private static final String   ATTR_SHOWPATH        = "showpath";
    private static final String   ATTR_HILITE_MATCHES  = "highlightmatches";
    private static final String NODE_TOP_FLIPPANE      = "TopFlipPane";
    private static final String NODE_LEFT_FLIPPANE     = "LeftFlipPane";

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    // Search
    
    /**
     * Allows for input of the search text.
     */
    private JTextField searchField_;
    
    /**
     * Executes the search.
     */
    private JButton searchButton_;
    
    /**
     * Executes a search for duplicate classes in the list of targets.
     */
    private JButton dupesButton_;
    
    /**
     * Allows user to toggle case sensetivity in the search.
     */
    private JCheckBox ignoreCaseCheckBox_;
    
    /**
     * Allows user to toggle the display of the path (thus shortening) the 
     * contents of the 'Source' column.
     */
    private JCheckBox showPathCheckBox_;
    
    /**
     * Allows user to toggle partial string hiliting of the search string
     * within each search result.
     */
    private JCheckBox hiliteMatchesCheckBox_;
   
    // Left flip pane
    
    /**
     * Flipper that houses the file explorer.
     */
    private JFlipPane leftFlipPane_;
    
    /**
     * File explorer used to add additional jar/directory search targets.
     */
    private JFileExplorer fileExplorer_;
    
    // Top flip pane
    
    /**
     * Flipper that houses the search targets panel and the decompiler panel. 
     */
    private JFlipPane topFlipPane_;
    
    /**
     * List containing the jars/directories that are included in the search.
     */
    private JList searchList_;
    
    /**
     * Data model for the list of search targets.
     */
    private DefaultListModel searchListModel_;
    
    /**
     * Popup menu activated by right clicking on the search target list. 
     * Allows the user to clear the list or remove individual entries.
     */
    private JPopupMenu searchPopupMenu_;

    // Results
    
    /**
     * Table that displays the results of the search.
     */
    private JTable resultTable_;
    
    /**
     * Data model for the list of search results.
     */
    private ResultsTableModel resultTableModel_;
    
    /**
     * Enables sorting in the search results table by clicking on the table
     * column header.
     */
    private TableSorter resultTableSorter_;
    
    /**
     * Scroller for the results.
     */
    private JScrollPane resultPane_;
    
    /**
     * Non-UI component used to do the actual grunt work of the search.
     */
    private FindClass findClass_;

    /**
     * Status bar shared with the workspace.
     */
    private IStatusBar statusBar_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a JFindClass.
     */
    public JFindClass()
    {
        super("JFindClass");
    }

    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------
 
    /**
     * Initiailizes the GUI.
     * 
     * @param params Map of initialization objects
     */
    protected void init(Map params)
    {
        if (params != null)
            statusBar_ = (IStatusBar)params.get(PluginWorkspace.PROP_STATUSBAR);
            
        buildView();
        findClass_ = new FindClass();
        //findClass_.addSearchListener(new SearchListener());        
        List targets = findClass_.getSearchTargets();
        
        for (Iterator i = targets.iterator(); i.hasNext(); 
            searchListModel_.addElement(i.next()));
    }

    
    /**
     * Builds the GUI and adds it to the contentPane.
     */
    protected void buildView()
    {
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        // Search bar - north
        //buildSearchPanel();
        
        // Top flip pane (classpath & decompiler) - center - north
        buildTopFlipPane();
        
        // SearchResults - center - center
        buildSearchResultsPanel();
        
        // Left flip pane with file explorer - west
        buildLeftFlipPane();

        // Post tweaks
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    
    /**
     * Builds the search panel at the top of the GUI with the ignore case box.
     * Allows the user to enter a regular expression into the search field
     * and initiate a search.
     */    
    protected JPanel buildSearchPanel()
    {
        // Search Panel
        
        // Action is stared by search textfield and button
        Action searchAction = new SearchAction();

        JLabel searchLabel = new JSmartLabel("Find Class");
        
        searchField_ = new JSmartTextField(20);
        searchField_.setAction(searchAction);
        searchField_.setToolTipText("I like Regular expressions!");
        
        searchButton_          = new JSmartButton(searchAction);
        dupesButton_           = new JSmartButton(new FindDupesAction());
        ignoreCaseCheckBox_    = new JSmartCheckBox("Ignore Case", true);
        showPathCheckBox_      = new JSmartCheckBox("Show Path", true);
        hiliteMatchesCheckBox_ = new JSmartCheckBox("Highlight Match", true);
        
        JPanel searchPanel = new JPanel(new FlowLayout());
        searchPanel.add(searchLabel);
        searchPanel.add(searchField_); 
        searchPanel.add(searchButton_);
        searchPanel.add(dupesButton_);
        searchPanel.add(new JSmartLabel("      "));
        searchPanel.add(ignoreCaseCheckBox_);
        searchPanel.add(showPathCheckBox_);
        searchPanel.add(hiliteMatchesCheckBox_);
     
        // Make sure table repaints if hilite/show path is toggled
        Action repaintAction = new TableRepaintAction();
        showPathCheckBox_.addActionListener(repaintAction);
        hiliteMatchesCheckBox_.addActionListener(repaintAction);
        
        return searchPanel;
        //getContentPane().add(searchPanel, BorderLayout.NORTH);
    }

    
    /**
     * Builds the Classpath panel which shows all paths/archives that have been
     * targeted for the current search.
     * 
     * @return Classpath Panel
     */
    protected JPanel buildClasspathFlipper()
    {
        searchListModel_ = new DefaultListModel(); 
        searchList_ = new JSmartList(searchListModel_);
        
        // Create popup menu and wire it to the JList
        searchPopupMenu_ = new JSmartPopupMenu();
        searchPopupMenu_.add(new JSmartMenuItem(new ClearTargetsAction()));
        searchPopupMenu_.add(new JSmartMenuItem(new RemoveTargetsAction()));
        searchPopupMenu_.add(new JSmartMenuItem(new AddClasspathTargetAction()));
                    
        searchList_.addMouseListener(new JPopupListener(searchPopupMenu_));
        
        JPanel pathPanel = new JPanel(new BorderLayout());
        pathPanel.add(new JScrollPane(searchList_), BorderLayout.CENTER);
        pathPanel.setPreferredSize(new Dimension(100, 400));
        return pathPanel;
    }

    
    /**
     * Builds the flippane at the top of the application which contains the
     * Classpath and Decompiler panels.
     * 
     * @return Top flip pane
     */
    protected JPanel buildTopFlipPane()
    {
        JPanel pathPanel = buildClasspathFlipper();        
        JPanel decompilerPanel = new DecompilerPanel(resultTable_);
                
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
     * and have the resulting source code appear in the Decompiler panel.
     */
    protected void buildSearchResultsPanel()
    {
        // Search Results panel        
        JLabel resultLabel = new JSmartLabel("Results");
        
        // Setup sortable table
        resultTableModel_  = new ResultsTableModel();
        resultTableSorter_ = new TableSorter(resultTableModel_);
        resultTable_       = new JSmartTable(resultTableSorter_);
        resultPane_        = new JScrollPane(resultTable_);
        resultTableSorter_.addMouseListenerToHeaderInTable(resultTable_);
        tweakTable();
        
        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.add(resultLabel, BorderLayout.NORTH);
        resultPanel.add(resultPane_, BorderLayout.CENTER);
        
        JPanel glue = new JPanel(new BorderLayout());
        glue.add(BorderLayout.NORTH, buildSearchPanel());
        glue.add(BorderLayout.SOUTH, buildTopFlipPane());

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(BorderLayout.NORTH, glue);
        centerPanel.add(BorderLayout.CENTER, resultPanel);
        
        getContentPane().add(BorderLayout.CENTER, centerPanel);
    }

    
    /**
     * Builds the left flip pane which contains the file explorer. The file
     * explorer can be used to add additional search targets to the Classpath
     * panel.
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
     * Tweaks the table columns for width and extents.
     */
    protected void tweakTable()
    {
        resultTable_.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        
        resultTable_.setTableHeader(
            new JSmartTableHeader(resultTable_.getColumnModel()));
            
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
        for (int i=0; i < resultTableModel_.getColumnCount(); i++)
            resultTable_.setDefaultRenderer(resultTable_.getColumnClass(i), 
                new ResultsTableCellRenderer());
    }

    //--------------------------------------------------------------------------
    // IPreferenced Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.workspace.IPreferenced#applyPrefs(nu.xom.Element)
     */
    public void applyPrefs(Element prefs) throws Exception
    {
        Element root = 
            XOMUtil.getFirstChildElement(prefs, 
                NODE_JFINDCLASS_PLUGIN, new Element(NODE_JFINDCLASS_PLUGIN));
            
        ignoreCaseCheckBox_.setSelected(
            XOMUtil.getBooleanAttribute(root, ATTR_IGNORECASE, true));
            
        showPathCheckBox_.setSelected(
            XOMUtil.getBooleanAttribute(root, ATTR_SHOWPATH, true));

        hiliteMatchesCheckBox_.setSelected(
            XOMUtil.getBooleanAttribute(root, ATTR_HILITE_MATCHES, false));
            
        searchField_.setText(
            XOMUtil.getStringAttribute(root, ATTR_SEARCH, ""));
        
        leftFlipPane_.applyPrefs(
            XOMUtil.getFirstChildElement(
                root, NODE_LEFT_FLIPPANE, new Element(NODE_LEFT_FLIPPANE)));
                
        topFlipPane_.applyPrefs(
            XOMUtil.getFirstChildElement(
                root, NODE_TOP_FLIPPANE, new Element(NODE_TOP_FLIPPANE)));
            
        fileExplorer_.applyPrefs(root);

		// TODO: Integrate preferences for DecompilerPanel
    }
    
    
    /**
     * @see toolbox.workspace.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs)
    {
        Element root = new Element(NODE_JFINDCLASS_PLUGIN);
        
        fileExplorer_.savePrefs(root);

		// TODO: Integrate preferences for DecompilerPanel
        
        Element topFlipPane = new Element(NODE_TOP_FLIPPANE);
        topFlipPane_.savePrefs(topFlipPane);
        root.appendChild(topFlipPane);

        Element leftFlipPane = new Element(NODE_LEFT_FLIPPANE);
        leftFlipPane_.savePrefs(leftFlipPane);
        root.appendChild(leftFlipPane);
        
        root.addAttribute(new Attribute(
            ATTR_IGNORECASE, ignoreCaseCheckBox_.isSelected()+""));

        root.addAttribute(new Attribute(
            ATTR_SHOWPATH, showPathCheckBox_.isSelected()+""));

        root.addAttribute(new Attribute(
            ATTR_HILITE_MATCHES, hiliteMatchesCheckBox_.isSelected()+""));

        root.addAttribute(new Attribute(
            ATTR_SEARCH, searchField_.getText().trim()));
            
        XOMUtil.insertOrReplace(prefs, root);
    }

    //--------------------------------------------------------------------------
    // SearchListener
    //--------------------------------------------------------------------------

    /**
     * Listens for events generated by the search process and updates the
     * GUI accordingly. 
     */
    class SearchListener extends FindClassAdapter
    {
        //----------------------------------------------------------------------
        // Overrides toolbox.findclass.FindClassAdapter
        //----------------------------------------------------------------------
            
        /**
         * When a class is found, add it to the result table.
         * 
         * @param searchResult Info on class that was found
         * @see toolbox.findclass.IFindClassListener#classFound(
         *      toolbox.findclass.FindClassResult)
         */
        public void classFound(FindClassResult searchResult)
        {
            resultTableModel_.addResult(searchResult);
        }
        
        
        /**
         * When a target is searched, update the status bar and hilight the
         * archive being search in the search list.
         * 
         * @param target Target that is being searched
         * @see toolbox.findclass.IFindClassListener#searchingTarget(
         *      java.lang.String)
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
         * When a search is cancelled, update the status bar.
         * 
         * @see toolbox.findclass.IFindClassListener#searchCancelled()
         */
        public void searchCancelled()
        {
            statusBar_.setStatus("Search cancelled");
            resultTableSorter_.setEnabled(true);
        }
        
        
        /**
         * @see toolbox.findclass.IFindClassListener#searchCompleted(
         *      java.lang.String)
         */
        public void searchCompleted(String search)
        {
            logger_.debug("Search completed.");
            
            // TODO: Hack alert. Figure out way to know when last row has been
            //       added to the table.
            
            
            SwingUtilities.invokeLater(new Runnable()
            {
                public void run()
                {
                    //resultTableSorter_.setEnabled(true);
                    statusBar_.setStatus(
                        resultTableModel_.getRowCount() + " matches found.");
                }
            });
            
        }
    }

    //--------------------------------------------------------------------------
    // FindDupesListener
    //--------------------------------------------------------------------------
    
    /**
     * Listener for classes found by find duplicates process. This class 
     * keeps track of which classes are found more than once and keeps a 
     * running tally in addition to adding it to the search result table.  
     */
    class FindDupesListener extends FindClassAdapter
    {
        /** 
         * Map of duplicates. The key is the class name and the value is an
         * List containing the FindClassResult's found for that class.
         */
        private Map dupes_;
        
        public FindDupesListener()
        {
            dupes_ = new HashMap();
        }
        
        protected void addResults(FindClassResult result)
        {        
            resultTableModel_.addResult(result);
        }

        protected void runFinally()
        {
            SwingUtilities.invokeLater(new Runnable()
            {
                public void run()
                {
                    resultTableSorter_.setEnabled(true);
                    statusBar_.setStatus(
                        resultTableModel_.getRowCount() +" duplicates  found.");
                }
            });
        }
                
        //----------------------------------------------------------------------
        // Overrides toolbox.findclass.FindClassAdapter
        //----------------------------------------------------------------------
            
        /**
         * When a class is found, add it to the result table.
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
            statusBar_.setStatus("Searching " + target + " ...");
            searchList_.setSelectedValue(target, true);    
        }
        
        
        /**
         * When a search is cancelled, update the status bar.
         */
        public void searchCancelled()
        {
            statusBar_.setStatus("Search canceled");
            runFinally();
        }
        
        
        /**
         * @see toolbox.findclass.IFindClassListener#searchCompleted(
         *      java.lang.String)
         */
        public void searchCompleted(String search)
        {
            logger_.debug("Search completed.");
            runFinally();
        }
    }
    
    //--------------------------------------------------------------------------
    // FileExplorerListener
    //--------------------------------------------------------------------------
    
    /**
     * Handler class for the file explorer.
     */
    class FileExplorerListener extends JFileExplorerAdapter
    {
        //----------------------------------------------------------------------
        // Overrides toolbox.util.ui.explorer.JFileExplorerAdapter
        //----------------------------------------------------------------------
        
        /**
         * Adds a directory to the path list.
         * 
         * @param folder Directory to add
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
         * Adds a file to the path list.
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
 
    //--------------------------------------------------------------------------
    // ResultsTableCellRenderer
    //--------------------------------------------------------------------------
        
    /**
     * Renderer for the contents of the Results table.
     */   
    class ResultsTableCellRenderer extends SmartTableCellRenderer
    {
        private DecimalFormat decimalFormatter_;
        
        public ResultsTableCellRenderer()
        {
            decimalFormatter_ = new DecimalFormat("###,###");    
        }
        
        //----------------------------------------------------------------------
        // Overrides javax.swing.table.DefaultTableCellRenderer
        //----------------------------------------------------------------------
        
        /**
         * Returns the default table cell renderer.
         *
         * @param table JTable
         * @param value Value to assign to the cell at [row, column]
         * @param isSelected True if the cell is selected
         * @param hasFocus True if cell has focus
         * @param row Row of the cell to render
         * @param column Column of the cell to render
         * @return Default table cell renderer
         */
        public Component getTableCellRendererComponent(
            JTable  table,
            Object  value,
            boolean isSelected,
            boolean hasFocus,
            int     row,
            int     column)
        {
            String text = value.toString();
            
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

            switch (column)
            {
                case ResultsTableModel.COL_NUM:
                    setHorizontalAlignment(SwingConstants.CENTER);
                    setValue(text);
                    break;
                    
                case ResultsTableModel.COL_SIZE:
                    setHorizontalAlignment(SwingConstants.CENTER);
                    setValue(decimalFormatter_.format(value));
                    break;
                    
                case ResultsTableModel.COL_TIMESTAMP:
                    setValue(DateTimeUtil.format((Date)value));                    
                    break;
                    
                case ResultsTableModel.COL_SOURCE:
                    if (!showPathCheckBox_.isSelected())
                        setValue(FileUtil.stripPath(text));
                    else
                        setValue(text);
                    break;

                case ResultsTableModel.COL_CLASS:
                
                    // Hilight search string match in the entire classes' FQCN                
                    if (hiliteMatchesCheckBox_.isSelected())
                    {
                        // Apparently, the number value has to pulled from the 
                        // sorted model instead of the base model while 
                        // rendering the cells.
                
                        String number = resultTableSorter_.getValueAt(row,0)+"";
                        
                        FindClassResult result = 
                            resultTableModel_.getResult(number);
                        
                        int start = result.getMatchBegin();
                        int end = result.getMatchEnd();
                        
                        StringBuffer sb = new StringBuffer();
                        sb.append("<html>");
                        sb.append(text.substring(0, start));
                        sb.append("<font color=#cc0000>");
                        sb.append(text.substring(start, end));
                        sb.append("</font>");
                        sb.append(text.substring(end));
                        sb.append("</html>");
                            
                        setValue(sb);
                    }
                    else
                        setValue(text);
                        
                    break;
                    
                default:
                    setValue(value);
            }
            
            return this;
        }
    }
    
    //--------------------------------------------------------------------------
    // FindDupesAction
    //--------------------------------------------------------------------------

    /**
     * Searches for duplicate classes.
     */
    class FindDupesAction extends WorkspaceAction
    {
        FindDupesAction()
        {
            super("Find Duplicates", true, null, statusBar_);
            putValue(MNEMONIC_KEY, new Integer('F'));    
            putValue(SHORT_DESCRIPTION, "Searches for duplicates classes");
        }
        
        
        /**
         * @see toolbox.util.ui.SmartAction#runAction(
         *      java.awt.event.ActionEvent)
         */
        public void runAction(ActionEvent e) throws Exception
        {
            statusBar_.setInfo("Searching...");
            findDupes();
        }
        
        
        void findDupes() throws RESyntaxException, IOException 
        {
            logger_.debug("Finding duplicates");
            
            // Empty results table
            resultTableModel_.clear();
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

    
    //--------------------------------------------------------------------------
    // SearchAction
    //--------------------------------------------------------------------------
    
    /**
     * Kicks off the search for a class matching the search criteria.
     */
    class SearchAction extends WorkspaceAction
    {
        private static final String SEARCH = "Search";
        private static final String CANCEL = "Cancel";
        
        private Thread searchThread_;
        private String search_;

        SearchAction()
        {
            super(SEARCH, true, null, statusBar_);
            putValue(MNEMONIC_KEY, new Integer('S'));    
            putValue(SHORT_DESCRIPTION, "Searches for a class");
        }

        
        /**
         * Branch on whether we're searching of canceling an existing search.
         */
        public void runAction(ActionEvent e) throws Exception
        {
            if (getValue(NAME).equals(SEARCH))
            {
                search_ = searchField_.getText().trim();
                
                if (StringUtil.isNullOrBlank(search_))
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
         * Do the search. 
         * 
         * @throws RESyntaxException on regular expression error 
         * @throws IOException on I/O error
         */
        void doSearch() throws RESyntaxException, IOException 
        {
            logger_.debug("Searching for: " + search_);
            
            // Flip title
            putValue(NAME, CANCEL);
            putValue(MNEMONIC_KEY, new Integer('C'));
            
            // Empty results table
            resultTableModel_.clear();
            resultTableSorter_.setEnabled(false);
            
            // Clear out from previous runs
            findClass_.removeSearchTargets();
            findClass_.removeSearchListeners();
            findClass_.addSearchListener(new SearchListener());
            
            // Copy 1-1 from the seach list model to FindClass
            for (int i=0, n=searchListModel_.size(); i<n; i++) 
                findClass_.addSearchTarget(""+searchListModel_.elementAt(i));
            
            // Execute the search
            findClass_.findClass(search_, ignoreCaseCheckBox_.isSelected());             
            
            //statusBar_.setStatus(results.length + " matches found.");
            
            putValue(NAME, SEARCH);
            putValue(MNEMONIC_KEY, new Integer('S'));
        }
    }
    
    //--------------------------------------------------------------------------
    // ClearTargetsAction
    //--------------------------------------------------------------------------
    
    /**
     * Clears all entries in the search list.
     */
    class ClearTargetsAction extends AbstractAction
    {
        ClearTargetsAction()
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
    
    //--------------------------------------------------------------------------
    // AddClasspathTargetAction
    //--------------------------------------------------------------------------
    
    /**
     * Adds the current classpath to the search list.
     */
    class AddClasspathTargetAction extends AbstractAction
    {
        AddClasspathTargetAction()
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
    
    //--------------------------------------------------------------------------
    // RemoteTargetsAction
    //--------------------------------------------------------------------------
    
    /**
     * Removes selected target entries from the search list.
     */
    class RemoveTargetsAction extends AbstractAction
    {
        RemoveTargetsAction()
        {
            super("Remove");
            putValue(MNEMONIC_KEY, new Integer('R'));
            putValue(SHORT_DESCRIPTION, "Removes search targets");
        }
        
        
        public void actionPerformed(ActionEvent e)
        {
            Object[] selected = searchList_.getSelectedValues();
            
            for (int i=0; i<selected.length; i++)
                searchListModel_.removeElement(selected[i]);
        }
    }
    
    //--------------------------------------------------------------------------
    // TableRepaintAction
    //--------------------------------------------------------------------------
    
    /**
     * Forces results table to repaint itself.
     */
    class TableRepaintAction extends AbstractAction
    {
        public void actionPerformed(ActionEvent e)
        {
            resultTable_.repaint();
        }
    }
}