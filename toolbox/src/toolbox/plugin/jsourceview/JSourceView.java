package toolbox.jsourceview;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.apache.log4j.Logger;

import nu.xom.Attribute;
import nu.xom.Element;

import toolbox.util.ArrayUtil;
import toolbox.util.ElapsedTime;
import toolbox.util.ExceptionUtil;
import toolbox.util.FileUtil;
import toolbox.util.MathUtil;
import toolbox.util.Queue;
import toolbox.util.XOMUtil;
import toolbox.util.io.filter.DirectoryFilter;
import toolbox.util.io.filter.ExtensionFilter;
import toolbox.util.io.filter.OrFilter;
import toolbox.util.ui.SmartAction;
import toolbox.util.ui.plugin.IPreferenced;
import toolbox.util.ui.plugin.IStatusBar;
import toolbox.util.ui.plugin.WorkspaceAction;
import toolbox.util.ui.table.SmartTableModel;
import toolbox.util.ui.table.TableSorter;

/**
 * JSourceView gathers statistics on one or more source files and presents
 * them in a table format for viewing.
 */
public class JSourceView extends JFrame implements ActionListener, IPreferenced
{
    // TODO: Update Queue to BlockingQueue
    // TODO: Figure out how to save table column sizes
    // TODO: Convert actionPerformed() to Actions
    // TODO: Add chart for visualization
    // TODO: Custom table cell renders to color code unusually high or low 
    //       numbers, etc
    // TODO: Add regex filter to include/exclude files

    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------
            
    private static final Logger logger_ = 
        Logger.getLogger(JSourceView.class);

	/**
	 * XML: Root preferences element
	 */
    private static final String NODE_JSOURCEVIEW_PLUGIN = "JSourceViewPlugin";
    
    /**
     * XML: Attribute of JSourceViewPlugin that stores the current directory
     */
    private static final String ATTR_LAST_DIR = "dir";
    
    private static final String LABEL_GO     = "Go!";
    private static final String LABEL_CANCEL = "Cancel";

    private static final int COL_NUM        = 0;
    private static final int COL_DIR        = 1;
    private static final int COL_FILE       = 2;
    private static final int COL_CODE       = 3;
    private static final int COL_COMMENTS   = 4;
    private static final int COL_BLANK      = 5;
    private static final int COL_THROWN_OUT = 6;
    private static final int COL_TOTAL      = 7;
    private static final int COL_PERCENTAGE = 8;

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    private JTextField  dirField_;
    private JButton     goButton_;
    private JButton     pickDirButton_;
    private JLabel      scanStatusLabel_;
    private JLabel      parseStatusLabel_;
    
    private JMenuBar    menuBar_;
    private JMenuItem   saveMenuItem_;
    private JMenuItem   aboutMenuItem_;
    
    private JTable          table_;
    private SmartTableModel tableModel_;
    private TableSorter     tableSorter_;
    private Queue           workQueue_;
    
    private Thread        scanDirThread_;
    private ScanDirWorker scanDirWorker_;
    private Thread        parserThread_;
    private ParserWorker  parserWorker_;

    /** 
     * Workspace status bar (in addition to the two we're already got) 
     */
    private IStatusBar workspaceStatusBar_;
    
    /** 
     * Platform path separator 
     */
    private String pathSeparator_;

    /** 
     * Table column names 
     */    
    private String colNames_[] = 
    {
        "Num",
        "Directory", 
        "File", 
        "Code", 
        "Comments", 
        "Blank",
        "Thrown Out", 
        "Total", 
        "Percentage"
    };
    
    /** 
     * Filter to identify source files 
     */
    private static OrFilter sourceFilter_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a JSourceView
     */    
    public JSourceView()
    {
        super("JSourceView");
        buildView();
    }

	//--------------------------------------------------------------------------
	// Public 
	//--------------------------------------------------------------------------
    
	/**
	 * Sets the text of the scan status
	 * 
	 * @param  status  Status of the scan activity
	 */
	public void setScanStatus(String status)
	{
		scanStatusLabel_.setText(status);
	}

