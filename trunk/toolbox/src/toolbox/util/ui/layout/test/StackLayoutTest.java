package toolbox.util.ui.layout.test;

import java.awt.Container;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import junit.framework.TestCase;
import junit.textui.TestRunner;

import toolbox.util.SwingUtil;
import toolbox.util.ui.layout.Direction;
import toolbox.util.ui.layout.StackLayout;

/**
 * StackLayout unit test
 */
public class StackLayoutTest extends TestCase
{
    /**
     * Entrypoint
     * 
     * @param  args  None
     */
    public static void main(String[] args)
    {
        TestRunner.run(StackLayoutTest.class);
    }

    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Constructor for StackLayoutTest.
     * 
     * @param arg0 Name
     */
    public StackLayoutTest(String arg0)
    {
        super(arg0);
    }

    //--------------------------------------------------------------------------
    //  Unit Tests
    //--------------------------------------------------------------------------

    /**
     * Tests Stack layout
     */
    public void testStackLayout()
    {
        JFrame jf = new JFrame("StackLayout Unit Test");
        Container f = jf.getContentPane();
        f.setLayout(new StackLayout());
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

//        b2.setFont(new Font("serif", Font.PLAIN, 24));
//        f.add(new JLabel("Some buttons:"));
          f.add(b1, new Integer(Direction.LEFT));
//        f.add(new JLabel("A long label:"));
          f.add(b2, new Integer(Direction.LEFT));
//        f.add(b3);
//        f.add(new JLabel("Short label:"));
//        f.add(b4);
//        f.add(b5);
//        f.add(b6);
//        f.add(b7);
//        f.add(b8);
//        f.add(new JLabel("Text:"));
//        f.add(t1);
//        f.add(new JLabel("More text:"));
//        f.add(t2);
//        f.add(new JLabel("miles"));
//        f.add(new JLabel("A text area:"));
//        f.add(new JScrollPane(t3));
        jf.pack();
        SwingUtil.centerWindow(jf);
        jf.setVisible(true);
    }
}