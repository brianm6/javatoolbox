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
    /** 
     * Main interface class to FOP 
     */
    private Driver driver_;

    //--------------------------------------------------------------------------
    // Interface FOProcessor 
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.ui.plugin.FOProcessor#initialize()
     */
    public void initialize()
    {
        driver_ = new Driver();
        Logger logger = new ConsoleLogger(ConsoleLogger.LEVEL_INFO);
        driver_.setLogger(logger);
        MessageHandler.setScreenLogger(logger);
        driver_.setRenderer(Driver.RENDER_PDF);
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
            driver_.reset();
            driver_.setOutputStream(pdfStream);
            Reader reader = new InputStreamReader(foStream, "UTF-8");
            driver_.setInputSource(new InputSource(reader));
            driver_.run();
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
}