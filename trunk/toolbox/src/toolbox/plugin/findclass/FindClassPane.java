package toolbox.findclass;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
import toolbox.util.ThreadUtil;
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

    /**
     * Entrypoint
     */    
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
        searchField.addActionListener(this);
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
        resultList.setFont(new Font("Lucida Console", Font.PLAIN, 10));
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
     */    
    public void actionPerformed(ActionEvent e)
    {
        Object obj = e.getSource();
        
        if (obj == searchButton || obj == searchField)
        {
            ThreadUtil.run(
                this, "searchButtonClicked", new Object[] { "String" });
        }
    }
    
    /**
     * Execute search
     */
    public void searchButtonClicked(String s) 
    {
        try
        {
            String search = searchField.getText().trim();
            
            if (StringUtil.isNullOrEmpty(search))
                return;
            
            resultModel.clear();
                
            FindClass fc = new FindClass();
            fc.addFindClassListener(this);
            fc.findClass(search, true);             
            ThreadUtil.sleep(2000);
            resultList.ensureIndexIsVisible(resultModel.getSize()-1);
            System.out.println("Done");
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(this, ExceptionUtil.getStackTrace(e), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * IFindClassListener
     */
    public void classFound(FindClassResult searchResult)
    {
        resultModel.addElement(searchResult);
        statusLabel.setText(searchResult.toString());
        resultList.ensureIndexIsVisible(resultModel.getSize()-1);
    }    
}