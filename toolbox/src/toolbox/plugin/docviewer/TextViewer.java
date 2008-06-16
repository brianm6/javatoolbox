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
 * A viewer for plain text documents.
 */
public class TextViewer extends AbstractViewer {

    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------

    /**
     * The contents of the file are dumped into this text area.
     */
    private JSmartTextArea textArea_;

    /**
     * Wrapper around the text area. This scrollpane is returned by
     * getComponent().
     */
    private JScrollPane scroller_;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public TextViewer() {
        this("Text Viewer");
    }


    public TextViewer(String name) {
        super(name);
    }

    // -------------------------------------------------------------------------
    // Protected
    // -------------------------------------------------------------------------

    /**
     * Returns the textarea. For use by subclasses which needs access to the
     * textarea.
     * 
     * @return JSmartTextArea
     */
    public JSmartTextArea getTextArea() {
        return textArea_;
    }

    // -------------------------------------------------------------------------
    // Initializable Interface
    // -------------------------------------------------------------------------

    public void initialize(Map init) {
        textArea_ = new JSmartTextArea();
        textArea_.setAntiAliased(SwingUtil.getDefaultAntiAlias());
        scroller_ = new JScrollPane(textArea_);
    }

    // -------------------------------------------------------------------------
    // DocumentViewer Interface
    // -------------------------------------------------------------------------

    /**
     * Reads in the file via a Reader and displays it in the textarea.
     * 
     * @see toolbox.plugin.docviewer.DocumentViewer#view(java.io.File)
     */
    public void view(File file) throws DocumentViewerException {
        try {
            String text = FileUtil.getFileContents(file.getCanonicalPath());
            textArea_.setText(text);
            textArea_.setCaretPosition(0);
        }
        catch (FileNotFoundException e) {
            throw new DocumentViewerException(e);
        }
        catch (IOException e) {
            throw new DocumentViewerException(e);
        }
    }


    public void view(InputStream is) throws DocumentViewerException {
        throw new UnsupportedOperationException("Not implemented");
    }


    /**
     * Accepts all file types.
     * 
     * @see toolbox.plugin.docviewer.DocumentViewer#canView(java.io.File)
     */
    public boolean canView(File file) {
        // View all files
        return true;
    }


    public String[] getViewableFileTypes() {
        return null;
    }


    /**
     * Returns the JScrollPane wrapping the text area.
     * 
     * @see toolbox.plugin.docviewer.DocumentViewer#getComponent()
     */
    public JComponent getComponent() {
        return scroller_;
    }

    // -------------------------------------------------------------------------
    // Destroyable Interface
    // -------------------------------------------------------------------------

    public void destroy() {
        ; // No-op
    }
}