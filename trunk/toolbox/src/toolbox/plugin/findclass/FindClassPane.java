package toolbox.findclass;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.apache.log4j.Category;

import toolbox.util.DateTimeUtil;
import toolbox.util.MathUtil;
import toolbox.util.StringUtil;
import toolbox.util.SwingUtil;
import toolbox.util.ThreadUtil;
import toolbox.util.ui.JFileExplorer;
import toolbox.util.ui.JFileExplorerAdapter;
import toolbox.util.ui.JSmartOptionPane;
import toolbox.util.ui.ThreadSafeTableModel;

/**
 * JFindClass is a GUI front end to FindClass
 */
public class JFindClass extends JFrame
{
    /** Logger **/
    private static final Category logger_ = 
        Category.getInstance(JFindClass.class);
    
    private JTextField  searchField_;
    private JButton     searchButton_;
    private JLabel      statusLabel_;
    
    private JCheckBox   ignoreCaseCheckBox_;
    
    private JList            pathList_;
    private DefaultListModel pathModel_;
    
    private JTable            resultTable_;
    private DefaultTableModel resultTableModel_;
    private JScrollPane       resultPane_;
    private int               resultCount_;
    
    /** Result table columns **/    
    private String[] resultColumns_ = new String[] 
    {
        "Num", 
        "Source", 
        "Class File",
        "Size", 
        "Timestamp"
    };

    private FindClass findClass_;
    private JFileExplorer explorer_;

    
    /**
     * Entrypoint
     * 
     * @param  args  None recognized
     */    
    public static void main(String[] args)
    {
        JFindClass jfc = new JFindClass();
        jfc.setVisible(true);
    }

    
    /**
     * Constructor for JFindClass
     */
    public JFindClass()
    {
        this("JFindClass");
    }

    
    /**
     * Constructor for JFindClass
     * 
     * @param  title  Window title
     */
    public JFindClass(String title)
    {
        super(title);
        buildView();
        init();
        pack();
        SwingUtil.centerWindow(this);
    }


    /**
     * Initiailizes 
     */
    protected void init()
    {
        findClass_ = new FindClass();
        findClass_.addFindClassListener(new FindClassHandler());        
        List targets = findClass_.getSearchTargets();
        
        for (Iterator i = targets.iterator(); i.hasNext(); 
            pathModel_.addElement(i.next()));
    }


    /**
     * Builds the GUI and to the contentPane
     */
    protected void buildView()
    {
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        // Search Panel
        JLabel searchLabel = new JLabel("Find Class");

        ActionListener actionHandler = new ActionHandler();
        
        searchField_ = new JTextField(20);
        searchField_.addActionListener(actionHandler);
        searchField_.setFont(SwingUtil.getPreferredMonoFont());
        
        searchButton_ = new JButton("Find");
        searchButton_.addActionListener(actionHandler);
        
        ignoreCaseCheckBox_ = new JCheckBox("Ignore Case", true);
                
        JPanel searchPanel = new JPanel(new FlowLayout());
        searchPanel.add(searchLabel);
        searchPanel.add(searchField_);
        searchPanel.add(searchButton_);
        searchPanel.add(new JLabel("      "));
        searchPanel.add(ignoreCaseCheckBox_);
        
        contentPane.add(searchPanel, BorderLayout.NORTH);

        // Path & Explorer Panel
        JLabel pathListLabel = new JLabel("Classpath");
        pathModel_ = new DefaultListModel(); 
        pathList_  = new JList(pathModel_);
        pathList_.setFont(SwingUtil.getPreferredMonoFont());
        
        JPanel pathPanel = new JPanel(new BorderLayout());
        pathPanel.add(pathListLabel, BorderLayout.NORTH);
        pathPanel.add(new JScrollPane(pathList_), BorderLayout.CENTER);
        
        explorer_ = new JFileExplorer();
        explorer_.addJFileExplorerListener(new JFileExplorerHandler());
        
        //JPanel explorerPanel = new JPanel(new BorderLayout());
        //explorerPanel.add(explorer_, BorderLayout.WEST);
        //explorerPanel.add(pathPanel, BorderLayout.EAST);
        
        JSplitPane topSplitPane = 
            new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, explorer_, pathPanel);
        
