package toolbox.workspace.lookandfeel.skinlf;

/**
 * Look and Feel that is just a wrapper for the SkinLF look and feel coupled
 * with the Cell Shaded theme.
 */
public class CellShadedLookAndFeel extends AbstractSkinLookAndFeel
{
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a CellShadedLookAndFeel 
     */
    public CellShadedLookAndFeel()
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
        return "skinlf/cellshaded.zip";
    }

    /**
     * @see toolbox.workspace.lookandfeel.skinlf.AbstractSkinLookAndFeel
     *      #getThemeName()
     */
    public String getThemeName()
    {
        return "Cell Shaded";
    }
}