	/**
	 * Sets the text of the parse status
	 * 
	 * @param  status  Status of the parse activity
	 */
	public void setParseStatus(String status)
	{
		parseStatusLabel_.setText(status);
	}

    /**
     * Workspace status bar!
     * 
     * @param statusBar  Workspace statusbar
     */
    public void setStatusBar(IStatusBar statusBar)
    {
        workspaceStatusBar_ = statusBar;
        
        // The action depends on havign a reference to the workspace status
        // bar (not available yet in buildView()).
        goButton_.setAction(new SearchAction());
        dirField_.setAction(new SearchAction());
    }

    //--------------------------------------------------------------------------
    // ActionListener Interface
    //--------------------------------------------------------------------------

    /**
     * Handles actions from the GUI
     *
     * @param  actionevent  Action to handle
     */
    public void actionPerformed(ActionEvent actionevent)
    {
        Object obj = actionevent.getSource();
        
        try
        {
            if (obj == saveMenuItem_)
                saveResults();
            else if (obj == aboutMenuItem_)
                showAbout();
        }
        catch (Exception e)
        {
            ExceptionUtil.handleUI(e, logger_);
        }
    }

	//--------------------------------------------------------------------------
	// IPreferenced Interface
	//--------------------------------------------------------------------------

    /**
     * @see toolbox.util.ui.plugin.IPreferenced#applyPrefs(nu.xom.Element)
     */
    public void applyPrefs(Element prefs) throws Exception
    {
        Element root = prefs.getFirstChildElement(NODE_JSOURCEVIEW_PLUGIN);
        
        dirField_.setText(XOMUtil.getStringAttribute(root, ATTR_LAST_DIR, ""));
        dirField_.setCaretPosition(0);
    }

    /**
     * @see toolbox.util.ui.plugin.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs)
    {
        Element root = new Element(NODE_JSOURCEVIEW_PLUGIN);
        
        root.addAttribute(
            new Attribute(ATTR_LAST_DIR, dirField_.getText().trim()));
            
        XOMUtil.injectChild(prefs, root);
    }


    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------
    
    /**
     * Builds the GUI
     */
    protected void buildView()
    {
        dirField_ = new JTextField(25);
        goButton_ = new JButton();
        pickDirButton_ = new JButton(new PickDirectoryAction());
        
        JPanel topPanel = new JPanel();
        scanStatusLabel_ = new JLabel(" ");
        parseStatusLabel_ = new JLabel(" ");
        menuBar_ = new JMenuBar();
        pathSeparator_ = System.getProperty("file.separator");
        
        topPanel.setLayout(new FlowLayout());
        topPanel.add(new JLabel("Directory"));
        
        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        
        Dimension d = new Dimension(12, dirField_.getPreferredSize().height);
        pickDirButton_.setPreferredSize(d);
        
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 9;
        JPanel p = new JPanel(gbl);
        p.add(dirField_, gbc);
        gbc.gridx = 2;
        gbc.weightx = 1;
        p.add(pickDirButton_, gbc);
        topPanel.add(p);
        topPanel.add(goButton_);

        // Setup sortable table
        tableModel_  = new SmartTableModel(colNames_, 0);
        tableSorter_ = new TableSorter(tableModel_);
        table_       = new JTable(tableSorter_);
        tableSorter_.addMouseListenerToHeaderInTable(table_);
        
        // Set alternating row renderer
        table_.setDefaultRenderer(Integer.class, new TableCellRenderer());
        table_.setDefaultRenderer(String.class, new TableCellRenderer());
        
        tweakTable();
        
        getContentPane().add(topPanel, BorderLayout.NORTH);
        getContentPane().add(new JScrollPane(table_), BorderLayout.CENTER);
        
        JPanel jpanel = new JPanel();
        jpanel.setLayout(new BorderLayout());
        jpanel.add(scanStatusLabel_, BorderLayout.NORTH);
        jpanel.add(parseStatusLabel_, BorderLayout.SOUTH);
        getContentPane().add(jpanel, BorderLayout.SOUTH);
        
        setJMenuBar(createMenuBar());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        sourceFilter_ = new OrFilter();
        sourceFilter_.addFilter(new ExtensionFilter("c"));
        sourceFilter_.addFilter(new ExtensionFilter("cpp"));
        sourceFilter_.addFilter(new ExtensionFilter("java"));
        sourceFilter_.addFilter(new ExtensionFilter("h"));
    }
    
