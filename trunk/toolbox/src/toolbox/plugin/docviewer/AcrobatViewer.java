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
import toolbox.util.ExceptionUtil;
import toolbox.util.FileUtil;
import toolbox.util.service.ServiceException;

/**
 * AcrobatViewer is a wrapper for the Acrobat JavaBean which is used to render
 * and view PDF documents.
 */
public class AcrobatViewer extends AbstractViewer
{
    private static final Logger logger_ = Logger.getLogger(AcrobatViewer.class);
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Acrobat viewer component.
     */
    private Viewer viewer_;

    /**
     * Component used by this viewer.
     */
    private JPanel panel_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates an AcrobatViewer.
     */
    public AcrobatViewer()
    {
        super("Acrobat");
    }
    
    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------
    
    /**
     * Lazily loads the UI component.
     */
    protected void lazyLoad() 
    {
        if (viewer_ == null)
        {    
            try
            {
                Viewer.setEnableDebug(true);
                panel_ = new JPanel(new BorderLayout());
                viewer_ = new Viewer();
                panel_.add(viewer_, BorderLayout.CENTER);
            }
            catch (Exception e)
            {
                ExceptionUtil.handleUI(e, logger_);
            }
        }
    }
    
    //--------------------------------------------------------------------------
    // Initializable Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.service.Initializable#initialize(java.util.Map)
     */
    public void initialize(Map init) throws ServiceException 
    {
        ; // No-op
    }
    
    //--------------------------------------------------------------------------
    // DocumentViewer Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.plugin.docviewer.DocumentViewer#view(java.io.File)
     */
    public void view(File file) throws DocumentViewerException
    { 
        lazyLoad();
        
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
        lazyLoad();
        
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
//               setSize(viewer_.getSize().width+1, viewer_.getSize().height+1);
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
     * @see toolbox.plugin.docviewer.DocumentViewer#canView(java.io.File)
     */
    public boolean canView(File file)
    {
        return ArrayUtil.contains(
            getViewableFileTypes(), 
            FileUtil.getExtension(file).toLowerCase());
    }

    
    /**
     * @see toolbox.plugin.docviewer.DocumentViewer#getComponent()
     */
    public JComponent getComponent()
    {
        lazyLoad();
        return panel_;
    }

    //--------------------------------------------------------------------------
    // Destroyable Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.service.Destroyable#destroy()
     */
    public void destroy()
    {
        viewer_.deactivate();
        viewer_ = null;
        panel_ = null;
    }
}