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
 * Unit test for FOPProcessor
 */
public class FOPProcessorTest extends TestCase
{
    private static final Logger logger_ =
        Logger.getLogger(FOPProcessorTest.class);

    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------

    /**
     * Entrypoint 
     * 
     * @param  args  None recognized
     */
    public static void main(String[] args)
    {
        TestRunner.run(FOPProcessorTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------

    /**
     * Tests renderPDF()
     *  
     * @throws Exception on error
     */
    public void testRenderPDF() throws Exception
    {
        logger_.info("Running testRenderPDF...");
        
        final String foXML = new String(
            ResourceUtil.getResourceAsBytes(
                "/toolbox/util/xslfo/test/FOPProcessorTest.fo"));

        class RenderRequest implements Runnable
        {
            int cnt_;

            public RenderRequest(int cnt)
            {
                cnt_ = cnt;
            }

            public void run()
            {
                try
                {
                    // Weirdness: ILog's don't work from this thread
                    logger_.info("Request " + cnt_+ " processing...");
                     
                    FOProcessor fop = 
                        FOProcessorFactory.createProcessor(
                            FOProcessorFactory.FO_IMPL_APACHE);
                            
                    fop.initialize(new Properties());
                    
                    StringInputStream input = new StringInputStream(foXML);
                    ByteArrayOutputStream output = new ByteArrayOutputStream();            
                    fop.renderPDF(input, output);
                    byte[] pdfBytes = output.toByteArray();
                    
                    logger_.info("Request " + cnt_ + " done!");

                    assertNotNull(pdfBytes);
                    assertTrue(pdfBytes.length > 0);
                    
                    logger_.info("Input FO: " + foXML.length() + 
                        " --> Output PDF: " + pdfBytes.length);
                }
                catch (Exception e)
                {
                    logger_.error(e.getMessage(), e);
                }
            }
        }

        int NUM = 10;

        Thread t[] = new Thread[NUM];

        for (int i = 0; i < NUM; i++)
        {
            t[i] = new Thread(new RenderRequest(i));
            t[i].start();
        }

        // Wait for all to complete
        for (int i = 0; i < NUM; i++)
            t[i].join();
    }
    
    /**
     * Tests renderPostscript()
     *  
     * @throws Exception on error
     */
    public void testRenderPostscript() throws Exception
    {
        logger_.info("Running testRenderPostscript...");
        
        final String foXML = new String(
            ResourceUtil.getResourceAsBytes(
                "/toolbox/util/xslfo/test/FOPProcessorTest.fo"));

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
}