    /**
     * Tweaks the table columns for width and extents
     */
    protected void tweakTable()
    {
        table_.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        TableColumnModel columnModel = table_.getColumnModel();

        // Tweak file number column
        TableColumn column = columnModel.getColumn(COL_NUM);
        column.setMinWidth(50);
        column.setPreferredWidth(50);        
        column.setMaxWidth(100); 
        
        column = columnModel.getColumn(COL_CODE);
        column.setMinWidth(50);
        column.setPreferredWidth(50);
        column.setMaxWidth(130);
    
        int min  = 50;
        int pref = 70;
        int max  = 150;
    
        column = columnModel.getColumn(COL_BLANK);
        column.setMinWidth(min);
        column.setPreferredWidth(pref);
        column.setMaxWidth(max);

        column = columnModel.getColumn(COL_COMMENTS);
        column.setMinWidth(min);
        column.setPreferredWidth(pref);
        column.setMaxWidth(max);
        
        column = columnModel.getColumn(COL_THROWN_OUT);
        column.setMinWidth(min);
        column.setPreferredWidth(pref);
        column.setMaxWidth(max);
        
        column = columnModel.getColumn(COL_TOTAL);
        column.setMinWidth(min);
        column.setPreferredWidth(pref);
        column.setMaxWidth(max);
        
        column = columnModel.getColumn(COL_PERCENTAGE);
        column.setMinWidth(min);
        column.setPreferredWidth(pref);
        column.setMaxWidth(max);
    }
    
    /**
     * Returns the menubar
     * 
     * @return  Menubar
     */
    protected JMenuBar createMenuBar()
    {
        JMenu jmenu = new JMenu("File");
        JMenu jmenu1 = new JMenu("Help");
        
        saveMenuItem_ = new JMenuItem("Save");
        saveMenuItem_.addActionListener(this);
        
        jmenu.add(saveMenuItem_);
        jmenu.addSeparator();
        
        aboutMenuItem_ = new JMenuItem("About");
        aboutMenuItem_.addActionListener(this);
        
        jmenu1.add(aboutMenuItem_);
        
        menuBar_.add(jmenu);
        menuBar_.add(jmenu1);
        
        return menuBar_;
    }

    /**
     * Saves the results to a file
     * 
     * @throws IOException on error
     */
    protected void saveResults() throws IOException
    {
        String s = JOptionPane.showInputDialog("Save to file");
        
        if (s.length() > 0)
            tableModel_.saveToFile(s);
    }
    
    /**
     * Shows About dialog box
     */
    protected void showAbout()
    {
        JOptionPane.showMessageDialog(null, 
            "E-mail: analogue@yahoo.com\n" + 
            "Webpage: http://members.tripod.com/analogue73\n" + 
            "Usage: Just enter the starting directory and hit Go button.\n" + 
            "Program will recurse through all subdirs and count lines\n" + 
            "in all .java, .cpp, .c, and .h files.\n\n" +
            "Comments/bugs/etc appreciated.\n\n" + 
            "Disclaimer: This thing was hacked together over a few hours. " +
            "Use at your own risk.", 
            "About JSourceView", 1);
    }
    
    //--------------------------------------------------------------------------
    // Actions
    //--------------------------------------------------------------------------

    /**
     * Action that triggers the search/scanning/parsing process to produce
     * source code statistics.
     */
    class SearchAction extends WorkspaceAction
    {
        SearchAction()
        {
            super(LABEL_GO, true, null, workspaceStatusBar_);
        }

