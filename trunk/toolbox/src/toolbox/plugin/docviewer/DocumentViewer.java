package toolbox.plugin.pdf;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

import javax.swing.JComponent;

/**
 * Generic interface that defines API necessary to view a document.
 */
public interface DocumentViewer
{
	/**
	 * Starts up or initializes the document viewer.
	 * 
	 * @param init Initialization properties.
	 * @throws DocumentViewerException on error.
	 */
    public void startup(Map init) throws DocumentViewerException;
    
    
    /**
     * Views the given file.
     * 
     * @param file File containing document to view.
     * @throws DocumentViewerException on error.
     */
    public void view(File file) throws DocumentViewerException;
    
    
    /**
     * Views the document associated with the given inputstream.
     * 
     * @param is InputStream to read the document from.
     * @throws DocumentViewerException on error.
     */
    public void view(InputStream is) throws DocumentViewerException;

	
	/**
	 * Determines if a given file type extension is viewable by this document
	 * viewer.
	 * 
	 * @param fileType File extension for the file type.
	 * @return True if the file type is viewable or false otherwise.
 	 */    
    public boolean isViewable(String fileType);
    
    
    /**
     * Returns a list of all the file types that this document viewer supports.
     * 
     * @return String array of file types.
     */
    public String[] getViewableFileTypes();
    
    
    /**
     * Returns the UI component of the document viewer.
     * 
     * @return JComponent
     */
    public JComponent getComponent();
    
    
    /**
     * Shuts down the document viewer.
     */
    public void shutdown();
}