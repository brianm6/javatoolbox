package toolbox.plugin.jtail.config;

import java.awt.Font;

import toolbox.workspace.IPreferenced;

/**
 * ITailPaneConfig describes the interface necessary to configure a TailPane.
 */
public interface ITailPaneConfig extends IPreferenced
{
    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------
    
    /** 
     * Default antialias is true. 
     */
    boolean DEFAULT_ANTIALIAS = true;    
    
    /** 
     * Default autotail is true.
     */
    boolean DEFAULT_AUTOTAIL = true;
    
    /** 
     * Default show line number is false. 
     */
    boolean DEFAULT_LINENUMBERS = false;
    
    /** 
     * Default autostart is true.
     */
    boolean DEFAULT_AUTOSTART = true;
    
    /** 
     * Default regular expression is empty string. 
     */
    String  DEFAULT_REGEX = "";
    
    /**
     * Default match case on regular expression is false.
     */
    boolean DEFAULT_REGEX_MATCHCASE = false;
    
    /** 
     * Default cut expression is empty string. 
     */
    String  DEFAULT_CUT_EXPRESSION = "";

    //--------------------------------------------------------------------------
    // Interface
    //--------------------------------------------------------------------------
    
    /**
     * Returns true if autoTail is enabled, false otherwise.
     * 
     * @return boolean
     */
    boolean isAutoTail();


    /**
     * Returns an array of filenames, each of which is being tailed.
     * 
     * @return String[]
     */
    String[] getFilenames();


    /**
     * Returns the flag that toggles the display of line numbers.
     * 
     * @return boolean
     */
    boolean isShowLineNumbers();


    /**
     * Sets the autoTail flag.
     * 
     * @param autoTail The autoTail to set.
     */
    void setAutoTail(boolean autoTail);


    /**
     * Sets the names of the file being tailed.
     * 
     * @param filenames Filenames.
     */
    void setFilenames(String[] filenames);


    /**
     * Sets the showLineNumbers flag.
     * 
     * @param showLineNumbers The showLineNumbers to set.
     */
    void setShowLineNumbers(boolean showLineNumbers);

  
    /**
     * Returns the font used in the text area.
     * 
     * @return Font
     */
    Font getFont();


    /**
     * Sets the font used in the text area.
     * 
     * @param font The font to set
     */
    void setFont(Font font);
    
    
    /**
     * Returns the filter (regular expression) used to include/exchage lines.
     * 
     * @return String
     */
    String getRegularExpression();


    /**
     * Sets the filter (regular expression) used to include/exclude lines.
     * 
     * @param regex The filter to set.
     */
    void setRegularExpression(String regex);


    /**
     * Returns the cut expression used to exclude columns. 
     * 
     * @return String
     */
    String getCutExpression();


    /**
     * Sets the cut expression used to exclude columns.
     * 
     * @param cutExpression Cut expression.
     */
    void setCutExpression(String cutExpression);
    
    
    /**
     * Accessor for the antialias flag.
     * 
     * @return True if antialias is on, false otherwise.
     */
    boolean isAntiAliased();
    
    
    /**
     * Mutator for the antialias flag.
     * 
     * @param b True to turn antialias on, false otherwise.
     */
    void setAntiAliased(boolean b);
    
    
    /**
     * Mutator for the autostart flag.
     * 
     * @param autoStart True to turn autostart on, false otherwise.
     */
    void setAutoStart(boolean autoStart);
    
    
    /**
     * Accessor for the autostart flag.
     * 
     * @return Autostart flag.
     */
    boolean isAutoStart();
}