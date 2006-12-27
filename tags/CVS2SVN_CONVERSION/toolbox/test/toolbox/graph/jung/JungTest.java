package toolbox.graph.jung;

import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.graph.AbstractGraphTest;
import toolbox.graph.GraphLib;
import toolbox.graph.GraphLibFactory;

/**
 * JungTest is responsible for testing classes in package toolbox.graph.jung. 
 */
public class JungTest extends AbstractGraphTest
{
    private static final Logger logger_ = Logger.getLogger(JungTest.class);
   
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    public static void main(String[] args)
    {
        TestRunner.run(JungTest.class);
    }

    //--------------------------------------------------------------------------
    // Abstract Methods
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.graph.AbstractGraphTest#getGraphLib()
     */
    public GraphLib getGraphLib()
    {
        return GraphLibFactory.create(GraphLibFactory.TYPE_JUNG);
    }
    
    //--------------------------------------------------------------------------
    // Overrides AbstractGraphTest
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.graph.AbstractGraphTest#testLayouts()
     */
    public void testLayouts() throws Exception
    {
        super.testLayouts();
    }
    
}