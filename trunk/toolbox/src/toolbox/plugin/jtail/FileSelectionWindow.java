package toolbox.jtail;

import java.awt.event.ActionListener;

import javax.swing.JInternalFrame;

import org.apache.log4j.Category;

/**
 * Simple internal frame wrapper for a FileSelectionPane
 */
public class FileSelectionWindow extends JInternalFrame 
{
    /** Pane with contains the file explorer and the button panel **/	
    private FileSelectionPane fileSelectionPane_;
    
    		
	/**
	 * Default constructor
	 */
	public FileSelectionWindow()
	{
		super("File Explorer", true, true, true, true);
		build();	
	}

	
	/**
	 * Builds the GUI
	 */
	protected void build()
	{
        fileSelectionPane_ = new FileSelectionPane();
		setContentPane(fileSelectionPane_);
	}
    
    /**
     * Returns the fileSelectionPane.
     * 
     * @return FileSelectionPane
     */
    public FileSelectionPane getFileSelectionPane()
    {
        return fileSelectionPane_;
    }

}