package toolbox.util.ui.font;

/** 
 * Indicates that an invalid font is currently specified 
 */
public class FontSelectionException extends Exception
{
    /**
     * Creates a FontSelectionException
     * 
     * @parm  msg  Exception message
     */
    public FontSelectionException(String msg)
    {
        super(msg);
    }
}
