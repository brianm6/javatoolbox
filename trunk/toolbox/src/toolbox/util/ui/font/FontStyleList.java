package toolbox.util.ui.font;

import java.awt.Font;

import javax.swing.JList;

/**
 * Represents a list of the four font styles: plain, bold, italic, and 
 * bold italic
 */
public class FontStyleList extends JList
{
    
    /**
     * Construct a new FontStyleList, using the supplied values for style
     * display names
     * 
     * @param   styleDisplayNames   
     * 
     *      Must contain exactly four members. The members of this array 
     *      represent the following styles, in order: Font.PLAIN, 
     *      Font.BOLD, Font.ITALIC, and Font.BOLD+Font.ITALIC
     * 
     * @throws IllegalArgumentException if styleDisplayNames does not
     *         contain exactly four String values
     */
    public FontStyleList(String[] styleDisplayNames)
    {
        super(validateStyleDisplayNames(styleDisplayNames));
    }
    
    
    /**
     * Validates style display names
     * 
     * @param  styleDisplayNames  Style display names
     * @return String array
     */
    private static String[] validateStyleDisplayNames(String[] styleDisplayNames)
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
    
    
    /**
     * @return currently selected font style
     * @throws FontSelectionException thrown if no font style is 
     *         currently selected
     */
    public int getSelectedStyle() throws FontSelectionException
    {
        switch (this.getSelectedIndex())
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
                throw new FontSelectionException(
                    "No font style is currently selected");
        }
    }
    
    
    /**
     * Change the currently selected style in this FontStyleList
     * 
     * @param   style   New selected style for this FontStyleList
     * @throws  IllegalArgumentException thrown if style is not one of 
     *          Font.PLAIN, Font.BOLD, Font.ITALIC, or Font.BOLD+Font.ITALIC
     */
    public void setSelectedStyle(int style)
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
}
