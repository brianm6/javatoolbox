package toolbox.findclass;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import toolbox.util.StringUtil;
import toolbox.util.ThreadUtil;
import toolbox.util.ui.SafeListModel;
/**
 * enclosing_type 
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
    
    public static void main(String[] args)
    {
        JFindClass jfc = new JFindClass();
        jfc.setVisible(true);
    }
    
    /**
     * Constructor for JFindClass.
     * @throws HeadlessException
     */
    public JFindClass() throws HeadlessException
    {
        this("JFindClass");
    }

    
    /**
     * Constructor for JFindClass.
     * @param title
     * @throws HeadlessException
     */
    public JFindClass(String title) throws HeadlessException
    {
        super(title);
        buildView();
        
        pack();
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
        searchButton = new JButton("Find");
        searchButton.addActionListener(this);
                
        JPanel searchPanel = new JPanel(new FlowLayout());
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        
        getContentPane().add(searchPanel, BorderLayout.NORTH);

        //====================================================

        JLabel pathListLabel = new JLabel("Classpath");
        pathList = new JList();
        
        JPanel pathPanel = new JPanel(new BorderLayout());
        pathPanel.add(pathListLabel, BorderLayout.NORTH);
        pathPanel.add(new JScrollPane(pathList), BorderLayout.CENTER);
        
        //====================================================
        
        JLabel resultListLabel = new JLabel("Results");
        resultModel = new SafeListModel();
        resultList = new JList(resultModel);        
        resultPane = new JScrollPane(resultList);;
        
        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.add(resultListLabel, BorderLayout.NORTH);
        resultPanel.add(resultPane, BorderLayout.CENTER);
        
        //====================================================
        
        JSplitPane splitPane = 
            new JSplitPane(JSplitPane.VERTICAL_SPLIT, pathPanel, resultPanel);
        
        getContentPane().add(splitPane, BorderLayout.CENTER);
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);        
    }

    /**
     * ActionListener
     */    
    public void actionPerformed(ActionEvent e)
    {
        Object obj = e.getSource();
        
        if (obj == searchButton)
            ThreadUtil.run(this, "searchButtonClicked", new Object[] { "String" });
    }
    
    /**
     * Execute search
     */
    public void searchButtonClicked(String s)
    {
        String search = searchField.getText().trim();
        
        if (StringUtil.isNullOrEmpty(search))
            return;
        
        resultModel.clear();
            
        FindClass fc = new FindClass();
        fc.addFindClassListener(this);
        fc.findClass(search, true);
        resultList.ensureIndexIsVisible(resultModel.getSize()-1);
        System.out.println("Done");
    }

    /**
     * IFindClassListener
     */
    public void classFound(FindClassResult searchResult)
    {
        resultModel.addElement(searchResult);
        //resultList.ensureIndexIsVisible(resultModel.getSize()-1);
    }    
}