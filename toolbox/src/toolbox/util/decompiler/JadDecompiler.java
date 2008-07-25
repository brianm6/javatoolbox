package toolbox.util.decompiler;

import java.io.File;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import toolbox.util.ClassUtil;

/**
 * Decompiler adapter for the <a href="http://kpdus.tripod.com/jad.html">JAD</a>
 * decompiler.
 * 
 * @see toolbox.util.decompiler.DecompilerFactory
 */
public class JadDecompiler extends AbstractDecompiler {
    
    private static final Logger logger_ = Logger.getLogger(JadDecompiler.class);

    // --------------------------------------------------------------------------
    // Static Fields
    // --------------------------------------------------------------------------

    /**
     * Flag set if the jad executable is found on the filesystem.
     */
    private static Boolean executableFound_;

    // --------------------------------------------------------------------------
    // Constructors
    // --------------------------------------------------------------------------

    public JadDecompiler() {
        super("Jad");
    }

    // --------------------------------------------------------------------------
    // Public
    // --------------------------------------------------------------------------

    /**
     * Returns true if the jad executable is found on the system path, false
     * otherwise.
     * 
     * @return boolean
     */
    public static boolean isFound() {
        if (executableFound_ == null)
            executableFound_ = new Boolean(ClassUtil.findInPath("jad.exe") != null);

        return executableFound_.booleanValue();
    }

    // --------------------------------------------------------------------------
    // Decompiler Interface
    // --------------------------------------------------------------------------

    public String decompile(File classFile) throws DecompilerException {
        
        if (!isFound())
            throw new UnsupportedOperationException("Jad.exe executable not found on the system path.");

        String javaSource = null;

        try {
            String cmdLine = "jad -space -o -ff -s java -p -& \"" + classFile.getCanonicalPath() + "\"";
            Process process = Runtime.getRuntime().exec(cmdLine);
            javaSource = IOUtils.toString(process.getInputStream());

        } 
        catch (Exception e) {
            throw new DecompilerException(e);
        }

        return javaSource;
    }

    /**
     * Not supported.
     */
    public String decompile(String className, String classPath) throws DecompilerException {
        throw new UnsupportedOperationException("Not supported");
    }
}