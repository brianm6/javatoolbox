package toolbox.plugin.docviewer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;

import org.apache.commons.io.IOUtils;
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

import toolbox.jedit.JEditPopupMenu;
import toolbox.jedit.JEditTextArea;
import toolbox.jedit.JavaDefaults;
import toolbox.util.FileUtil;
import toolbox.util.FontUtil;
import toolbox.util.service.ServiceException;

/**
 * A viewer to for popular text formats with syntax hiliting.
 */
public class JEditViewer extends AbstractViewer {

    private static final Logger logger_ = Logger.getLogger(JEditViewer.class);

    // -------------------------------------------------------------------------
    // Constants
    // -------------------------------------------------------------------------

    /**
     * Map of file extensions to their corresponding TokenMarker class.
     */
    protected static final Map EXT_MAP;

    // -------------------------------------------------------------------------
    // Static Initializers
    // -------------------------------------------------------------------------

    static {
        EXT_MAP = new HashMap();
        EXT_MAP.put("java", JavaTokenMarker.class);
        EXT_MAP.put("groovy", JavaTokenMarker.class);

        // XML
        for (int i = 0; i < FileTypes.XML.length; i++)
            EXT_MAP.put(FileTypes.XML[i], XMLTokenMarker.class);

        EXT_MAP.put("bat", BatchFileTokenMarker.class);
        EXT_MAP.put("properties", PropsTokenMarker.class);
        EXT_MAP.put("props", PropsTokenMarker.class);
        EXT_MAP.put("sh", ShellScriptTokenMarker.class);
        EXT_MAP.put("sql", SQLTokenMarker.class);
        EXT_MAP.put("ddl", SQLTokenMarker.class);
        EXT_MAP.put("html", HTMLTokenMarker.class);
        EXT_MAP.put("htm", HTMLTokenMarker.class);
        EXT_MAP.put("pl", PerlTokenMarker.class);
        EXT_MAP.put("js", JavaScriptTokenMarker.class);
        EXT_MAP.put("c", CTokenMarker.class);
        EXT_MAP.put("h", CTokenMarker.class);
        EXT_MAP.put("cc", CCTokenMarker.class);
        EXT_MAP.put("cpp", CCTokenMarker.class);

        // TODO: Find dtd marker
        EXT_MAP.put("dtd", JavaTokenMarker.class);
    }

    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------

    /**
     * The contents of the file are dumped into this text area.
     */
    private JEditTextArea textArea_;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Creates a JEditViewer.
     */
    public JEditViewer() {
        this("JEdit Viewer");
    }


    /**
     * Creates a JEditViewer.
     * 
     * @param name Viewer name.
     */
    public JEditViewer(String name) {
        super(name);
    }

    // -------------------------------------------------------------------------
    // Protected
    // -------------------------------------------------------------------------

    /**
     * Returns the textarea.
     * 
     * @return JEditTextArea
     */
    protected JEditTextArea getTextArea() {
        return textArea_;
    }


    /**
     * Creates the text area
     * 
     * @param file File to create for.
     */
    protected void createTextArea(File file) {
        createTextArea(FileUtil.getExtension(file));
    }


    /**
     * Creates the text area
     * 
     * @param fileExtension File extension to activate syntax hiliting.
     */
    protected void createTextArea(String fileExtension) {
        Class c = (Class) EXT_MAP.get(fileExtension.toLowerCase());

        if (c != null) {
            try {
                textArea_ = new JEditTextArea(
                    (TokenMarker) c.newInstance(), new JavaDefaults());
            }
            catch (Exception e) {
                logger_.warn("Error instantiating " + c.getName() + ".");
                textArea_ = new JEditTextArea();
            }
        }
        else {
            textArea_ = new JEditTextArea(
                new PropsTokenMarker(), new JavaDefaults());
        }

        // Set font and popup menu...
        textArea_.getPainter().setFont(FontUtil.getPreferredMonoFont());
        textArea_.setPopupMenu(new JEditPopupMenu(textArea_));
    }

    // -------------------------------------------------------------------------
    // Initializable Interface
    // -------------------------------------------------------------------------

    public void initialize(Map configuration) throws ServiceException {
        ; // No-op
    }

    // -------------------------------------------------------------------------
    // DocumentViewer Interface
    // -------------------------------------------------------------------------

    public void view(File file) throws DocumentViewerException {
        try {
            createTextArea(file);
            String text = FileUtil.getFileContents(file.getCanonicalPath());
            textArea_.setText(text);
            textArea_.scrollTo(0, 0);
        }
        catch (FileNotFoundException e) {
            throw new DocumentViewerException(e);
        }
        catch (IOException e) {
            throw new DocumentViewerException(e);
        }
    }


    public void view(InputStream is) throws DocumentViewerException {
        createTextArea("???");
        String text;

        try {
            text = IOUtils.toString(is);
        }
        catch (IOException e) {
            throw new DocumentViewerException(e);
        }

        textArea_.setText(text);
        textArea_.scrollTo(0, 0);
    }


    public boolean canView(File file) {
        // View all files
        return true;
    }


    public String[] getViewableFileTypes() {
        return null;
    }


    public JComponent getComponent() {
        return textArea_;
    }

    // -------------------------------------------------------------------------
    // Destroyable Interface
    // -------------------------------------------------------------------------

    public void destroy() {
        textArea_.setText("");
        textArea_ = null;
    }
}