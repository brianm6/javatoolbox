package toolbox.util.decompiler;

import java.io.File;

import toolbox.util.service.Nameable;

/**
 * Common interface for various decompiler implementations. Use 
 * {@link DecompilerFactory} to create concrete instances.
 * <p>
 * <b>Example:</b>
 * <pre class="snippet">
 * Decompiler d = DecompilerFactory.create(DecompilerFactory.DECOMPILER_DEFAULT);
 * String sourceCode = d.decompile(new File("a.class"));
 * </pre>
 * 
 * @see toolbox.util.decompiler.DecompilerFactory
 */
public interface Decompiler extends Nameable
{
    /**
     * Decompiles a class file and returns the source code as a string.
     * 
     * @param classFile Class file to decompile.
     * @return String
     * @throws DecompilerException on decompilation error.
     */
    String decompile(File classFile) throws DecompilerException;
    
    
    /**
     * Decompiles a class that exists on the given classpath and returns the
     * source code as a string.
     *  
     * @param className Name of the class.
     * @param classPath Classpath that the class exists in.
     * @return String
     * @throws DecompilerException on decompilation error.
     */
    String decompile(String className, String classPath) 
        throws DecompilerException;    
}