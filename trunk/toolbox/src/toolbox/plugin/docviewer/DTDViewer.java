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
 * A tree viewer for DTDs makes use of Matra @ http://matra.sf.net.
 */
public class DTDViewer extends JEditViewer
{
    private static final Logger logger_ = Logger.getLogger(DTDViewer.class);
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a DTDViewer.
     */
    public DTDViewer()
    {
    }

    //--------------------------------------------------------------------------
    // Overrides JEditViewer
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.plugin.docviewer.DocumentViewer#view(java.io.File)
     */
    public void view(File file) throws DocumentViewerException
    {
        try
        {
            createTextArea(file);
            JEditTextArea textArea = getTextArea();
            String text = FileUtil.getFileContents(file.getCanonicalPath());
            
            if (StringUtil.getLine(text, 0).startsWith("<?xml")) {
                logger_.debug("nuking first line");
                int eol = text.indexOf('\n');
                text = text.substring(eol);
            }

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
        catch (FileNotFoundException fnfe)
        {
            throw new DocumentViewerException(fnfe);
        }
        catch (IOException ioe)
        {
            throw new DocumentViewerException(ioe);
        }
        catch (DTDException dtde)
        {
            throw new DocumentViewerException(dtde);
        }
    }

    
    /**
     * @see toolbox.plugin.docviewer.DocumentViewer#canView(java.io.File)
     */
    public boolean canView(File file)
    {
        return (ArrayUtil.contains(
            getViewableFileTypes(), 
            FileUtil.getExtension(file).toLowerCase()));
    }

    
    /**
     * @see toolbox.plugin.docviewer.DocumentViewer#getViewableFileTypes()
     */
    public String[] getViewableFileTypes()
    {
        return new String[] {"dtd"};
    }

    
    /**
     * @see toolbox.plugin.docviewer.DocumentViewer#getName()
     */
    public String getName()
    {
        return "DTD Viewer";
    }
}