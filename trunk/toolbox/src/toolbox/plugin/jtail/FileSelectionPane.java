package toolbox.jtail;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.apache.log4j.Logger;
import toolbox.util.ui.JFileExplorer;

/**
 * Custom explorer panel for JTail window
 */
public class FileSelectionPane extends JPanel implements ActionListener
{
    /** Logger */
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
    //  Implementation
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
		tailButton_.addActionListener(this);
		buttonPanel.add(tailButton_);
		add(buttonPanel, BorderLayout.SOUTH);
	}

	
	/**
	 * ActionListener interface
	 */
	public void actionPerformed(ActionEvent e)
	{
		Object obj = e.getSource();
		
		if(obj == tailButton_)
			tailButtonClicked();
		else
			logger_.warn("No handler for " + e);
	}
	
    
	/**
	 * Tail button 
	 */
	protected void tailButtonClicked()
	{
		logger_.info("tail");
	}
    
    //--------------------------------------------------------------------------
    //  Accessors/Mutators
    //--------------------------------------------------------------------------
        
    /**
     * Returns the file explorer component
     * 
     * @return JFileExplorer
     */
    public JFileExplorer getFileExplorer()
    {
        return fileExplorer_;
    }


    /**
     * Returns the tailButton.
     * 
     * @return JButton
     */
    public JButton getTailButton()
    {
        return tailButton_;
    }


    /**
     * Sets the tailButton.
     * 
     * @param tailButton The tailButton to set
     */
    public void setTailButton(JButton tailButton)
    {
        tailButton_ = tailButton;
    }
}
