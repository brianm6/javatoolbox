package toolbox.graph.jung;

import java.util.ArrayList;
import java.util.List;

import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.graph.Edge;
import toolbox.graph.Graph;
import toolbox.graph.GraphLib;
import toolbox.graph.GraphView;
import toolbox.graph.Vertex;
import toolbox.graph.prefuse.PrefuseGraphLib;
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
        
        GraphLib factory = new PrefuseGraphLib();
        Graph graph = factory.createGraph();
        
        Vertex sun = factory.createVertex(graph, "sun");
        graph.addVertex(sun);
        
        Vertex moon = factory.createVertex(graph, "moon");
        graph.addVertex(moon);
        
        Edge edge = factory.createEdge(sun, moon);
        graph.addEdge(edge);
        
        GraphView view = factory.createView(graph);
        
        launchInDialog(view.getComponent()); //, SCREEN_TWO_THIRDS);
    }
    
    
    public void testJung2() throws Exception
    {
        logger_.info("Running testJung2...");
        
        GraphLib lib = new PrefuseGraphLib();
        Graph graph = lib.createGraph();
        
        List nodes = new ArrayList();
        
        for (int i = 0; i < 50; i++)
        {
            Vertex v = lib.createVertex(graph, i + 1 + "");
            nodes.add(v);
            graph.addVertex(v);
        }
        
        for (int i = 0; i < 50; i++)
        {
            Vertex from = (Vertex) nodes.get(i);
            Vertex to = (Vertex) RandomUtil.nextElement(nodes);
            Edge edge = lib.createEdge(from, to);
            graph.addEdge(edge);
        }
        
        GraphView view = lib.createView(graph);
        
        launchInDialog(view.getComponent());
    }
}