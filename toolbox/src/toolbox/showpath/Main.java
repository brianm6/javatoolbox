package toolbox.showpath;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import toolbox.util.Platform;
import toolbox.util.collections.CaseInsensetiveSet;

/**
 * Print out the contents of the system path environment variable.
 */
public class Main {
    
    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------

    /**
     * Set of paths to recognize dupes.
     */
    private Set pathSet_;

    // -------------------------------------------------------------------------
    // Main
    // -------------------------------------------------------------------------

    /**
     * Entrypoint for showclasspath.
     * 
     * @param args None recognized.
     */
    public static void main(String[] args) {
        new Main();
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Creates a Main.
     */
    public Main() {
        pathSet_ = new HashSet();

        // Make sure paths are treated as case-insensitive when not on a unix
        // platform.
        if (!Platform.isUnix())
            pathSet_ = new CaseInsensetiveSet(pathSet_);

        StringTokenizer st = 
            new StringTokenizer(
                System.getProperty("java.library.path"), 
                System.getProperty("path.separator"));

        while (st.hasMoreElements()) {
            String path = st.nextToken();
            System.out.print(path);

            if (pathSet_.contains(path))
                System.out.print("\t** DUPLICATE **");
            else
                pathSet_.add(path);

            System.out.println();
        }
    }
}