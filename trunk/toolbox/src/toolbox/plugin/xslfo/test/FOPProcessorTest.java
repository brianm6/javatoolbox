package toolbox.util.xslfo.test;

import java.io.ByteArrayOutputStream;
import java.util.Properties;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.ResourceUtil;
import toolbox.util.io.StringInputStream;
import toolbox.util.xslfo.FOProcessor;
import toolbox.util.xslfo.FOProcessorFactory;

/**
 * Unit test for FOPProcessor.
 */
public class FOPProcessorTest extends TestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(FOPProcessorTest.class);

    //--------------------------------------------------------------------------
    // Constants 
    //--------------------------------------------------------------------------
    
    /**
     * File containing sample xslfo content suitable for testing.
     */
    private static final String FILE_TEST_XSLFO = 
        "/toolbox/util/xslfo/test/FOPProcessorTest.fo";

    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------

    /**
     * Entrypoint. 
     * 
     * @param args None recognized.
     */
    public static void main(String[] args)
    {
        TestRunner.run(FOPProcessorTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------

    /**
     * Tests renderPDF() with successive calls in parallel.
     *  
     * @throws Exception on error.
     */
    public void testRenderPDFInParallel() throws Exception
    {
        logger_.info("Running testRenderPDFInParallel...");
        
        String foXML = new String(
            ResourceUtil.getResourceAsBytes(FILE_TEST_XSLFO));

        int iterations = 10;
        Thread threads[] = new Thread[iterations];

        for (int i = 0; i < iterations; i++)
        {
            threads[i] = new Thread(new RenderRequest(i, foXML));
            threads[i].start();
        }

        // Wait for all to complete
        for (int i = 0; i < iterations; i++)
        {
            logger_.info("Waiting for thread " + i + " to complete...");
            threads[i].join();
        }
    }

    
    /**
     * Tests renderPDF() with successive calls in sequence.
     *  
     * @throws Exception on error.
     */
    public void testRenderPDFInSequence() throws Exception
    {
        logger_.info("Running testRenderPDFInSequence...");
        
        int iterations = 10;
        
        String foXML = 
            new String(ResourceUtil.getResourceAsBytes(FILE_TEST_XSLFO));

        for (int i = 0; i < iterations; i++)
            new RenderRequest(i, foXML).run();
    }
    
    
    /**
     * Tests renderPostscript()
     *  
     * @throws Exception on error.
     */
    public void testRenderPostscript() throws Exception
    {
        logger_.info("Running testRenderPostscript...");
        
        String foXML = new String(
            ResourceUtil.getResourceAsBytes(FILE_TEST_XSLFO));

        logger_.info("Rendering...");
         
        FOProcessor fop = 
            FOProcessorFactory.createProcessor(
                FOProcessorFactory.FO_IMPL_APACHE);
                
        fop.initialize(new Properties());
        
        StringInputStream input = new StringInputStream(foXML);
        ByteArrayOutputStream output = new ByteArrayOutputStream();            
        fop.renderPostscript(input, output);
        byte[] psBytes = output.toByteArray();
        
        logger_.info("Rendering done!");

        assertNotNull(psBytes);
        assertTrue(psBytes.length > 0);
        
        logger_.info("Input FO: " + foXML.length() + 
            " --> Output Postscript: " + psBytes.length);
    }
    
    //--------------------------------------------------------------------------
    // RenderRequest
    //--------------------------------------------------------------------------

    /**
     * PDF render request.
     */    
    class RenderRequest implements Runnable
    {
        private int cnt_;
        private String foXML_;

        RenderRequest(int cnt, String foXML)
        {
            cnt_ = cnt;
            foXML_ = foXML;
        }

        //----------------------------------------------------------------------
        // Runnable Interface 
        //----------------------------------------------------------------------
        
        public void run()
        {
            try
            {
                logger_.info("Request " + cnt_+ " processing...");
                     
                FOProcessor fop = 
                    FOProcessorFactory.createProcessor(
                        FOProcessorFactory.FO_IMPL_APACHE);
                            
                fop.initialize(new Properties());
                    
                StringInputStream input = new StringInputStream(foXML_);
                ByteArrayOutputStream output = new ByteArrayOutputStream();            
                fop.renderPDF(input, output);
                byte[] pdfBytes = output.toByteArray();
                    
                logger_.info("Render request " + cnt_ + " to PDF done!");

                assertNotNull(pdfBytes);
                assertTrue(pdfBytes.length > 0);
                    
                logger_.info(
                    "Input FO: " + foXML_.length() + 
                    " --> Output PDF: " + pdfBytes.length);
            }
            catch (Exception e)
            {
                logger_.error(e.getMessage(), e);
            }
        }
    }
}