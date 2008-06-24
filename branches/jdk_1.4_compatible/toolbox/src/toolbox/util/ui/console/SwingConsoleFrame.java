package toolbox.util.ui.console;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.WindowConstants;

import toolbox.util.ui.JSmartFrame;

/**
 * Frame containing a {@link SwingConsole}.
 */
public class SwingConsoleFrame extends JSmartFrame 
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * Sink for input/output.
     */
    private SwingConsole console_;

    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    /**
     * Entrypoint.
     * 
     * @param args None.
     */
    public static void main(String[] args)
    {
         JFrame f = new SwingConsoleFrame("Hello", 10, 80);
         f.pack();
         f.setVisible(true);
    }
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a UIConsole.
     * 
     * @param title Window title.
     * @param rows Number of visible rows.
     * @param columns Number of visible columns.
     */
    public SwingConsoleFrame(String title, int rows, int columns)
    {
        super(title);
        setJMenuBar(buildMenuBar());
        console_ = new SwingConsole("SwingConsole", rows, columns);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(BorderLayout.CENTER, console_.getView());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------
    
    /**
     * Builds the menu bar.
     * 
     * @return JMenuBar
     */
    protected JMenuBar buildMenuBar()
    {
        // File
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic('F');
        fileMenu.addSeparator();

        // Exit
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.setMnemonic('x');
        exitItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent action)
            {
                dispose();
                System.exit(0);
            }
        });
        fileMenu.add(exitItem);


        // Edit
        JMenu editMenu = new JMenu("Edit");
        editMenu.setMnemonic('E');

        // Clear
        JMenuItem clearItem = new JMenuItem("Clear all");
        clearItem.setMnemonic('a');
        clearItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent action)
            {
                getConsole().clear();
            }
        });
        editMenu.add(clearItem);
        editMenu.addSeparator();

//        // Cut
//        JMenuItem cutItem = new JMenuItem("Cut");
//        cutItem.setMnemonic('t');
//        cutItem.addActionListener(new ActionListener()
//        {
//            public void actionPerformed(ActionEvent action)
//            {
//                console_.cut();
//            }
//        });
//        editMenu.add(cutItem);
//
//        // Copy
//        JMenuItem copyItem = new JMenuItem("Copy");
//        copyItem.setMnemonic('C');
//        copyItem.addActionListener(new ActionListener()
//        {
//            public void actionPerformed(ActionEvent action)
//            {
//                SwingConsoleFrame.this.console_.copy();
//            }
//        });
//        editMenu.add(copyItem);

        // Paste at end
        JMenuItem pasteItem = new JMenuItem("Paste at end");
        pasteItem.setMnemonic('P');
        pasteItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent action)
            {
                //console_.paste();
            }
        });
        editMenu.add(pasteItem);

        // Add menu bar
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        return menuBar;
    }
    
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------

    /**
     * Returns the text area within the UI console.
     * 
     * @return UIConsoleArea
     */
    public SwingConsole getConsole()
    {
        return console_;
    }
}