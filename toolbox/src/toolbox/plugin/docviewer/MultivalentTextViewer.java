package toolbox.plugin.docviewer;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import tool.doc.ExtractText;

import toolbox.util.FileUtil;
import toolbox.util.collections.CaseInsensetiveSet;

/**
 * A viewer that operates on all Multivalent supported formats (PDF, HTML, DVI)
 * and extracts only the plain text for display.
 */
public class MultivalentTextViewer extends TextViewer {

    // --------------------------------------------------------------------------
    // Fields
    // --------------------------------------------------------------------------

    /**
     * Set of supported file extensions.
     */
    private Set extensions_;

    // --------------------------------------------------------------------------
    // Constructors
    // --------------------------------------------------------------------------

    public MultivalentTextViewer() {
        super("Multivalent Text Viewer");
    }

    // --------------------------------------------------------------------------
    // Overrides TextViewer
    // --------------------------------------------------------------------------

    /**
     * Builds the list of viewable file types.
     * 
     * @see toolbox.util.service.Initializable#initialize(java.util.Map)
     */
    public void initialize(Map init) {
        super.initialize(init);
        extensions_ = new CaseInsensetiveSet(new HashSet());

        extensions_.addAll(Arrays.asList(new String[] {
            "pdf",
            "html",
            "htm",
            "dvi",
            "xml"
            }
        ));
    }


    public boolean canView(File file) {
        return extensions_.contains(FileUtil.getExtension(file));
    }


    /**
     * Converts the passed in PDF, HTML, DVI to plain text and displays it in
     * the textarea.
     * 
     * @see toolbox.plugin.docviewer.DocumentViewer#view(java.io.File)
     */
    public void view(File file) throws DocumentViewerException {
        try {
            ExtractText extract = new ExtractText();
            String text = extract.extract(file.toURI());
            getTextArea().setText(text);
            getTextArea().setCaretPosition(0);
        }
        catch (Exception e) {
            throw new DocumentViewerException(e);
        }
    }
}