package toolbox.plugin.docviewer;

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
    void startup(Map init) throws DocumentViewerException;
    
    
    /**
     * Views the given file.
     * 
     * @param file File containing document to view.
     * @throws DocumentViewerException on error.
     */
    void view(File file) throws DocumentViewerException;
    
    
    /**
     * Views the document associated with the given inputstream.
     * 
     * @param is InputStream to read the document from.
     * @throws DocumentViewerException on error.
     */
    void view(InputStream is) throws DocumentViewerException;

    
    /**
     * Determines if a given file type extension is viewable by this document
     * viewer.
     * 
     * @param file File to test if this viewer is capable of viewing it.
     * @return True if the file is viewable by the plugin, false. 
      */    
    boolean canView(File file);
    
    
    /**
     * Returns a list of all the file types that this document viewer supports.
     * 
     * @return String array of file types.
     */
    String[] getViewableFileTypes();
    
    
    /**
     * Returns the UI component of the document viewer.
     * 
     * @return JComponent
     */
    JComponent getComponent();
    
    
    /**
     * Returns the UI friendly name of this document viewer.
     * 
     * @return String
     */
    String getName();
    
    
    /**
     * Shuts down the document viewer.
     */
    void shutdown();
}