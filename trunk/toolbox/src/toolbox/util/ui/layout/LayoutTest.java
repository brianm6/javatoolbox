package toolbox.util.ui.layout;
import java.awt.Container;

import javax.swing.JButton;
import javax.swing.JFrame;

import toolbox.util.ui.JSmartButton;


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
        gridLayoutPlus();
        basicGridLayout();
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
}
