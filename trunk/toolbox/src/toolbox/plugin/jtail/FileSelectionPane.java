package toolbox.jtail;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import toolbox.util.ui.JFileExplorer;

/**
 * Custom file explorer panel for JTail. Just adds a "Tail" button to the
 * bottom of the panel.
 */
public class FileSelectionPane extends JPanel
{
    private static final Logger logger_ =
        Logger.getLogger(FileSelectionPane.class);
    
    private JFileExplorer  fileExplorer_;
    private JButton        tailButton_;
    
    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Default constructor
     */
    public FileSelectionPane()
    {
        this(null);
    }
    
    /**
     * Creates a FileSelectionPane with the given directory selected
     *
     * @param  dir  Directory to select by default
     */
    public FileSelectionPane(String dir)
    {
        super(new BorderLayout(), false);
        buildView();
        
        if (dir != null)
            fileExplorer_.selectFolder(dir);        
    }
    
    //--------------------------------------------------------------------------
    // Private
    //--------------------------------------------------------------------------
    
    /**
     * Builds the GUI
     */
    protected void buildView()
    {
        // File explorer         
        fileExplorer_ = new JFileExplorer(false);
        add(fileExplorer_, BorderLayout.CENTER);
        
        // Button panel    
        JPanel buttonPanel = new JPanel(new FlowLayout());
        tailButton_ = new JButton("Tail");
        buttonPanel.add(tailButton_);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    //--------------------------------------------------------------------------
    //  Accessors/Mutators
    //--------------------------------------------------------------------------
        
    /**
     * @return File explorer
     */
    public JFileExplorer getFileExplorer()
    {
        return fileExplorer_;
    }

    /**
     * @return Tail button
     */
    public JButton getTailButton()
    {
        return tailButton_;
    }
}