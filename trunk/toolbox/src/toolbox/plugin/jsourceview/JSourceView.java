package toolbox.jsourceview;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;

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
import toolbox.util.Queue;
import toolbox.util.SwingUtil;
import toolbox.util.io.filter.DirectoryFilter;
import toolbox.util.io.filter.ExtensionFilter;
import toolbox.util.io.filter.OrFilter;
import toolbox.util.ui.JSmartOptionPane;
import toolbox.util.ui.ThreadSafeTableModel;

/**
 * JSourceView gathers statistics on one or more source files and presents
 * them in a table format for viewing.
 */
public class JSourceView extends JFrame implements ActionListener
{
    private static final Logger logger_ = 
        Logger.getLogger(JSourceView.class);
    
    private static OrFilter sourceFilter_;

    private static final String TEXT_GO     = "Go!";
    private static final String TEXT_CANCEL = "Cancel";
    
    private JTextField  dirField_;
    private JButton     goButton_;
    private JLabel      scanStatusLabel_;
    private JLabel      parseStatusLabel_;
    
    private JMenuBar    menuBar_;
    private JMenuItem   saveMenuItem_;
    private JMenuItem   exitMenuItem_;
    private JMenuItem   aboutMenuItem_;
    
    private JTable               table_;
    private ThreadSafeTableModel tableModel_;
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

    static
    {
        // create filter to flesh out source files
        sourceFilter_ = new OrFilter();
        sourceFilter_.addFilter(new ExtensionFilter("c"));
        sourceFilter_.addFilter(new ExtensionFilter("cpp"));
        sourceFilter_.addFilter(new ExtensionFilter("java"));
        sourceFilter_.addFilter(new ExtensionFilter("h"));
    }

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
        super("JSourceView v1.1");
        
        dirField_ = new JTextField(12);
        dirField_.setFont(SwingUtil.getPreferredSerifFont());
        dirField_.addActionListener(this);
        
        goButton_ = new JButton("Go!");
        JPanel topPanel = new JPanel();
        scanStatusLabel_ = new JLabel(" ");
        parseStatusLabel_ = new JLabel(" ");
        menuBar_ = new JMenuBar();
        pathSeparator_ = System.getProperty("file.separator");
        
        topPanel.setLayout(new FlowLayout());
        topPanel.add(new JLabel("Directory"));
        topPanel.add(dirField_);
        topPanel.add(goButton_);
        
