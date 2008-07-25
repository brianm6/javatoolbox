package toolbox.util.decompiler;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.commons.io.output.NullWriter;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Decompiler adapter for the <a href="http://jode.sf.net">Jode</a> decompiler.
 * 
 * @see toolbox.util.decompiler.DecompilerFactory
 */
public class JodeDecompiler extends AbstractDecompiler {
    
    private static final Logger logger_ = Logger.getLogger(JodeDecompiler.class);

    // --------------------------------------------------------------------------
    // Fields
    // --------------------------------------------------------------------------

    /**
     * Reference to Jode's implementation of a java decompiler.
     */
    private jode.decompiler.Decompiler decompiler_;

    // --------------------------------------------------------------------------
    // Constructors
    // --------------------------------------------------------------------------

    public JodeDecompiler() {
        super("Jode");
        decompiler_ = new jode.decompiler.Decompiler();
        decompiler_.setOption("style", "pascal");
        decompiler_.setErr(new PrintWriter(new NullWriter()));

        // Tabwidth was removed in the latest version
        // decompiler_.setOption("tabwidth", "4");
    }

    // --------------------------------------------------------------------------
    // Decompiler Interface
    // --------------------------------------------------------------------------

    /**
     * Not supported.
     */
    public String decompile(File classFile) {
        throw new UnsupportedOperationException("Not supported");
    }

    public String decompile(String className, String classPath) throws DecompilerException {

        decompiler_.setClassPath(classPath);
        StringWriter javaWriter = new StringWriter();

        try {
            decompiler_.decompile(className, javaWriter, null);
        } 
        catch (IOException e) {
            throw new DecompilerException(e);
        }

        // Nuke the tabs. They're hardcoded as a width of 8 in jode.
        return StringUtils.replace(javaWriter.toString(), "\t", "        ");
    }
}