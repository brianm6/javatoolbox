package toolbox.util.test;

import junit.framework.TestCase;
import junit.textui.TestRunner;
import org.apache.log4j.Category;

import toolbox.util.RollingCounter;

/**
 * Unit test for RollingCounter
 */
public class RollingCounterTest extends TestCase
{
    private static final Category logger_ = 
        Category.getInstance(RollingCounterTest.class);
        
    /**
     * Entrypoint
     */
    public static void main(String[] args)
    {
        TestRunner.run(RollingCounterTest.class);
    }

    /**
     * Constructor for RollingCounterTest.
     * 
     * @param arg0 Name
     */
    public RollingCounterTest(String arg0)
    {
        super(arg0);
    }
    
    /**
     * Tests increment()
     */
    public void testRangeIsSmall()
    {
        RollingCounter c = new RollingCounter(1,5,1);
        
        StringBuffer sb = new StringBuffer();
        
        for(int j=0; j<4; j++)
        {
            int cnt = 1;
            
            for(int i=1; i<=5; i++)
            {
                assertEquals("count doesn't match", cnt++, c.getCount());
                c.increment();
            }
        }
        
        
        for(int i=0; i<20; i++)
        {
            sb.append(c.getCount() + " ");
            c.increment();
        }
        
        logger_.info(sb);
    }
    
    /**
     * Tests increment()
     */
    public void testRangeIsOne()
    {
        RollingCounter c = new RollingCounter(1,1,1);
        
        for(int i=0; i<20; i++)
        {
            System.out.print(c.getCount() + " ");
            c.increment();
        }
        System.out.println();
    }
    
    /**
     * Test a range that is negative
     */
    public void testRangeIsNegative()
    {
        RollingCounter c = new RollingCounter(-10,-5,-10);
        
        for(int i=0; i<20; i++)
        {
            System.out.print(c.getCount() + " ");
            c.increment();
        }
        System.out.println();
    }
    
    /**
     * Test a range that is negative
     */
    public void testRangeIsSigned()
    {
        RollingCounter c = new RollingCounter(-5,5,-5);
        
        for(int i=0; i<22; i++)
        {
            System.out.print(c.getCount() + " ");
            c.increment();
        }
    }

    /**
     * Test listener
     */
    public void testListener()
    {
        class Ear implements RollingCounter.IRollingCounterListener
        {
            public void afterRoll(RollingCounter rc)
            {
                System.out.print("\n[");
                //System.out.print("*" + rc.getCount() + "*");
            }
            
            public void beforeRoll(RollingCounter rc)
            {
                System.out.print("]");
                //System.out.print("*" + rc.getCount() + "*");                
            }
        }
        
        Ear ear = new Ear();
        RollingCounter rc = new RollingCounter(1,5,1);
        rc.addRollingCounterListener(ear);
        
        for(int i=0; i<33; i++)
        {
            System.out.print(rc.getCount());
            rc.increment();
        }       
    }
    
    /**
     * Test listener
     */
    public void testListener2()
    {
        class Ear implements RollingCounter.IRollingCounterListener
        {
            public void afterRoll(RollingCounter rc)
            {
                System.out.print("\n[");
                //System.out.print("*" + rc.getCount() + "*");
            }
            
            public void beforeRoll(RollingCounter rc)
            {
                System.out.print("]");
                //System.out.print("*" + rc.getCount() + "*");                
            }
        }
        
        Ear ear = new Ear();
        RollingCounter rc = new RollingCounter(1,1,1);
        rc.addRollingCounterListener(ear);
        
        for(int i=0; i<33; i++)
        {
            System.out.print(rc.getCount());
            rc.increment();
        }       
    }    
    
}