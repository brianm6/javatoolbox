package toolbox.plugin.docviewer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.swing.JComponent;

import org.jedit.syntax.JavaTokenMarker;

import toolbox.jedit.JEditTextArea;
import toolbox.jedit.JavaDefaults;
import toolbox.util.FileUtil;
import toolbox.util.FontUtil;
import toolbox.util.SwingUtil;

/**
 * A viewer to for text documents.
 */
public class JEditViewer implements DocumentViewer
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * The contents of the file are dumped into this text area.
     */
    private JEditTextArea textArea_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a TextViewer.
     */
    public JEditViewer()
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
        //textArea_ = new JEditTextArea();
        //textArea_.setAntiAliased(SwingUtil.getDefaultAntiAlias());
        //scroller_ = new JScrollPane(textArea_);
    }

    
    /**
     * @see toolbox.plugin.docviewer.DocumentViewer#view(java.io.File)
     */
    public void view(File file) throws DocumentViewerException
    {
        try
        {
            if (FileUtil.getExtension(file).equalsIgnoreCase("java"))
            {
                textArea_ = new JEditTextArea(
                    new JavaTokenMarker(), new JavaDefaults()); 
            }
            else
            {    
                textArea_ = new JEditTextArea();
            }
            
            String text = FileUtil.getFileContents(file.getCanonicalPath());
            textArea_.setText(text);
            textArea_.scrollTo(0,0);
            
            textArea_.getPainter().setFont(
                FontUtil.increaseSize(FontUtil.getPreferredMonoFont(), -2));
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
        return textArea_;
    }

    
    /**
     * @see toolbox.plugin.docviewer.DocumentViewer#getName()
     */
    public String getName()
    {
        return "JEdit Viewer";
    }

    
    /**
     * @see toolbox.plugin.docviewer.DocumentViewer#shutdown()
     */
    public void shutdown()
    {
    }
}