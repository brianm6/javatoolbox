package toolbox.util.ui.layout.test;

import java.awt.Color;
import java.awt.Container;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.junit.UITestCase;
import toolbox.util.FontUtil;
import toolbox.util.ui.JSmartButton;
import toolbox.util.ui.JSmartLabel;
import toolbox.util.ui.JSmartTextArea;
import toolbox.util.ui.layout.StackLayout;

/**
 * Unit test for StackLayout.
 */
public class StackLayoutTest extends UITestCase
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

        JPanel cp = new JPanel();
        
        JPanel p;
        JSmartTextArea t;
        Color color = Color.red;

        cp.setLayout(new StackLayout(StackLayout.VERTICAL));

        cp.add(new JSmartLabel("StackLayout Demo & Tester")).
            setFont(FontUtil.getPreferredMonoFont());

        p = addHPanel(cp);
        p.add("Left", new JSmartButton("Left"));
        
        p.add("Wide", new JSmartLabel(
            "A Wide label going on and on and on and on and on and on and on"));
        
        p.add("Right", new JSmartButton("Right"));

        cp.add("Fill", new JScrollPane(t = new JSmartTextArea()));
        
        t.append(
            "This is a Fill'ed Text Area.\n" 
            + "With some text in it.\nIt is stretchy");

        cp.add("Center", new JSmartLabel("Below is an hrule"));
        cp.add("Wide Height=3 Flush", new JPanel()).setBackground(color);
        cp.add("Wide Tall*2", new JScrollPane(t = new JSmartTextArea()));
        
        t.append(
            "This is another Text Area,\n"
            + "with some text in it.\n"
            + "It's height is filled with weight 2");
        
        cp.add("Wide", new JSmartButton("A wide Button"));
        cp.add("Wide Height=3 Flush", new JPanel()).setBackground(color);
        cp.add("Center", new JSmartLabel("Ugly, but shows placement"));

        p = addHPanel(cp);
        p.add("Left", new JSmartButton("Left"));
        p.add("Left", new JSmartButton("Left, too"));
        p.add("Right", new JSmartButton("Right"));

        p = addHPanel(cp);
        p.add("Left", new JSmartButton("Left"));
        p.add("Center", new JSmartButton("Center"));
        p.add("Center", new JSmartButton("Center,too"));
        p.add("Right", new JSmartButton("Right"));

        p = addHPanel(cp);
        p.add("Left", new JSmartButton("Left"));
        p.add("Center", new JSmartButton("Center"));
        p.add("Left", new JSmartButton("Left!!"));
        p.add("Right", new JSmartButton("Right"));

        p = addHPanel(cp);
        p.add("Left", new JSmartButton("Left"));
        p.add("Center", new JSmartButton("Center"));
        p.add("Right", new JSmartButton("Right"));
        p.add("Right", new JSmartButton("Right, too"));

        cp.add("Wide Height=3 Flush", 
                new JPanel()).setBackground(color);

        p = addHPanel(cp);
        p.add("Left", new JSmartLabel("Horizontal panel w/50pixel Strut"));
        p.add("Left Width=3 Height=50 Flush", new JPanel()).
            setBackground(color);
        
        p.add("Top", new JSmartButton("Top"));
        p.add("Center", new JSmartButton("Center"));
        p.add("Bottom", new JSmartButton("Bottom"));
        p.add("Tall", new JSmartButton("Tall"));

        launchInDialog(cp);
    }

    
    JPanel addHPanel(Container jp)
    {
        JPanel p = new JPanel();
        p.setLayout(new StackLayout(StackLayout.HORIZONTAL));
        jp.add("Wide Flush", p);
        return p;
    }
}