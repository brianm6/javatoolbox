package toolbox.junit;

import java.util.Enumeration;
import java.util.Vector;

import junit.runner.SimpleTestCollector;
import junit.runner.TestCollector;

/**
 * CompleteTestCollector serves as a compound test collector that
 * merges the behavior of the JUnit provided SimpleTestCollector and
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
 * TestCollectorClass=com.swa.turbo.util.junit.CompleteTestCollector
 * ---End---
 * 
 * </pre>
 */
public class CompleteTestCollector implements TestCollector
{
    /** Introspects jars for unit tests **/
    TestCollector jarCollector;
    
    /** Scans directories for unit tests **/
    TestCollector dirCollector;
    
    /**
     * Constructor for CompleteTestCollector.
     */
    public CompleteTestCollector()
    {
        jarCollector = new JarTestCollector();
        dirCollector = new SimpleTestCollector();
    }

    /**
     * Merge the results of the JarTestCollector and the SimpleTestCollector
     * 
     * @return  Enumeration of class names
     * @see     TestCollector#collectTests()
     */
    public Enumeration collectTests()
    {
        Vector results = new Vector();

        // Merge the two result sets        
        for(Enumeration e = jarCollector.collectTests();e.hasMoreElements();)
            results.add(e.nextElement());    

        for(Enumeration e = dirCollector.collectTests();e.hasMoreElements();)
            results.add(e.nextElement());    
        
        return results.elements();
    }
}
