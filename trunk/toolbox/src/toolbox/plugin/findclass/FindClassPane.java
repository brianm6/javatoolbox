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
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java2html.Java2Html;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
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

import toolbox.util.ClassUtil;
import toolbox.util.DateTimeUtil;
import toolbox.util.MathUtil;
import toolbox.util.StringUtil;
import toolbox.util.SwingUtil;
import toolbox.util.ThreadUtil;
import toolbox.util.io.NullWriter;
import toolbox.util.ui.JFileExplorer;
import toolbox.util.ui.JFileExplorerAdapter;
import toolbox.util.ui.JFlipPane;
import toolbox.util.ui.JPopupListener;
import toolbox.util.ui.JSmartOptionPane;
import toolbox.util.ui.JStatusPane;
import toolbox.util.ui.ThreadSafeTableModel;

/**
 * GUI for FindClass
 */
public class JFindClass extends JFrame
{
    /** Logger */
    private static final Logger logger_ = 
        Logger.getLogger(JFindClass.class);

    // Search    
    private JTextField           searchField_;
    private JButton              searchButton_;
    private JCheckBox            ignoreCaseCheckBox_;

    // Left flip pane            
    private JFlipPane            leftFlipPane_;
    private JFileExplorer        fileExplorer_;
    
    // Top flip pane
    private JList                searchList_;
    private DefaultListModel     searchListModel_;
    private JPopupMenu           searchPopupMenu_;
    private JEditorPane          sourceArea_;
    
    // Results    
    private JTable               resultTable_;
    private ThreadSafeTableModel resultTableModel_;
    private JScrollPane          resultPane_;
    private int                  resultCount_;
    private FindClass            findClass_;

    // Status
    private JStatusPane          statusBar_;
    
    
    /** Result table columns */    
    private String[] resultColumns_ = new String[] 
    {
        "Num", 
        "Source", 
        "Class File",
        "Size", 
        "Timestamp"
    };

