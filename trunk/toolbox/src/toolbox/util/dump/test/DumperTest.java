package toolbox.util.dump.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Stack;

import javax.swing.JFrame;
import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.StringUtil;
import toolbox.util.dump.Dumper;

/**
 * Unit test for ObjectDumper
 */
public class DumperTest extends TestCase
{

    private static final Logger logger_ = 
        Logger.getLogger(DumperTest.class);
                

    /**
     * Entrypoint
     * 
     * @param args  None
     */
    public static void main(String[] args)
    {
        TestRunner.run(DumperTest.class);
    }

    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Constructor for ObjectDumperTest.
     * 
     * @param arg0  Name
     */
    public DumperTest(String arg0)
    {
        super(arg0);
    }

    //--------------------------------------------------------------------------
    //  Overridden Methods from TestCase
    //--------------------------------------------------------------------------

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        
        System.out.println("\n" + StringUtil.repeat("=", 80));
    }


    //--------------------------------------------------------------------------
    //  Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests an empty class
     */
    public void testDumpEmptyObject()
    {
        logger_.info("Running testDumpEmptyObject...");
        logger_.info("\n\n" + Dumper.dump(new EmptyClass()));
    }

    /**
     * Tests a class with simple fields 
     */
    public void testDumpSimpleObject()
    {
        logger_.info("Running testDumpSimpleObject...");
        logger_.info("\n\n" + Dumper.dump(new Address()));
    }

    /**
     * Tests object dumper for a simple test case
     */
    public void testDumpNestedObject()
    {
        logger_.info("Running testDumpNestedObject...");
        logger_.info("\n\n" + Dumper.dump(new Employee()));            
    }
    
    /**
     * Tests dump of JFrame
     */
    public void testDumpJFrame()
    {
        logger_.info("Running testDumpJFrame...");
        logger_.info("\n\n" + Dumper.dump(new JFrame()));
    }
    
    /**
     * Tests dump of duplicate objects existing in the object graph
     */
    public void testDumpMultipleReferences()
    {
        logger_.info("Running testDumpMultipleReferences...");
        logger_.info("\n\n" + Dumper.dump(new MultipleReferences()));        
    }
    
    /** 
     * Tests dump of object with a constraining maxDepth
     */
    public void testDumpMaxDepth()
    { 
        logger_.info("Running testDumpMaxDepth...");
        logger_.info("\n\n" + Dumper.dump(new Employee(), 3));        
    }
    
    /**
     * Tests dumping of collection classes
     */    
    public void testDumpCollection()
    {
        class CollectionDump
        {
            private Collection coll_;
            
            public CollectionDump()
            {
                coll_ = new ArrayList();
                coll_.add("one");
                coll_.add(new Country());
                coll_.add(new Employee());
            }
        }
        
        logger_.info("Running testDumpCollection...");
        logger_.info("\n\n" + Dumper.dump(new CollectionDump()));
    }

    // TODO: Update dumper to handle collections/arrays/vectors/etc!
     
    /**
     * Tests legacy version 
     */   
    public void testLegacy()
    {
        logger_.info("Running testLegacy...");
        String dump = Dumper.dump(new D());
        logger_.info("\n\n" + dump);
    }
            
    //--------------------------------------------------------------------------
    //  Tests Helper Classes
    //--------------------------------------------------------------------------
            
    class EmptyClass
    {
        // No fields
    }
    
    /**
     * Employee
     */            
    class Employee
    {
        private String  firstName_;
        private String  lastName_;
        private int     ssn_;
        private float   salary_;
        private Address address_;
        private Status  status_;
            
        public Employee()
        {
            firstName_ = "Daffy";
            lastName_  = "Duck";
            ssn_       = 232323;
            salary_    = 567.89f;
            
            address_ = new Address();
            status_  = new Status();
        }
    }    
    
    /**
     * Address
     */
    class Address
    {
        private String street_;
        private String city_;
        private String state_;
        private String zipCode_;
        private Country country_;
                
        public Address()
        {
            street_ = "1010 Main St";
            city_ = "Dallas";
            state_ = "TX";
            zipCode_ = "76123";
            country_ = new Country();
        }
    }

    /**
     * Country
     */
    public class Country
    {
        private String country_ = "USA";
    }
    
    /**
     * Status 
     */
    public class Status
    {
        private boolean citizen_ = true;
        private String  status_  = null; //"Naturalized";
    }
    
    /**
     * MultipleReferences
     */
    public class MultipleReferences
    {
        private Address address_   = new Address();
        private Address reference_ = address_; 
    } 
}

class B extends A
{
    protected transient int aBVariable;
    private int xDeclaredInAandB = 8;
}

class C extends B
{
    volatile boolean aCVariable = false;
}

class D extends C
{
    //  protected Object aDVariable = new Object();
    //  private String myString1 = "asdfsadfsadf\nasdfsadf\njasdfs\ndafsadfsadfdsaasdfasdf";
    //  private String myString2 = "aaaa\nbbbb\n\n\ncccc\n\n";
    //  Object differentType = new C();
    private volatile static Integer synchInteger = new Integer(9);
    protected transient FUN fun = new MEGAFUN();
    //  static D loop = new D();
    public Stack stack = new Stack();
    //  public javax.swing.JLabel jLabel= new javax.swing.JLabel();
}

class FUN extends Object
{
    private Integer funInteger = new Integer(13);
}

class MEGAFUN extends FUN
{
    private Integer megaFunInteger = new Integer(14);
    public String toString()
    {
        return "this is what you get from calling \"toString()\" !!!";
    }
}

class TestObject2
{

    public int jInt = 5;
    private transient float jFloat = 6;
    public Integer jInteger = new Integer(7);
    private Double jDouble = new Double(8);

    public D d = new D();
    public static final TestObject2 to2 = new TestObject2();

}

class A
{
    private int anAVariable;
    private int xDeclaredInAandB = 5;
}


