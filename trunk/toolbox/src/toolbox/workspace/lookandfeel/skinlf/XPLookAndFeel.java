package toolbox.workspace.lookandfeel.skinlf;

/**
 * Look and Feel that is just a wrapper for the SkinLF look and feel coupled
 * with the XP theme.
 */
public class XPLookAndFeel extends AbstractSkinLookAndFeel
{
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a XPLookAndFeel 
     */
    public XPLookAndFeel()
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
        return "skinlf/xpluna.zip";
    }

    /**
     * @see toolbox.workspace.lookandfeel.skinlf.AbstractSkinLookAndFeel
     *      #getThemeName()
     */
    public String getThemeName()
    {
        return "XP";
    }
}