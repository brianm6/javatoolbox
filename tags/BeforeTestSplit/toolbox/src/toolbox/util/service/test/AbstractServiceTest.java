package toolbox.util.service.test;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.service.AbstractService;
import toolbox.util.service.Service;
import toolbox.util.service.ServiceException;
import toolbox.util.service.ServiceListener;

/**
 * Unit test for AbstractService.
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
        Service s = new MockService();
        s.addServiceListener(new MockServiceListener());
        s.start();
        assertTrue(s.isRunning());
    }

    
    /**
     * Tests stop()
     * 
     * @throws ServiceException on service error.
     */
    public void testStop() throws ServiceException
    {
        Service s = new MockService();
        s.addServiceListener(new MockServiceListener());
        s.start();
        s.stop();
        assertFalse(s.isRunning());
    }

    
    /**
     * Tests pause()
     * 
     * @throws ServiceException on service error.
     */
    public void testPause() throws ServiceException
    {
        Service s = new MockService();
        s.addServiceListener(new MockServiceListener());
        s.start();
        s.pause();
        assertTrue(s.isPaused());
    }

    
    /**
     * Tests resume()
     * 
     * @throws ServiceException on service error.
     */
    public void testResume() throws ServiceException 
    {
        Service s = new MockService();
        s.addServiceListener(new MockServiceListener());
        s.start();
        s.pause();
        s.resume();
        assertFalse(s.isPaused());
    }

    //--------------------------------------------------------------------------
    // MockService
    //--------------------------------------------------------------------------
    
    class MockService extends AbstractService
    {
        /**
         * @see toolbox.util.service.AbstractService#start()
         */
        public void start() throws ServiceException
        {
            try
            {
                //checkState(STATE_START);
            
                // do your thang
            
                super.start();
            }
            catch (Exception e)
            {
                ; // Rollback on any exceptions
            }
        }
    }
    
    //--------------------------------------------------------------------------
    // MockServiceListener
    //--------------------------------------------------------------------------
    
    class MockServiceListener implements ServiceListener
    {
        /**
         * @see toolbox.util.service.ServiceListener#servicePaused(
         *      toolbox.util.service.Service)
         */
        public void servicePaused(Service service) throws ServiceException
        {
            logger_.info("Event: service paused");
        }

        /**
         * @see toolbox.util.service.ServiceListener#serviceResumed(
         *      toolbox.util.service.Service)
         */
        public void serviceResumed(Service service) throws ServiceException
        {
            logger_.info("Event: service resumed");
            
        }

        /**
         * @see toolbox.util.service.ServiceListener#serviceStarted(
         *      toolbox.util.service.Service)
         */
        public void serviceStarted(Service service) throws ServiceException
        {
            logger_.info("Event: service started");
        }

        /**
         * @see toolbox.util.service.ServiceListener#serviceStopped(
         *      toolbox.util.service.Service)
         */
        public void serviceStopped(Service service) throws ServiceException
        {
            logger_.info("Event: service stopped");
        }
    }
}
