package toolbox.jtail;

import javax.swing.JInternalFrame;
import javax.swing.JDesktopPane;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JMenuBar;
import javax.swing.JFrame;

import java.awt.event.*;
import java.awt.*;

public class InternalFrameDemo extends JFrame {
    JDesktopPane desktop;

    public InternalFrameDemo() {
        super("InternalFrameDemo");

        //Make the big window be indented 50 pixels from each edge 
        //of the screen.
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset, 
                  screenSize.width - inset*2, 
                  screenSize.height-inset*2);

        //Quit this app when the big window closes.
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        //Set up the GUI.
        desktop = new JDesktopPane(); //a specialized layered pane
        createFrame(); //Create first window
        setContentPane(desktop);
        setJMenuBar(createMenuBar());

        //Make dragging faster:
        desktop.putClientProperty("JDesktopPane.dragMode", "outline");
    }

    protected JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu menu = new JMenu("Document");
        menu.setMnemonic(KeyEvent.VK_D);
        JMenuItem menuItem = new JMenuItem("New");
        menuItem.setMnemonic(KeyEvent.VK_N);
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createFrame();
            }
        });
        menu.add(menuItem);
        menuBar.add(menu);

        return menuBar;
    }

    protected void createFrame() {
        MyInternalFrame frame = new MyInternalFrame();
	frame.setVisible(true); //necessary as of kestrel
        desktop.add(frame);
        try {
            frame.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {}
    }

    public static void main(String[] args) {
        InternalFrameDemo frame = new InternalFrameDemo();
        frame.setVisible(true);
    }
}
