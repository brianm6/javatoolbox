package toolbox.plugin.xslfo;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import com.renderx.xep.XSLDriver;

import org.apache.commons.io.IOUtils;

import toolbox.util.FileUtil;

/**
 * FOPProcessor is a concrete implementation of a {@link FOProcessor} specific
 * to the RenderX implementation of formatting objects called <a
 * href=http://www.renderx.com>XEP </a>.
 */
public class XEPProcessor implements FOProcessor
{
    //--------------------------------------------------------------------------
    // FOProcessor Interface
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.plugin.xslfo.FOProcessor#initialize(java.util.Properties)
     */
    public void initialize(Properties props)
    {
        // TODO: Get rid of XEP absolute key
        System.setProperty("com.renderx.xep.ROOT", "C:\\dev\\XEP");
    }

    
    /**
     * @see toolbox.plugin.xslfo.FOProcessor#renderPDF(java.io.InputStream, 
     *      java.io.OutputStream)
     */
    public void renderPDF(InputStream foStream, OutputStream pdfStream)
        throws Exception
    {
        String foFile = FileUtil.createTempFilename() + ".xml";
        FileUtil.setFileContents(foFile, IOUtils.toByteArray(foStream), false);
        String pdfFile = foFile + ".pdf";
        renderPDF(new File(foFile), new File(pdfFile));
        byte[] pdfBytes = FileUtil.getFileAsBytes(pdfFile);
        pdfStream.write(pdfBytes);
        pdfStream.flush();
        pdfStream.close();
    }
    
    
    /**
     * @see toolbox.plugin.xslfo.FOProcessor#renderPostscript(java.io.InputStream,
     *      java.io.OutputStream)
     */
    public void renderPostscript(InputStream foStream, OutputStream psStream)
        throws Exception
    {
        throw new IllegalArgumentException("Not implemented"); 
    }

    //--------------------------------------------------------------------------
    // Private
    //--------------------------------------------------------------------------
    
    /**
     * XEP forces us to the the filesystem as the point of interface.
     * 
     * @param foFile XSL-FO input file.
     * @param pdfFile PDF output file.
     * @throws Exception on error.
     */    
    private void renderPDF(File foFile, File pdfFile) throws Exception
    {
        XSLDriver.main(
            new String[] 
            { 
                "-fo", 
                foFile.getAbsolutePath(), 
                pdfFile.getAbsolutePath() 
            });
    }
}