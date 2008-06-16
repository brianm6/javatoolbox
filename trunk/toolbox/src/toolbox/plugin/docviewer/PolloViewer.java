package toolbox.plugin.docviewer;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JScrollPane;

import org.apache.log4j.Logger;

import org.outerj.pollo.xmleditor.XmlEditor;
import org.outerj.pollo.xmleditor.displayspec.GenericDisplaySpecification;
import org.outerj.pollo.xmleditor.model.XmlModel;

import toolbox.util.ArrayUtil;
import toolbox.util.ExceptionUtil;
import toolbox.util.FileUtil;
import toolbox.util.FontUtil;
import toolbox.util.SwingUtil;

/**
 * XML document viewer that uses Pollo for rendering the document.
 */
public class PolloViewer extends AbstractViewer {

    private static final Logger logger_ = Logger.getLogger(PolloViewer.class);

    // --------------------------------------------------------------------------
    // Fields
    // --------------------------------------------------------------------------

    /**
     * XML UI widget.
     */
    private XmlEditor editor_;

    /**
     * Scrollpane wrapping the XmlEditor.
     */
    private JScrollPane scroller_;

    // --------------------------------------------------------------------------
    // Constructors
    // --------------------------------------------------------------------------

    public PolloViewer() {
        super("Pollo");
    }

    // --------------------------------------------------------------------------
    // Protected
    // --------------------------------------------------------------------------

    /**
     * Lazy loads the UI component for faster plugin loading.
     */
    protected void lazyLoad() {
        if (editor_ == null) {
            try {
                GenericDisplaySpecification displaySpec = 
                    new GenericDisplaySpecification();

                HashMap initMap = new HashMap();

                initMap.put("use-random-colors", "true");
                // init.put("fixed-color", "0xffeeff");
                // init.put("background-color", null);

                initMap.put("treetype", "pollo");
                // initMap.put("treetype", "classic");

                displaySpec.init(initMap);
                editor_ = new XmlEditor(null, displaySpec, -1);
                editor_.setAntialiasing(SwingUtil.getDefaultAntiAlias());
                editor_.setElementNameFont(FontUtil.getPreferredMonoFont());
                editor_.setCharacterDataFont(FontUtil.getPreferredMonoFont());
                editor_.setAttributeNameFont(FontUtil.getPreferredMonoFont());
                editor_.setAttributeValueFont(FontUtil.getPreferredMonoFont());
                scroller_ = new JScrollPane(editor_);
            }
            catch (Exception pe) {
                ExceptionUtil.handleUI(pe, logger_);
            }
        }
    }

    // --------------------------------------------------------------------------
    // Initializable Interface
    // --------------------------------------------------------------------------

    public void initialize(Map init) {
        ; // No-op
    }

    // --------------------------------------------------------------------------
    // DocumentViewer Interface
    // --------------------------------------------------------------------------

    public JComponent getComponent() {
        lazyLoad();
        return scroller_;
    }


    public String[] getViewableFileTypes() {
        return FileTypes.XML;
    }


    public boolean canView(File file) {
        return ArrayUtil.contains(getViewableFileTypes(), FileUtil
            .getExtension(file).toLowerCase());
    }


    public void view(File file) throws DocumentViewerException {
        lazyLoad();

        try {
            XmlModel model = new XmlModel(0);
            model.readFromResource(file);
            editor_.setXmlModel(model);
            editor_.validate();
            editor_.repaint();
        }
        catch (Exception e) {
            throw new DocumentViewerException(e);
        }
    }


    public void view(InputStream is) throws DocumentViewerException {
        throw new IllegalArgumentException("view(InputStream) not supported");
    }

    // --------------------------------------------------------------------------
    // Destroyable Interface
    // --------------------------------------------------------------------------

    public void destroy() {
        scroller_ = null;
        editor_ = null;
    }
}