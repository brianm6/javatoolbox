package toolbox.findclass;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;

import toolbox.util.ExceptionUtil;
import toolbox.util.StringUtil;
import toolbox.util.SwingUtil;
import toolbox.util.ThreadUtil;
import toolbox.util.ui.JSmartOptionPane;
import toolbox.util.ui.SafeListModel;

/**
 * JFindClass
 */
public class JFindClass extends JFrame implements ActionListener,
    IFindClassListener
{
    private JLabel searchLabel;
    private JTextField searchField;
    private JButton searchButton;

    private JList  pathList;
    private DefaultListModel pathModel;
    
    private JList  resultList;
    private DefaultListModel resultModel;
    private JScrollPane resultPane;
    
    private JLabel statusLabel;

    private FindClass findClass;
    
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
    }


    /**
     * Initiailizes 
     */
    protected void init()
    {
        findClass = new FindClass();
        findClass.addFindClassListener(this);        
        List targets = findClass.getSearchTargets();
        
        for (Iterator i = targets.iterator(); i.hasNext(); 
            pathModel.addElement(i.next()));
    }


    /**
     * Builds the view
     */
    protected void buildView()
    {
        getContentPane().setLayout(new BorderLayout());

        //====================================================
        
        searchLabel = new JLabel("Find Class");
        searchField = new JTextField(15);
        searchField.addActionListener(this);
        searchField.setFont(SwingUtil.getMonospacedFont());
        searchButton = new JButton("Find");
        searchButton.addActionListener(this);
                
        JPanel searchPanel = new JPanel(new FlowLayout());
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        
        getContentPane().add(searchPanel, BorderLayout.NORTH);

        //====================================================

        JLabel pathListLabel = new JLabel("Classpath");
        pathModel = new DefaultListModel(); 
        pathList = new JList(pathModel);
        pathList.setFont(SwingUtil.getMonospacedFont());
        
        JPanel pathPanel = new JPanel(new BorderLayout());
        pathPanel.add(pathListLabel, BorderLayout.NORTH);
        pathPanel.add(new JScrollPane(pathList), BorderLayout.CENTER);
        
        //====================================================
        
        JLabel resultListLabel = new JLabel("Results");
        resultModel = new SafeListModel();
        resultList = new JList(resultModel);
        resultList.setFont(SwingUtil.getMonospacedFont());
        resultPane = new JScrollPane(resultList);;
        
        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.add(resultListLabel, BorderLayout.NORTH);
        resultPanel.add(resultPane, BorderLayout.CENTER);
        
        //====================================================
        
        JSplitPane splitPane = 
            new JSplitPane(JSplitPane.VERTICAL_SPLIT, pathPanel, resultPanel);
        
        getContentPane().add(splitPane, BorderLayout.CENTER);

        //====================================================
        
        statusLabel = new JLabel("Enter regular expressions and hit Find!");
        getContentPane().add(statusLabel, BorderLayout.SOUTH);
        
        //====================================================
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);        
    }


    /**
     * ActionListener
     * 
     * @param  e  Action event to handle
     */    
    public void actionPerformed(ActionEvent e)
    {
        Object obj = e.getSource();
        
        if (obj == searchButton || obj == searchField)
        {
            /* spawn search on separate thread */
            ThreadUtil.run(
                this, "searchButtonClicked", new Object[] { "String" });
        }
    }
    
    /**
     * Execute search
     * 
     * @param  s  Search regular expression
     */
    public void searchButtonClicked(String s) 
    {
        try
        {
            String search = searchField.getText().trim();
            
            if (StringUtil.isNullOrEmpty(search))
                return;
            
            resultModel.clear();
            Object results[] = findClass.findClass(search, true);             
            //ThreadUtil.sleep(2000);
            //resultList.ensureIndexIsVisible(resultModel.getSize()-1);
            statusLabel.setText(results.length + " matches found");
        }
        catch (Exception e)
        {
            JSmartOptionPane.showDetailedMessageDialog(this, e.getMessage(), 
                ExceptionUtil.getStackTrace(e), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * IFindClassListener
     */
    public void classFound(FindClassResult searchResult)
    {
        resultModel.addElement(searchResult);
        //statusLabel.setText(searchResult.toString());
        //resultList.ensureIndexIsVisible(resultModel.getSize()-1);
    }
    
    public void searchingTarget(String target)
    {
        statusLabel.setText("Searching " + target + " ...");    
    }    
      
}