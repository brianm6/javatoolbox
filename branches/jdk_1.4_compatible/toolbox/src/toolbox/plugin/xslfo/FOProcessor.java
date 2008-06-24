package toolbox.plugin.xslfo;

import java.io.InputStream;
import java.io.OutputStream;

import toolbox.util.service.Initializable;

/**
 * Common interface for all 3rd party XSL-FO implementations.
 * 
 * @see toolbox.plugin.xslfo.FOProcessorFactory
 */
public interface FOProcessor extends Initializable
{
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