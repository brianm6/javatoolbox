package toolbox.jtail;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

import org.apache.log4j.Category;
import toolbox.util.ArrayUtil;
import toolbox.util.ExceptionUtil;
import toolbox.util.SwingUtil;
import toolbox.util.file.FileStuffer;
import toolbox.util.ui.JFileExplorerAdapter;
import toolbox.util.ui.JSmartOptionPane;
import toolbox.util.ui.JSmartStatusBar;
import toolbox.util.ui.font.FontSelectionDialog;
import toolbox.util.ui.font.FontSelectionException;
import toolbox.util.ui.font.FontSelectionPanel;
import toolbox.util.ui.font.IFontDialogListener;

import electric.xml.Document;
import electric.xml.Element;
import electric.xml.Elements;
import electric.xml.ParseException;

/**
 * GUI front end to the tail
 */
public class JTail extends JFrame
{
    /** Logger **/
    private static final Category logger_ = 
        Category.getInstance(JTail.class);

    private static final String ELEMENT_JTAIL = "JTail";
    private static final String ATTR_HEIGHT   = "height";
    private static final String ATTR_WIDTH    = "width";
    private static final String ATTR_X        = "x";
    private static final String ATTR_Y        = "y";
    
    private static final String CONFIG_FILE = ".jtail.xml";
    
    
    private FileSelectionPane fileSelectionPane_;
    private JTabbedPane       tabbedPane_;
    private JSmartStatusBar   statusBar_;    
    private JMenuBar          menuBar_;    
    private Map               tailMap_;
    private Point             location_;
    private Dimension         size_;
    private boolean           testMode_ = true;
    private Font              defaultFont_;
        
        
    /**
     * Entry point 
     * 
     * @param  args  None recognized
     */
    public static void main(String[] args)
    {
        JTail jtail = new JTail();
        jtail.setVisible(true);
    }

    //
    //  CONSTRUCTORS
    //
    
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

    //
    //  IMPLEMENTATION
    //
    
