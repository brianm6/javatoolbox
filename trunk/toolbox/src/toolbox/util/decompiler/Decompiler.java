package toolbox.util.decompiler;

import java.io.File;

/**
 * Decompiler Interface.
 */
public interface Decompiler
{
    /**
     * Returns UI friendly name for the decompiler.
     * 
     * @return String
     */
    String getName();
    
    
    /**
     * Decompiles the given classFile.
     * 
     * @param classFile Class to decompile
     * @return Decompiled class
     * @throws DecompilerException on error
     */
    String decompile(File classFile) throws DecompilerException;
    
    
    /**
     * Decompiles the given class.
     *  
     * @param className Name of the class.
     * @param classPath Classpath to search for the class on.
     * @return Decompiled class.
     */
    String decompile(String className, String classPath) 
        throws DecompilerException;    
}