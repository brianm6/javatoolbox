package toolbox.graph.jung;

import java.util.ArrayList;
import java.util.List;

import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.graph.Edge;
import toolbox.graph.Graph;
import toolbox.graph.GraphFactory;
import toolbox.graph.GraphView;
import toolbox.graph.Vertex;
import toolbox.junit.testcase.UITestCase;
import toolbox.util.RandomUtil;

/**
 * JungTest is responsible for _____.
 */
public class JungTest extends UITestCase
{
    private static final Logger logger_ = Logger.getLogger(JungTest.class);
    
    public static void main(String[] args)
    {
        TestRunner.run(JungTest.class);
    }

    public void testJung1() throws Exception
    {
        logger_.info("Running testJung1...");
        
        GraphFactory factory = new JungGraphFactory();
        Graph graph = factory.createGraph();
        
        Vertex sun = factory.createVertex(graph, "sun");
        Vertex moon = factory.createVertex(graph, "moon");
        Edge edge = factory.createEdge(sun, moon);
        GraphView view = factory.createView(graph);
        
        launchInDialog(view.getComponent()); //, SCREEN_TWO_THIRDS);
    }
    
    
    public void testJung2() throws Exception
    {
        logger_.info("Running testJung2...");
        
        GraphFactory factory = new JungGraphFactory();
        Graph graph = factory.createGraph();
        
        List nodes = new ArrayList();
        
        for (int i = 0; i < 50; i++) 
             nodes.add(factory.createVertex(graph, i + 1 + ""));
        
        for (int i = 0; i < 50; i++)
        {
            Vertex from = (Vertex) nodes.get(i);
            Vertex to = (Vertex) RandomUtil.nextElement(nodes);
            Edge edge = factory.createEdge(from, to);
        }
        
        GraphView view = factory.createView(graph);
        
        launchInDialog(view.getComponent()); //, SCREEN_TWO_THIRDS);
        //Thread.currentThread().join();
    }
}