    /** 
     * Initializes program
     */
    protected void init()
    {
        try
        {
            // Init variables
            tailMap_  = new HashMap();
            
            buildView();
            addListeners();
            setDefaultCloseOperation(EXIT_ON_CLOSE);            
            applyConfiguration(loadConfiguration());
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
    protected void buildView()
    {
        getContentPane().setLayout(new BorderLayout());
        
        fileSelectionPane_ = new FileSelectionPane();
        tabbedPane_ = new JTabbedPane();

        JSplitPane rootSplitPane = 
            new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                fileSelectionPane_, 
                tabbedPane_);                

        getContentPane().add(BorderLayout.CENTER, rootSplitPane);
        
        statusBar_ = new JSmartStatusBar();
        getContentPane().add(BorderLayout.SOUTH, statusBar_);
        
        setJMenuBar(createMenuBar());
    }
    
    
    /**
     * Creates the menu bar
     * 
     * @return Menu bar
     */
    protected JMenuBar createMenuBar()
    {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu   = new JMenu("File");
        fileMenu.setMnemonic('F');
        fileMenu.add(new SetFontAction());
        fileMenu.addSeparator();
        fileMenu.add(new SaveAction());
        fileMenu.add(new ExitAction());
        
        if (testMode_)
        {
            fileMenu.addSeparator();
            fileMenu.add(new CreateFileAction());
        }
            
        menuBar.add(fileMenu);
        return menuBar;
    }


    /**
     * Adds a tail of the given configuration to the output area
     * 
     * @param  config  Tail configuration
     */     
    protected void addTail(TailConfig config)
    {
        try
        {
            logger_.debug("[tail  ]\n" + config);
            TailPane tailPane = new TailPane(config);

            JButton closeButton = tailPane.getCloseButton();
            
            // Create map of (closeButton, tailPane) so that the 
            // tail pane can be reassociated if it needs to be 
            // removed from the tabbed pane
            tailMap_.put(closeButton, tailPane);
            
            closeButton.addActionListener(new CloseButtonListener());
            
            tabbedPane_.addTab(config.getFilename(), tailPane);
            tabbedPane_.setSelectedComponent(tailPane);
        }
        catch (FileNotFoundException e)
        {
            logger_.error(e.getMessage(), e);            
            JSmartOptionPane.showExceptionMessageDialog(this, e);
        }
    }

         
    /**
     * Loads properties from $HOME/.jtail.xml
     * 
     * @throws  IOException on I/O error
     */
    protected TailConfig[] loadConfiguration() throws IOException
    {
        TailConfig[] configs = new TailConfig[0];
        
        String userHome = System.getProperty("user.home");
        String filename = userHome + File.separator + CONFIG_FILE;
     
        File xmlFile = new File(filename);
        
        if (!xmlFile.exists())
        {
            // create a new configuration
        }
        else if (!xmlFile.canRead())
        {
            JOptionPane.showMessageDialog(this, 
                "Cannot read configuration from " + filename + ". " + 
                "Using defaults.");
        }
        else if (!xmlFile.isFile())
        {
            JOptionPane.showMessageDialog(this,
                "Configuration file " + filename + " cannot be a directory. " +
                "Using Defaults.");
        }
        else
        {
            try
            {
                Document config = new Document(xmlFile);            
                Element jtailNode = config.getRoot();
                
                // Read optional window location
                if ((jtailNode.getAttribute(ATTR_X) != null) &&
                    (jtailNode.getAttribute(ATTR_Y) != null))
                {
                    location_ = new Point();
                    
                    location_.x = 
                        Integer.parseInt(jtailNode.getAttribute(ATTR_X));
                        
                    location_.y = 
                        Integer.parseInt(jtailNode.getAttribute(ATTR_Y));
                }
                
                // Read optional window size
                if ((jtailNode.getAttribute(ATTR_HEIGHT) != null) &&
                    (jtailNode.getAttribute(ATTR_WIDTH)!= null))
                {
                    size_ = new Dimension();
                    
                    size_.height = Integer.parseInt(
                        jtailNode.getAttribute(ATTR_HEIGHT));
                        
                    size_.width  = Integer.parseInt(
                        jtailNode.getAttribute(ATTR_WIDTH));
                }
                
                // Handle optional default font element    
                Element fontNode = 
                    jtailNode.getElement(TailConfig.ELEMENT_FONT);
                
                if (fontNode != null)
                {
                    String family = 
                        fontNode.getAttribute(TailConfig.ATTR_FAMILY);
                        
                    int style = Integer.parseInt(
                        fontNode.getAttribute(TailConfig.ATTR_STYLE));
                        
                    int size = Integer.parseInt(
                        fontNode.getAttribute(TailConfig.ATTR_SIZE));
                        
                    defaultFont_ = new Font(family, style, size);
                }
                else
                {
                    defaultFont_ = SwingUtil.getPreferredMonoFont();
                }
                 
                                
                // Iterate through each "tail" element, hydrate the 
                // corresponding TailConfig object
                for (Elements tails = 
                     jtailNode.getElements(TailConfig.ELEMENT_TAIL); 
                     tails.hasMoreElements();)
                {
                    Element tail = tails.next();
                    TailConfig props = TailConfig.unmarshal(tail);                    
                    
                    configs = (TailConfig[])
                        ArrayUtil.addElement(configs, props);
                }
            }
            catch (ParseException pe)
            {
                JSmartOptionPane.showExceptionMessageDialog(this, pe);
            }
        }
        
        return configs;
    }    
    
    
    /**
     * Saves the current configuration of all tail instances to 
     * $HOME/.jtail.xml
     */
    protected void saveConfiguration()
    {
        String userHome = System.getProperty("user.home");
        String filename = userHome + File.separator + ".jtail.xml";
     
        File configFile = new File(filename);

        try
        {
            Element jtailNode = new Element(ELEMENT_JTAIL);
            jtailNode.setAttribute(ATTR_HEIGHT, getSize().height + "");
            jtailNode.setAttribute(ATTR_WIDTH, getSize().width + "");
            jtailNode.setAttribute(ATTR_X, getLocation().x + "");
            jtailNode.setAttribute(ATTR_Y, getLocation().y + "");

            // Save default font
            Element fontNode = new Element(TailConfig.ELEMENT_FONT);
            
            fontNode.setAttribute(
                TailConfig.ATTR_FAMILY, getFont().getFamily());
                
            fontNode.setAttribute(
                TailConfig.ATTR_STYLE, getFont().getStyle() + "");
                
            fontNode.setAttribute(
                TailConfig.ATTR_SIZE, getFont().getSize() + "");            
            
            jtailNode.addElement(fontNode);
            
            // Save tail configurations
            logger_.debug("Saving "+tailMap_.size()+" configurations");

            List keys = new ArrayList();
            keys.addAll(tailMap_.keySet());
            
            // Flip collection so read in originally created order
            Collections.reverse(keys);            
            
            for (Iterator i = keys.iterator(); i.hasNext(); )
            {
                TailPane tail = (TailPane)tailMap_.get(i.next());
                TailConfig config = tail.getConfiguration();
                jtailNode.addElement(config.marshal());                
            }

            Document document = new Document();    
            document.setRoot(jtailNode);
            document.write(configFile);
            
            logger_.debug("\n" + document);
        }
        catch (IOException ioe)
        {
            JSmartOptionPane.showExceptionMessageDialog(this, ioe);                        
        }
    }
    
    
    /**
     * Applies configurations
     * 
     * @param  configs  Tail configurations
     */
    protected void applyConfiguration(TailConfig[] configs)
    {
        // Window size
        if (size_ != null)
            setSize(size_);
        else
            setSize(800,400);
        
        // Window location    
        if (location_ != null)
            setLocation(location_);
        else
            SwingUtil.centerWindow(this);
        
        // Tails left running since last save
        for (int i=0; i<configs.length; i++)
        {
            TailConfig config = configs[i];
            
            // Apply defaults if any
            if (config.getFont() == null)
                config.setFont(getDefaultFont());
                
            addTail(config);
        }
    }    
    

    /**
     * Adds listeners
     */
    protected void addListeners()
    {
        // Intercept closing of app to save configuration
        addWindowListener(new WindowListener());
        
        fileSelectionPane_.getFileExplorer().
            addJFileExplorerListener(new FileSelectionListener());
            
        fileSelectionPane_.getTailButton().
            addActionListener(new TailButtonListener());
    }
    
    
    /**
     * @return  Currently selected tail in the tabbed pane
     */
    protected TailPane getSelectedTail()
    {
        return (TailPane)tabbedPane_.getSelectedComponent();
    }
    
    
    /**
     * @return  Default font
     */
    protected Font getDefaultFont()
    {
        return defaultFont_;
    }
    

    /**
     * Unmarshals an XML element representing a TailProp object
     * 
     * @param   tail  Element representing a tailprops
     * @return  Fully populated TailProp
     */
    public static TailConfig unmarshal(Element jtailNode) throws IOException 
    {
        
        
//        TailConfig props = new TailConfig();
//        
//        // Handle tail element
//        props.setFilename(tail.getAttribute(ATTR_FILE));
//        props.setAutoScroll(
//            new Boolean(tail.getAttribute(ATTR_AUTOSCROLL)).booleanValue());
//        props.setShowLineNumbers(
//            new Boolean(tail.getAttribute(ATTR_LINENUMBERS)).booleanValue());
//        
//        // Handle font element    
//        Element fontNode = tail.getElement(ELEMENT_FONT);
//        String  family   = fontNode.getAttribute(ATTR_FAMILY);
//        int     style    = Integer.parseInt(fontNode.getAttribute(ATTR_STYLE));
//        int     size     = Integer.parseInt(fontNode.getAttribute(ATTR_SIZE));
//        props.setFont(new Font(family, style, size));
//            
//        return props;

        return null;
    }


    /**
     * Marshals from Java object representation to XML representation
     * 
     * @return  Tail XML node
     */
    public Element marshal()  throws IOException 
    {
//        // Tail element
//        Element tail = new Element(ELEMENT_TAIL);
//        tail.setAttribute(ATTR_FILE, getFilename());
//        tail.setAttribute(ATTR_AUTOSCROLL, 
//            new Boolean(isAutoScroll()).toString());
//        tail.setAttribute(ATTR_LINENUMBERS, 
//            new Boolean(isShowLineNumbers()).toString());
//        
//        // Font element    
//        Element font = new Element(ELEMENT_FONT);
//        font.setAttribute(ATTR_FAMILY, getFont().getFamily());
//        font.setAttribute(ATTR_STYLE, getFont().getStyle() + "");
//        font.setAttribute(ATTR_SIZE, getFont().getSize() + "");            
//        
//        // Make font child of tail
//        tail.addElement(font);
//        
//        return tail;

        return null;
    }


    //
    //  LISTENERS
    //

    /**
     * Listener for the file explorer
     */
    private class FileSelectionListener extends JFileExplorerAdapter
    {
        /**
         * Tail files that have been double clicked on
         * 
         * @param  file  File double clicked
         */
        public void fileDoubleClicked(String file)
        {
            TailConfig config = new TailConfig(file, true, false, 
                SwingUtil.getPreferredMonoFont(), "");
                
            addTail(config);
        }
    }
    
    
    /**
     * Tail button listener
     */
    private class TailButtonListener implements ActionListener
    {
        /**
         * Tail button clicked
         * 
         * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
         */
        public void actionPerformed(ActionEvent e)
        {
            
            TailConfig config = new TailConfig(
                fileSelectionPane_.getFileExplorer().getFilePath(), 
                    true, false, SwingUtil.getPreferredMonoFont(), "");
            
            addTail(config);
        }
    }
    
    
    /**
     * Tail button listener
     */
    private class CloseButtonListener implements ActionListener
    {
        /**
         * The source is the closeButton on the tail pane. 
         * Get the tailPane from the tailMap using the button as
         * the key and then temove the tail pane from the tabbed pane
         * and them remove the tailpane from the tailmap itself.
         * 
         * @param  e  ActionEvent
         */
        public void actionPerformed(ActionEvent e)
        {
            Object closeButton = e.getSource();
            TailPane pane = (TailPane)tailMap_.get(closeButton);
            tabbedPane_.remove(pane);        
            tailMap_.remove(closeButton);
            
            // Save the configuration since the tail is gone
            saveConfiguration();
        }
    }


    /**
     * Saves the configuration when the application is being closed
     */
    private class WindowListener extends WindowAdapter
    {
        public void windowClosing(WindowEvent e)
        {
            saveConfiguration();
        }
    }

    //
    //  ACTIONS
    //
    
    /**
     * Action to exit the application. The configurations
     * are saved before exit.
     */
    private class ExitAction extends AbstractAction
    {
        public ExitAction()
        {
            super("Exit");
            putValue(MNEMONIC_KEY, new Integer('x'));    
            putValue(SHORT_DESCRIPTION, "Exits JTail");
            putValue(ACCELERATOR_KEY, 
                KeyStroke.getKeyStroke(KeyEvent.VK_X, Event.CTRL_MASK));
        }
        
        /**
         * Saves the configuration and exits the application
         */
        public void actionPerformed(ActionEvent e)
        {
            saveConfiguration();
            System.exit(0);
        }
    }


    /**
     * Action to save configurations
     */
    private class SaveAction extends AbstractAction
    {
        public SaveAction()
        {
            super("Save Settings");
            putValue(MNEMONIC_KEY, new Integer('S'));
            putValue(SHORT_DESCRIPTION, "Saves tail configuration");
            putValue(ACCELERATOR_KEY, 
                KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK));
        }
    
        /**
         * Saves the configuration
         */
        public void actionPerformed(ActionEvent e)
        {
            saveConfiguration();
            statusBar_.setStatus("Saved configuration");
        }
    }


