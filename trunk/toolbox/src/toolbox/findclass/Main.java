package toolbox.findclass;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Category;
import toolbox.util.ArrayUtil;

/**
 * Utility that finds all occurences of a given class in the 
 * CLASSPATH, current directory, and archives (recursively)
 */
public class Main implements IFindClassListener
{ 
    /** Logger **/
    private static final Category logger_ = Category.getInstance(Main.class);
    
    /** command line flag to specify case sensetivity **/
    private static final String caseSensetiveFlag_ = "-cs";
    
    /**
     * FindClass entry point
     * 
     * @param   args   Args
     */
    public static void main(String args[])
    {
        /* init log4j */
        BasicConfigurator.configure();
        
        FindClass finder = new FindClass();
        finder.addFindClassListener(new Main());
        String classToFind;
        boolean ignoreCase;

        /* handle args */        
        if (args.length == 1) 
        {
            classToFind = args[0];
            ignoreCase  = true;
            
            finder.findClass(classToFind, ignoreCase);
        }
        else if (args.length == 2)
        { 
            switch (ArrayUtil.indexOf(args, caseSensetiveFlag_))
            {
                case -1: printUsage(); 
                         break;
                
                case  0: classToFind = args[1]; 
                         ignoreCase = false;
                         finder.findClass(classToFind, ignoreCase);
                         break;                
                
                case  1: classToFind = args[0]; 
                         ignoreCase = false;
                         finder.findClass(classToFind, ignoreCase);
                         break; 
            }
        }
        else 
            printUsage();
    }

    /**
     * Prints program usage
     */
    private static void printUsage()
    {
        System.out.println("FindClass searches for all occurrences of a class");
        System.out.println("in your classpath and archives visible from the");
        System.out.println("current directory.");
        System.out.println();
        
        System.out.println("Usage  : java toolbox.findclass.Main -cs " +
                           "<regular expression>");
                           
        System.out.println("Options: -cs => Case sensetive search");
    }
 
    /**
     * Implemenation of IFindClassListener
     * 
     * @param  searchResult  Results of class that was found.
     */   
    public void classFound(FindClassResult searchResult)
    {
        System.out.println(
            searchResult.getClassLocation() + " => " + 
            searchResult.getClassFQN());   
    }
}
