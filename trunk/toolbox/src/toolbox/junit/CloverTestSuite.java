package toolbox.junit;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import junitx.util.BasicTestFilter;
import junitx.util.DirectorySuiteBuilder;
import junitx.util.TestFilter;

import org.apache.log4j.Logger;

import toolbox.util.ArrayUtil;

/**
 * CloverTests.
 */
public class CloverTestSuite extends TestSuite
{
    private static final Logger logger_ = Logger.getLogger(CloverTestSuite.class);
 
    //--------------------------------------------------------------------------
    // Main 
    //--------------------------------------------------------------------------
    
    public static void main(String[] args) throws Exception
    {
        TestRunner.run(suite());
        
    }
    
    //--------------------------------------------------------------------------
    // Public 
    //--------------------------------------------------------------------------
    
    public static Test suite() throws Exception
    {
        System.out.println("suite() called");
        TestFilter cloverFilter = new CloverTestFilter();
        DirectorySuiteBuilder builder = new DirectorySuiteBuilder(cloverFilter);
        //return builder.suite("c:\\workspaces\\workspace-toolbox\\toolbox\\classes");
        Test test = builder.suite("classes");
        
        System.out.println("Total tests: " + test.countTestCases());
        
        return test;
    }

    //--------------------------------------------------------------------------
    // CloverTestFilter 
    //--------------------------------------------------------------------------
    
    static class CloverTestFilter extends BasicTestFilter
    {
        private ClassPool classPool_;
        private CtClass uiTestCase_;
        private CtClass standAloneTestCase_;
        
        /**
         * Creates a CloverTestFilter. 
         * 
         * @throws NotFoundException if test case class not found.
         */
        public CloverTestFilter() throws NotFoundException
        {
            classPool_ = ClassPool.getDefault();
            uiTestCase_ = classPool_.get(UITestCase.class.getName());
            
            standAloneTestCase_ = 
                classPool_.get(StandaloneTestCase.class.getName());
        }
        
        //----------------------------------------------------------------------
        // Overrides BasicTestFilter 
        //----------------------------------------------------------------------
        
        /**
         * @see junitx.util.TestFilter#include(java.lang.Class)
         */
        public boolean include(Class arg0)
        {
            boolean b = super.include(arg0);
            
            
            try
            {
                CtClass clazz = classPool_.get(arg0.getName());
                
                if (clazz.subclassOf(uiTestCase_))
                {    
                    b = false;
                    System.out.println("Rejecting UITestCase!!! " + arg0.getName());
                }
                else if (ArrayUtil.contains(clazz.getInterfaces(), standAloneTestCase_))
                {
                    b = false;
                    System.out.println("Rejecting StandAloneTestCase!!! " + arg0.getName());
                }
            }
            catch (NotFoundException e)
            {
                logger_.error(e);
            }
            finally
            {
                if (b)
                    System.out.println("in class: " + arg0);
            }
            
            return b;
        }

        /**
         * @see junitx.util.TestFilter#include(java.lang.String)
         */
        public boolean include(String arg0)
        {
            boolean b = super.include(arg0);
            
            if (b)
                System.out.println("in string: " + arg0);
            
            return b;
        }
    }
    
}
