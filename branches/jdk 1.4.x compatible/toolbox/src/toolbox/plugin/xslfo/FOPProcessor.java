package toolbox.plugin.xslfo;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.util.Map;

import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.avalon.framework.logger.Logger;
import org.apache.commons.io.IOUtils;
import org.apache.fop.apps.Driver;
import org.apache.fop.messaging.MessageHandler;

import org.xml.sax.InputSource;

import toolbox.util.service.ServiceException;
import toolbox.util.service.ServiceState;

/**
 * FOPProcessor is a concrete implementation of a {@link FOProcessor} specific
 * to the Apache implementation of formatting objects called 
 * <a href=http://xml.apache.org/fop>FOP</a> (Formatting Objects Processor).
 */
public class FOPProcessor implements FOProcessor
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * Driver for PDF generation.
     */
    private Driver pdfDriver_;

    /**
     * Driver for Postscript generation.
     */
    private Driver psDriver_;

    /**
     * State of this processor.
     */
    private ServiceState state_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a FOPProcessor.
     */
    public FOPProcessor()
    {
        state_ = ServiceState.UNINITIALIZED;
    }
    
    //--------------------------------------------------------------------------
    // Initializable Interface
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.util.service.Initializable#initialize(java.util.Map)
     */
    public void initialize(Map configuration) 
        throws IllegalStateException, ServiceException
    {
        // Common
        Logger logger = new ConsoleLogger(ConsoleLogger.LEVEL_FATAL);
        MessageHandler.setScreenLogger(logger);

        // PDF
        pdfDriver_ = new Driver();
        pdfDriver_.setLogger(logger);
        pdfDriver_.setRenderer(Driver.RENDER_PDF);

        // Postscript
        psDriver_ = new Driver();
        psDriver_.setLogger(logger);
        psDriver_.setRenderer(Driver.RENDER_PS);
        
        state_ = ServiceState.INITIALIZED;
    }

    //--------------------------------------------------------------------------
    // Service Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.service.Service#getState()
     */
    public ServiceState getState()
    {
        return state_;
    }
    
    //--------------------------------------------------------------------------
    // FOProcessor Interface
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.plugin.xslfo.FOProcessor#renderPDF(java.io.InputStream,
     *      java.io.OutputStream)
     */
    public void renderPDF(InputStream foStream, OutputStream pdfStream)
        throws Exception
    {
        render(pdfDriver_, foStream, pdfStream);
    }


    /**
     * @see toolbox.plugin.xslfo.FOProcessor#renderPostscript(
     *      java.io.InputStream, java.io.OutputStream)
     */
    public void renderPostscript(InputStream foStream, OutputStream psStream)
        throws Exception
    {
        render(psDriver_, foStream, psStream);
    }

    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------
    
    /**
     * Renders xslfo to a given output format using a driver.
     * 
     * @param driver Driver initialized to use an output format.
     * @param in InputStream to read xsl-fo from.
     * @param out OutputStream to write resulting output to.
     * @throws Exception on error.
     */
    public void render(Driver driver, InputStream in, OutputStream out)
        throws Exception
    {
        try
        {
            driver.reset();
            driver.setOutputStream(out);
            Reader reader = new InputStreamReader(in, "UTF-8");
            driver.setInputSource(new InputSource(reader));
            driver.run();
        }
        finally
        {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }
    }
}