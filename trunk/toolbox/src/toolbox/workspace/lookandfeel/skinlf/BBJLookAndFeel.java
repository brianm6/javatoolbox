package toolbox.workspace.lookandfeel.skinlf;

/**
 * Look and Feel that is just a wrapper for the SkinLF look and feel coupled
 * with the BBJ theme.
 */
public class BBJLookAndFeel extends AbstractSkinLookAndFeel
{
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a BBJLookAndFeel 
     */
    public BBJLookAndFeel()
    {
    }

    //--------------------------------------------------------------------------
    // AbstractSkinLookAndFeel Impl
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.workspace.lookandfeel.skinlf.AbstractSkinLookAndFeel
     *      #getThemeFile()
     */
    public String getThemeFile()
    {
        return "skinlf/bbj.zip";
    }

    /**
     * @see toolbox.workspace.lookandfeel.skinlf.AbstractSkinLookAndFeel
     *      #getThemeName()
     */
    public String getThemeName()
    {
        return "BBJ";
    }
}