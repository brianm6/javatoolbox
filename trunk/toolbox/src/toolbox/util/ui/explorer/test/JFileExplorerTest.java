package toolbox.util.ui.test;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Category;

import toolbox.util.ThreadUtil;
import toolbox.util.ui.JFileExplorer;
import toolbox.util.ui.JFileExplorerListener;

/**
 * Unit test for JFileExplorer
 */
public class JFileExplorerTest extends TestCase
{
    /** Logger **/
    private static final Category logger_ = 
        Category.getInstance(JFileExplorerTest.class);
        
        
    /**
     * Entrypoint
     * 
     * param  args  None recognized
     */
    public static void main(String[] args)
    {
        TestRunner.run(JFileExplorerTest.class);
        //new JFileExplorerTest("").testJFileExplorer();
    }
    
    
    /**
     * Constructor for JFileExplorerTest.
     * 
     * @param arg0
     */
    public JFileExplorerTest(String arg0)
    {
        super(arg0);
    }
    
    
    /** 
     * Tests launcher
     */
    public void testJFileExplorer()
    {
        /* just launch the file explorer in a jframe..nuttin else */
        JFrame testFrame = new TestFrame();
        testFrame.pack();
        testFrame.setVisible(true);
        //ThreadUtil.join(10000);
    }
    
    
    /**
     * Test class
     */
    static class TestFrame extends JFrame implements JFileExplorerListener, 
        ActionListener
    {
        /** Textfield for testing **/
        private JTextField testField;
        
        /** Button for testing **/
        private JButton    testButton;
        
        /** Explorer for testing **/
        private JFileExplorer jfe;
        
        /** 
         * Creates a test frame 
         */
        public TestFrame()
        {
            super("JFileExplorer");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            jfe = new JFileExplorer(false);
            jfe.addJFileExplorerListener(this);
            getContentPane().setLayout(new BorderLayout());
            getContentPane().add(jfe, BorderLayout.CENTER);
            
            JPanel testPanel = new JPanel(new FlowLayout());
            testField = new JTextField(15);
            testButton = new JButton("Set folder");
            testButton.addActionListener(this);
            testPanel.add(new JLabel("Folder"));
            testPanel.add(testField);
            testPanel.add(testButton);
            getContentPane().add(testPanel, BorderLayout.SOUTH);
        }        


        /**
         * @param  file  File double clicked 
         */
        public void fileDoubleClicked(String file)
        {
            logger_.info("file " + file + " double clicked");
        }
        
        
        /**
         * @param  folder  Folder that was selected
         */
        public void folderSelected(String folder)
        {
            logger_.info("folder " + folder + " selected");
        }
        
        
        public void folderDoubleClicked(String folder)
        {
            logger_.info("folder " + folder + " double clicked");    
        }
                
                
        /**
         * @param  e  Action performed
         */
        public void actionPerformed(ActionEvent e)
        {
            jfe.selectFolder(this.testField.getText());
        }
    }
}
