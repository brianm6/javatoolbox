package toolbox.plugin.docviewer;

import java.io.File;
import java.io.InputStream;

import javax.swing.JComponent;

import toolbox.util.service.Destroyable;
import toolbox.util.service.Initializable;
import toolbox.util.service.Nameable;

/**
 * Generic interface that defines API necessary to view a document.
 * 
 * @see toolbox.plugin.docviewer.DocumentViewerPlugin
 */
public interface DocumentViewer extends Initializable, Destroyable, Nameable
{
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
}