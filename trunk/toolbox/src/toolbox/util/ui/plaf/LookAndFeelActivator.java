package toolbox.util.ui.plaf;

/**
 * LookAndFeelActivator is an interface for object which wish to activate a
 * look and feel in a consistent manner using LAFInfo.
 */
public interface LookAndFeelActivator
{
    /**
     * Sets the look and feel.
     * 
     * @param lookAndFeelInfo The Look and Feel will be set according to the 
     *        configuration info in this object.
     */
    void setLookAndFeelInfo(LAFInfo lookAndFeelInfo);
    
    
    /**
     * Returns the current look and feel.
     * 
     * @return LAFInfo
     */
    LAFInfo getLookAndFeelInfo();
    
    
    /**
     * Activates the look and feel.
     * 
     * @throws Exception on error.
     */
    void activate() throws Exception;
}
