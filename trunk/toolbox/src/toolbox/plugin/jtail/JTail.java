package toolbox.jtail;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JTabbedPane;

import org.apache.log4j.Category;
import toolbox.util.ExceptionUtil;
import toolbox.util.SwingUtil;
import toolbox.util.file.FileStuffer;
import toolbox.util.ui.JFileExplorerAdapter;
import toolbox.util.ui.JSmartOptionPane;

/**
 * GUI front end to the tail
 */
public class JTail extends JFrame
{
    /** Logger **/
    private static final Category logger_ = 
        Category.getInstance(JTail.class);
    
    
    private JDesktopPane desktop_;
    private FileSelectionWindow fileExplorerWindow_;
    private JTabbedPane tabbedPane_;
    private Properties props_;
    
    static
    {
        FileStuffer stuffer = new FileStuffer(new File("c:\\crap.txt"), 1000);
        stuffer.start();
    }
      
        
    /**
     * Entry point 
     */
    public static void main(String[] args)
    {
        JTail jtail = new JTail();
        jtail.setVisible(true);
        SwingUtil.tile(jtail.getDesktop());
    }


    /**
     * Constructor for JTail.
     */
    public JTail()
    {
        this("JTail");
    }


    /**
     * Constructor for JTail.
     * 
     * @param title
     */
    public JTail(String title)
    {
        super(title);
        init();
    }


    /** 
     * Initializes program
     */
    protected void init()
    {
        try
        {
            build();
            addListeners();
            setDefaultCloseOperation(EXIT_ON_CLOSE);            
            loadProperties();
            processProperties();
            setSize(800,400);
            SwingUtil.centerWindow(this);            
        }
        catch (Exception e)
        {
            System.err.println("Error: " + e.getMessage());
            System.err.println(ExceptionUtil.getStackTrace(e));
            System.exit(1);
        }
    }


    /**
     * Builds the GUI
     */
    protected void build()
    {
        desktop_ = new JDesktopPane();
        setContentPane(desktop_);
        
        fileExplorerWindow_ = new FileSelectionWindow();
        fileExplorerWindow_.setVisible(true);
        //fileExplorer_.pack();
        
        desktop_.add(fileExplorerWindow_);
                
        tabbedPane_ = new JTabbedPane();
        //tabbedPane_.addTab("Testing", new JLabel("Testing"));
        
        JInternalFrame tabbedWindow = new JInternalFrame("JTail");
        tabbedWindow.getContentPane().setLayout(new BorderLayout());
        tabbedWindow.getContentPane().add(tabbedPane_, BorderLayout.CENTER);
        tabbedWindow.setVisible(true);     
        desktop_.add(tabbedWindow);   
        tabbedWindow.setLocation(1,1);
        tabbedWindow.setSize(200,200);
    }
    
    
    /**
     * Tails a file
     * 
     * @param  file   File to tail
     */     
    protected void tailFile(File file)
    {
        try
        {
            TailPane tailPane = new TailPane(file);
            tabbedPane_.addTab(file.getName(), tailPane);
            tabbedPane_.setSelectedComponent(tailPane);
        }
        catch (FileNotFoundException e)
        {
            logger_.error(e.getMessage(), e);            
            JSmartOptionPane.showExceptionMessageDialog(this, e);
        }
    }

         
    /**
     * Loads properties from $HOME/.jtail.properties    
     */
    protected void loadProperties() throws FileNotFoundException, IOException
    {
        String userHome = System.getProperty("user.home");
        String propsFile = userHome + File.separator + ".junit.properties";
        
        try
        {
            props_.load(new FileInputStream(propsFile));
        }
        catch (FileNotFoundException e)
        {
            // do nothing
        }
    }    
    
    
    /**
     * Applies the properties to the current configuration
     */
    protected void processProperties()
    {
        // TODO: fill me in
    }    
    
    
    /**
     * Returns the desktop.
     * 
     * @return JDesktopPane
     */
    public JDesktopPane getDesktop()
    {
        return desktop_;
    }


    /**
     * Sets the desktop.
     * 
     * @param desktop The desktop to set
     */
    public void setDesktop(JDesktopPane desktop)
    {
        desktop_ = desktop;
    }


    /**
     * Adds listeners
     */
    protected void addListeners()
    {
        fileExplorerWindow_.getFileSelectionPane().getFileExplorer().
            addJFileExplorerListener(new FileSelectionListener());
            
        fileExplorerWindow_.getFileSelectionPane().getTailButton().
            addActionListener(new TailButtonListener());
    }


    /**
     * Listener for the file explorer
     */
    class FileSelectionListener extends JFileExplorerAdapter
    {
        /**
         * Tail files that have been double clicked on
         * 
         * @param  file  File double clicked
         */
        public void fileDoubleClicked(String file)
        {
            tailFile(new File(file));
        }
    }
    
    
    /**
     * Tail button listener
     */
    class TailButtonListener implements ActionListener
    {
        /**
         * Tail button clicked
         * 
         * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
         */
        public void actionPerformed(ActionEvent e)
        {
            tailFile(new File(fileExplorerWindow_.getFileSelectionPane().
                getFileExplorer().getFilePath()));
        }
    }
}
