package toolbox.util.ui.test;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.SwingUtil;
import toolbox.util.ui.explorer.JFileExplorer;
import toolbox.util.ui.explorer.FileExplorerListener;
import toolbox.util.ui.JSmartButton;
import toolbox.util.ui.JSmartLabel;
import toolbox.util.ui.JSmartTextField;

/**
 * Unit test for JFileExplorer.
 */
public class JFileExplorerTest extends TestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(JFileExplorerTest.class);
        
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
            
    /**
     * Entrypoint.
     * 
     * @param args None recognized
     * @throws Exception on error
     */
    public static void main(String[] args) throws Exception
    {
        SwingUtil.setPreferredLAF();
        TestRunner.run(JFileExplorerTest.class);
    }
    
    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /** 
     * Tests JFileExplorer.
     */
    public void testJFileExplorer()
    {
        logger_.info("Running testJFileExplorer...");
        
        // Just launch the file explorer in a jframe..nuttin else
        JDialog dialog = new TestDialog();
        dialog.pack();
        SwingUtil.centerWindow(dialog);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);
        logger_.info("After setVisible()");
        dialog.dispose();
        logger_.info("After dispose()");
        
    }
    
    //--------------------------------------------------------------------------
    // Inner Classes
    //--------------------------------------------------------------------------
    
    /**
     * Test dialog.
     */
    static class TestDialog extends JDialog 
                            implements FileExplorerListener, 
                                       ActionListener
    {
        private JTextField testField_;
        private JButton testButton_;
        private JFileExplorer jfe_;
        //private static JFrame frame_ = ;
        
        public TestDialog()
        {
            super((JFrame) null , "JFileExplorer", true);

            jfe_ = new JFileExplorer(false);
            jfe_.addFileExplorerListener(this);
            getContentPane().setLayout(new BorderLayout());
            getContentPane().add(jfe_, BorderLayout.CENTER);
            
            JPanel testPanel = new JPanel(new FlowLayout());
            testField_ = new JSmartTextField(15);
            testButton_ = new JSmartButton("Set folder");
            testButton_.addActionListener(this);
            testPanel.add(new JSmartLabel("Folder"));
            testPanel.add(testField_);
            testPanel.add(testButton_);
            getContentPane().add(testPanel, BorderLayout.SOUTH);
        }        

        public void fileDoubleClicked(String file)
        {
            logger_.info("file " + file + " double clicked");
        }
        
        public void folderSelected(String folder)
        {
            logger_.info("folder " + folder + " selected");
        }
        
        public void folderDoubleClicked(String folder)
        {
            logger_.info("folder " + folder + " double clicked");    
        }
                
        public void fileSelected(String file)
        {
            logger_.info("file " + file + " selected");            
        }
                
        public void actionPerformed(ActionEvent e)
        {
            jfe_.selectFolder(testField_.getText());
        }
    }
}