package toolbox.util.xslfo;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Common interface for all 3rd party XSL-FO implementations
 */
public interface FOProcessor
{
    /**
     * Intializes the FO processor
     */
    public void initialize();
    
    /**
     * Transforms a FO file to a PDF file
     * 
     * @param   foFile    File containing FO XML instructions
     * @param   pdfFile   File to render the output to
     * @throws  Exception on error
     */
    public void renderPDF(File foFile, File pdfFile) throws Exception;
    
    /**
     * Transforms the FO originating from an inputstream and writes the
     * rendered PDF to an outputstream.
     * 
     * @param   foStream   Stream to read FO XML instructions from
     * @param   pdfStream  Stream to write PDF output to
     * @throws  Exception on error
     */
    public void renderPDF(InputStream foStream, OutputStream pdfStream) 
        throws Exception;
    
    /**
     * Transforms a FO xml string to a PDF. 
     * 
     * @param   foXML  String containing FO XML instructions
     * @return  PDF in the form of a byte array
     * @throws  Exception on error
     */    
    public byte[] renderPDF(String foXML) throws Exception;
}