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
 * Interface to access the Apache FOP XSLFO processor
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
    
    /**
     * @see toolbox.util.ui.plugin.FOProcessor#initialize()
     */
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

    /**
     * @see toolbox.util.ui.plugin.FOProcessor#
     *      renderToPDF(java.io.File, java.io.File)
     */
    public void renderPDF(File foFile, File pdfFile) throws Exception
    {
        renderPDF(new FileInputStream(foFile), new FileOutputStream(pdfFile));
    }

    /**
     * @see toolbox.util.ui.plugin.FOProcessor#
     *      render2PDF(java.io.InputStream, java.io.OutputStream)
     */
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
    
    /**
     * @see toolbox.util.xslfo.FOProcessor#renderToPDF(java.lang.String)
     */
    public byte[] renderPDF(String foXML) throws Exception
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        renderPDF(new ByteArrayInputStream(foXML.getBytes("UTF-8")), baos);
        return baos.toByteArray();
    }

    /**
     * @see toolbox.util.xslfo.FOProcessor
     *      #renderPostscript(java.io.InputStream, java.io.OutputStream)
     */    
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