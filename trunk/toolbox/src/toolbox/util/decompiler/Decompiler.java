package toolbox.util.decompiler;

import java.io.File;

/**
 * Decompiler Interface
 */
public interface Decompiler
{
    /**
     * Returns UI friendly name for the decompiler
     * 
     * @return String
     */
    public String getName();
    
    /**
     * Decompiles the given classFile
     * 
     * @param classFile Class to decompile
     * @return Java code representing the class file
     */
    public String decompile(File classFile) throws DecompilerException;
    
    /**
     * 
     * @param className
     * @param classPath
     * @return
     */
    public String decompile(String className, String classPath)
        throws DecompilerException;    
}