        // Result panel        
        JLabel resultLabel = new JLabel("Results");
        
        resultTableModel_ = new ThreadSafeTableModel(resultColumns_,0);
        resultTable_      = new JTable(resultTableModel_);
        resultPane_       = new JScrollPane(resultTable_);
        tweakTable();
        
        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.add(resultLabel, BorderLayout.NORTH);
        resultPanel.add(resultPane_, BorderLayout.CENTER);
       
        
        // Split pane
        JSplitPane splitPane = 
            new JSplitPane(
                JSplitPane.VERTICAL_SPLIT, 
                topSplitPane, 
                resultPanel);
        
        contentPane.add(splitPane, BorderLayout.CENTER);

        // Status bar        
        statusLabel_ = new JLabel("Enter a regular expression and hit Find!");
        contentPane.add(statusLabel_, BorderLayout.SOUTH);
        
        // Post tweaks
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);        
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
     * Executes search based on the contents of the search field
     * 
     * @param  s  Search string (can be a regular expression)
     */
    public void searchButtonClicked(String s) 
    {
        try
        {
            String search = searchField_.getText().trim();
            
            logger_.debug("Searching for " + search);
            
            if (StringUtil.isNullOrEmpty(search))
            {
                setStatus("Enter class to search");
            }
            else
            {
                resultTableModel_.setNumRows(0);
                resultCount_      = 0;
                
                Object results[]  = findClass_.findClass(
                    search, ignoreCaseCheckBox_.isSelected());             
                    
                setStatus(results.length + " matches found");
            }
        }
        catch (Exception e)
        {
            JSmartOptionPane.showExceptionMessageDialog(JFindClass.this, e);
        }
    }
    
    
    /**
     * Sets the text of the status bar
     * 
     * @param  s  Status text
     */
    protected void setStatus(String s)
    {
        statusLabel_.setText(s);
    }


    /**
     * Handles actions generated by search controls 
     */
    class ActionHandler implements ActionListener
    {
        /**
         * ActionListener
         * 
         * @param  e  Action event to handle
         */    
        public void actionPerformed(ActionEvent e)
        {
            Object obj = e.getSource();
            
            if (obj == searchButton_ || obj == searchField_)
            {
                /* spawn search on separate thread */
                ThreadUtil.run(
                    JFindClass.this, 
                    "searchButtonClicked", 
                    new Object[] { "String" });
            }
        }
    }


    /**
     * Handler for events generated by JFileExplorer
     */
    class FindClassHandler extends FindClassAdapter
    {
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
            setStatus("Searching " + target + " ...");
            pathList_.setSelectedValue(target, true);    
        }

        
        /**
         * When a search is cancelled, update the status bar
         */
        public void searchCancelled()
        {
            setStatus("Search cancelled");
        }    
    }

    
    /**
     * Handler class for the file explorer
     */
    class JFileExplorerHandler extends JFileExplorerAdapter
    {
        /**
         * Adds a directory to the path list
         * 
         * @param  folder  Directory to add
         */
        public void folderDoubleClicked(String folder)
        {
            findClass_.addSearchTarget(folder);
            syncPathModel();
        }
 
        /**
         * Adds a file to the path list
         * 
         * @param  file  File to add
         */       
        public void fileDoubleClicked(String file)
        {
            findClass_.addSearchTarget(file);
            syncPathModel();
        }
     
        /**
         * Syncs the path list model with the search targets in FindClass
         */   
        protected void syncPathModel()
        {
            pathModel_.clear();
            List targets = findClass_.getSearchTargets();
            
            for (int i=0; 
                 i<targets.size(); 
                 pathModel_.addElement(targets.get(i++)));
        }
    }
 
 
    /**
     * Alternating color cell renderer to make the results table easier on
     * the eyes
     */   
    class AlternatingCellRenderer extends DefaultTableCellRenderer
    {
        /**
         * Returns the default table cell renderer.
         *
         * @param   table       JTable
         * @param   value       Value to assign to the cell at [row, column]
         * @param   isSelected  True if the cell is selected
         * @param   isFocus     True if cell has focus
         * @param   row         Row of the cell to render
         * @param   column      Column of the cell to render
         * 
         * @return  Ddfault table cell renderer
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
}