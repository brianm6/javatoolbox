package toolbox.jtail.config;

import java.awt.Font;

/**
 * ITailPaneConfig describes the interface necessary to configure a TailPane
 */
public interface ITailPaneConfig
{
    public static final boolean DEFAULT_ANTIALIAS       = false;    
    public static final boolean DEFAULT_AUTOSCROLL      = true;
    public static final boolean DEFAULT_LINENUMBERS     = false;
    public static final String  DEFAULT_REGEX           = "";
    public static final boolean DEFAULT_REGEX_MATCHCASE = false;
    public static final String  DEFAULT_CUT_EXPRESSION  = "";

    /**
     * Returns the autoScroll nature of the text area.
     * 
     * @return boolean
     */
    public boolean isAutoScroll();


    /**
     * Returns the filename that is being tailed.
     * 
     * @return String
     */
    public String getFilename();


    /**
     * Returns the flag that toggles the display of line numbers
     * 
     * @return boolean
     */
    public boolean isShowLineNumbers();


    /**
     * Sets the autoScroll flag
     * 
     * @param autoScroll The autoScroll to set
     */
    public void setAutoScroll(boolean autoScroll);


    /**
     * Sets the name of the file being tailed
     * 
     * @param filename The filename to set
     */
    public void setFilename(String filename);


    /**
     * Sets the showLineNumbers flag
     * 
     * @param showLineNumbers The showLineNumbers to set
     */
    public void setShowLineNumbers(boolean showLineNumbers);

  
    /**
     * Returns the font used in the text area
     * 
     * @return Font
     */
    public Font getFont();


    /**
     * Sets the font used in the text area
     * 
     * @param font The font to set
     */
    public void setFont(Font font);
    
    
    /**
     * Returns the filter (regular expression) used to include/exchage lines
     * 
     * @return String
     */
    public String getRegularExpression();


    /**
     * Sets the filter (regular expression) used to include/exclude lines
     * 
     * @param regex The filter to set
     */
    public void setRegularExpression(String regex);


    /**
     * Returns the cut expression used to exclude columns 
     * 
     * @return String
     */
    public String getCutExpression();


    /**
     * Sets the cut expression used to exclude columns
     * 
     * @param  cutExpression  Cut expression
     */
    public void setCutExpression(String cutExpression);

    
    
    /**
     * Accessor for the antialias flag
     * 
     * @return  True if antialias is on, false otherwise
     */
    public boolean isAntiAlias();
    
    
    /**
     * Mutator for the antialias flag
     * 
     * @param  b  True to turn antialias on, false otherwise
     */
    public void setAntiAlias(boolean b);
}
