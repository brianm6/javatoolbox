package toolbox.util.test;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Category;
import toolbox.util.MapUtil;

/**
 * Unit test for MapUtil
 */
public class MapUtilTest extends TestCase
{
    private static final Category logger_ = 
        Category.getInstance(MapUtilTest.class);
        
    /**
     * Entrypoint
     * 
     * @param  args  None recognized
     */
    public static void main(String[] args)
    {
        TestRunner.run(MapUtilTest.class);
    }

    /**
     * Constructor for MapUtilTest.
     * 
     * @param arg0 Name
     */
    public MapUtilTest(String arg0)
    {
        super(arg0);
    }

    
    /**
     * Tests toString()
     */
    public void testToString()
    {
        Map map = new HashMap();
        
        map.put("name", "john");
        map.put("age", "34");
        map.put("misc", "stuff");
        
        logger_.info("\n" + MapUtil.toString(map));
    }
}