        goButton_.addActionListener(this);
        tableModel_ = new ThreadSafeTableModel(colNames_, 0);
        table_ = new JTable(tableModel_);
        table_.setFont(SwingUtil.getPreferredSerifFont());
        
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
            else if (obj == exitMenuItem_)
            {
                dispose();
                System.exit(0);
            }
        }
        catch (Exception e)
        {
            JSmartOptionPane.showExceptionMessageDialog(this, e);
        }
    }

    //--------------------------------------------------------------------------
    // Public 
    //--------------------------------------------------------------------------
    
    /**
     * Sets the text of the scan status
     * 
     * @param  s  Status text
     */
    public void setScanStatus(String s)
    {
        scanStatusLabel_.setText(s);
    }

    /**
     * Sets the text of the parse status
     * 
     * @param  s  Parse status text
     */
    public void setParseStatus(String s)
    {
        parseStatusLabel_.setText(s);
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
        
        exitMenuItem_ = new JMenuItem("Exit");
        exitMenuItem_.addActionListener(this);
        
        jmenu.add(saveMenuItem_);
        jmenu.addSeparator();
        jmenu.add(exitMenuItem_);
        
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
        
        if(s.length() > 0)
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
     * Starts the scanning
     */
    protected void goButtonPressed()
    {
        if (goButton_.getText().equals(TEXT_GO))
        {
            goButton_.setText(TEXT_CANCEL);
            String s = dirField_.getText();
            workQueue_ = new Queue();
            
            table_.setModel(
                tableModel_ = new ThreadSafeTableModel(colNames_, 0));
            
            scanDirWorker_ = new ScanDirWorker(new File(s));
            scanDirThread_ = new Thread(scanDirWorker_);
            scanDirThread_.start();
            
            parserWorker_  = new ParserWorker();
            parserThread_  = new Thread(parserWorker_);
            parserThread_.start();
        }
        else
        {
            goButton_.setText(TEXT_GO);
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

    /**
     * Returns directory portion only of a path
     * 
     * @param  s  Path 
     * @return Directory portion of the path
     */
    protected String getDirectoryOnly(String s)
    {
        return s.substring(0, s.lastIndexOf(pathSeparator_));
    }

    /**
     * Returns the file portion of a path
     * 
     * @param  s  Path
     * @return File portion of a path
     */
    protected String getFileOnly(String s)
    {
        return s.substring(s.lastIndexOf(pathSeparator_) + 1, s.length());
    }

    //--------------------------------------------------------------------------
    //  Inner Classes
    //--------------------------------------------------------------------------
    
    /** 
     * Scans directory
     */
    private class ScanDirWorker implements Runnable
    {
        /** Directory to scan */
        private File file_;

        /** Cancel flag */
        private boolean cancel_ = false;
        
        /**
         * Creates a scanner
         * 
         * @param  file1  Directory to scann
         */
        public ScanDirWorker(File dir)
        {
            file_ = dir;
        }

                
        /**
         * Starts scanning directory on a separate thread
         */
        public void run()
        {
            findJavaFiles(file_);
            setScanStatus("Done scanning.");
        }
        
        /**
         * Finds all java files in the given directory
         * 
         * @param  file  Directory to scan for files
         */
        protected void findJavaFiles(File file)
        {
            // Short circuit if operation canceled
            if (cancel_)
                return;
                
            Thread.yield();
            
            // Process files in current directory
            File srcFiles[] = file.listFiles(sourceFilter_);
            
            if (!ArrayUtil.isNullOrEmpty(srcFiles))
            {
                for (int i = 0; i < srcFiles.length; i++)
                    workQueue_.enqueue(srcFiles[i].getAbsolutePath());
            }
            
            // Process dirs in current directory
            File dirs[] = file.listFiles(new DirectoryFilter());
            
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
         * Cancels the operation
         */
        public void cancel()
        {
            cancel_ = true;
        }
    }

    /**
     * Pops files off of the work queue and parses them to gather stats
     */
    class ParserWorker implements Runnable
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
                String filename = (String)workQueue_.dequeue();
                
                if(filename != null)
                {
                    setParseStatus("Parsing [" + workQueue_.size() + "] " + 
                        filename + " ...");
                     
                    // Parse file and add to totals
                    FileStats fileStats = scanFile(filename);
                    totalStats.add(fileStats);
                    ++fileCount;

                    // Create table row data and append                    
                    String tableRow[] = new String[colNames_.length];
                    tableRow[0] = fileCount+"";
                    tableRow[1] = getDirectoryOnly(filename);
                    tableRow[2] = getFileOnly(filename);
                    tableRow[3] = String.valueOf(fileStats.getCodeLines());
                    tableRow[4] = String.valueOf(fileStats.getCommentLines());
                    tableRow[5] = String.valueOf(fileStats.getBlankLines());
                    tableRow[6] = String.valueOf(fileStats.getTotalLines());
                    tableRow[7] = fileStats.getPercent() + "%";
                    
                    tableModel_.addRow(tableRow);
                }
                
                Thread.yield();
            }
        
            // Make separator row
            String rulerRow[] = new String[colNames_.length];
            for(int i = 0; i < rulerRow.length; i++)
                rulerRow[i] =  "========";
            tableModel_.addRow(rulerRow);
            
     
            String totalRow[] = new String[colNames_.length];       
            totalRow[0] = "";
            totalRow[1] = "Grand";
            totalRow[2] = "Total";
            totalRow[3] = String.valueOf(totalStats.getCodeLines());
            totalRow[4] = String.valueOf(totalStats.getCommentLines());
            totalRow[5] = String.valueOf(totalStats.getBlankLines());
            totalRow[6] = String.valueOf(totalStats.getTotalLines());
            totalRow[7] = totalStats.getPercent() + "%";
            tableModel_.addRow(totalRow);
            
            setParseStatus("Done parsing.");
            goButton_.setText(TEXT_GO);
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
            
            try
            {
                LineNumberReader lineReader = 
                    new LineNumberReader(
                        new BufferedReader(
                            new FileReader(filename)));
                    
                LineStatus linestatus = new LineStatus();
                
                String line;
                
                while((line = lineReader.readLine()) != null) 
                {
                    filestats.setTotalLines(filestats.getTotalLines()+1);
                    
                    if (line.trim().length() == 0)
                    {
                        filestats.setBlankLines(filestats.getBlankLines()+1);
                    }
                    else
                    {
                        Machine.scanLine(new LineScanner(line), linestatus);
                        
                        if(linestatus.getCountLine())
                            filestats.setCodeLines(
                                filestats.getCodeLines() + 1);
                        else
                            filestats.setCommentLines(
                                filestats.getCommentLines() + 1);
                    }
                }
        
                lineReader.close();
            }
            catch (Exception e)
            {
                JSmartOptionPane.showExceptionMessageDialog(
                    JSourceView.this, e);
            }
            finally
            {
                return filestats;
            }
        }
        
        /** 
         * Cancels the operation
         */
        public void cancel()
        {
            cancel_ = true;
            logger_.debug("Cancel called : " + cancel_);            
        }
    }
}