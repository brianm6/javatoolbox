package toolbox.jtail;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

import toolbox.util.ui.JFileExplorer;

/**
 * Custom file explorer panel for JTail. Just adds a "Tail" button to the
 * bottom of the panel.
 */
public class FileSelectionPane extends JPanel
{
    /**
     * File explorer used to select the file to tail
     */    
    private JFileExplorer fileExplorer_;
    
    /**
     * Button that will tail the currently selected file in the file explorer
     */
    private JButton tailButton_;
    
    /**
     * Button that will tail the currently selected file and aggregate the 
     * output with the currently active TailPane
     */
    private JButton aggregateButton_;
    
    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a FileSelectionPane
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
        aggregateButton_ = new JButton("Aggregate");
        buttonPanel.add(tailButton_);
        buttonPanel.add(aggregateButton_);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    //--------------------------------------------------------------------------
    //  Accessors/Mutators
    //--------------------------------------------------------------------------
        
    /**
     * Returns the file explorer.
     * 
     * @return File explorer
     */
    public JFileExplorer getFileExplorer()
    {
        return fileExplorer_;
    }

    /**
     * Returns the tail button.
     * 
     * @return Tail button
     */
    public JButton getTailButton()
    {
        return tailButton_;
    }

    /**
     * Returns the aggregate button.
     * 
     * @return Aggregate button
     */
    public JButton getAggregateButton()
    {
        return aggregateButton_;
    }
}