package toolbox.util.xslfo;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * Common interface for all 3rd party XSL-FO implementations.
 */
public interface FOProcessor
{
    /**
     * Intializes the FO processor.
     * 
     * @param props Initialization properties.
     */
    void initialize(Properties props);
    
    
    /**
     * Transforms the FO originating from an inputstream and writes the
     * rendered PDF to an outputstream.
     * 
     * @param foStream Stream to read FO XML instructions from.
     * @param pdfStream Stream to write PDF output to.
     * @throws Exception on error.
     */
    void renderPDF(InputStream foStream, OutputStream pdfStream) 
        throws Exception;
    
    
    /**
     * Renders XSL-FO to a Postscript document.
     * 
     * @param foStream Source of XSL-FO.
     * @param psStream Destination of Postscript output.
     * @throws Exception on error.
     */    
    void renderPostscript(InputStream foStream, OutputStream psStream)
        throws Exception;
}