package toolbox.util.xslfo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;

import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.avalon.framework.logger.Logger;
import org.apache.fop.apps.Driver;
import org.apache.fop.messaging.MessageHandler;

import org.xml.sax.InputSource;

/**
 * FOPProcessor is a concrete implementation of a 
 * {@link FOProcessor <b>FO</b>Processor} specific to the Apache implementation
 * of formatting objects called  <a href=http://xml.apache.org/fop>FOP</a> 
 * (Formatting Objects Processor).
 */
public class FOPProcessor implements FOProcessor
{
    /** Driver for PDF generation */
    private Driver pdfDriver_;
    
    /** Driver for Postscript generation */
    private Driver psDriver_;

    //--------------------------------------------------------------------------
    // FOProcessor Interface
    //--------------------------------------------------------------------------
    
    public void initialize()
    {
        // Common
        Logger logger = new ConsoleLogger(ConsoleLogger.LEVEL_INFO);
        MessageHandler.setScreenLogger(logger);
                
        // PDF                
        pdfDriver_ = new Driver();
        pdfDriver_.setLogger(logger);
        pdfDriver_.setRenderer(Driver.RENDER_PDF);

        // Postscript        
        psDriver_ = new Driver();
        psDriver_.setLogger(logger);
        psDriver_.setRenderer(Driver.RENDER_PS);
    }

    public void renderPDF(File foFile, File pdfFile) throws Exception
    {
        renderPDF(new FileInputStream(foFile), new FileOutputStream(pdfFile));
    }

    public void renderPDF(InputStream foStream, OutputStream pdfStream)
        throws Exception
    {
        try
        {
            pdfDriver_.reset();
            pdfDriver_.setOutputStream(pdfStream);
            Reader reader = new InputStreamReader(foStream, "UTF-8");
            pdfDriver_.setInputSource(new InputSource(reader));
            pdfDriver_.run();
        }
        finally
        {
            foStream.close();
            pdfStream.close();            
        }
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
        try
        {
            psDriver_.reset();
            psDriver_.setOutputStream(psStream);
            Reader reader = new InputStreamReader(foStream, "UTF-8");
            psDriver_.setInputSource(new InputSource(reader));
            psDriver_.run();
        }
        finally
        {
            foStream.close();
            psStream.close();            
        }
    }
}