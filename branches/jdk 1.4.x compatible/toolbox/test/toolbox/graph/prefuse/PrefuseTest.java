package toolbox.graph.prefuse;

import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.graph.AbstractGraphTest;
import toolbox.graph.GraphLib;
import toolbox.graph.GraphLibFactory;

/**
 * PrefuseTest is responsible for testing classes in package 
 * toolbox.graph.prefuse. 
 */
public class PrefuseTest extends AbstractGraphTest
{
    private static final Logger logger_ = Logger.getLogger(PrefuseTest.class);
   
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    public static void main(String[] args)
    {
        TestRunner.run(PrefuseTest.class);
    }

    //--------------------------------------------------------------------------
    // Abstract Methods
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.graph.AbstractGraphTest#getGraphLib()
     */
    public GraphLib getGraphLib()
    {
        return GraphLibFactory.create(GraphLibFactory.TYPE_PREFUSE);
    }
}