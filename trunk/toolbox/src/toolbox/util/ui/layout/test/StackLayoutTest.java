package toolbox.util.ui.layout.test;

import java.awt.Color;
import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.SwingUtil;
import toolbox.util.ui.JSmartButton;
import toolbox.util.ui.JSmartLabel;
import toolbox.util.ui.JSmartTextArea;
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
	 * Entrypoint.
	 * 
	 * @param args None recognized
	 */
    public static void main(String[] args) throws Exception
    {
        SwingUtil.setPreferredLAF();
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

        JFrame frame = new JFrame("StackLayoutTest");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Container contentPane = frame.getContentPane();
        
        JPanel p;
        JSmartTextArea t;
        Color color = Color.red;

        contentPane.setLayout(new StackLayout(StackLayout.VERTICAL));

        contentPane.add(new JSmartLabel("StackLayout Demo & Tester")).
            setFont(SwingUtil.getPreferredMonoFont());

        p = addHPanel(frame);
        p.add("Left", new JSmartButton("Left"));
        
        p.add("Wide", new JSmartLabel(
            "A Wide label going on and on and on and on and on and on and on"));
        
        p.add("Right", new JSmartButton("Right"));

        contentPane.add("Fill", new JScrollPane(t = new JSmartTextArea()));
        
        t.append(
            "This is a Fill'ed Text Area.\n" 
            + "With some text in it.\nIt is stretchy");

        contentPane.add("Center", new JSmartLabel("Below is an hrule"));
        contentPane.add("Wide Height=3 Flush", new JPanel()).setBackground(color);
        contentPane.add("Wide Tall*2", new JScrollPane(t = new JSmartTextArea()));
        
        t.append(
            "This is another Text Area,\n"
            + "with some text in it.\n"
            + "It's height is filled with weight 2");
        
        contentPane.add("Wide", new JSmartButton("A wide Button"));
        contentPane.add("Wide Height=3 Flush", new JPanel()).setBackground(color);
        contentPane.add("Center", new JSmartLabel("Ugly, but shows placement"));

        p = addHPanel(frame);
        p.add("Left", new JSmartButton("Left"));
        p.add("Left", new JSmartButton("Left, too"));
        p.add("Right", new JSmartButton("Right"));

        p = addHPanel(frame);
        p.add("Left", new JSmartButton("Left"));
        p.add("Center", new JSmartButton("Center"));
        p.add("Center", new JSmartButton("Center,too"));
        p.add("Right", new JSmartButton("Right"));

        p = addHPanel(frame);
        p.add("Left", new JSmartButton("Left"));
        p.add("Center", new JSmartButton("Center"));
        p.add("Left", new JSmartButton("Left!!"));
        p.add("Right", new JSmartButton("Right"));

        p = addHPanel(frame);
        p.add("Left", new JSmartButton("Left"));
        p.add("Center", new JSmartButton("Center"));
        p.add("Right", new JSmartButton("Right"));
        p.add("Right", new JSmartButton("Right, too"));

        contentPane.add("Wide Height=3 Flush", new JPanel()).setBackground(color);

        p = addHPanel(frame);
        p.add("Left", new JSmartLabel("Horizontal panel w/50pixel Strut"));
        p.add("Left Width=3 Height=50 Flush", new JPanel()).
            setBackground(color);
        
        p.add("Top", new JSmartButton("Top"));
        p.add("Center", new JSmartButton("Center"));
        p.add("Bottom", new JSmartButton("Bottom"));
        p.add("Tall", new JSmartButton("Tall"));

        frame.pack();
        SwingUtil.centerWindow(frame);
        frame.setVisible(true);
    }

    
    JPanel addHPanel(JFrame f)
    {
        JPanel p = new JPanel();
        p.setLayout(new StackLayout(StackLayout.HORIZONTAL));
        f.getContentPane().add("Wide Flush", p);
        return p;
    }
}