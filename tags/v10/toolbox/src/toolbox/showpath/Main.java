package toolbox.showpath;

import java.util.*;

/**
 * Shows system path
 */
public class Main { 

    static Vector checkList = new Vector();

    public static boolean isDupe(String dupe) {

        for(Enumeration e=checkList.elements(); e.hasMoreElements();) {
            String check = (String)e.nextElement();
            if(dupe.toUpperCase().equals(check.toUpperCase()))
                return true;
        }

        return false;
    }
    
    public static void main(String args[]) { 

        StringTokenizer st = 
        	new StringTokenizer(
        		System.getProperty("java.library.path"), 
        		System.getProperty("path.separator"));

        // find longest for formatting
        while (st.hasMoreElements()) { 
            
            String path = (String)st.nextToken();
            System.out.print(path);

            if( isDupe(path) )
                System.out.print("\t** DUPLICATE **");
            else
                checkList.addElement(path);

            System.out.println();
        }
    }
}
