/**
 * Copyright 2002, Southwest Airlines
 * All Rights Reserved
 */
package toolbox.util.net.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.ThreadUtil;
import toolbox.util.net.AbstractConnection;
import toolbox.util.net.AsyncConnectionHandler;
import toolbox.util.net.IConnection;
import toolbox.util.net.IConnectionHandler;
import toolbox.util.thread.ReturnValue;
import toolbox.util.thread.ThreadDispatcher;
import toolbox.util.thread.strategy.ThreadPoolStrategy;

/**
 * Unit test for AsyncConnectionHandler
 */
public class AsyncConnectionHandlerTest extends TestCase
{
    /** Logger **/
    private static final Logger logger_ = 
        Logger.getLogger(AsyncConnectionHandlerTest.class);
    
    /**
     * Entry point
     */
    public static void main(String[] args)
    {
        TestRunner.run(AsyncConnectionHandlerTest.class);       
    }

    /**
     * Constructor for AsyncConnectionHandlerTest
     */
    public AsyncConnectionHandlerTest(String arg)
    {
        super(arg);
    }
    
    /**
     * Tests handle()
     */
    public void testHandle() throws Exception
    {
        final String helloWorld = "hello world!";
        
        /**
         * Dummy connection handler
         */ 
        class TestConnectionHandler implements IConnectionHandler
        {
            public Object handle(IConnection conn)
            {
                // Simulates long lived async activity
                ThreadUtil.sleep(5000);
                String result = helloWorld;
                logger_.debug(result);
                return result;
            }
        }

        /**
         * Dummy connection 
         */
        class TestConnection extends AbstractConnection implements IConnection
        {
            /**
             * Opens the connection
             */
            public void connect(){};
            
            /**
             * Closes the connection 
             */
            public void close() throws IOException {};
            
            /**
             * Accessor for the connections input stream
             * 
             * @return  InputStream
             */
            public InputStream getInputStream() throws IOException { return null;};
            
            /**
             * Accessor for the connections output stream
             * 
             * @return  OutputStream
             */
            public OutputStream getOutputStream() throws IOException { return null;};
            
            /**
             * @see IConnection#isConnected()
             */
            public boolean isConnected()
            {
                return false;
            }
        }
        
        IConnectionHandler handler = 
            new AsyncConnectionHandler(
                new TestConnectionHandler(), 
                    new ThreadDispatcher(new ThreadPoolStrategy(5,5)));
                
        IConnection connection = new TestConnection();
        
        Object result = handler.handle(connection);
        
        assertNotNull("result is null", result);
        
        ReturnValue rv = (ReturnValue)result;

        /* 
         * handle() should be async and we know there is a 5sec delay in handle() 
         * so the return value should not be immediately available
         */
        assertTrue("return should not be available", !rv.isAvailable());    

        // sleep longer than the delay in handle() to guarantee (ahem) completion
        ThreadUtil.sleep(10000);

        // now the result should definitely be abailable
        assertTrue("return should be available", rv.isAvailable());
        assertEquals("return values not same", rv.getValue(), helloWorld);      
        
        logger_.debug(rv.getValue());
    }
}