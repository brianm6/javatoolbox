package toolbox.util.ui.layout;
import java.awt.Container;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import toolbox.util.ui.JSmartButton;
import toolbox.util.ui.JSmartLabel;
import toolbox.util.ui.JSmartTextArea;
import toolbox.util.ui.JSmartTextField;


/**
 * LayoutTest.
 */
public class LayoutTest
{
    /**
     * Entrypoint.
     * 
     * @param args None recognized.
     */
    public static void main(String[] args)
    {
        paragraphLayout();
        packerLayout();
        gridLayoutPlus();
        basicGridLayout();
        clockLayout();
    }

    
    /**
     * Tests paragraphLayout. 
     */
    public static void paragraphLayout()
    {
        JFrame jf = new JFrame("ParagraphLayout");
        Container f = jf.getContentPane();
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
        JTextArea t3 = new JSmartTextArea(5, 30);

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
        f.add(
            new JSmartLabel("A text area:"),
            ParagraphLayout.NEW_PARAGRAPH_TOP);
        f.add(t3);
        jf.pack();
        jf.show();
    }

    
    /**
     * Tests packerLayout() 
     */
    public static void packerLayout()
    {
        JFrame jf = new JFrame("PackerLayout");
        Container f = jf.getContentPane();
        f.setLayout(new PackerLayout());
        JButton b1 = new JSmartButton("One");
        JButton b2 = new JSmartButton("Two");
        JButton b3 = new JSmartButton("Three");
        JButton b4 = new JSmartButton("Four");
        JButton b5 = new JSmartButton("Five");
        JButton b6 = new JSmartButton("Six");

        b2.setFont(new Font("serif", Font.PLAIN, 24));
        f.add(b1);
        f.add(b2, PackerLayout.LEFT_CENTER);
        f.add(b3, PackerLayout.BOTTOM_CENTER_FILL);
        f.add(b4, PackerLayout.TOP_CENTER_FILL);
        f.add(b5, PackerLayout.TOP_LEFT);
        f.add(b6, PackerLayout.RIGHT_CENTER);
        jf.pack();
        jf.show();
    }

    
    /**
     * Tests GridLayoutPlus. 
     */
    public static void gridLayoutPlus()
    {
        JFrame jf = new JFrame("GridLayoutPlus");
        Container f = jf.getContentPane();
        GridLayoutPlus glp = new GridLayoutPlus(0, 3, 10, 10);
        glp.setColWeight(1, 2);
        glp.setColWeight(2, 1);
        glp.setRowWeight(2, 1);
        f.setLayout(glp);
        for (int r = 0; r < 6; r++)
        {
            for (int c = 0; c < 3; c++)
            {
                f.add(new JSmartButton(r + "," + c));
            }
        }
        jf.pack();
        jf.show();
    }

    
    /**
     * Tests BasicGridLayout. 
     */
    public static void basicGridLayout()
    {
        JFrame jf = new JFrame("BasicGridLayout");
        Container f = jf.getContentPane();
        BasicGridLayout l = new BasicGridLayout(0, 3, 10, 10);
        l.setColWeight(1);
        l.setRowWeight(1);
        l.setIncludeInvisible(false);
        f.setLayout(l);
        for (int r = 0; r < 6; r++)
        {
            for (int c = 0; c < 3; c++)
            {
                JButton b;
                f.add(b = new JSmartButton(r + "," + c));
                b.setVisible((r + c) % 4 != 0);
            }
        }
        jf.pack();
        jf.show();
    }

    
    /**
     * Tests ClockLayout. 
     */
    public static void clockLayout()
    {
        JFrame jf = new JFrame("ClockLayout");
        Container f = jf.getContentPane();
        f.setLayout(new ClockLayout());
        for (int r = 0; r < 12; r++)
        {
            f.add(new JSmartButton(r + ""));
        }
        jf.pack();
        jf.show();
    }

}
