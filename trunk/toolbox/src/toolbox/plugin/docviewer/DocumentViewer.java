package toolbox.plugin.pdf;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

import javax.swing.JComponent;

/**
 * Generic interface that defines API necessary to view a document
 */
public interface DocumentViewer
{
    public void startup(Map init) throws DocumentViewerException;
    public void view(File file) throws DocumentViewerException;
    public void view(InputStream is) throws DocumentViewerException;
    
    public boolean isViewable(String fileType);
    public String[] getViewableFileTypes();
    
    public JComponent getComponent();
    public void shutdown();
}
