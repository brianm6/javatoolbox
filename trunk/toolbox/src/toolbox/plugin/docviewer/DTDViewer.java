package toolbox.plugin.docviewer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import com.conradroche.matra.dtdparser.DTDParser;
import com.conradroche.matra.exception.DTDException;
import com.conradroche.matra.tree.DTDTree;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import toolbox.jedit.JEditTextArea;
import toolbox.jedit.JEditTextAreaOutputStream;
import toolbox.util.ArrayUtil;
import toolbox.util.FileUtil;
import toolbox.util.StringUtil;

/**
 * A tree viewer for DTDs that makes use of <a href=http://matra.sf.net>Matra</a>.
 */
public class DTDViewer extends JEditViewer {

    private static final Logger logger_ = Logger.getLogger(DTDViewer.class);

    // --------------------------------------------------------------------------
    // Constructors
    // --------------------------------------------------------------------------

    public DTDViewer() {
        super("DTD Viewer");
    }

    // --------------------------------------------------------------------------
    // Overrides JEditViewer
    // --------------------------------------------------------------------------

    public void view(File file) throws DocumentViewerException {
        try {
            createTextArea(file);
            JEditTextArea textArea = getTextArea();
            String text = FileUtil.getFileContents(file.getCanonicalPath());

            if (StringUtil.getLine(text, 0).startsWith("<?xml")) {
                logger_.debug("nuking xml decl from first line of dtd");
                int eol = text.indexOf('\n');
                text = text.substring(eol);
            }

            // Matra insists on writing the results to System.out so we have to
            // temporarily hijack it.

            PrintStream original = System.out;
            OutputStream os = new JEditTextAreaOutputStream(textArea);

            try {
                System.setOut(new PrintStream(os));
                DTDParser parser = new DTDParser();
                parser.parse(text);
                DTDTree tree = new DTDTree(parser);
                tree.printTrees();
                textArea.setCaretPosition(0);
                textArea.scrollToCaret();
            }
            finally {
                IOUtils.closeQuietly(os);
                System.setOut(original);
            }
        }
        catch (FileNotFoundException fnfe) {
            throw new DocumentViewerException(fnfe);
        }
        catch (IOException ioe) {
            throw new DocumentViewerException(ioe);
        }
        catch (DTDException dtde) {
            throw new DocumentViewerException(dtde);
        }
    }


    public boolean canView(File file) {
        return (ArrayUtil.contains(getViewableFileTypes(), FileUtil
            .getExtension(file).toLowerCase()));
    }


    public String[] getViewableFileTypes() {
        return new String[] { "dtd" };
    }
}