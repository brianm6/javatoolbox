package toolbox.util.xslfo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import com.renderx.xep.XSLDriver;

import toolbox.util.FileUtil;
import toolbox.util.StreamUtil;

/**
 * FOPProcessor is a concrete implementation of a 
 * {@link FOProcessor <b>FO</b>Processor} specific to the RenderX implementation
 * of formatting objects called  <a href=http://www.renderx.com>XEP</a>. 
 */
public class XEPProcessor implements FOProcessor
{
    //--------------------------------------------------------------------------
    // FOProcessor Interface
    //--------------------------------------------------------------------------
    
    public void initialize()
    {
        // TODO: Get rid of XEP absolute key
        System.setProperty("com.renderx.xep.ROOT", "C:\\dev\\XEP");
    }

    public void renderPDF(File foFile, File pdfFile) throws Exception
    {
        XSLDriver.main(new String[] { 
            "-fo", foFile.getAbsolutePath(), pdfFile.getAbsolutePath() } );
    }

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
    
    public byte[] renderPDF(String foXML) throws Exception
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        renderPDF(new ByteArrayInputStream(foXML.getBytes("UTF-8")), baos);        
        return baos.toByteArray();
    }
    
    public void renderPostscript(InputStream foStream, OutputStream psStream)
        throws Exception
    {
        // to do 
    }
}