package toolbox.util;

import java.io.File;

/**
 * Platform specific utility methods
 */
public class Platform
{
    private static final int UNIX = 0x31337;
    private static final int WINDOWS_9X = 0x640;
    private static final int WINDOWS_NT = 0x666;
    private static final int OS2 = 0xDEAD;
    private static final int MAC_OS_X = 0xABC;
    private static final int UNKNOWN = 0xBAD;

    private static int os;
    private static boolean java14;


    static
    {
        String osName = System.getProperty("os.name");
        
        if (osName.indexOf("Windows 9") != -1   || 
            osName.indexOf("Windows ME") != -1)
        {
            os = WINDOWS_9X;
        }
        else if (osName.indexOf("Windows") != -1)
        {
            os = WINDOWS_NT;
        }
        else if (osName.indexOf("OS/2") != -1)
        {
            os = OS2;
        }
        else if (File.separatorChar == '/' && new File("/dev").isDirectory())
        {
            if(osName.indexOf("Mac OS X") != -1)
                os = MAC_OS_X;
            else
                os = UNIX;
        }
        else
        {
            os = UNKNOWN;
        }

        if(System.getProperty("java.version").compareTo("1.4") >= 0)
            java14 = true;
    } 
    
    
    /**
     * @return  True if we're running Windows 95/98/ME/NT/2000/XP, or OS/2.
     */
    public static final boolean isDOSDerived()
    {
        return isWindows() || isOS2();
    }


    /**
     * @return  True if we're running Windows 95/98/ME/NT/2000/XP.
     */
    public static final boolean isWindows()
    {
        return os == WINDOWS_9X || os == WINDOWS_NT;
    } 


    /**
     * @return  True if we're running Windows 95/98/ME.
     */
    public static final boolean isWindows9x()
    {
        return os == WINDOWS_9X;
    } 


    /**
     * @return  True if we're running Windows NT/2000/XP.
     */
    public static final boolean isWindowsNT()
    {
        return os == WINDOWS_NT;
    } 


    /**
     * @return  True if we're running OS/2.
     */
    public static final boolean isOS2()
    {
        return os == OS2;
    } 


    /**
     * @return  True if we're running Unix (this includes MacOS X).
     */
    public static final boolean isUnix()
    {
        return os == UNIX || os == MAC_OS_X;
    } 


    /**
     * @return  True if we're running MacOS X.
     */
    public static final boolean isMacOS()
    {
        return os == MAC_OS_X;
    } 


    /**
     * @return  True if Java 2 version 1.4 is in use.
     */
    public static final boolean hasJava14()
    {
        return java14;
    } 
}