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

import org.apache.log4j.Logger;

import toolbox.util.SwingUtil;
import toolbox.util.ui.JFileExplorer;
import toolbox.util.ui.JFileExplorerListener;

/**
 * Unit test for JFileExplorer
 */
public class JFileExplorerTest extends TestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(JFileExplorerTest.class);
        
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
            
    /**
     * Entrypoint
     * 
     * @param  args  None recognized
     */
    public static void main(String[] args)
    {
        TestRunner.run(JFileExplorerTest.class);
    }
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Default constructor
     */
    public JFileExplorerTest() throws Exception
    {
        SwingUtil.setPreferredLAF();
    }
    
    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /** 
     * Tests launcher
     */
    public void testJFileExplorer()
    {
        // Just launch the file explorer in a jframe..nuttin else
        JFrame testFrame = new TestFrame();
        testFrame.pack();
        SwingUtil.centerWindow(testFrame);
        testFrame.setVisible(true);
    }
    
    //--------------------------------------------------------------------------
    // Inner Classes
    //--------------------------------------------------------------------------
    
    /**
     * Test class
     */
    static class TestFrame extends JFrame implements JFileExplorerListener, 
        ActionListener
    {
        /** Textfield for testing */
        private JTextField testField_;
        
        /** Button for testing */
        private JButton testButton_;
        
        /** Explorer for testing */
        private JFileExplorer jfe_;
        
        /** 
         * Creates a test frame 
         */
        public TestFrame()
        {
            super("JFileExplorer");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            jfe_ = new JFileExplorer(false);
            jfe_.addJFileExplorerListener(this);
            getContentPane().setLayout(new BorderLayout());
            getContentPane().add(jfe_, BorderLayout.CENTER);
            
            JPanel testPanel = new JPanel(new FlowLayout());
            testField_ = new JTextField(15);
            testButton_ = new JButton("Set folder");
            testButton_.addActionListener(this);
            testPanel.add(new JLabel("Folder"));
            testPanel.add(testField_);
            testPanel.add(testButton_);
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
            jfe_.selectFolder(testField_.getText());
        }
    }
}