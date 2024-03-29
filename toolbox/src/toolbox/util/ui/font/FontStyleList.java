package toolbox.util.ui.font;

import java.awt.Font;

import toolbox.util.ui.list.JSmartList;

/**
 * Represents a list of the four font styles: plain, bold, italic, and bold 
 * italic.
 */
public class FontStyleList extends JSmartList
{
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Construct a new FontStyleList, using the supplied values for style
     * display names.
     * 
     * @param styleDisplayNames Must contain exactly four members. The members
     *        of this array represent the following styles, in order:
     *        Font.PLAIN, Font.BOLD, Font.ITALIC, and Font.BOLD + Font.ITALIC.
     * @throws IllegalArgumentException if styleDisplayNames does not contain
     *         exactly four String values.
     */
    public FontStyleList(String[] styleDisplayNames) 
        throws IllegalArgumentException
    {
        super(validateStyleDisplayNames(styleDisplayNames));
    }
    
    //--------------------------------------------------------------------------
    // Public 
    //--------------------------------------------------------------------------
    
    /**
     * Returns currently selected font style. See Font.PLAIN, etc.
     * 
     * @return currently selected font style.
     * @throws FontChooserException thrown if no font style is currently
     *         selected.
     */
    public int getSelectedStyle() throws FontChooserException
    {
        switch (getSelectedIndex())
        {
            case 0 :
                return Font.PLAIN;
            case 1 :
                return Font.BOLD;
            case 2 :
                return Font.ITALIC;
            case 3 :
                return Font.BOLD + Font.ITALIC;
            default :
                throw new FontChooserException(
                    "No font style is currently selected");
        }
    }
    
    
    /**
     * Sets the currently selected style.
     * 
     * @param style Style to select.
     * @throws IllegalArgumentException thrown if style is not one of
     *         Font.PLAIN, Font.BOLD, Font.ITALIC, or Font.BOLD + Font.ITALIC.
     */
    public void setSelectedStyle(int style) throws IllegalArgumentException
    {
        switch (style)
        {
            case Font.PLAIN :
                this.setSelectedIndex(0);
                break;
            case Font.BOLD :
                this.setSelectedIndex(1);
                break;
            case Font.ITALIC :
                this.setSelectedIndex(2);
                break;
            case Font.BOLD + Font.ITALIC :
                this.setSelectedIndex(3);
                break;
            default :
                throw new IllegalArgumentException(
                    "int style must come from java.awt.Font");
        }
    }
    
    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------
    
    /**
     * Validates style display names.
     * 
     * @param styleDisplayNames Style display names.
     * @return String array.
     */
    protected static String[] validateStyleDisplayNames(
        String[] styleDisplayNames)
    {
        if (styleDisplayNames == null)
            throw new IllegalArgumentException(
                "String[] styleDisplayNames may not be null");
    
        if (styleDisplayNames.length != 4)
            throw new IllegalArgumentException(
                "String[] styleDisplayNames must have a length of 4");

        for (int i = 0; i < styleDisplayNames.length; i++)
        {
            if (styleDisplayNames[i] == null)
                throw new IllegalArgumentException(
                    "No member of String[] styleDisplayNames may be null");
        }
        
        return styleDisplayNames;
    }
}