package toolbox.util.ui.layout.test;

import java.awt.Container;

import javax.swing.JButton;
import javax.swing.JFrame;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.SwingUtil;
import toolbox.util.ui.JSmartButton;
import toolbox.util.ui.layout.Direction;
import toolbox.util.ui.layout.StackLayout;

/**
 * Unit test for StackLayout.
 */
public class StackLayoutTest extends TestCase
{
    private static final Logger logger_ =
        Logger.getLogger(StackLayoutTest.class);
        
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    /**
     * Entrypoint
     * 
     * @param args None recognized
     */
    public static void main(String[] args)
    {
        TestRunner.run(StackLayoutTest.class);
    }

    //--------------------------------------------------------------------------
    //  Unit Tests
    //--------------------------------------------------------------------------

    /**
     * Tests Stack layout.
     */
    public void testStackLayout()
    {
        logger_.info("Running testStackLayout...");
        
        JFrame jf = new JFrame("StackLayout Unit Test");
        Container f = jf.getContentPane();
        f.setLayout(new StackLayout());
        JButton b1 = new JSmartButton("One");
        JButton b2 = new JSmartButton("Two");
//        JButton b3 = new JSmartButton("Three");
//        JButton b4 = new JSmartButton("Four");
//        JButton b5 = new JSmartButton("Five");
//        JButton b6 = new JSmartButton("Six");
//        JButton b7 = new JSmartButton("Seven");
//        JButton b8 = new JSmartButton("Eight");
//        JTextField t1 = new JSmartTextField(4);
//        JTextField t2 = new JSmartTextField(20);
//        JTextArea t3 = new JSmartTextArea(5, 30);

//        b2.setFont(new Font("serif", Font.PLAIN, 24));
//        f.add(new JSmartLabel("Some buttons:"));
          f.add(b1, new Integer(Direction.LEFT));
//        f.add(new JSmartLabel("A long label:"));
          f.add(b2, new Integer(Direction.LEFT));
//        f.add(b3);
//        f.add(new JSmartLabel("Short label:"));
//        f.add(b4);
//        f.add(b5);
//        f.add(b6);
//        f.add(b7);
//        f.add(b8);
//        f.add(new JSmartLabel("Text:"));
//        f.add(t1);
//        f.add(new JSmartLabel("More text:"));
//        f.add(t2);
//        f.add(new JSmartLabel("miles"));
//        f.add(new JSmartLabel("A text area:"));
//        f.add(new JScrollPane(t3));
        jf.pack();
        SwingUtil.centerWindow(jf);
        jf.setVisible(true);
    }
}