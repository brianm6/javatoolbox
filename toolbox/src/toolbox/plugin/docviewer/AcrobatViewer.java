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
public class AcrobatViewer extends AbstractViewer {

    private static final Logger logger_ = Logger.getLogger(AcrobatViewer.class);

    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------

    /**
     * Acrobat viewer component.
     */
    private Viewer viewer_;

    /**
     * Component used by this viewer.
     */
    private JPanel panel_;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public AcrobatViewer() {
        super("Acrobat");
    }

    // -------------------------------------------------------------------------
    // Protected
    // -------------------------------------------------------------------------

    protected void lazyLoad() {
        if (viewer_ == null) {
            try {
                Viewer.setEnableDebug(true);
                panel_ = new JPanel(new BorderLayout());
                viewer_ = new Viewer();
                panel_.add(viewer_, BorderLayout.CENTER);
            }
            catch (Exception e) {
                ExceptionUtil.handleUI(e, logger_);
            }
        }
    }

    // -------------------------------------------------------------------------
    // Initializable Interface
    // -------------------------------------------------------------------------

    public void initialize(Map init) throws ServiceException {
        ; // No-op
    }

    // -------------------------------------------------------------------------
    // DocumentViewer Interface
    // -------------------------------------------------------------------------

    public void view(File file) throws DocumentViewerException {
        lazyLoad();

        try {
            view(new BufferedInputStream(new FileInputStream(file)));
        }
        catch (DocumentViewerException dve) {
            throw dve;
        }
        catch (Exception e) {
            throw new DocumentViewerException(e);
        }
    }


    public void view(InputStream is) throws DocumentViewerException {
        lazyLoad();

        try {
            viewer_.activate();
            viewer_.setDocumentInputStream(is);
            viewer_.execMenuItem("FitVisibleWidth");
            viewer_.execMenuItem("OneColumn");
        }
        catch (Exception e) {
            throw new DocumentViewerException(e);
        }
    }

    
    public String[] getViewableFileTypes() {
        return new String[] { "pdf" };
    }


    public boolean canView(File file) {
        return ArrayUtil.contains(
            getViewableFileTypes(), 
            FileUtil.getExtension(file).toLowerCase());
    }


    public JComponent getComponent() {
        lazyLoad();
        return panel_;
    }

    // -------------------------------------------------------------------------
    // Destroyable Interface
    // -------------------------------------------------------------------------

    public void destroy() {
        viewer_.deactivate();
        viewer_ = null;
        panel_ = null;
    }
}