        public void runAction(ActionEvent e) throws Exception
        {
            if (goButton_.getText().equals(LABEL_GO))
            {
                goButton_.setText(LABEL_CANCEL);
                String dir = dirField_.getText();
                workQueue_ = new Queue();
                tableModel_.setRowCount(0);
            
                // To avoid a whole mess of sorting going on while the table is
                // being populated, just disable the sorter temporarily. This is 
                // turned back on when the parser thread completes
                tableSorter_.setEnabled(false);
            
                scanDirWorker_ = new ScanDirWorker(new File(dir));
                scanDirThread_ = new Thread(scanDirWorker_);
                scanDirThread_.start();
            
                parserWorker_  = new ParserWorker();
                parserThread_  = new Thread(parserWorker_);
                parserThread_.start();
                
                if (scanDirThread_ != null && scanDirThread_.isAlive())
                    scanDirThread_.join();
                
                if (parserThread_ != null && parserThread_.isAlive())    
                    parserThread_.join();
            }
            else
            {
                goButton_.setText(LABEL_GO);
                scanDirWorker_.cancel();
                parserWorker_.cancel();
            
                try
                {
                    scanDirThread_.join();
                    parserThread_.join();
                }
                catch (InterruptedException ie)
                {
                    ; // Ignore
                }
            
                setScanStatus("Operation canceled");
                setParseStatus("");
            }
        }
    }

    /**
     * Allows user to pick a source directory through the file chooser instead 
     * of typing one in.
     */
    class PickDirectoryAction extends SmartAction
    {
        PickDirectoryAction()
        {
            super("...", true, false, null);
        }
        
        public void runAction(ActionEvent e) throws Exception
        {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            
            if (chooser.showDialog(getContentPane(), "Select Directory") == 
                JFileChooser.APPROVE_OPTION)
                dirField_.setText(chooser.getSelectedFile().getCanonicalPath());
        }
    }

    //--------------------------------------------------------------------------
    //  ScanDirWorker Inner Class
    //--------------------------------------------------------------------------
    
    /** 
     * Scans file system recursively for files containing source code.
     */
    class ScanDirWorker implements Runnable
    {
        /** 
         * Directory to scan recursively for source files 
         */
        private File dir_;

        /** 
         * Cancel flag 
         */
        private boolean cancel_;
        
        /** 
         * Filter for list on directories 
         */
        private FilenameFilter dirFilter_;
        
        //----------------------------------------------------------------------
        // Constructors
        //----------------------------------------------------------------------
        
        /**
         * Creates a scanner
         * 
         * @param  dir  Directory root to scan
         */
        ScanDirWorker(File dir)
        {
            dir_       = dir;
            dirFilter_ = new DirectoryFilter();
            cancel_    = false;
        }
        
        //----------------------------------------------------------------------
        // Protected
        //----------------------------------------------------------------------
        
        /**
         * Finds all java files in the given directory. Called recursively so
         * the directory is passed on each invocation.
         * 
         * @param  file  Directory to scan for files
         */
        protected void findJavaFiles(File dir)
        {
            // Short circuit if operation canceled
            if (cancel_)
                return;
                
            // Process files in current directory
            File srcFiles[] = dir.listFiles(sourceFilter_);
            
            if (!ArrayUtil.isNullOrEmpty(srcFiles))
                for (int i = 0; i < srcFiles.length; i++)
                    workQueue_.enqueue(srcFiles[i].getAbsolutePath());
            
            // Process dirs in current directory
            File dirs[] = dir.listFiles(dirFilter_);
            
            if (!ArrayUtil.isNullOrEmpty(dirs))
            {
                for (int i=0; i<dirs.length; i++)
                {
                    setScanStatus("Scanning " + dirs[i] + " ...");
                    findJavaFiles(dirs[i]);
                }    
            }
        }

        /** 
         * Cancels the scanning activity
         */
        protected void cancel()
        {
            cancel_ = true;
        }
        
        //----------------------------------------------------------------------
        // Runnable Interface
        //----------------------------------------------------------------------
                
        /**
         * Starts the scanning activity on a separate thread
         */
        public void run()
        {
            findJavaFiles(dir_);
            setScanStatus("Done scanning.");
        }
    }

    //--------------------------------------------------------------------------
    // ParserWorker Inner Class
    //--------------------------------------------------------------------------

    /**
     * Pops files off of the work queue and parses them to gather stats
     */
    class ParserWorker implements Runnable
    {
        private boolean cancel_ = false;
        
