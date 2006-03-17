package toolbox.plugin.xslfo;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import com.renderx.xep.XSLDriver;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import toolbox.util.FileUtil;
import toolbox.util.service.ServiceException;
import toolbox.util.service.ServiceState;

/**
 * FOPProcessor is a concrete implementation of a {@link FOProcessor} specific
 * to the RenderX implementation of formatting objects called <a
 * href=http://www.renderx.com>XEP </a>.
 */
public class XEPProcessor implements FOProcessor
{
    //--------------------------------------------------------------------------
    // Initializable Interface
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.util.service.Initializable#initialize(java.util.Map)
     */
    public void initialize(Map configuration) throws IllegalStateException,
        ServiceException
    {
        // TODO: Get rid of XEP absolute key
        System.setProperty("com.renderx.xep.ROOT", "C:\\dev\\XEP");
    }

    //--------------------------------------------------------------------------
    // FOProcessor Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.plugin.xslfo.FOProcessor#renderPDF(
     *      java.io.InputStream, java.io.OutputStream)
     */
    public void renderPDF(InputStream foStream, OutputStream pdfStream)
        throws Exception
    {
        String foFile = FileUtil.createTempFilename() + ".xml";
        FileUtil.setFileContents(foFile, IOUtils.toByteArray(foStream), false);
        String pdfFilename = foFile + ".pdf";
        File pdfFile = new File(pdfFilename);
        renderPDF(new File(foFile), pdfFile);
        byte[] pdfBytes = FileUtils.readFileToByteArray(pdfFile);
        pdfStream.write(pdfBytes);
        pdfStream.flush();
        IOUtils.closeQuietly(pdfStream);
    }

    
    /**
     * @see toolbox.plugin.xslfo.FOProcessor#renderPostscript(
     *      java.io.InputStream, java.io.OutputStream)
     */
    public void renderPostscript(InputStream foStream, OutputStream psStream)
        throws Exception
    {
        throw new IllegalArgumentException("Not implemented"); 
    }

    
    //--------------------------------------------------------------------------
    // Service Interface
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.util.service.Service#getState()
     */
    public ServiceState getState()
    {
        return null;
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