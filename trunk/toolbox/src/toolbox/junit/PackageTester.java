package toolbox.junit;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestSuite;
import junit.textui.TestRunner;

import toolbox.util.ArrayUtil;
import toolbox.util.ClassUtil;

/**
 *  Runs unit tests by java package
 */
public class PackageTester
{
    private List packages_ = new ArrayList();

    /**
     * Runs tests cases in one or more given package names
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
     */
    public PackageTester(String packageName)
    {
        addPackage(packageName);
    }


    //--------------------------------------------------------------------------
    //  Implementation
    //--------------------------------------------------------------------------

    /**
     * Adds a package to the list of packages to be tested
     */
    public void addPackage(String packageName)
    {
        packages_.add(packageName);
    }

    /**
     * Returns the number of packages to be tested
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
            
            System.out.println(ArrayUtil.toString(classes, true));
            
            for (int i=0; i<classes.length; i++)
            {
                try
                {
                    if (classes[i].endsWith("Test"))
                    {
                        Class clazz = Class.forName(packageName + "." + classes[i]);
                        testSuite.addTestSuite(clazz);
                    }
                }
                catch (ClassNotFoundException cnfe)
                {
                    System.out.println(cnfe);                
                }
            }
        }
                    
        TestRunner.run(testSuite);
    }
}