        public void run()
        {
            ElapsedTime elapsed = new ElapsedTime();
            FileStats totals = new FileStats();
            int fileCount = 0;
            StatsCollector statsCollector = new StatsCollector();
            
            while (!workQueue_.isEmpty() || scanDirThread_.isAlive()) 
            {
                if (cancel_)
                    break;
                    
                // Pop file of the queue
                String filename = (String) workQueue_.dequeue();
                
                if (filename != null)
                {
                    setParseStatus("Parsing [" + workQueue_.size() + "] " + 
                        filename + " ...");
                     
                    // Parse file and add to totals
                    FileStats fileStats = null;
                    try
                    {
                        fileStats = statsCollector.getStats(filename);
                    }
                    catch (IOException ioe)
                    {
                        ExceptionUtil.handleUI(ioe, logger_);
                    }

                    totals.add(fileStats);
                    ++fileCount;

                    // Create table row data and append                    
                    Object tableRow[] = new Object[colNames_.length];
                    tableRow[0] = new Integer(fileCount);
                    tableRow[1] = FileUtil.stripFile(filename);
                    tableRow[2] = FileUtil.stripPath(filename);
                    tableRow[3] = new Integer(fileStats.getCodeLines());
                    tableRow[4] = new Integer(fileStats.getCommentLines());
                    tableRow[5] = new Integer(fileStats.getBlankLines());
                    tableRow[6] = new Integer(fileStats.getThrownOutLines());
                    tableRow[7] = new Integer(fileStats.getTotalLines());
                    tableRow[8] = new Integer(fileStats.getPercent()); // + "%";
                    
                    tableModel_.addRow(tableRow);
                }
            }
        
            NumberFormat df = DecimalFormat.getIntegerInstance();
            
            setParseStatus(
             "[Total " + df.format(totals.getTotalLines()) + "]  " +
             "[Code " + df.format(totals.getCodeLines()) + "]  " +
             "[Comments " + df.format(totals.getCommentLines()) + "]  " +
             "[Empty " + df.format(totals.getBlankLines()) + "]  " +
             "[Thrown out " + df.format(totals.getThrownOutLines()) + "]  " + 
             "[Percent code vs comments " + df.format(totals.getPercent()) + 
             "%]"); 
            
            setScanStatus("Done parsing.");
            goButton_.setText(LABEL_GO);
            
            // Turn the sorter back on
            tableSorter_.setEnabled(true);
            
            elapsed.setEndTime();
            setScanStatus("Elapsed time: " + elapsed.toString());
        }
        
        /** 
         * Cancels the parsing activity
         */
        public void cancel()
        {
            cancel_ = true;
            tableSorter_.setEnabled(true);
            setParseStatus("Search canceled!");            
        }
    }

    //--------------------------------------------------------------------------
    // Inner Class: TableCellRenderer
    //--------------------------------------------------------------------------
        
    /**
     * Renderer for the contents of the table
     */   
    class TableCellRenderer extends DefaultTableCellRenderer
    {
        private DecimalFormat decimalFormatter_;
        private NumberFormat percentFormatter_;
        
        public TableCellRenderer()
        {
            decimalFormatter_ = new DecimalFormat("###,###");
            percentFormatter_ = NumberFormat.getPercentInstance();
        }
        
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
                case COL_NUM:
                
                    setHorizontalAlignment(SwingConstants.CENTER);
                    setValue(text);
                    break;
                    
                case COL_DIR:
                case COL_FILE:
                
                    setHorizontalAlignment(SwingConstants.LEFT);
                    setValue(text);
                    break;
                    
                case COL_CODE:
                case COL_COMMENTS:
                case COL_BLANK:
                case COL_THROWN_OUT:
                case COL_TOTAL:
                
                    setHorizontalAlignment(SwingConstants.CENTER);
                    setValue(decimalFormatter_.format(value));
                    break;
    
                case COL_PERCENTAGE:
                
                    setHorizontalAlignment(SwingConstants.CENTER);
                    int i = ((Integer) value).intValue();
                    Float f = new Float((float) i/100);
                    setValue(percentFormatter_.format(f));
                    break;
                    
                default:
                    setValue(value);
            }
            
            return this;
        }
    }
    
}