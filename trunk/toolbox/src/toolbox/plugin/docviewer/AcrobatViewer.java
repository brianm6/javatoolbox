package toolbox.plugin.pdf;

import java.awt.BorderLayout;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.adobe.acrobat.Viewer;

import org.apache.log4j.Logger;

import toolbox.util.ArrayUtil;
import toolbox.util.ThreadUtil;

/**
 * AcrobatViewer is a wrapper for Acrobat JavaBean used to view PDF documents.
 */
public class AcrobatViewer extends JPanel implements DocumentViewer
{
    private static final Logger logger_ = Logger.getLogger(AcrobatViewer.class);
    
    /**
     * Acrobat viewer component
     */
    private Viewer viewer_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates an AcrobatViewer
     */
    public AcrobatViewer()
    {
    }
    
    //--------------------------------------------------------------------------
    // DocumentViewer Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.plugin.pdf.DocumentViewer#startup(java.util.Map)
     */
    public void startup(Map init) throws DocumentViewerException 
    {
        try
        {
            Viewer.setEnableDebug(true);
            
            setLayout(new BorderLayout());
            viewer_ = new Viewer();
            viewer_.activate();
            
            //viewer_.activateWithoutBars();
            add(new JButton("FUCK ME WITH A POLE"));
            ThreadUtil.sleep(2000);
            add(viewer_, BorderLayout.CENTER);
            logger_.debug("Done");
            
            
        }
        catch (Exception e)
        {
            throw new DocumentViewerException(e);
        }
    }
    
    /**
     * Opens a file for viewing
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
     * @see toolbox.plugin.pdf.DocumentViewer#view(java.io.InputStream)
     */
    public void view(InputStream is) throws DocumentViewerException
    {
        try
        {
            viewer_.setDocumentInputStream(is);
            viewer_.execMenuItem("FitVisibleWidth");
            
            SwingUtilities.invokeLater(new Runnable()
            {
                public void run()
                {
                    invalidate();
                    repaint();
                    setSize(viewer_.getSize().width+1, viewer_.getSize().height+1);
                }
            });
            
            
            
        }
        catch (Exception e)
        {
            throw new DocumentViewerException(e);
        }        
    }
    
    /**
     * @see toolbox.plugin.pdf.DocumentViewer#getViewableFileTypes()
     */
    public String[] getViewableFileTypes()
    {
        return new String[] {"pdf"};
    }    

    /**
     * @see toolbox.plugin.pdf.DocumentViewer#isViewable(java.lang.String)
     */
    public boolean isViewable(String fileType)
    {
        return ArrayUtil.contains(getViewableFileTypes(), fileType);
    }
    
    /**
     * @see toolbox.plugin.pdf.DocumentViewer#getComponent()
     */
    public JComponent getComponent()
    {
        return this;
    }

    /**
     * @see toolbox.plugin.pdf.DocumentViewer#shutdown()
     */
    public void shutdown()
    {
        viewer_.deactivate();
    }
}