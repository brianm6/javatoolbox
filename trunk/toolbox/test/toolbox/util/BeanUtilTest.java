package toolbox.util;

import javax.swing.JLabel;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Unit test for {@link toolbox.util.BeanUtil}.
 */
public class BeanUtilTest extends TestCase
{
    private static final Logger logger_ = Logger.getLogger(BeanUtilTest.class);
    
    // --------------------------------------------------------------------------
    // Main
    // --------------------------------------------------------------------------
    
    public static void main(String[] args)
    {
        TestRunner.run(BeanUtilTest.class);
    }

    // --------------------------------------------------------------------------
    // Unit Tests
    // --------------------------------------------------------------------------
    
    /**
	 * Tests toString(javabean)
	 */
    public void testToStringObject()
    {
        logger_.info("Running testToStringObject...");
        
        JLabel label = new JLabel("Whoopee!");
        
        logger_.debug(
            StringUtil.banner(
                StringUtils.center("JLabel BeanInfo", 80)
                + "\n"
                + StringUtil.BR 
                + "\n" 
                + BeanUtil.toString(label)));
    }
    
    /**
	 * Set all Integer values in bean to even though bean has no Integer values.
	 */
    public void testSetAllValuesForType_ZeroMatches() throws Exception {
      logger_.info("Running testSetAllValuesForType_ZeroMatches...");
      MockZeroMatchesBean javaBean = new MockZeroMatchesBean("myString");
      BeanUtil.setAllValuesForType(javaBean, Integer.class, new Integer(1));
      assertEquals("myString", javaBean.getName());
    }
    
    /**
	 * Set all Integer values in bean when bean has only one Integer value.
	 */
    public void testSetAllValuesForType_OneMatch() throws Exception {
      logger_.info("Running testSetAllValuesForType_OneMatch...");
      MockOneMatchBean javaBean = new MockOneMatchBean("myString", new Integer(99));
      BeanUtil.setAllValuesForType(javaBean, Integer.class, new Integer(101));
      assertEquals("myString", javaBean.getName());
      assertEquals(new Integer(101), javaBean.getAge());
    }

    /**
	 * Set all Integer values in bean when bean has more than one Integer value.
	 */
    public void testSetAllValuesForType_ManyMatches() throws Exception {
      logger_.info("Running testSetAllValuesForType_ManyMatches...");
      MockManyMatchesBean javaBean = new MockManyMatchesBean("myString", new Integer(1), new Integer(2));
      BeanUtil.setAllValuesForType(javaBean, Integer.class, new Integer(3));
      assertEquals("myString", javaBean.getName());
      assertEquals(new Integer(3), javaBean.getAge());
      assertEquals(new Integer(3), javaBean.getHeight());
    }

    /**
	 * Set all Integer values in bean where the write method is missing.
	 * 
	 * Method used to blow up with NPE..not so anymore.
	 */
    public void testSetAllValuesForType_NoWriteMethod() throws Exception {
      logger_.info("Running testSetAllValuesForType_NoWriteMethod...");
      MockNoWriteMethodBean javaBean = new MockNoWriteMethodBean();
      BeanUtil.setAllValuesForType(javaBean, Integer.class, new Integer(3));
      assertEquals(new Integer(23), javaBean.getAge());
    }
    
    // ========================================================================
    // Helper Classes
    // ========================================================================

    /**
	 * Javabean with a single String bean property
	 */
    class MockZeroMatchesBean extends Object {
      private String name;
     
      public MockZeroMatchesBean(String s) {
        setName(s);
      }
      
      public void setName(String s) { 
        name = s;
      }
      
      public String getName() { 
        return name; 
      }
    }

    /**
	 * Javabean with a single String and a single Integer bean property.
	 */
    class MockOneMatchBean extends MockZeroMatchesBean {

      private Integer age;
      
      public MockOneMatchBean(String s, Integer i) {
        super(s);
        setAge(i);
      }

      public Integer getAge() {
        return age;
      }

      public void setAge(Integer age) {
        this.age = age;
      }
    }

    /**
	 * Javabean with a string and two integer bean properties.
	 */
    class MockManyMatchesBean extends MockOneMatchBean {
      
      private Integer height;
      
      public MockManyMatchesBean(String s, Integer i, Integer j) {
        super(s,i);
        setHeight(j);
      }

      public Integer getHeight() {
        return height;
      }

      public void setHeight(Integer height) {
        this.height = height;
      }
    }
    
    /**
     * Javabean with a single String and a single Integer bean property.
     */
    class MockNoWriteMethodBean  {

      public Integer getAge() {
        return new Integer(23);
      }
    }    
}
