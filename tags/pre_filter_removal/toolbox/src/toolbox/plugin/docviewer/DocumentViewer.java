package toolbox.plugin.docviewer;

import java.io.File;
import java.io.InputStream;

import javax.swing.JComponent;

import toolbox.util.service.Destroyable;
import toolbox.util.service.Initializable;
import toolbox.util.service.Nameable;

/**
 * Generic interface that defines an API necessary to view any arbitrary 
 * document based on its file type (extension). 
 * <p>
 * TODO: Extend to also support mime/types.
 * 
 * @see toolbox.plugin.docviewer.DocumentViewerPlugin
 */
public interface DocumentViewer extends Initializable, Destroyable, Nameable
{
    /**
     * Views the document associated with the given file.
     * 
     * @param file File associated with the document to view.
     * @throws DocumentViewerException on error.
     */
    void view(File file) throws DocumentViewerException;
    
    
    /**
     * Views the document associated with the given InputStream.
     * 
     * @param is InputStream to read the document from.
     * @throws DocumentViewerException on error.
     */
    void view(InputStream is) throws DocumentViewerException;

    
    /**
     * Returns true if the given file is viewable by this DocumentViewer based 
     * on the file's extension, false otherwise.
     * 
     * @param file File to check.
     * @return boolean 
      */    
    boolean canView(File file);
    
    
    /**
     * Returns a list of all the file types that this document viewer supports.
     * The list of extensions is not prefixed with a dot.
     * 
     * @return String[]
     */
    String[] getViewableFileTypes();
    
    
    /**
     * Returns the UI component of this document viewer.
     * 
     * @return JComponent
     */
    JComponent getComponent();
}