package toolbox.plugin.docviewer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JScrollPane;

import toolbox.util.FileUtil;
import toolbox.util.SwingUtil;
import toolbox.util.ui.JSmartTextArea;

/**
 * A viewer to for text documents.
 */
public class TextViewer implements DocumentViewer
{
    /**
     * The contents of the file are dumped into this text area.
     */
    private JSmartTextArea textArea_;
    
    /**
     * Wrapper around the text area. This scrollpane is returned by
     * getComponent().
     */
    private JScrollPane scroller_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a TextViewer.
     */
    public TextViewer()
    {
    }

    //--------------------------------------------------------------------------
    // DocumentViewer Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.plugin.docviewer.DocumentViewer#startup(java.util.Map)
     */
    public void startup(Map init) throws DocumentViewerException
    {
        textArea_ = new JSmartTextArea();
        textArea_.setAntiAliased(SwingUtil.getDefaultAntiAlias());
        scroller_ = new JScrollPane(textArea_);
    }

    
    /**
     * @see toolbox.plugin.docviewer.DocumentViewer#view(java.io.File)
     */
    public void view(File file) throws DocumentViewerException
    {
        try
        {
            String text = FileUtil.getFileContents(file.getCanonicalPath());
            textArea_.setText(text);
            textArea_.setCaretPosition(0);
        }
        catch (FileNotFoundException e)
        {
            throw new DocumentViewerException(e);
        }
        catch (IOException e)
        {
            throw new DocumentViewerException(e);
        }
    }

    
    /**
     * @see toolbox.plugin.docviewer.DocumentViewer#view(java.io.InputStream)
     */
    public void view(InputStream is) throws DocumentViewerException
    {
        throw new UnsupportedOperationException("Not implemented");
    }

    
    /**
     * @see toolbox.plugin.docviewer.DocumentViewer#canView(java.io.File)
     */
    public boolean canView(File file)
    {
        // View all files
        return true;
    }

    
    /**
     * @see toolbox.plugin.docviewer.DocumentViewer#getViewableFileTypes()
     */
    public String[] getViewableFileTypes()
    {
        return null;
    }

    
    /**
     * @see toolbox.plugin.docviewer.DocumentViewer#getComponent()
     */
    public JComponent getComponent()
    {
        return scroller_;
    }

    
    /**
     * @see toolbox.plugin.docviewer.DocumentViewer#getName()
     */
    public String getName()
    {
        return "Text Viewer";
    }

    
    /**
     * @see toolbox.plugin.docviewer.DocumentViewer#shutdown()
     */
    public void shutdown()
    {
    }
}