    //private static final int COL_NUM       = 0;
    private static final int COL_SOURCE    = 1;
    private static final int COL_CLASS     = 2;
    //private static final int COL_SIZE      = 3;
    //private static final int COL_TIMESTAMP = 4;
    
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
        jfc.setVisible(true);
    }

    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------

    /**
     * Constructor for JFindClass
     * 
     * @throws Exception on error
     */
    public JFindClass() throws Exception
    {
        this("JFindClass");
    }
    
    /**
     * Constructor for JFindClass
     * 
     * @param  title  Window title
     * @throws Exception on error
     */
    public JFindClass(String title) throws Exception
    {
        super(title);
        buildView();
        init();
        setSize(800,600);
        SwingUtil.centerWindow(this);
    }

    //--------------------------------------------------------------------------
    //  Private
    //--------------------------------------------------------------------------
 
    /**
     * Initiailizes the GUI
     */
    protected void init()
    {
        findClass_ = new FindClass();
        findClass_.addFindClassListener(new FindClassHandler());        
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

        // Status bar
        buildStatusBar();
        
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
        SearchCancelAction searchAction = new SearchCancelAction();

        JLabel searchLabel = new JLabel("Find Class");
        searchField_ = new JTextField(20);
        searchField_.addActionListener(searchAction);
        searchField_.setFont(SwingUtil.getPreferredMonoFont());
        searchField_.setToolTipText("I like Regular expressions!");
        searchButton_ = new JButton(searchAction);
        ignoreCaseCheckBox_ = new JCheckBox("Ignore Case", true);
        
        JPanel searchPanel = new JPanel(new FlowLayout());
        searchPanel.add(searchLabel);
        searchPanel.add(searchField_);
        searchPanel.add(searchButton_);
        searchPanel.add(new JLabel("      "));
        searchPanel.add(ignoreCaseCheckBox_);
        
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
        // Decpmpiler 
        JPanel decompilerPanel = new JPanel(new BorderLayout());
        sourceArea_ = new JEditorPane();
        sourceArea_.setContentType("text/html");
        sourceArea_.setFont(SwingUtil.getPreferredMonoFont());
        decompilerPanel.add(new JScrollPane(sourceArea_), BorderLayout.CENTER);
        JButton decompileButton = new JButton(new DecompileAction());
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
        searchList_.setFont(SwingUtil.getPreferredMonoFont());
        
        // Create popup menu and wire it to the JList
        searchPopupMenu_ = new JPopupMenu();
        searchPopupMenu_.add(new JMenuItem(new ClearSearchListAction()));
        
        searchPopupMenu_.add(
            new JMenuItem(new AddClasspathToSearchListAction()));
            
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
        JFlipPane topFlipPane = new JFlipPane(JFlipPane.TOP);
        topFlipPane.addFlipper("Classpath", pathPanel);
        topFlipPane.addFlipper("Decompiler", decompilerPanel);
        topFlipPane.setSelectedFlipper(pathPanel);
        return topFlipPane;
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
        
        resultTableModel_ = new ThreadSafeTableModel(resultColumns_,0);
        resultTable_      = new JTable(resultTableModel_);
        resultPane_       = new JScrollPane(resultTable_);
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
     * Builds the status bar 
     */
    protected void buildStatusBar()
    {
        // Status bar
        statusBar_ = new JStatusPane();
        statusBar_.setStatus("Enter a regular expression and hit Find!");
        getContentPane().add(statusBar_, BorderLayout.SOUTH);
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
        fileExplorer_.addJFileExplorerListener(new JFileExplorerHandler());
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
        resultTable_.setFont(SwingUtil.getPreferredSerifFont());
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
        
        resultTable_.setDefaultRenderer(resultTable_.getColumnClass(0) , 
            new AlternatingCellRenderer());

        resultTable_.setDefaultRenderer(resultTable_.getColumnClass(1) , 
            new AlternatingCellRenderer());

        resultTable_.setDefaultRenderer(resultTable_.getColumnClass(2) , 
            new AlternatingCellRenderer());

        resultTable_.setDefaultRenderer(resultTable_.getColumnClass(3) , 
            new AlternatingCellRenderer());
            
        resultTable_.setDefaultRenderer(resultTable_.getColumnClass(4) , 
            new AlternatingCellRenderer());
    }

    /**
     * Generic error handler for GUI exceptions
     * 
     * @param  t   Exception causing error
     * @param  c   Logger to log to
     */
    protected void handleException(Throwable t, Logger c)
    {
        c.error(t.getMessage(), t);
        JSmartOptionPane.showExceptionMessageDialog(this, t);
    }

    //--------------------------------------------------------------------------
    //  Inner Classes
    //--------------------------------------------------------------------------

    /**
     * Handler for events generated by JFileExplorer
     */
    class FindClassHandler extends FindClassAdapter
    {
        //----------------------------------------------------------------------
        // Overridden from FindClassAdapter
        //----------------------------------------------------------------------
            
        /**
         * When a class is found, add it to the result table
         * 
         * @param  searchResult   Info on class that was found
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
         * When a target is searched, update the status bar
         * 
         * @param  target   Target that is being searched
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
            statusBar_.setStatus("Search cancelled");
        }    
    }
    
    /**
     * Handler class for the file explorer
     */
    class JFileExplorerHandler extends JFileExplorerAdapter
    {
        private Logger logger_ = 
            Logger.getLogger(JFileExplorerHandler.class);
        
        //----------------------------------------------------------------------
        // Overridden from JFileExplorerAdapter
        //----------------------------------------------------------------------
        
        /**
         * Adds a directory to the path list
         * 
         * @param  folder  Directory to add
         */
        public void folderDoubleClicked(String folder)
        {
            List targets = findClass_.getArchivesInDir(new File(folder));
            
            logger_.info("Found " + targets.size() + " targets to add");
            
            Iterator i = targets.iterator();
            
            while (i.hasNext())
            {
                String target = (String) i.next();
                searchListModel_.addElement(target);
            }
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
        // Overridden from DefaultTableCellRenderer
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
                super.setForeground(table.getSelectionForeground());
                super.setBackground(table.getSelectionBackground());
            }
            else
            {
                super.setForeground(table.getForeground());

                // Alternate colors                
                if (MathUtil.isEven(row))
                    super.setBackground(table.getBackground());
                else
                    super.setBackground(new Color(240,240,240));
            }

            setFont(table.getFont());

            if (hasFocus)
            {
                setBorder(
                    UIManager.getBorder("Table.focusCellHighlightBorder"));
                    
                if (table.isCellEditable(row, column))
                {
                    super.setForeground(
                        UIManager.getColor("Table.focusCellForeground"));
                        
                    super.setBackground(
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
                        
                    System.out.println(javaSource);    
                        
                    StringReader javaReader = new StringReader(javaSource);
                    StringWriter htmlWriter = new StringWriter();
                    new Java2Html(javaReader, htmlWriter);
                    
                    sourceArea_.setText(htmlWriter.toString());
                    sourceArea_.setCaretPosition(0);
                }
                catch(IOException ioe)
                {
                    handleException(ioe, logger_);
                }

            }           
        }
        
    }
    
    /**
     * Searches for a class in the displayed classpaths
     */
    protected class SearchCancelAction extends AbstractAction
    {
        private static final String SEARCH = "Search";
        private static final String CANCEL = "Cancel";
        private Thread searchThread_;
        
        public SearchCancelAction()
        {
            super(SEARCH);
            putValue(MNEMONIC_KEY, new Integer('S'));    
            putValue(SHORT_DESCRIPTION, "Searches for a class");
        }
        
        public void actionPerformed(ActionEvent e)
        {
            if (getValue(NAME).equals(SEARCH))
            {
                searchThread_ = 
                    ThreadUtil.run(SearchCancelAction.this, "doSearch", null);
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
        
        public void doSearch()
        {
            String method = "[search] ";
            
            try
            {
                String search = searchField_.getText().trim();
                
                logger_.debug(method + "Searching for: " + search);
                
                if (StringUtil.isNullOrEmpty(search))
                {
                    statusBar_.setStatus("Enter class to search");
                }
                else
                {
                    // Flip title
                    putValue(NAME, CANCEL);
                    putValue(MNEMONIC_KEY, new Integer('C'));
                    
                    // Empty results table
                    resultTableModel_.setNumRows(0);
                    resultCount_ = 0;
                    
                    // Clear out from previous runs
                    findClass_.removeAllSearchTargets();
                    
                    // Copy 1-1 from the seach list model to FindClass
                    for (Enumeration e = searchListModel_.elements(); 
                         e.hasMoreElements(); )
                    {
                        findClass_.addSearchTarget((String)e.nextElement());
                    }
                    
                    // Execute the search
                    Object results[]  = findClass_.findClass(
                        search, ignoreCaseCheckBox_.isSelected());             
                    
                    statusBar_.setStatus(results.length + " matches found");
                    
                    putValue(NAME, SEARCH);
                    putValue(MNEMONIC_KEY, new Integer('S'));
                }
            }
            catch (Exception ex)
            {
                handleException(ex, logger_);
            }
        }
    }
    
    /**
     * Clears all entries in the search list
     */
    protected class ClearSearchListAction extends AbstractAction
    {
        public ClearSearchListAction()
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
    protected class AddClasspathToSearchListAction extends AbstractAction
    {
        public AddClasspathToSearchListAction()
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