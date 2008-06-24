package toolbox.util.beans;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Unit test for {@link toolbox.util.beans.BeanPropertyFilter}.
 */
public class BeanPropertyFilterTest extends TestCase
{
    private static final Log logger_ = 
        LogFactory.getLog(BeanPropertyFilterTest.class);

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    private MockBean vanilla = new MockBean("vanilla", 7);
    private MockBean roast = new MockBean("roast", 1);
    private MockBean french = new MockBean("french", 7);
    private MockBean origin = new MockBean(
        "ignore", 0, new Origin("south america"));
    
    private List beans;

    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------

    public static void main(String[] args)
    {
        TestRunner.run(BeanPropertyFilterTest.class);
    }

    //--------------------------------------------------------------------------
    // Overrides TestCase
    //--------------------------------------------------------------------------

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        beans = new ArrayList();
        beans.add(vanilla);
        beans.add(roast);
        beans.add(french);
        beans.add(origin);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------

    /**
     * Filter matches zero elements.
     */
    public void testEvaluateZero()
    {
        logger_.info("Running testEvaluateZero...");

        // Match 0
        BeanPropertyFilter bogusFilter = new BeanPropertyFilter("flavor",
            "bogus");
        Collection bogii = CollectionUtils.select(beans, bogusFilter);
        assertTrue(bogii.isEmpty());
    }


    /**
     * Filter matches one element.
     */
    public void testEvaluateOne()
    {
        logger_.info("Running testEvaluateOne...");

        // Match 1
        BeanPropertyFilter vanillaFilter = new BeanPropertyFilter("flavor",
            "vanilla");
        Collection vanillas = CollectionUtils.select(beans, vanillaFilter);
        assertEquals(1, vanillas.size());
        assertEquals(vanilla, vanillas.iterator().next());
    }


    /**
     * Filter matches > 1 element.
     */
    public void testEvaluateMany()
    {
        logger_.info("Running testEvaluateMany...");

        // Match > 1
        BeanPropertyFilter manyFilter = new BeanPropertyFilter("acidity",
            new Integer(7));

        Collection many = CollectionUtils.select(beans, manyFilter);
        assertEquals(2, many.size());
        assertTrue(many.contains(vanilla));
        assertTrue(many.contains(french));
    }


    /**
     * Filter that matches a non-trivial javabean property (not primitive, not
     * string).
     */
    public void testEvaluateObject()
    {
        logger_.info("Running testEvaluateObject...");

        BeanPropertyFilter filter = new BeanPropertyFilter("origin",
            new Origin("south america"));
        Collection match = CollectionUtils.select(beans, filter);
        assertEquals(1, match.size());
        assertEquals(origin, match.iterator().next());
    }

    
    /**
     * Filter is an array.
     */
    public void testEvaluateArrayZero()
    {
        logger_.info("Running testEvaluateArrayZero...");

        BeanPropertyFilter filter =
            new BeanPropertyFilter("flavor", new String[] {});
        
        Collection result = CollectionUtils.select(beans, filter);
        assertTrue(result.isEmpty());
    }


    /**
     * Filter matches one element in a array of size one.
     */
    public void testEvaluateArrayOneMatch()
    {
        logger_.info("Running testEvaluateArrayOneMatch...");

        BeanPropertyFilter filter = 
            new BeanPropertyFilter("flavor", new Object[] {"vanilla"});
        
        Collection results = CollectionUtils.select(beans, filter);
        assertEquals(1, results.size());
        assertEquals(vanilla, results.iterator().next());
    }

    
    /**
     * Filter does not match one element in a array of size one.
     */
    public void testEvaluateArrayOneMismatch()
    {
        logger_.info("Running testEvaluateArrayOneMismatch...");

        BeanPropertyFilter filter = 
            new BeanPropertyFilter("flavor", new Object[] {"bogus"});
        
        Collection results = CollectionUtils.select(beans, filter);
        assertEquals(0, results.size());
    }

    
    /**
     * Filter matches one element in a array of size > 1.
     */
    public void testEvaluateArrayManyMatch()
    {
        logger_.info("Running testEvaluateArrayManyMatch...");

        BeanPropertyFilter filter = 
            new BeanPropertyFilter("flavor", 
                new Object[] {"bogus1", "vanilla", "bogus2"});
        
        Collection results = CollectionUtils.select(beans, filter);
        assertEquals(1, results.size());
        assertEquals(vanilla, results.iterator().next());
    }

    
    /**
     * Filter matches one element in a array of size > 1.
     */
    public void testEvaluateCollectionManyMatch()
    {
        logger_.info("Running testEvaluateCollectionManyMatch...");

        List values = new ArrayList();
        values.add("bogus1");
        values.add("vanilla");
        values.add("bogus2");
        
        BeanPropertyFilter filter = new BeanPropertyFilter("flavor", values); 
        Collection results = CollectionUtils.select(beans, filter);
        assertEquals(1, results.size());
        assertEquals(vanilla, results.iterator().next());
    }
    
    //--------------------------------------------------------------------------
    // MockBean
    //--------------------------------------------------------------------------

    /**
     * Simple class with two bean properties.
     */
    public class MockBean
    {
        /**
         * Bean propery name = flavor.
         */
        private String flavor_;

        /**
         * Bean property name = acidity.
         */
        private int acidity_;

        /**
         * Bean that does not have a default equals() impl.
         */
        private Origin origin_;

        //----------------------------------------------------------------------
        // Constructors
        //----------------------------------------------------------------------

        /**
         * Creates a MockBean.
         * 
         * @param flavor Flavor
         * @param acidity Acidity
         */
        public MockBean(String flavor, int acidity)
        {
            setFlavor(flavor);
            setAcidity(acidity);
        }


        /**
         * Creates a MockBean.
         * 
         * @param flavor Flavor
         * @param acidity Acidity
         */
        public MockBean(String flavor, int acidity, Origin origin)
        {
            setFlavor(flavor);
            setAcidity(acidity);
            setOrigin(origin);
        }

        //----------------------------------------------------------------------
        // Accessors/Mutators
        //----------------------------------------------------------------------

        /**
         * Returns the acidity.
         * 
         * @return int
         */
        public int getAcidity()
        {
            return acidity_;
        }


        /**
         * Sets the value of acidity.
         * 
         * @param acidity The acidity to set.
         */
        public void setAcidity(int acidity)
        {
            acidity_ = acidity;
        }


        /**
         * Returns the flavor.
         * 
         * @return String
         */
        public String getFlavor()
        {
            return flavor_;
        }


        /**
         * Sets the value of flavor.
         * 
         * @param flavor The flavor to set.
         */
        public void setFlavor(String flavor)
        {
            flavor_ = flavor;
        }


        /**
         * @return Origin
         */
        public Origin getOrigin()
        {
            return origin_;
        }


        /**
         * @param origin
         */
        public void setOrigin(Origin origin)
        {
            origin_ = origin;
        }

    }

    //--------------------------------------------------------------------------
    // Origin
    //--------------------------------------------------------------------------

    /**
     * Bean origin.
     */
    public class Origin
    {
        String name_;


        public Origin(String s)
        {
            name_ = s;
        }


        public boolean equals(Object obj)
        {
            if (obj == null)
                return false;

            return name_.equals(((Origin) obj).getName());
        }


        public String getName()
        {
            return name_;
        }


        public void setName(String string)
        {
            name_ = string;
        }
    }
}