    /**
     * Generates a file with intermittent output so that the file can be
     * tailed.
     */
    private class CreateFileAction extends AbstractAction
    {
        public CreateFileAction()
        {
            super("Create test file");
            putValue(MNEMONIC_KEY, new Integer('C'));
            putValue(SHORT_DESCRIPTION, "Create test file to tail");
            
            putValue(LONG_DESCRIPTION, 
                "Create a file to tail for testing purposes");
            
            putValue(ACCELERATOR_KEY, 
                KeyStroke.getKeyStroke(KeyEvent.VK_C, Event.CTRL_MASK));
        }
    
        /**
         * Saves the configuration
         */
        public void actionPerformed(ActionEvent e)
        {
            String file = "c:\\crap.txt";
            FileStuffer stuffer = new FileStuffer(new File(file), 100);
            stuffer.start();
            statusBar_.setStatus("Created " + file + " for tailing");
        }
    }

    
    /**
     * Select Font action
     */
    private class SetFontAction extends AbstractAction 
        implements IFontDialogListener
    {
        private Font lastFont_;
        
        public SetFontAction()
        {
            super("Set font ..");
            putValue(MNEMONIC_KEY, new Integer('t'));
            
            putValue(SHORT_DESCRIPTION, 
                "Sets the font of the tail output");
                
            putValue(ACCELERATOR_KEY, 
                KeyStroke.getKeyStroke(KeyEvent.VK_T, Event.CTRL_MASK));
        }
    
        /**
         * Pops up the Font selection dialog
         */
        public void actionPerformed(ActionEvent e)
        {
            lastFont_ = getSelectedTail().getTailFont();
            
            // Show font selection dialog with font from the current
            // tail set as the default selected font
            FontSelectionDialog fsd = 
                new FontSelectionDialog(JTail.this, false, 
                    lastFont_);
            fsd.setTitle("Select font");
            
            fsd.addFontDialogListener(this);
            SwingUtil.centerWindow(fsd);
            fsd.setVisible(true);
        }
        
        /**
         * Sets the font of the currently selected tail to the chosen 
         * font in the font selection dialog
         * 
         * @param  fontPanel  Font selection panel in the dialog box
         */
        public void applyButtonPressed(FontSelectionPanel fontPanel)
        {
            try
            {
                getSelectedTail().setTailFont(
                    fontPanel.getSelectedFont());
            }
            catch (FontSelectionException fse)
            {
                JSmartOptionPane.showExceptionMessageDialog(
                    JTail.this, fse);
            }
        }

        /**
         *  Cancel button was pressed 
         */
        public void cancelButtonPressed(FontSelectionPanel fontPanel)
        {
            // Restore last font
            getSelectedTail().setTailFont(lastFont_);            
        }
        
        /**
         * OK button was pressed
         */
        public void okButtonPressed(FontSelectionPanel fontPanel)
        {
            try
            {
                getSelectedTail().setTailFont(
                    fontPanel.getSelectedFont());
            }
            catch (FontSelectionException fse)
            {
                JSmartOptionPane.showExceptionMessageDialog(
                    JTail.this, fse);
            }
        }
    }
}