package toolbox.junit;

import junit.runner.ClassPathTestCollector;

/**
 * An implementation of a TestCollector that considers a class to be a test 
 * class when it ends with the pattern "Test" in its name.
 */
public class FileTestCollector extends ClassPathTestCollector 
{
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a FileTestCollector.
     */
    public FileTestCollector() 
    {
    }
    
    //--------------------------------------------------------------------------
    // Overrides ClassPathTestCollector
    //--------------------------------------------------------------------------
    
    /**
     * @see junit.runner.ClassPathTestCollector#isTestClass(java.lang.String)
     */
    protected boolean isTestClass(String classFileName) 
    {
        return classFileName.endsWith("Test.class") && 
               classFileName.indexOf('$') < 0;
    }
}