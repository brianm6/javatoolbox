package toolbox.jsourceview;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
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

/**
 * JSourceView
 */
public class JSourceView extends JFrame 
    implements ActionListener, FilenameFilter, Runnable
{
    JTextField dirField;
    JButton goButton;
    JPanel topPanel;
    JLabel scanStatusLabel;
    JLabel parseStatusLabel;
    JMenuBar menuBar;
    JMenuItem saveMenuItem;
    JMenuItem exitMenuItem;
    JMenuItem aboutMenuItem;
    JTable table;
    ThreadSafeTableModel model;
    Queue workQueue;
    String colNames[] = {
        "Directory", "File", "Code", "Comments", "Blank", "Total", "Percentage"
    };
    
    /** Thread to scan directories **/
    private Thread scanDirThread;
    
    /** Platform path separator **/
    private String pathSeparator;

    /**
     * Entrypoint
     * 
     * @param  args  None recognized 
     */
    public static void main(String args[])
    {
        new JSourceView().setVisible(true);
    }

    
    /** 
     * Scans directory
     */
    private class ScanDirWorker implements Runnable
    {
        /** Directory to scan **/
        private File file;

        /**
         * Creates a scanner
         * 
         * @param  file1  Directory to scann
         */
        public ScanDirWorker(File file1)
        {
            file = file1;
        }

                
        /**
         * Starts scanning directory on a separate thread
         */
        public void run()
        {
            Thread thread = new Thread(JSourceView.this);
            thread.start();
            findJavaFiles(file);
            setScanStatus("Done scanning.");
        }
    }

    /**
     * Constructs JSourceview
     */    
    public JSourceView()
    {
        super("JSourceView v1.1");
        dirField = new JTextField(12);
        goButton = new JButton("Go!");
        topPanel = new JPanel();
        scanStatusLabel = new JLabel(" ");
        parseStatusLabel = new JLabel(" ");
        menuBar = new JMenuBar();
        workQueue = new Queue();
        pathSeparator = System.getProperty("file.separator");
        topPanel.setLayout(new FlowLayout());
        topPanel.add(new JLabel("Directory"));
        topPanel.add(dirField);
        topPanel.add(goButton);
        goButton.addActionListener(this);
        model = new ThreadSafeTableModel(colNames, 0);
        table = new JTable(model);
        getContentPane().add(topPanel, "North");
        getContentPane().add(new JScrollPane(table), "Center");
        JPanel jpanel = new JPanel();
        jpanel.setLayout(new BorderLayout());
        jpanel.add(scanStatusLabel, "North");
        jpanel.add(parseStatusLabel, "South");
        getContentPane().add(jpanel, "South");
        setJMenuBar(makeMenuBar());
        setSize(500, 500);
        setDefaultCloseOperation(2);
        addWindowListener(new WindowAdapter() {
    
            public void windowClosing(WindowEvent windowevent)
            {
                dispose();
                System.exit(0);
            }
    
        });
    }

    /**
     * Creates the menu bar 
     * 
     * @return  Menubar
     */
    private JMenuBar makeMenuBar()
    {
        JMenu jmenu = new JMenu("File");
        JMenu jmenu1 = new JMenu("Help");
        saveMenuItem = new JMenuItem("Save");
        saveMenuItem.addActionListener(this);
        exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(this);
        jmenu.add(saveMenuItem);
        jmenu.addSeparator();
        jmenu.add(exitMenuItem);
        aboutMenuItem = new JMenuItem("About");
        aboutMenuItem.addActionListener(this);
        jmenu1.add(aboutMenuItem);
        menuBar.add(jmenu);
        menuBar.add(jmenu1);
        return menuBar;
    }

    
    /**
     * Handles actions from the GUI
     *
     * @param  actionevent  Action to handle
     */
    public void actionPerformed(ActionEvent actionevent)
    {
        Object obj = actionevent.getSource();
        if(obj == goButton)
        {
            goButtonPressed();
            return;
        }
        if(obj == exitMenuItem)
        {
            dispose();
            System.exit(0);
            return;
        }
        if(obj == saveMenuItem)
        {
            saveResults();
            return;
        }
        if(obj == aboutMenuItem)
            showAbout();
    }


    /**
     * Saves the results to a file
     */
    void saveResults()
    {
        String s = JOptionPane.showInputDialog("Save to file");
        if(s.length() > 0)
            model.saveToFile(s);
    }

    
    /**
     * Shows About dialog box
     */
    void showAbout()
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
    void goButtonPressed()
    {
        goButton.setEnabled(false);
        String s = dirField.getText();
        workQueue.removeAllElements();
        table.setModel(model = new ThreadSafeTableModel(colNames, 0));
        scanDirThread = new Thread(new ScanDirWorker(new File(s)));
        scanDirThread.start();
    }

    /**
     * Sets the text of the scan status
     * 
     * @param  s  Status text
     */
    public void setScanStatus(String s)
    {
        scanStatusLabel.setText(s);
    }


    /**
     * Sets the text of the parse status
     * 
     * @param  s  Parse status text
     */
    public void setParseStatus(String s)
    {
        parseStatusLabel.setText(s);
    }


    /**
     * Runs the doohickey
     */
    public void run()
    {
        FileStats filestats = new FileStats();
        while(!workQueue.empty() || scanDirThread.isAlive()) 
        {
            String s = (String)workQueue.dequeue();
            if(s != null)
            {
                setParseStatus("Parsing " + s + " ...");
                FileStats filestats1 = scanFile(s);
                filestats.add(filestats1);
                String as1[] = new String[colNames.length];
                as1[0] = getDirectoryOnly(s);
                as1[1] = getFileOnly(s);
                as1[2] = String.valueOf(filestats1.codeLines);
                as1[3] = String.valueOf(filestats1.commentLines);
                as1[4] = String.valueOf(filestats1.blankLines);
                as1[5] = String.valueOf(filestats1.totalLines);
                as1[6] = filestats1.getPercent() + "%";
                model.addRow(as1);
            }
            try
            {
                Thread.currentThread();
                Thread.yield();
            }
            catch(Exception ex) {}
        }
    
        String as[] = new String[colNames.length];
        for(int i = 0; i < as.length; i++)
            as[i] = "--------";
    
        model.addRow(as);
        as[0] = "Grand";
        as[1] = "Total";
        as[2] = String.valueOf(filestats.codeLines);
        as[3] = String.valueOf(filestats.commentLines);
        as[4] = String.valueOf(filestats.blankLines);
        as[5] = String.valueOf(filestats.totalLines);
        as[6] = filestats.getPercent() + "%";
        model.addRow(as);
        setParseStatus("Done parsing.");
        goButton.setEnabled(true);
    }


    /**
     * Returns directory portion only of a path
     * 
     * @param  s  Path 
     * @return Directory portion of the path
     */
    public String getDirectoryOnly(String s)
    {
        return s.substring(0, s.lastIndexOf(pathSeparator));
    }


    /**
     * Returns the file portion of a path
     * 
     * @param  s  Path
     * @return File portion of a path
     */
    public String getFileOnly(String s)
    {
        return s.substring(s.lastIndexOf(pathSeparator) + 1, s.length());
    }

    
    /**
     * Scans a given file and generates statistics
     * 
     * @param   s  Name of the file
     * @return  Stats of the file
     */
    public FileStats scanFile(String s)
    {
        Object obj = null;
        FileStats filestats = new FileStats();
        try
        {
            LineNumberReader linenumberreader = new LineNumberReader(new BufferedReader(new FileReader(s)));
            LineStatus linestatus = new LineStatus();
            String s1;
            while((s1 = linenumberreader.readLine()) != null) 
            {
                filestats.totalLines++;
                if(s1.trim().length() == 0)
                {
                    filestats.blankLines++;
                }
                else
                {
                    Machine.scanLine(new Line(s1), linestatus);
                    if(linestatus.countLine)
                        filestats.codeLines++;
                    else
                        filestats.commentLines++;
                }
            }
    
            linenumberreader.close();
        }
        catch(Exception exception)
        {
            System.out.println(exception.toString());
            exception.printStackTrace();
        }
        finally
        {
            return filestats;
        }
    }

    void findJavaFiles(File file)
    {
        try
        {
            Thread.currentThread();
            Thread.yield();
        }
        catch(Exception ex) {}
        String as[] = file.list(this);
        if(as != null && as.length > 0)
        {
            for(int i = 0; i < as.length; i++)
            {
                File file1 = new File(file, as[i]);
                if(file1.isDirectory())
                {
                    setScanStatus("Scanning " + file1 + " ...");
                    findJavaFiles(file1);
                }
            }
    
        }
    }

    public boolean accept(File file, String s)
    {
        if(new File(file, s).isDirectory())
            return true;
        if(s.toUpperCase().endsWith(".CPP") || s.toUpperCase().endsWith(".C") || s.toUpperCase().endsWith(".H") || s.toUpperCase().endsWith(".JAVA"))
        {
            workQueue.enqueue(file + pathSeparator + s);
            return true;
        }
        else
        {
            return false;
        }
    }
}