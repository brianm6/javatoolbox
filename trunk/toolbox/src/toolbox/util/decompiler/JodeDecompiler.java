package toolbox.util.decompiler;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import net.sf.jode.decompiler.Decompiler;

import org.apache.log4j.Logger;

import toolbox.util.StringUtil;
import toolbox.util.io.NullWriter;

/**
 * Decompiler bridge to the Jode decompiler @ http://jode.sf.net 
 */
public class JodeDecompiler implements toolbox.util.decompiler.Decompiler
{
    private static final Logger logger_ = 
        Logger.getLogger(JodeDecompiler.class);
    
    /**
     * Jode decompiler 
     */
    private Decompiler decompiler_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    public JodeDecompiler()
    {
        decompiler_ = new Decompiler();
        decompiler_.setOption("style", "pascal");
        decompiler_.setOption("tabwidth", "4");
        decompiler_.setErr(new PrintWriter(new NullWriter()));
    }
    
    //--------------------------------------------------------------------------
    // Decompiler Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.decompiler.Decompiler#getName()
     */
    public String getName()
    {
        return "Jode";
    }
    
    /**
     * @see toolbox.util.decompiler.Decompiler#decompile(java.io.File)
     */
    public String decompile(File classFile)
    {
        throw new IllegalArgumentException("Not supported");
    }
    
    /**
     * 
     * @param className
     * @param classPath
     * @return
     * @throws IOException
     */
    public String decompile(String className, String classPath)
        throws DecompilerException
    {
        decompiler_.setClassPath(classPath);
        StringWriter javaWriter = new StringWriter();
        
        try
        {
            decompiler_.decompile(className, javaWriter, null);
        }
        catch (IOException e)
        {
            throw new DecompilerException(e);
        }
                
        // Nuke the tabs                
        String javaSource = 
            StringUtil.replace(javaWriter.toString(), "\t", "    ");
                    
        //logger_.debug("\n" + javaSource);    

        return javaSource;
    }
    
    //--------------------------------------------------------------------------
    // Overrides java.lang.Object
    //--------------------------------------------------------------------------
    
    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return getName();
    }
    
}
