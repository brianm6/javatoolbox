package toolbox.util.xslfo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import com.renderx.xep.XSLDriver;

import org.apache.fop.apps.Driver;

import toolbox.util.FileUtil;
import toolbox.util.StreamUtil;

/**
 * Interface to access the RenderX XSLFO processor
 */
public class XEPProcessor implements FOProcessor
{
    //--------------------------------------------------------------------------
    // Interface FOProcessor
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.ui.plugin.FOProcessor#initialize()
     */
    public void initialize()
    {
        // Get rid of this
        System.setProperty("com.renderx.xep.ROOT", "C:\\dev\\XEP");
    }

    /**
     * @see toolbox.util.ui.plugin.FOProcessor#
     *      renderToPDF(java.io.File, java.io.File)
     */
    public void renderPDF(File foFile, File pdfFile) throws Exception
    {
        XSLDriver.main(new String[] { 
            "-fo", foFile.getAbsolutePath(), pdfFile.getAbsolutePath() } );
    }

    /**
     * @see toolbox.util.ui.plugin.FOProcessor#
     *      render2PDF(java.io.InputStream, java.io.OutputStream)
     */
    public void renderPDF(InputStream foStream, OutputStream pdfStream)
        throws Exception
    {
        String foFile = FileUtil.getTempFilename() + ".xml";
        FileUtil.setFileContents(foFile, StreamUtil.toBytes(foStream), false);
        String pdfFile = foFile + ".pdf";
        renderPDF(new File(foFile), new File(pdfFile));
        byte[] pdfBytes = FileUtil.getFileAsBytes(pdfFile);
        pdfStream.write(pdfBytes);
        pdfStream.flush();
        pdfStream.close();
    }
    
    /**
     * @see toolbox.util.xslfo.FOProcessor#renderToPDF(java.lang.String)
     */
    public byte[] renderPDF(String foXML) throws Exception
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        renderPDF(new ByteArrayInputStream(foXML.getBytes("UTF-8")), baos);        
        return baos.toByteArray();
    }
}