package toolbox.workspace.lookandfeel.skinlf;

/**
 * Look and Feel that is just a wrapper for the SkinLF look and feel coupled
 * with the Mac OS theme.
 */
public class MacLookAndFeel extends AbstractSkinLookAndFeel
{
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a MacLookAndFeel 
     */
    public MacLookAndFeel()
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
        return "skinlf/macos.zip";
    }

    /**
     * @see toolbox.workspace.lookandfeel.skinlf.AbstractSkinLookAndFeel
     *      #getThemeName()
     */
    public String getThemeName()
    {
        return "Mac";
    }
}