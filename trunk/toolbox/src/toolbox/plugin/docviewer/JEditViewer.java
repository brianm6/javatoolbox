package toolbox.plugin.docviewer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;

import org.apache.log4j.Logger;

import org.jedit.syntax.BatchFileTokenMarker;
import org.jedit.syntax.CCTokenMarker;
import org.jedit.syntax.CTokenMarker;
import org.jedit.syntax.HTMLTokenMarker;
import org.jedit.syntax.JavaScriptTokenMarker;
import org.jedit.syntax.JavaTokenMarker;
import org.jedit.syntax.PerlTokenMarker;
import org.jedit.syntax.PropsTokenMarker;
import org.jedit.syntax.SQLTokenMarker;
import org.jedit.syntax.ShellScriptTokenMarker;
import org.jedit.syntax.TokenMarker;
import org.jedit.syntax.XMLTokenMarker;

import toolbox.jedit.JEditTextArea;
import toolbox.jedit.JavaDefaults;
import toolbox.util.FileUtil;
import toolbox.util.FontUtil;

/**
 * A viewer to for popular text documents with syntax hiliting.
 */
public class JEditViewer implements DocumentViewer
{
    private static final Logger logger_ = Logger.getLogger(JEditViewer.class);
    
    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------
    
    /**
     * Map of file extensions to their corresponding TokenMarker class.
     */
    private static final Map EXT_MAP;
    
    //--------------------------------------------------------------------------
    // Static Initializers
    //--------------------------------------------------------------------------
    
    static
    {
        EXT_MAP = new HashMap();
        EXT_MAP.put("java", JavaTokenMarker.class);
        EXT_MAP.put("xml", XMLTokenMarker.class);
        EXT_MAP.put("xsl", XMLTokenMarker.class);
        EXT_MAP.put("bat", BatchFileTokenMarker.class);
        EXT_MAP.put("properties", PropsTokenMarker.class);
        EXT_MAP.put("props", PropsTokenMarker.class);
        EXT_MAP.put("sh", ShellScriptTokenMarker.class);
        EXT_MAP.put("sql", SQLTokenMarker.class);
        EXT_MAP.put("html", HTMLTokenMarker.class);
        EXT_MAP.put("htm", HTMLTokenMarker.class);
        EXT_MAP.put("pl", PerlTokenMarker.class);
        EXT_MAP.put("js", JavaScriptTokenMarker.class);
        EXT_MAP.put("c", CTokenMarker.class);
        EXT_MAP.put("h", CTokenMarker.class);
        EXT_MAP.put("cc", CCTokenMarker.class);
        EXT_MAP.put("cpp", CCTokenMarker.class);
    }
    
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
     * Creates a JEditViewer
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
    }

    
    /**
     * @see toolbox.plugin.docviewer.DocumentViewer#view(java.io.File)
     */
    public void view(File file) throws DocumentViewerException
    {
        try
        {
            Class c = 
                (Class) EXT_MAP.get(FileUtil.getExtension(file).toLowerCase());
            
            if (c != null)
            {
                try
                {
                    textArea_ = new JEditTextArea(
                        (TokenMarker) c.newInstance(), new JavaDefaults());
                    
                }
                catch (Exception e)
                {
                    logger_.warn("Error instantiating " + c.getName() + ".");
                    textArea_ = new JEditTextArea();
                } 
            }
            else
            {    
                textArea_ = new JEditTextArea();
            }

            textArea_.getPainter().setFont(FontUtil.getPreferredMonoFont());
            String text = FileUtil.getFileContents(file.getCanonicalPath());
            textArea_.setText(text);
            textArea_.scrollTo(0,0);
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
        textArea_ = null;
    }
}