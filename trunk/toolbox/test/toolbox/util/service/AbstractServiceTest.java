package toolbox.util.service;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

/**
 * Unit test for {@link toolbox.util.service.AbstractService}.
 */
public class AbstractServiceTest extends TestCase
{
    // TODO: Remove me!
    
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
    
    public void testCrap()
    {
        logger_.info("Running testCrap...");
    }
    
//    /**
//     * Tests start()
//     * 
//     * @throws ServiceException on service error.
//     */
//    public void testStart() throws ServiceException
//    {
//        logger_.debug("Running testStart...");
//        
//        Service s = new MockService();
//        s.initialize(Collections.EMPTY_MAP);
//        s.start();
//        assertTrue(s.isRunning());
//        
//        s = new MockService();
//        s.initialize(Collections.EMPTY_MAP);
//        s.start();
//        s.stop();
//        s.start();
//        assertTrue(s.isRunning());
//
//        // TODO: add later
////        s = new MockService();
////        s.initialize(Collections.EMPTY_MAP);
////        s.start();
////        s.suspend();
////        s.start();
//    }
//
//    
//    /**
//     * Tests invalid state transitions on start()
//     * 
//     * @throws ServiceException on service error.
//     */
//    public void testStartInvalid() throws ServiceException
//    {
//        logger_.info("Running testStartInvalid...");
//        
//        try 
//        {
//            Service s = new MockService();
//            s.start();
//            fail("Invalid start from uninitialized");
//        }
//        catch (IllegalStateException ise) 
//        {
//            logger_.debug("SUCCESS --> " + ise);
//        }
//
//        try 
//        {
//            Service s = new MockService();
//            s.initialize(Collections.EMPTY_MAP);
//            s.start();
//            s.suspend();
//            s.resume();
//            s.start();
//            fail("Invalid start from resume");
//        }
//        catch (IllegalStateException ise) 
//        {
//            logger_.debug("SUCCESS --> " + ise);
//        }
//     
//        try 
//        {
//            Service s = new MockService();
//            s.initialize(Collections.EMPTY_MAP);
//            s.destroy();
//            s.start();
//            fail("Invalid start from destroy");
//        }
//        catch (IllegalStateException ise) 
//        {
//            logger_.debug("SUCCESS --> " + ise);
//        }
//    }
//    
//
//    
//    /**
//     * Tests suspend()
//     * 
//     * @throws ServiceException on service error.
//     */
//    public void testSuspend() throws ServiceException
//    {
//        logger_.info("Running testSuspend...");
//        
//        Service s = new MockService();
//        s.addServiceListener(new MockServiceListener());
//        s.initialize(Collections.EMPTY_MAP);
//        s.start();
//        s.suspend();
//        assertTrue(s.isSuspended());
//    }
//
//
//    /**
//     * Tests invalid state transitions on suspend()
//     * 
//     * @throws ServiceException on service error.
//     */
//    public void testSuspendInvalid() throws ServiceException
//    {
//        logger_.debug("Running testSuspendInvalid...");
//        
//        try 
//        {
//            Service s = new MockService();
//            s.suspend();
//            fail("Invalid suspend from uninitialized");
//        }
//        catch (IllegalStateException ise) 
//        {
//            logger_.debug("SUCCESS --> " + ise);
//        }
//
//        try 
//        {
//            Service s = new MockService();
//            s.initialize(Collections.EMPTY_MAP);
//            s.suspend();
//            fail("Invalid suspend from initialized");
//        }
//        catch (IllegalStateException ise) 
//        {
//            logger_.debug("SUCCESS --> " + ise);
//        }
//     
//        try 
//        {
//            Service s = new MockService();
//            s.initialize(Collections.EMPTY_MAP);
//            s.start();
//            s.stop();
//            s.suspend();
//            fail("Invalid suspend from stop");
//        }
//        catch (IllegalStateException ise) 
//        {
//            logger_.debug("SUCCESS --> " + ise);
//        }
//        
//        try 
//        {
//            Service s = new MockService();
//            s.initialize(Collections.EMPTY_MAP);
//            s.destroy();
//            s.suspend();
//            fail("Invalid suspend from destroy");
//        }
//        catch (IllegalStateException ise) 
//        {
//            logger_.debug("SUCCESS --> " + ise);
//        }
//    }
//    
//
//    /**
//     * Tests resume()
//     * 
//     * @throws ServiceException on service error.
//     */
//    public void testResume() throws ServiceException 
//    {
//        logger_.info("Running testResume...");
//        
//        Service s = new MockService();
//        s.addServiceListener(new MockServiceListener());
//        s.initialize(Collections.EMPTY_MAP);
//        s.start();
//        s.suspend();
//        s.resume();
//        assertFalse(s.isSuspended());
//    }
//
//    
//    /**
//     * Tests invalid state transitions on resume()
//     * 
//     * @throws ServiceException on service error.
//     */
//    public void testResumeInvalid() throws ServiceException
//    {
//        logger_.info("Running testResumeInvalid...");
//        
//        try 
//        {
//            Service s = new MockService();
//            s.resume();
//            fail("Invalid resume from uninitialized");
//        }
//        catch (IllegalStateException ise) 
//        {
//            logger_.debug("SUCCESS --> " + ise);
//        }
//        
//        try 
//        {
//            Service s = new MockService();
//            s.initialize(Collections.EMPTY_MAP);
//            s.resume();
//            fail("Invalid resume from initialized");
//        }
//        catch (IllegalStateException ise) 
//        {
//            logger_.debug("SUCCESS --> " + ise);
//        }
//        
//        try 
//        {
//            Service s = new MockService();
//            s.initialize(Collections.EMPTY_MAP);
//            s.start();
//            s.resume();
//            fail("Invalid resume from start");
//        }
//        catch (IllegalStateException ise) 
//        {
//            logger_.debug("SUCCESS --> " + ise);
//        }
//        
//        try 
//        {
//            Service s = new MockService();
//            s.initialize(Collections.EMPTY_MAP);
//            s.destroy();
//            s.start();
//            s.stop();
//            s.suspend();
//            fail("Invalid resume from stopped");
//        }
//        catch (IllegalStateException ise) 
//        {
//            logger_.debug("SUCCESS --> " + ise);
//        }
//        
//        try 
//        {
//            Service s = new MockService();
//            s.initialize(Collections.EMPTY_MAP);
//            s.destroy();
//            s.suspend();
//            fail("Invalid resume from destryed");
//        }
//        catch (IllegalStateException ise) 
//        {
//            logger_.debug("SUCCESS --> " + ise);
//        }
//    }
//    
//    
//    /**
//     * Tests stop()
//     * 
//     * @throws ServiceException on service error.
//     */
//    public void testStop() throws ServiceException
//    {
//        logger_.info("Running testStop...");
//        
//        Service s = new MockService();
//        ServiceListener listener = new MockServiceListener();
//        s.addServiceListener(listener);
//        s.initialize(Collections.EMPTY_MAP);
//        s.start();
//        s.stop();
//        assertFalse(s.isRunning());
//        s.removeServiceListener(listener);
//    }
//
//
//    /**
//     * Tests invalid state transitions on stop()
//     * 
//     * @throws ServiceException on service error.
//     */
//    public void testStopInvalid() throws ServiceException
//    {
//        logger_.info("Running testStopInvalid...");
//        
//        try 
//        {
//            Service s = new MockService();
//            s.stop();
//            fail("Invalid stop from uninitialized");
//        }
//        catch (IllegalStateException ise) 
//        {
//            logger_.debug("SUCCESS --> " + ise);
//        }
//        
//        try 
//        {
//            Service s = new MockService();
//            s.initialize(Collections.EMPTY_MAP);
//            s.stop();
//            fail("Invalid stop from initialized");
//        }
//        catch (IllegalStateException ise) 
//        {
//            logger_.debug("SUCCESS --> " + ise);
//        }
//        
//        try 
//        {
//            Service s = new MockService();
//            s.initialize(Collections.EMPTY_MAP);
//            s.destroy();
//            s.stop();
//            fail("Invalid stop from destroyed");
//        }
//        catch (IllegalStateException ise) 
//        {
//            logger_.debug("SUCCESS --> " + ise);
//        }
//    }
//
//    
//    /**
//     * Tests destroy()
//     * 
//     * @throws ServiceException on service error.
//     */
//    public void testDestroy() throws ServiceException
//    {
//        logger_.info("Running testDestroy...");
//        
//        Service s = new MockService();
//        s.addServiceListener(new MockServiceListener());
//        s.initialize(Collections.EMPTY_MAP);
//        s.start();
//        s.stop();
//        s.destroy();
//        assertFalse(s.isRunning());
//    }
//    
//    
//    /**
//     * Tests invalid state transitions on destroy()
//     * 
//     * @throws ServiceException on service error.
//     */
//    public void testDestroyInvalid() throws ServiceException
//    {
//        logger_.info("Running testResumeInvalid...");
//        
//        try 
//        {
//            Service s = new MockService();
//            s.destroy();
//            fail("Invalid destroy from uninitialized");
//        }
//        catch (IllegalStateException ise) 
//        {
//            logger_.debug("SUCCESS --> " + ise);
//        }
//        
//        try 
//        {
//            Service s = new MockService();
//            s.initialize(Collections.EMPTY_MAP);
//            s.start();
//            s.destroy();
//            fail("Invalid destroy from start");
//        }
//        catch (IllegalStateException ise) 
//        {
//            logger_.debug("SUCCESS --> " + ise);
//        }
//        
//        try 
//        {
//            Service s = new MockService();
//            s.initialize(Collections.EMPTY_MAP);
//            s.start();
//            s.suspend();
//            s.resume();
//            s.destroy();
//            fail("Invalid destroy from resume");
//        }
//        catch (IllegalStateException ise) 
//        {
//            logger_.debug("SUCCESS --> " + ise);
//        }
//
//        try 
//        {
//            Service s = new MockService();
//            s.initialize(Collections.EMPTY_MAP);
//            s.start();
//            s.suspend();
//            s.destroy();
//            fail("Invalid destroy from suspend");
//        }
//        catch (IllegalStateException ise) 
//        {
//            logger_.debug("SUCCESS --> " + ise);
//        }
//    }

    
    /**
     * Tests non-strict state transitions. 
     */
//    public void testNotStrict() throws Exception
//    {
//        logger_.info("Running testNotStrict...");
//        
//        MockService s = new MockService(false);
//        s.stop();
//        s.initialize(MapUtils.EMPTY_MAP);
//        s.suspend();
//        s.destroy();
//    }
    
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
            logger_.debug("State = " + service.getState());
        }
    }
}
