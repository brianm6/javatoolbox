package toolbox.util.dump.test;

import java.util.Stack;

import javax.swing.JFrame;
import junit.framework.TestCase;
import junit.textui.TestRunner;

import toolbox.util.StringUtil;
import toolbox.util.dump.Dumper;

/**
 * Unit test for ObjectDumper
 */
public class DumperTest extends TestCase
{
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
        System.out.println(Dumper.dump(new EmptyClass()));
    }

    /**
     * Tests a class with simple fields 
     */
    public void testDumpSimpleObject()
    {
        System.out.println(Dumper.dump(new Address()));
    }


    /**
     * Tests object dumper for a simple test case
     */
    public void testDumpNestedObject()
    {
        System.out.println(Dumper.dump(new Employee()));            
    }
    
    /**
     * Tests dump of JFrame
     */
    public void testDumpJFrame()
    {
        System.out.println(Dumper.dump(new JFrame()));
    }
    
    /**
     * Tests dump of duplicate objects existing in the object graph
     */
    public void testDumpMultipleReferences()
    {
        System.out.println(Dumper.dump(new MultipleReferences()));        
    }
    
    ////////////////////////////////////////////////////////////////////////////
       
    public void testLegacy()
    {
        System.out.println();
        String dump = Dumper.dump(new D());
        System.out.println(dump);
    }
            
    //--------------------------------------------------------------------------
	//  Tests Helper Classes
	//--------------------------------------------------------------------------
            
    class EmptyClass
    {
        // No fields
    }
                
    public class Employee
    {
        String  firstName_;
        String  lastName_;
        int     ssn_;
        float   salary_;
        Address address_;
        Status  status_;
            
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
    
    public class Address
    {
        String street_;
        String city_;
        String state_;
        String zipCode_;
        
        public Address()
        {
            street_ = "1010 Main St";
            city_ = "Dallas";
            state_ = "TX";
            zipCode_ = "76123";
        }
    }
    
    public class Status
    {
        boolean citizen_ = true;
        String  status_  = null; //"Naturalized";
    }
    
    public class MultipleReferences
    {
        Address address_   = new Address();
        Address reference_ = address_; 
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
}class FUN extends Object
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


