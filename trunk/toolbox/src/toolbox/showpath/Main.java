package toolbox.showpath;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Print out the contents of the system path environemnt variable.
 */
public class Main
{
    /** 
     * List of paths remembered so dupes can be flagged. 
     */
    private List checkList_;

    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    /**
     * Entrypoint for showclasspath.
     * 
     * @param args None recognized 
     */
    public static void main(String[] args)
    {
        new Main();
    }
    
    //--------------------------------------------------------------------------
    // Constructors 
    //--------------------------------------------------------------------------
    
    /**
     * Creates a Main.
     */
    public Main()
    {
        checkList_ = new ArrayList();
        
        StringTokenizer st = new StringTokenizer(
            System.getProperty("java.library.path"), 
                System.getProperty("path.separator"));

        // Find longest for formatting
        while (st.hasMoreElements())
        {
            String path = st.nextToken();
            System.out.print(path);

            if (isDupe(path))
                System.out.print("\t** DUPLICATE **");
            else
                checkList_.add(path);

            System.out.println();
        }
    }
    
    //--------------------------------------------------------------------------
    // Private 
    //--------------------------------------------------------------------------
    
    /**
     * Checks if a path is a duplicate by checking the existing list. The
     * comparison is case-insensetive.
     * 
     * @param dupe Path to check for duplicate
     * @return True if duplicate, flase otherwise
     */
    private boolean isDupe(String dupe)
    {
        for (Iterator i = checkList_.iterator(); i.hasNext();)
        {
            String check = (String) i.next();

            if (dupe.toUpperCase().equals(check.toUpperCase()))
                return true;
        }

        return false;
    }
}