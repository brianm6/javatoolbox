package toolbox.util.decompiler;

import java.io.File;

/**
 * Common interface for various decompiler implementations.
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
     * @param classFile Class file to decompile
     * @return Souce code of the decompiled class
     * @throws DecompilerException on decompilation error
     */
    String decompile(File classFile) throws DecompilerException;
    
    
    /**
     * Decompiles a class that exists on the given classpath.
     *  
     * @param className Name of the class.
     * @param classPath Classpath that the class exists in.
     * @return Source code of the decompiled class.
     * @throws DecompilerException on decompilation error.
     */
    String decompile(String className, String classPath) 
        throws DecompilerException;    
}