package toolbox.jsourceview;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Properties;

import javax.swing.JButton;
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

import org.apache.log4j.Logger;

import toolbox.util.ArrayUtil;
import toolbox.util.ExceptionUtil;
import toolbox.util.FileUtil;
import toolbox.util.Queue;
import toolbox.util.ResourceCloser;
import toolbox.util.StringUtil;
import toolbox.util.SwingUtil;
import toolbox.util.io.filter.DirectoryFilter;
import toolbox.util.io.filter.ExtensionFilter;
import toolbox.util.io.filter.OrFilter;
import toolbox.util.ui.ThreadSafeTableModel;
import toolbox.util.ui.table.TableSorter;

/**
 * JSourceView gathers statistics on one or more source files and presents
 * them in a table format for viewing.
 * 
 * <pre>
 * TODO: Added explorer to pick directory
 * TODO: Update Queue to BlockingQueue
 * TODO: Figure out how to save table column sizes
 * TODO: Convert actionPerformed() to Actions
 * TODO: Add chart for visualization
 * TODO: Custom table cell renders to align cell contents/color code unusually
 *       high or low numbers, etc
 * TODO: Add regex filter to include/exclude files
 * TODO: Unit tests for non-gui statistics collection
 * </pre> 
 * 
 */
public class JSourceView extends JFrame implements ActionListener
{
    private static final Logger logger_ = 
        Logger.getLogger(JSourceView.class);
    
    private static final String PROP_LAST_DIR = "jsourceview.dir";
    
    private static final String LABEL_GO     = "Go!";
    private static final String LABEL_CANCEL = "Cancel";
    
    private JTextField  dirField_;
    private JButton     goButton_;
    private JLabel      scanStatusLabel_;
    private JLabel      parseStatusLabel_;
    
    private JMenuBar    menuBar_;
    private JMenuItem   saveMenuItem_;
    private JMenuItem   aboutMenuItem_;
    
    private JTable               table_;
    private ThreadSafeTableModel tableModel_;
    private TableSorter          tableSorter_;
    private Queue                workQueue_;
    
