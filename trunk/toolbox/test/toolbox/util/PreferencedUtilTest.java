package toolbox.util;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import nu.xom.Element;

import org.apache.log4j.Logger;

/**
 * Unit Test for PreferencedUtil.
 * 
 * @see toolbox.util.PreferencedUtil
 */
public class PreferencedUtilTest extends TestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(PreferencedUtilTest.class);
    
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    public static void main(String[] args)
    {
        TestRunner.run(PreferencedUtilTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests writing bean properties to an xml string.
     */
    public void testWritePreferences() throws Exception
    {
        logger_.info("Running testWritePreferences...");
        
        String expectedFlavor = "vanilla";
        int expectedAcidity = 4;
        
        Element root = new Element("root");
        PreferencedUtil.writePreferences(
            new MockBean(expectedFlavor, expectedAcidity), 
            root, 
            MockBean.PROPS);
        
        logger_.debug(StringUtil.banner(XOMUtil.toXML(root)));
        
        assertEquals(
            expectedFlavor, 
            root.getAttribute(MockBean.PROP_FLAVOR).getValue());
        
        assertEquals(
            expectedAcidity, 
            Integer.parseInt(
                root.getAttribute(MockBean.PROP_ACIDITY).getValue()));
    }

    
    /**
     * Tests writing for a non-existant property.
     */
    public void testWritePreferencesNonExistantProperty() throws Exception
    {
        logger_.info("Running testWritePreferencesNonExistantProperty...");
        
        Element root = new Element("root");
        
        PreferencedUtil.writePreferences(
            new MockBean(), root, new String[] {"nonExistantProperty"});
        
        logger_.debug(StringUtil.banner(XOMUtil.toXML(root)));
        assertEquals(0, root.getAttributeCount());
    }

    
    /**
     * Tests writing for a property with a null value.
     */
    public void testWritePreferencesNullValue() throws Exception
    {
        logger_.info("Running testWritePreferencesNullValue..."); 
        
        Element root = new Element("root");
        MockBean bean = new MockBean();
        bean.setFlavor(null);
        
        PreferencedUtil.writePreferences(
            bean, root, new String[] {MockBean.PROP_FLAVOR});
        
        logger_.debug(StringUtil.banner(XOMUtil.toXML(root)));
        assertEquals(0, root.getAttributeCount());
    }

    
    /**
     * Tests reading bean properties from an xml string.
     */
    public void testReadPreferences() throws Exception
    {
        logger_.info("Running testReadPreferences...");
        
        String expectedFlavor = "vanilla";
        int expectedAcidity = 4;
        
        String xml = 
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
            + "<root acidity=\"" 
            + expectedAcidity 
            + "\" flavor=\""
            + expectedFlavor 
            + "\"/>";
        
        Element root = XOMUtil.toElement(xml);
        MockBean bean = new MockBean();
        PreferencedUtil.readPreferences(bean, root, MockBean.PROPS);
        assertEquals(expectedFlavor, bean.getFlavor());
        assertEquals(expectedAcidity, bean.getAcidity());
    }

    
    /**
     * Tests reading for a non-existant property.
     * 
     * @throws Exception on error.
     */
    public void testReadPreferencesNonExistantProperty() throws Exception
    {
        logger_.info("Running testReadPreferencesNonExistantProperty...");
        
        String xml = 
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
            + "<root/>";
        
        PreferencedUtil.readPreferences(
            new MockBean(), 
            XOMUtil.toElement(xml), 
            new String[] {"nonExistantProperty"});
        
        // No exception thrown == success
    }
    
    //--------------------------------------------------------------------------
    // MockBean
    //--------------------------------------------------------------------------

    /**
     * Simple class with two bean properties.
     */
    public static class MockBean
    {
        //----------------------------------------------------------------------
        // JavaBean Constants
        //----------------------------------------------------------------------
        
        public static final String PROP_FLAVOR = "flavor";
        public static final String PROP_ACIDITY = "acidity";
        public static final String[] PROPS = {PROP_FLAVOR, PROP_ACIDITY};
        
        //----------------------------------------------------------------------
        // Fields
        //----------------------------------------------------------------------
        
        /**
         * Bean propery name = flavor.
         */
        private String flavor_;

        /**
         * Bean property name = acidity.
         */
        private int acidity_;

        //----------------------------------------------------------------------
        // Constructors
        //----------------------------------------------------------------------

        /**
         * Creates a MockBean.
         */
        public MockBean()
        {
        }
        
        
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
    }
}