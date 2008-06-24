package toolbox.util.formatter;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import de.hunsicker.jalopy.Jalopy;

import org.apache.log4j.Logger;

import toolbox.util.FileUtil;

/**
 * Java source code formatter that uses 
 * <a href="http://jalopy.sf.net">Jalopy</a> internally for formatting.
 * <p>
 * <b>Example:</b>
 * <pre class="snippet">
 * Formatter f = new JavaFormatter();
 * String sourceCode = getSomeJavaCode();
 * String formattedCode = f.format(sourceCode);
 * </pre>
 */
public class JavaFormatter extends AbstractFormatter
{
    // TODO: Implement IPreferenced
    
    private static final Logger logger_ = Logger.getLogger(JavaFormatter.class);

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Jalopy java source code formatter.
     */
    private Jalopy jalopy_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a JavaFormatter.
     */
    public JavaFormatter()
    {
        super("Java Formatter");
        jalopy_ = new Jalopy();
    }

    //--------------------------------------------------------------------------
    // Formatter Interface
    //--------------------------------------------------------------------------
    
    /**
     * Formats java source code.
     * 
     * @see toolbox.util.formatter.Formatter#format(java.io.InputStream, 
     *      java.io.OutputStream)
     */
    public void format(InputStream input, OutputStream output) throws Exception
    {
        // specify input and output target
        File fakeInputFile = FileUtil.createTempFile();
        FileUtil.setFileContents(fakeInputFile, "delete me", false);
        jalopy_.setInput(input, fakeInputFile.getCanonicalPath());
        jalopy_.setOutput(new OutputStreamWriter(output));

        // format and overwrite the given input file
        boolean formatted = jalopy_.format();

        if (jalopy_.getState() == Jalopy.State.OK)
            logger_.debug("Java formatted successfully");
        else if (jalopy_.getState() == Jalopy.State.WARN)
            logger_.warn("Java formatted with warnings");
        else if (jalopy_.getState() == Jalopy.State.ERROR)
            logger_.error("Java could not be formatted");
        
        FileUtil.deleteQuietly(fakeInputFile);
        output.flush();
    }
}