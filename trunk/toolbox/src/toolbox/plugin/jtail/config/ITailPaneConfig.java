package toolbox.jtail.config;

import java.awt.Font;

import toolbox.workspace.IPreferenced;

/**
 * ITailPaneConfig describes the interface necessary to configure a TailPane.
 */
public interface ITailPaneConfig extends IPreferenced
{
    /** 
     * Default antialis is false. 
     */
    public static final boolean DEFAULT_ANTIALIAS = false;    
    
    /** 
     * Default autoscroll is true.
     */
    public static final boolean DEFAULT_AUTOSCROLL = true;
    
    /** 
     * Default show line number is false. 
     */
    public static final boolean DEFAULT_LINENUMBERS = false;
    
    /** 
     * Default autostart is true.
     */
    public static final boolean DEFAULT_AUTOSTART = true;
    
    /** 
     * Default regular expression is empty string. 
     */
    public static final String  DEFAULT_REGEX = "";
    
    /**
     * Default match case on regular expression is false.
     */
    public static final boolean DEFAULT_REGEX_MATCHCASE = false;
    
    /** 
     * Default cut expression is empty string. 
     */
    public static final String  DEFAULT_CUT_EXPRESSION = "";

    
    /**
     * Returns the autoScroll nature of the text area.
     * 
     * @return boolean
     */
    boolean isAutoScroll();


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
     * Sets the autoScroll flag.
     * 
     * @param autoScroll The autoScroll to set
     */
    void setAutoScroll(boolean autoScroll);


    /**
     * Sets the names of the file being tailed.
     * 
     * @param filenames Filenames 
     */
    void setFilenames(String[] filenames);


    /**
     * Sets the showLineNumbers flag.
     * 
     * @param showLineNumbers The showLineNumbers to set
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
     * @param regex The filter to set
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
     * @param cutExpression Cut expression
     */
    void setCutExpression(String cutExpression);
    
    
    /**
     * Accessor for the antialias flag.
     * 
     * @return True if antialias is on, false otherwise
     */
    boolean isAntiAliased();
    
    
    /**
     * Mutator for the antialias flag.
     * 
     * @param b True to turn antialias on, false otherwise
     */
    void setAntiAlias(boolean b);
    
    
    /**
     * Mutator for the autostart flag.
     * 
     * @param autoStart True to turn autostart on, false otherwise
     */
    void setAutoStart(boolean autoStart);
    
    
    /**
     * Accessor for the autostart flag.
     * 
     * @return Autostart flag
     */
    boolean isAutoStart();
}