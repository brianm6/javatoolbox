package toolbox.util;

public interface Stringz
{
    /** Line break */
    public static final String BR = StringUtil.repeat("=", 80);
    
    /** New line */
    public static final String NL = System.getProperty("line.separator");
    
    /** Break with newline */
    public static final String BRNL = BR + NL;
}
