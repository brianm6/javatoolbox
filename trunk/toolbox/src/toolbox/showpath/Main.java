package toolbox.showpath;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Shows system path
 */
public class Main
{
    /** 
     * List of paths remembered so dupes can be flagged 
     */
    private List checkList_;

    /**
     * Entrypoint for showclasspath
     * 
     * @param  args  None recognized 
     */
    public static void main(String[] args)
    {
        new Main();
    }
    
    //--------------------------------------------------------------------------
    // Constructors 
    //--------------------------------------------------------------------------
    
    /**
     * Default constructor
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
            String path = (String)st.nextToken();
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
     * Checks if a path is a duplicate by checking the existing list
     * 
     * @param   dupe   Path to check for duplicate
     * @return  True if duplicate, flase otherwise
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