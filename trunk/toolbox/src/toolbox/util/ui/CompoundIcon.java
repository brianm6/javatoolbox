
package toolbox.util.ui;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.UIManager;

public class CompoundIcon implements Icon
{

    private Icon left, right;
    private int gap;


    public CompoundIcon(Icon left, Icon right, int gap)
    {
        if (left == null || right == null)
            throw new NullPointerException();
        this.left = left;
        this.right = right;
        this.gap = gap;
    }


    public int getIconHeight()
    {
        return Math.max(left.getIconHeight(), right.getIconHeight());
    }


    public int getIconWidth()
    {
        return left.getIconWidth() + gap + right.getIconWidth();
    }


    public void paintIcon(Component c, Graphics g, int x, int y)
    {
        int h = getIconHeight();
        left.paintIcon(c, g, x, y + (h - left.getIconHeight()) / 2);
        right.paintIcon(c, g, x + left.getIconWidth() + gap, y
            + (h - right.getIconHeight()) / 2);
    }


    public static void main(String[] args) throws MalformedURLException
    {
        System.setProperty("proxyHost", "proxy.swacorp.com");
        System.setProperty("proxyPort", "8080");
        
        JPanel p = new JPanel(new GridLayout(0, 1));
        URL url1 = new URL(
            "http://forum.java.sun.com/images/toolbox_settings.gif");
        URL url2 = new URL(
            "http://forum.java.sun.com/images/toolbox_watches.gif");
        URL url3 = new URL(
            "http://forum.java.sun.com/images/toolbox_dukedollars.gif");

        Icon radioIcon = UIManager.getIcon("RadioButton.icon");
        ButtonGroup bg = new ButtonGroup();
        add(url1, p, bg, radioIcon);
        add(url2, p, bg, radioIcon);
        add(url3, p, bg, radioIcon);

        JFrame f = new JFrame("Example");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().add(p);
        f.setSize(400,400);
        //f.setLocationRelativeTo(null);
        f.setVisible(true);
    }


    static void add(URL url, JPanel p, ButtonGroup bg, Icon radioIcon)
    {
        JRadioButton btn = new JRadioButton(new CompoundIcon(radioIcon,
            new ImageIcon(url), 3));
        p.add(btn);
        bg.add(btn);
    }
}