package toolbox.plugin.docviewer;

import java.awt.BorderLayout;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;

import com.javio.webwindow.HTMLPane;
import com.javio.webwindow.WebWindow;

import toolbox.util.ArrayUtil;
import toolbox.util.FileUtil;
import toolbox.util.FontUtil;

/**
 * HTML document viewer that uses the Calpha HTML component.
 */
public class WebWindowViewer implements DocumentViewer
{
    /**
     * Base pane that houses the browser and button panel.
     */
    private JPanel viewerPane_;
    
    /**
     * HTML viewer component.
     */
    private WebWindow webWindow_;
    private HTMLPane webPane_;
        
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a CalphaViewer.
     */
    public WebWindowViewer()
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
        webWindow_ = new WebWindow();
        webPane_ = webWindow_.getHTMLPane();
        webPane_.setDefaultFont(FontUtil.getPreferredSerifFont());
        viewerPane_ = new JPanel(new BorderLayout());
        viewerPane_.add(webWindow_, BorderLayout.CENTER);
    }

    
    /**
     * @see toolbox.plugin.docviewer.DocumentViewer#view(java.io.File)
     */
    public void view(File file) throws DocumentViewerException
    {
        try
        {
            webPane_.loadPage(file.toURL());
        }
        catch (MalformedURLException e)
        {
            throw new DocumentViewerException(e);
        }        
    }

    
    /**
     * @see toolbox.plugin.docviewer.DocumentViewer#view(java.io.InputStream)
     */
    public void view(InputStream is) throws DocumentViewerException
    {
        webPane_.loadPage(new InputStreamReader(is), null);        
    }

    
    /**
     * @see toolbox.plugin.docviewer.DocumentViewer#canView(java.io.File)
     */
    public boolean canView(File file)
    {
        return ArrayUtil.contains(
                getViewableFileTypes(), FileUtil.getExtension(file));
    }

    
    /**
     * @see toolbox.plugin.docviewer.DocumentViewer#getViewableFileTypes()
     */
    public String[] getViewableFileTypes()
    {
        return new String[] { "html", "htm" };
    }

    
    /**
     * @see toolbox.plugin.docviewer.DocumentViewer#getComponent()
     */
    public JComponent getComponent()
    {
        return viewerPane_;
    }

    
    /**
     * @see toolbox.plugin.docviewer.DocumentViewer#getName()
     */
    public String getName()
    {
        return "Web Window";
    }

    
    /**
     * @see toolbox.plugin.docviewer.DocumentViewer#shutdown()
     */
    public void shutdown()
    {
    }
}