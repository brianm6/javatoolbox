package toolbox.plugin.jtail.config;

import java.awt.Font;

import toolbox.workspace.IPreferenced;

/**
 * ITailViewConfig describes the interface necessary to configure a TailPane.
 */
public interface ITailViewConfig extends IPreferenced
{
    //--------------------------------------------------------------------------
    // Defaults Constants
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

/*
================================================================================
CVS History as ITailViewConfig.java
================================================================================
1.18 2004/08/11 Updated config methods to javabean naming conventions
1.17 2004/01/31 Moved JTail plugin from toolbox.jtail to toolbox.plugin.jtail.
1.16 2004/01/25 Javadoc updates
1.15 2004/01/07 Checkstyle updates
1.14 2003/11/23 Removed redundant public modifier
1.13 2003/10/06 Workspace moved toolbox.workspace
1.12 2003/09/18 Fixed isAntiAlias() -> isAntiAliased() javadoc
1.11 2003/09/13 Javadoc updates
1.10 2003/07/09 Added ability to aggregate multiple tails within a single 
                TailPane
1.9  2003/06/21 Changed preference persistence strategy from a properties based
                file to an XML document.
1.8  2003/03/24 Removed tabs
1.7  2003/03/15 Tailpane now remembers state of tail (start/stopped) between 
                sessions.
1.6  2003/01/06 Formatting updates
1.5  2002/12/04 Checkstyle updates
1.4  2002/11/11 Renamed filter to regular expression and added cut expression
1.3  2002/08/25 Added antialias
1.2  2002/08/24 Javadoc updates
1.1  2002/08/14 Initial version
*/