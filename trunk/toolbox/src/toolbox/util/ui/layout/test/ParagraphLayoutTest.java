package toolbox.util.ui.layout.test;

import java.awt.Container;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import junit.framework.TestCase;
import junit.textui.TestRunner;

import toolbox.util.SwingUtil;
import toolbox.util.ui.layout.ParagraphLayout;

/**
 * Unit test for ParagraphLayout
 */
public class ParagraphLayoutTest extends TestCase
{
    /**
     * Entrypoint
     * 
     * @param  args  None
     */
    public static void main(String[] args)
    {
        TestRunner.run(ParagraphLayoutTest.class);
    }

    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Constructor for ParagraphLayoutTest.
     * 
     * @param  arg0  Test name
     */
    public ParagraphLayoutTest(String arg0)
    {
        super(arg0);
    }

    //--------------------------------------------------------------------------
    //  Unit Tests
    //--------------------------------------------------------------------------

    /**
     * Tests paragraph layout
     */
    public void testParagraphLayout()
    {
        JFrame jf = new JFrame("ParagraphLayout Unit Test");
        Container f = jf.getContentPane();
        f.setLayout(new ParagraphLayout());
        JButton b1 = new JButton("One");
        JButton b2 = new JButton("Two");
        JButton b3 = new JButton("Three");
        JButton b4 = new JButton("Four");
        JButton b5 = new JButton("Five");
        JButton b6 = new JButton("Six");
        JButton b7 = new JButton("Seven");
        JButton b8 = new JButton("Eight");
        JTextField t1 = new JTextField(4);
        JTextField t2 = new JTextField(20);
        JTextArea t3 = new JTextArea(5, 30);

        b2.setFont(new Font("serif", Font.PLAIN, 24));
        f.add(new JLabel("Some buttons:"), ParagraphLayout.NEW_PARAGRAPH);
        f.add(b1);
        f.add(new JLabel("A long label:"), ParagraphLayout.NEW_PARAGRAPH);
        f.add(b2);
        f.add(b3);
        f.add(new JLabel("Short label:"), ParagraphLayout.NEW_PARAGRAPH);
        f.add(b4);
        f.add(b5, ParagraphLayout.NEW_LINE);
        f.add(b6);
        f.add(b7);
        f.add(b8, ParagraphLayout.NEW_LINE);
        f.add(new JLabel("Text:"), ParagraphLayout.NEW_PARAGRAPH);
        f.add(t1);
        f.add(new JLabel("More text:"), ParagraphLayout.NEW_PARAGRAPH);
        f.add(t2);
        f.add(new JLabel("miles"));
        f.add(new JLabel("A text area:"), ParagraphLayout.NEW_PARAGRAPH_TOP);
        f.add(new JScrollPane(t3));
        jf.pack();
        SwingUtil.centerWindow(jf);
        jf.setVisible(true);
    }
}