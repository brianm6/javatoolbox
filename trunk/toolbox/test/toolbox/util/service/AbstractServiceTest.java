package toolbox.util.service;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

/**
 * Unit test for AbstractService.
 * 
 * @see toolbox.util.service.AbstractService
 */
public class AbstractServiceTest extends TestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(AbstractServiceTest.class);

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
        TestRunner.run(AbstractServiceTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests start()
     * 
     * @throws ServiceException on service error.
     */
    public void testStart() throws ServiceException
    {
        logger_.info("Running testStart...");
        
        Service s = new MockService();
        s.addServiceListener(new MockServiceListener());
        s.initialize();
        s.start();
        assertTrue(s.isRunning());
    }

    
    /**
     * Tests suspend()
     * 
     * @throws ServiceException on service error.
     */
    public void testSuspend() throws ServiceException
    {
        logger_.info("Running testSuspend...");
        
        Service s = new MockService();
        s.addServiceListener(new MockServiceListener());
        s.initialize();
        s.start();
        s.suspend();
        assertTrue(s.isSuspended());
    }

    
    /**
     * Tests resume()
     * 
     * @throws ServiceException on service error.
     */
    public void testResume() throws ServiceException 
    {
        logger_.info("Running testResume...");
        
        Service s = new MockService();
        s.addServiceListener(new MockServiceListener());
        s.initialize();
        s.start();
        s.suspend();
        s.resume();
        assertFalse(s.isSuspended());
    }

    
    /**
     * Tests stop()
     * 
     * @throws ServiceException on service error.
     */
    public void testStop() throws ServiceException
    {
        logger_.info("Running testStop...");
        
        Service s = new MockService();
        s.addServiceListener(new MockServiceListener());
        s.initialize();
        s.start();
        s.stop();
        assertFalse(s.isRunning());
    }

    
    /**
     * Tests destroy()
     * 
     * @throws ServiceException on service error.
     */
    public void testDestroy() throws ServiceException
    {
        logger_.info("Running testDestroy...");
        
        Service s = new MockService();
        s.addServiceListener(new MockServiceListener());
        s.initialize();
        s.start();
        s.stop();
        s.destroy();
        assertFalse(s.isRunning());
    }
    
    //--------------------------------------------------------------------------
    // MockService
    //--------------------------------------------------------------------------
    
    class MockService extends AbstractService
    {
        // No-op so instance can be created.
    }
    
    //--------------------------------------------------------------------------
    // MockServiceListener
    //--------------------------------------------------------------------------
    
    class MockServiceListener implements ServiceListener
    {
        /**
         * @see toolbox.util.service.ServiceListener#serviceStateChanged(
         *      toolbox.util.service.Service)
         */
        public void serviceStateChanged(Service service) throws ServiceException
        {
            logger_.info("State = " + service.getState());
        }
    }
}
