package toolbox.showpath;

import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Shows system path
 */
public class Main
{
    /** 
     * List of paths 
     */
    private static Vector checkList_ = new Vector();

    /**
     * Entrypoint for showclasspath
     * 
     * @param  args  none recognized 
     */
    public static void main(String[] args)
    {
        StringTokenizer st = new StringTokenizer(
                                     System.getProperty("java.library.path"), 
                                     System.getProperty("path.separator"));

        // find longest for formatting
        while (st.hasMoreElements())
        {
            String path = (String)st.nextToken();
            System.out.print(path);

            if (isDupe(path))
                System.out.print("\t** DUPLICATE **");
            else
                checkList_.addElement(path);

            System.out.println();
        }
    }


    /**
     * Checks if a path is a duplicate by checking the existing list
     * 
     * @param  dupe  Path to check for duplicate
     * @return True if duplicate, flase otherwise
     */
    public static boolean isDupe(String dupe)
    {
        for (Enumeration e = checkList_.elements(); e.hasMoreElements();)
        {
            String check = (String)e.nextElement();

            if (dupe.toUpperCase().equals(check.toUpperCase()))
                return true;
        }

        return false;
    }

}