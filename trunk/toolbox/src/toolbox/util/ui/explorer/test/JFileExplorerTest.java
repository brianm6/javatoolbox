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
     * Presents JFileExplorer in a window so that it can be tested 
     * interactively.
     * <p>
     * <ul>
     * <li>The listener can be verified since it prints out messages on event
     *     notification.
     * <li>selectFolder() can be tested by entering a file path in the
     *     textfield and clicking on the Set Folder button.
     * <li>Refresh can be tested by clicking on the icon in the infobar
     * <ul>
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
            /**
             * @see toolbox.util.ui.explorer.FileExplorerListener#
             *      fileDoubleClicked(java.lang.String)
             */
            public void fileDoubleClicked(String file)
            {
                logger_.info("file " + file + " double clicked");
            }
            
            /**
             * @see toolbox.util.ui.explorer.FileExplorerListener#
             *      folderSelected(java.lang.String)
             */
            public void folderSelected(String folder)
            {
                logger_.info("folder " + folder + " selected");
            }
            
            /**
             * @see toolbox.util.ui.explorer.FileExplorerListener#
             *      folderDoubleClicked(java.lang.String)
             */
            public void folderDoubleClicked(String folder)
            {
                logger_.info("folder " + folder + " double clicked");    
            }
            
            /**
             * @see toolbox.util.ui.explorer.FileExplorerListener#
             *      fileSelected(java.lang.String)
             */
            public void fileSelected(String file)
            {
                logger_.info("file " + file + " selected");            
            }
            
            /**
             * @see java.awt.event.ActionListener#actionPerformed(
             *      java.awt.event.ActionEvent)
             */
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