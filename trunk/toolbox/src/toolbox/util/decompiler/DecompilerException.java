/*
 * Created on Oct 17, 2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package toolbox.util.decompiler;

/**
 * @author analogue
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class DecompilerException extends Exception
{
    /**
     * 
     */
    public DecompilerException()
    {
    }

    /**
     * @param message
     */
    public DecompilerException(String message)
    {
        super(message);
    }

    /**
     * @param cause
     */
    public DecompilerException(Throwable cause)
    {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public DecompilerException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
