package toolbox.util.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.ResourceCloser;
import toolbox.util.net.AbstractConnection;
import toolbox.util.net.IConnection;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

/**
 * Unit test for ResourceCloser.
 */
public class ResourceCloserTest extends TestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(ResourceCloserTest.class);

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
        TestRunner.run(ResourceCloserTest.class);    
    }
    
    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------

    /**
     * Tests close(Context) for a valid, null, and exception throwing Context.
     */
    public void testCloseContext()
    {
        logger_.info("Running testCloseContext...");
        
        ResourceCloser.close(new EmptyContext());
        ResourceCloser.close((Context) null);
        ResourceCloser.close(new ThrowingContext());

    }           

    /**
     * Tests close(IConnection) for a valid, null, and exception throwing
     * IConnection.
     */
    public void testCloseIConnection()
    {
        logger_.info("Running testCloseIConnection...");

        ResourceCloser.close(new EmptyConnection());
        ResourceCloser.close((IConnection) null);
        ResourceCloser.close(new ThrowingConnection());
    }

    //--------------------------------------------------------------------------
    // EmptyConnection
    //--------------------------------------------------------------------------
    
    /**
     * No-op implementation of an IConnection.
     */
    class EmptyConnection extends AbstractConnection 
    {
        public boolean isConnected()
        {
            return false;
        }
        
        public void connect() throws IOException
        {
        }
        
        public OutputStream getOutputStream() throws IOException
        {
            return null;
        }
        
        public InputStream getInputStream() throws IOException
        {
            return null;
        }
        
        public void close() throws IOException
        {
            logger_.info("Closing IConnection");
        }
    }    

    //--------------------------------------------------------------------------
    // ThrowingConnection
    //--------------------------------------------------------------------------
    
    /**
     * IConnection that throws an exception on close().
     */
    class ThrowingConnection extends EmptyConnection 
    {
        public void close() throws IOException
        {
            throw new IOException("Thrown on purpose.");
        }
    }    
    
    //--------------------------------------------------------------------------
    // EmptyContext
    //--------------------------------------------------------------------------
    
    /**
     * No-op implementation of a Context.
     */
    class EmptyContext implements Context
    {
        public void close() throws NamingException
        {
        }


        public String getNameInNamespace() throws NamingException
        {
            return null;
        }


        public void destroySubcontext(String name) throws NamingException
        {
        }


        public void unbind(String name) throws NamingException
        {
        }


        public Hashtable getEnvironment() throws NamingException
        {
            return null;
        }


        public void destroySubcontext(Name name) throws NamingException
        {
        }


        public void unbind(Name name) throws NamingException
        {
        }


        public Object lookup(String name) throws NamingException
        {
            return null;
        }


        public Object lookupLink(String name) throws NamingException
        {
            return null;
        }


        public Object removeFromEnvironment(String propName)
        throws NamingException
        {
            return null;
        }


        public void bind(String name, Object obj) throws NamingException
        {
        }


        public void rebind(String name, Object obj) throws NamingException
        {
        }


        public Object lookup(Name name) throws NamingException
        {
            return null;
        }


        public Object lookupLink(Name name) throws NamingException
        {
            return null;
        }


        public void bind(Name name, Object obj) throws NamingException
        {
        }


        public void rebind(Name name, Object obj) throws NamingException
        {
        }


        public void rename(String oldName, String newName)
        throws NamingException
        {
        }


        public Context createSubcontext(String name) throws NamingException
        {
            return null;
        }


        public Context createSubcontext(Name name) throws NamingException
        {
            return null;
        }


        public void rename(Name oldName, Name newName)
        throws NamingException
        {
        }


        public NameParser getNameParser(String name) throws NamingException
        {
            return null;
        }


        public NameParser getNameParser(Name name) throws NamingException
        {
            return null;
        }


        public NamingEnumeration list(String name) throws NamingException
        {
            return null;
        }


        public NamingEnumeration listBindings(String name)
        throws NamingException
        {
            return null;
        }


        public NamingEnumeration list(Name name) throws NamingException
        {
            return null;
        }


        public NamingEnumeration listBindings(Name name)
        throws NamingException
        {
            return null;
        }


        public Object addToEnvironment(String propName, Object propVal)
        throws NamingException
        {
            return null;
        }


        public String composeName(String name, String prefix)
        throws NamingException
        {
            return null;
        }


        public Name composeName(Name name, Name prefix)
        throws NamingException
        {
            return null;
        }
    };

    //--------------------------------------------------------------------------
    // ThrowingContext
    //--------------------------------------------------------------------------
    
    /**
     * Throws an exception when closed.
     */
    class ThrowingContext extends EmptyContext
    {
        public void close() throws NamingException
        {
            throw new NamingException("Thrown on purpose");
        }
    }
}