package toolbox.util.ui.layout.test;

import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.junit.UITestCase;
import toolbox.util.ui.JSmartButton;
import toolbox.util.ui.JSmartLabel;
import toolbox.util.ui.JSmartTextField;
import toolbox.util.ui.layout.ParagraphLayout;

/**
 * Unit test for ParagraphLayout.
 */
public class ParagraphLayoutTest extends UITestCase
{
    private static final Logger logger_ =
        Logger.getLogger(ParagraphLayoutTest.class);
        
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
        TestRunner.run(ParagraphLayoutTest.class);
    }

    //--------------------------------------------------------------------------
    //  Unit Tests
    //--------------------------------------------------------------------------

    /**
     * Tests paragraph layout.
     */
    public void testParagraphLayout()
    {
        logger_.info("Running testParagraphLayout...");
        
        JPanel f = new JPanel(); 
        f.setLayout(new ParagraphLayout());
        JButton b1 = new JSmartButton("One");
        JButton b2 = new JSmartButton("Two");
        JButton b3 = new JSmartButton("Three");
        JButton b4 = new JSmartButton("Four");
        JButton b5 = new JSmartButton("Five");
        JButton b6 = new JSmartButton("Six");
        JButton b7 = new JSmartButton("Seven");
        JButton b8 = new JSmartButton("Eight");
        JTextField t1 = new JSmartTextField(4);
        JTextField t2 = new JSmartTextField(20);
        JTextArea t3 = new JTextArea(5, 30);

        b2.setFont(new Font("serif", Font.PLAIN, 24));
        f.add(new JSmartLabel("Some buttons:"), ParagraphLayout.NEW_PARAGRAPH);
        f.add(b1);
        f.add(new JSmartLabel("A long label:"), ParagraphLayout.NEW_PARAGRAPH);
        f.add(b2);
        f.add(b3);
        f.add(new JSmartLabel("Short label:"), ParagraphLayout.NEW_PARAGRAPH);
        f.add(b4);
        f.add(b5, ParagraphLayout.NEW_LINE);
        f.add(b6);
        f.add(b7);
        f.add(b8, ParagraphLayout.NEW_LINE);
        f.add(new JSmartLabel("Text:"), ParagraphLayout.NEW_PARAGRAPH);
        f.add(t1);
        f.add(new JSmartLabel("More text:"), ParagraphLayout.NEW_PARAGRAPH);
        f.add(t2);
        f.add(new JSmartLabel("miles"));
        
        f.add(new JSmartLabel("A text area:"), 
                ParagraphLayout.NEW_PARAGRAPH_TOP);
        
        f.add(new JScrollPane(t3));
        
        launchInDialog(f);
    }
}