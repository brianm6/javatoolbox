package toolbox.util.ui.explorer.test;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.junit.UITestCase;
import toolbox.util.ui.JSmartButton;
import toolbox.util.ui.JSmartLabel;
import toolbox.util.ui.JSmartTextField;
import toolbox.util.ui.explorer.FileExplorerListener;
import toolbox.util.ui.explorer.JFileExplorer;

/**
 * Unit test for JFileExplorer.
 */
public class JFileExplorerTest extends UITestCase
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

        JPanel p = new JPanel(new BorderLayout()); 
        final JTextField testField = new JSmartTextField(15);
        final JFileExplorer jfe = new JFileExplorer(false);
        JButton testButton = new JSmartButton("Set folder");
        
        JPanel testPanel = new JPanel(new FlowLayout());
        testPanel.add(new JSmartLabel("Folder"));
        testPanel.add(testField);
        testPanel.add(testButton);
        
        p.add(jfe, BorderLayout.CENTER);
        p.add(testPanel, BorderLayout.SOUTH);
        
        class ExplorerListener implements FileExplorerListener, ActionListener
        {
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
                jfe.selectFolder(testField.getText());
            }
        }

        jfe.addFileExplorerListener(new ExplorerListener());
        testButton.addActionListener(new ExplorerListener());
        launchInDialog(p);
    }
}