package toolbox.util.ui.font;

/** 
 * Indicates that an invalid font is currently specified 
 */
public class FontChooserException extends Exception
{
    /**
     * Creates a FontChooserException
     * 
     * @parm  msg  Exception message
     */
    public FontChooserException(String msg)
    {
        super(msg);
    }
}
