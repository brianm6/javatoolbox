package toolbox.plugin.docviewer;

import java.awt.BorderLayout;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;

import com.adobe.acrobat.Viewer;

import org.apache.log4j.Logger;

import toolbox.util.ArrayUtil;

/**
 * AcrobatViewer is a wrapper for the Acrobat JavaBean which is used to 
 * render and view PDF documents.
 */
public class AcrobatViewer extends JPanel implements DocumentViewer
{
    private static final Logger logger_ = 
    	Logger.getLogger(AcrobatViewer.class);
    
    /**
     * Acrobat viewer component.
     */
    private Viewer viewer_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates an AcrobatViewer.
     */
    public AcrobatViewer()
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
        try
        {
            Viewer.setEnableDebug(true);
            
            setLayout(new BorderLayout());
            viewer_ = new Viewer();
            add(BorderLayout.CENTER, viewer_);
        }
        catch (Exception e)
        {
            throw new DocumentViewerException(e);
        }
    }
    
    
    /**
     * Opens a file for viewing.
     * 
     * @param file File to view
     */
    public void view(File file) throws DocumentViewerException
    {
        try
        {
            view(new BufferedInputStream(new FileInputStream(file)));
        }
        catch (DocumentViewerException dve)
        {
            throw dve;
        }
        catch (Exception e)
        {
            throw new DocumentViewerException(e);
        }
    }
    
    
    /**
     * @see toolbox.plugin.docviewer.DocumentViewer#view(java.io.InputStream)
     */
    public void view(InputStream is) throws DocumentViewerException
    {
        try
        {
            viewer_.activate();
            viewer_.setDocumentInputStream(is);
            viewer_.execMenuItem("FitVisibleWidth");
            viewer_.execMenuItem("OneColumn");
            
            
//            SwingUtilities.invokeLater(new Runnable()
//            {
//                public void run()
//                {
//                    invalidate();
//                    repaint();
//                    setSize(viewer_.getSize().width+1, viewer_.getSize().height+1);
//                }
//            });
            
        }
        catch (Exception e)
        {
            throw new DocumentViewerException(e);
        }        
    }
    
    
    /**
     * @see toolbox.plugin.docviewer.DocumentViewer#getViewableFileTypes()
     */
    public String[] getViewableFileTypes()
    {
        return new String[] {"pdf"};
    }    


    /**
     * @see toolbox.plugin.docviewer.DocumentViewer#isViewable(java.lang.String)
     */
    public boolean isViewable(String fileType)
    {
        return ArrayUtil.contains(getViewableFileTypes(), fileType);
    }

    
    /**
     * @see toolbox.plugin.docviewer.DocumentViewer#getComponent()
     */
    public JComponent getComponent()
    {
        return this;
    }


    /**
     * @see toolbox.plugin.docviewer.DocumentViewer#shutdown()
     */
    public void shutdown()
    {
        viewer_.deactivate();
    }
}