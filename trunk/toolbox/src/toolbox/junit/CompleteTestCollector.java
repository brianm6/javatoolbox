package toolbox.junit;

import java.util.Enumeration;
import java.util.Vector;

import junit.runner.TestCollector;

/**
 * CompleteTestCollector serves as a compound test collector that
 * merges the behavior of the JUnit provided FileTestCollector and
 * the new JarTestCollector. The resulting collector will find
 * all JUnit test cases in a given classpath regardless of the location of
 * the class file (directory or archive). 
 * 
 * To activate this test collector, create the following files in your 
 * $HOME directory:
 * 
 * <pre>
 * 
 * junit.properties
 * ---Begin---
 * TestCollectorClass=toolbox.util.junit.CompleteTestCollector
 * ---End---
 * 
 * </pre>
 */
public class CompleteTestCollector implements TestCollector
{
    /** 
     * Introspects jars for unit tests.
     */
    private TestCollector jarCollector_;
    
    /** 
     * Scans directories for unit tests. 
     */
    private TestCollector dirCollector_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a CompleteTestCollector.
     */
    public CompleteTestCollector()
    {
        jarCollector_ = new JarTestCollector();
        dirCollector_ = new FileTestCollector();
    }

    //--------------------------------------------------------------------------
    // TestCollector Interface
    //--------------------------------------------------------------------------
    
    /**
     * Merges the results of the JarTestCollector and the FileTestCollector.
     * 
     * @return Enumeration of class names
     * @see TestCollector#collectTests()
     */
    public Enumeration collectTests()
    {
        Vector results = new Vector();

        // Merge the two result sets        
        for (Enumeration e = jarCollector_.collectTests(); e.hasMoreElements();)
            results.add(e.nextElement());    

        for (Enumeration e = dirCollector_.collectTests(); e.hasMoreElements();)
            results.add(e.nextElement());    
        
        return results.elements();
    }
}