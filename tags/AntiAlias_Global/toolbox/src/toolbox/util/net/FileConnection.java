package toolbox.util.net;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import toolbox.util.Assert;
import toolbox.util.StreamUtil;

/**
 * Concrete implementation of an IConnection that uses one file to read input
 * from and other file to write output to.
 */
public class FileConnection extends AbstractConnection implements IConnection
{
    /**
     * File tied to the InputStream
     */
    private File inputFile_;

    /**
     * InputStream tied to the connection
     */
    private InputStream inputStream_;
        
    /**
     * File tied to the OutputStream
     */
    private File outputFile_;
    
    /**
     * OutputStream tied to the connection
     */
    private OutputStream outputStream_;

    /**
     * Flag that tracks the connected state of the connection
     */
    private boolean connected_ = false;

    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a FileConnection with no connection endpoint
     */
    public FileConnection()
    {
        addConnectionListener(new InternalFileConnectionListener());
    }
    
    /**
     * Creates a FileConnection with a single file for input and another file
     * for output.
     * 
     * @param  inputFile   File tied to the input stream
     * @param  outputFile  File tied to the output stream
     */
    public FileConnection(File inputFile, File outputFile)
    {
        inputFile_  = inputFile;
        outputFile_ = outputFile;
        addConnectionListener(new InternalFileConnectionListener());
        connected_ = true;
    }

    /**
     * Creates a FileConnection with the given input and output file names
     * 
     * @param   inputFile  File tied to the input stream
     * @param   outputFile File tied to the output stream
     * @throws  IOException on I/O error
     */
    public FileConnection(String inputFile, String outputFile) 
        throws IOException 
    {
        this(new File(inputFile), new File(outputFile));
    }

    //--------------------------------------------------------------------------
    //  IConnection Interface
    //--------------------------------------------------------------------------
    
    /**
     * Opens the connection
     * 
     * @throws IOException on I/O error
     */
    public void connect() throws IOException
    {
        // Input file validations
        
        Assert.isTrue(inputFile_.exists(), 
            "Input file " + inputFile_ + "does not exist.");
        
        Assert.isTrue(inputFile_.canRead(),
            "Cannot read from input file " + inputFile_ + ".");
            
        Assert.isTrue(inputFile_.isFile(),
            "Input file " + inputFile_ + "cannot be a directory.");

        inputStream_ = 
            new BufferedInputStream(new FileInputStream(inputFile_));
        
        // Output file validations
           
        outputStream_ = 
            new BufferedOutputStream(new FileOutputStream(outputFile_));        
            
        fireConnectionStarted(this);
    }

    /**
     * Closes the connection
     * 
     * @throws IOException on I/O error
     */
    public void close() throws IOException
    {
        fireConnectionClosing(this);
        
        if (inputStream_ != null)
            StreamUtil.close(inputStream_);
            
        if (outputStream_ != null)
            StreamUtil.close(outputStream_);
            
        fireConnectionClosed(this);
    }

    /**
     * Accessor for the input stream
     * 
     * @return InputStream
     * @throws IOException on I/O error
     */
    public InputStream getInputStream() throws IOException
    {
        return inputStream_;
    }

    /**
     * Accessor for the output stream
     * 
     * @return OutputStream
     * @throws IOException on I/O error
     */
    public OutputStream getOutputStream() throws IOException
    {
        return outputStream_;
    }

    /**
     * Returns true if connected, false otherwise
     * 
     * @return boolean
     */
    public boolean isConnected()
    {
        return connected_;
    }

    //--------------------------------------------------------------------------
    // Overrides java.lang.Object
    //--------------------------------------------------------------------------
    
    /**
     * Returns string containing file connections name, input file, and output
     * file.
     * 
     * @return String
     */
    public String toString()
    {
        return  getName() + " file connection: " +
                "Input: " + inputFile_ + "  " +
                "Output: " + outputFile_;
    }

    //--------------------------------------------------------------------------
    //  Inner Classes
    //--------------------------------------------------------------------------
    
    /**
     * Internal socket connection listener that keeps track of the connected
     * state based on generated events.
     */
    class InternalFileConnectionListener implements IConnectionListener 
    {
        public void connectionClosed(IConnection connection)
        {
            connected_ = false;
        }
        
        public void connectionClosing(IConnection connection)
        {
        }

        public void connectionInterrupted(IConnection connection)
        {
            connected_ = false;
        }
        
        public void connectionStarted(IConnection connection)
        {
            connected_ = true;
        }
    }
}