    private Thread        scanDirThread_;
    private ScanDirWorker scanDirWorker_;
    private Thread        parserThread_;
    private ParserWorker  parserWorker_;
    
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
        "Total", 
        "Percentage"
    };

    //  Filter to identify source files
    private static OrFilter sourceFilter_;
    
    static
    {
        sourceFilter_ = new OrFilter();
        sourceFilter_.addFilter(new ExtensionFilter("c"));
        sourceFilter_.addFilter(new ExtensionFilter("cpp"));
        sourceFilter_.addFilter(new ExtensionFilter("java"));
        sourceFilter_.addFilter(new ExtensionFilter("h"));
    }

    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    /**
     * Entrypoint
     * 
     * @param  args  None recognized 
     * @throws Exception on error
     */
    public static void main(String args[]) throws Exception
    {
        SwingUtil.setPreferredLAF();
        new JSourceView().setVisible(true);
    }

    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Constructs JSourceview
     */    
    public JSourceView()
    {
        super("JSourceView");
        
        dirField_ = new JTextField(25);
        dirField_.addActionListener(this);
        
        goButton_ = new JButton(LABEL_GO);
        goButton_.addActionListener(this);
        
        JPanel topPanel = new JPanel();
        scanStatusLabel_ = new JLabel(" ");
        parseStatusLabel_ = new JLabel(" ");
        menuBar_ = new JMenuBar();
        pathSeparator_ = System.getProperty("file.separator");
        
        topPanel.setLayout(new FlowLayout());
        topPanel.add(new JLabel("Directory"));
        topPanel.add(dirField_);
        topPanel.add(goButton_);

        // Setup sortable table
        tableModel_  = new ThreadSafeTableModel(colNames_, 0);
        tableSorter_ = new TableSorter(tableModel_);
        table_       = new JTable(tableSorter_);
        tableSorter_.addMouseListenerToHeaderInTable(table_);
        
        getContentPane().add(topPanel, BorderLayout.NORTH);
        getContentPane().add(new JScrollPane(table_), BorderLayout.CENTER);
        
        JPanel jpanel = new JPanel();
        jpanel.setLayout(new BorderLayout());
        jpanel.add(scanStatusLabel_, BorderLayout.NORTH);
        jpanel.add(parseStatusLabel_, BorderLayout.SOUTH);
        getContentPane().add(jpanel, BorderLayout.SOUTH);
        
        setJMenuBar(createMenuBar());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        pack();
        SwingUtil.centerWindow(this);
    }

    //--------------------------------------------------------------------------
    //  ActionListener Interface
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
            if (obj == goButton_)
                goButtonPressed();
            else if (obj == dirField_)
                goButtonPressed();
            else if (obj == saveMenuItem_)
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
     * Saves preferences 
     * 
     * @param prefs Properties to save preferences to
     */
    public void savePrefs(Properties prefs)
    {
        String dir = dirField_.getText();
        
        if (!StringUtil.isNullOrEmpty(dir))
            prefs.setProperty(PROP_LAST_DIR, dir.trim());
    }

    /**
     * Applies preferences
     * 
     * @param prefs Properties to read preferences from
     */
    public void applyPrefs(Properties prefs)
    {
        String dir = prefs.getProperty(PROP_LAST_DIR);
        
        if (!StringUtil.isNullOrEmpty(dir))
        {
            dirField_.setText(dir);
            dirField_.setCaretPosition(0);
        }
    }

    //--------------------------------------------------------------------------
    //  Private
    //--------------------------------------------------------------------------
    
    /**
     * Creates the menu bar 
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
    
    /** 
     * Starts the scanning/parsing activities in parallel
     */
    protected void goButtonPressed()
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

    //--------------------------------------------------------------------------
    //  ScanDirWorker Inner Class
    //--------------------------------------------------------------------------
    
    /** 
     * Scans file system recursively for files containing source code.
     */
    private class ScanDirWorker implements Runnable
    {
        /** Directory to scan recursively for source files */
        private File dir_;

        /** Cancel flag */
        private boolean cancel_;
        
        /** Filter for list on directories */
        private FilenameFilter dirFilter_;
        
        //----------------------------------------------------------------------
        // Constructors
        //----------------------------------------------------------------------
        
        /**
         * Creates a scanner
         * 
         * @param  dir  Directory root to scan
         */
        public ScanDirWorker(File dir)
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
    private class ParserWorker implements Runnable
    {
        private boolean cancel_ = false;
        
        /**
         * Parses each file on the workqueue and adds the statistics to the
         * table.
         */
        public void run()
        {
            FileStats totalStats = new FileStats();
            int fileCount = 0;
            
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
                    FileStats fileStats = scanFile(filename);
                    totalStats.add(fileStats);
                    ++fileCount;

                    // Create table row data and append                    
                    Object tableRow[] = new Object[colNames_.length];
                    tableRow[0] = new Integer(fileCount);
                    tableRow[1] = FileUtil.stripFile(filename);
                    tableRow[2] = FileUtil.stripPath(filename);
                    tableRow[3] = new Integer(fileStats.getCodeLines());
                    tableRow[4] = new Integer(fileStats.getCommentLines());
                    tableRow[5] = new Integer(fileStats.getBlankLines());
                    tableRow[6] = new Integer(fileStats.getTotalLines());
                    tableRow[7] = new Integer(fileStats.getPercent()); // + "%";
                    
                    tableModel_.addRow(tableRow);
                }
            }
        
            NumberFormat df = DecimalFormat.getIntegerInstance();
            NumberFormat pf = DecimalFormat.getPercentInstance();
            
            setParseStatus(
             "[Total lines " + df.format(totalStats.getTotalLines()) + "]  " +
             "[Code lines " + df.format(totalStats.getCodeLines()) + "]  " +
             "[Comment lines " + df.format(totalStats.getCommentLines()) + "]  " +
             "[Empty lines " + df.format(totalStats.getBlankLines()) + "]  " +
             "[Percent code vs comments " + df.format(totalStats.getPercent()) + 
             "%]"); 
            
            setScanStatus("Done parsing.");
            goButton_.setText(LABEL_GO);
            
            // Turn the sorter back on
            tableSorter_.setEnabled(true);
        }
        
        /**
         * Scans a given file and generates statistics
         * 
         * @param   filename  Name of the file
         * @return  Stats of the file
         */
        protected FileStats scanFile(String filename)
        {
            FileStats filestats = new FileStats();
            LineStatus  status  = new LineStatus();
            LineScanner scanner = new LineScanner();
            String line;
            
            try
            {
                BufferedReader reader = 
                    new BufferedReader(new FileReader(filename));
                
                while ((line = reader.readLine()) != null) 
                {
                    filestats.incrementTotalLines();
                    line = line.replace('\t',' ');
                    
                    if (line.trim().length() == 0)  
                    {
                        filestats.incrementBlankLines();
                    }
                    else
                    {
                        scanner.setLine(line);
                        Machine.scanLine(scanner, status);
                        
                        if (status.isRealCode())
                            filestats.incrementCodeLines();
                        else
                            filestats.incrementCommentLines();
                    }
                }
                
                ResourceCloser.close(reader);
            }
            catch (Exception e)
            {
                ExceptionUtil.handleUI(e, logger_);
            }
            finally
            {
                return filestats;
            }
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
}