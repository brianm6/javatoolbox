package toolbox.junit;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.ArrayUtil;
import toolbox.util.ClassUtil;

/**
 *  Runs unit tests by java package
 */
public class PackageTester
{
    /** Logger */
    public static final Logger logger_ = 
        Logger.getLogger(PackageTester.class);
    
    private List packages_ = new ArrayList();

    /**
     * Runs tests cases in one or more given package names
     * 
     * @param  args  List of packages to test
     */            
    public static void main(String args[])
    {
        PackageTester tester = new PackageTester();
                
        for (int i=0; i<args.length; i++)
            tester.addPackage(args[i]);

        if (tester.getPackageCount() > 0)        
            tester.run();
        else
            System.out.println("No packages to test.");
    }

    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------

    /**
     * Default constructor
     */
    public PackageTester() 
    {
    }
    
    /**
     * Constructor for PackageTester.
     * 
     * @param  packageName  Package name
     */
    public PackageTester(String packageName)
    {
        addPackage(packageName);
    }


    //--------------------------------------------------------------------------
    //  Public
    //--------------------------------------------------------------------------

    /**
     * Adds a package to the list of packages to be tested
     * 
     * @param  packageName  Package name to add
     */
    public void addPackage(String packageName)
    {
        packages_.add(packageName);
    }

    /**
     * Returns the number of packages to be tested
     * 
     * @return Number of packages
     */
    public int getPackageCount()
    {
        return packages_.size();
    }   

    /**
     * Runs test cases in all the packages identified 
     */
    public void run()
    {
        TestSuite testSuite = new TestSuite();
                    
        for (Iterator p = packages_.iterator(); p.hasNext(); )
        {
            String packageName = (String)p.next();
            String[] classes = ClassUtil.getClassesInPackage(packageName);
            
            logger_.debug("\n" + ArrayUtil.toString(classes, true));
            
            for (int i=0; i<classes.length; i++)
            {
                try
                {
                    if (classes[i].endsWith("Test"))
                    {
                        Class clazz = Class.forName(classes[i]);
                        testSuite.addTestSuite(clazz);
                    }
                }
                catch (ClassNotFoundException cnfe)
                {
                    logger_.error("run", cnfe);                
                }
            }
        }
                    
        TestRunner.run(testSuite);
    }
}