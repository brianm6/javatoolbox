package toolbox.junit;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

import junitx.util.BasicTestFilter;

import org.apache.log4j.Logger;

import toolbox.util.ArrayUtil;

/**
 * Filter that identifies only those tests suitable for execution under Clover.
 * Uses the javaassist library to do additional class inheritance and interface
 * implementation checks.
 */
public class CloverTestFilter extends BasicTestFilter
{
    private static final Logger logger_ = 
        Logger.getLogger(CloverTestFilter.class);
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Javassist class pool.
     */
    private ClassPool classPool_;
    
    /**
     * Javassist class used to filter out test cases that extend UITestCase.
     */
    private CtClass uiTestCase_;
    
    /**
     * Javassist class used to filter out test cases that implement 
     * StandAloneTestCase.
     */
    private CtClass standAloneTestCase_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a CloverTestFilter. 
     * 
     * @throws NotFoundException if test case class not found.
     */
    public CloverTestFilter() throws NotFoundException
    {
        classPool_ = ClassPool.getDefault();
        uiTestCase_ = classPool_.get(UITestCase.class.getName());
        standAloneTestCase_ =classPool_.get(StandaloneTestCase.class.getName());
    }
    
    //--------------------------------------------------------------------------
    // Overrides BasicTestFilter 
    //--------------------------------------------------------------------------
    
    /**
     * Rejects classes that exend UITestCase.
     * Rejects classes that implement StandAloneTestCase.
     * 
     * @see junitx.util.TestFilter#include(java.lang.Class)
     */
    public boolean include(Class clazz)
    {
        boolean b = super.include(clazz);
        
        try
        {
            CtClass c = classPool_.get(clazz.getName());
            
            if (c.subclassOf(uiTestCase_))
            {    
                b = false;
                logger_.debug("Rejecting UITestCase: " + clazz.getName());
            }
            else if (ArrayUtil.contains(c.getInterfaces(), standAloneTestCase_))
            {
                b = false;
                logger_.debug("Rejecting StandAloneTestCase: "+clazz.getName());
            }
        }
        catch (NotFoundException e)
        {
            logger_.error(e + ":" + clazz);
        }
        finally
        {
            if (b)
                logger_.debug("Accepted class: " + clazz);
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
            System.out.println("Accepted class: " + arg0);
        
        return b;
    }
}