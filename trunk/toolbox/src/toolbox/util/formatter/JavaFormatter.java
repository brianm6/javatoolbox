package toolbox.util.formatter;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import de.hunsicker.jalopy.Jalopy;

import org.apache.log4j.Logger;

import toolbox.util.FileUtil;

/**
 * Java source code formatter that uses Jalopy internally.
 * <pre class="snippet">
 * Usage:
 * 
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
     * Java source code formatter.
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
            logger_.info("Java formatted successfully formatted");
        else if (jalopy_.getState() == Jalopy.State.WARN)
            logger_.warn("Java formatted with warnings");
        else if (jalopy_.getState() == Jalopy.State.ERROR)
            logger_.error("Java could not be formatted");
        
        FileUtil.delete(fakeInputFile);
        output.flush();
    }
}