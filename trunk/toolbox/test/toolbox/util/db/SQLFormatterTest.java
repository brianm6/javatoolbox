package toolbox.util.db;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import nu.xom.Element;

import org.apache.log4j.Logger;

import toolbox.util.StringUtil;

/**
 * Unit test for SQLFormatter.
 * 
 * @see toolbox.util.db.SQLFormatter
 */
public class SQLFormatterTest extends TestCase
{
    private static final Logger logger_ =
        Logger.getLogger(SQLFormatterTest.class);

    //--------------------------------------------------------------------------
    // Test SQL Statements
    //--------------------------------------------------------------------------
    
    private static final String SQL_LOWER = 
        "select * from user where name like 'Joe'";
    
    private static final String SQL_UPPER = 
        "SELECT * FROM USER WHERE NAME LIKE 'Joe'";
    
    private static final String SQL_MIXED = 
        "sElEcT * fRoM uSeR WhERe nAMe liKE 'Joe'";
    
    private static final String[] SQL_ALL = 
        new String[] {SQL_LOWER, SQL_UPPER, SQL_MIXED };
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Shared formatter use by unit tests.
     */
    private SQLFormatter formatter_ = new SQLFormatter();
    
    //--------------------------------------------------------------------------
    // Main 
    //--------------------------------------------------------------------------
    
    /**
     * Entrypoint.
     * 
     * @param args None recognized.
     */
    public static void main(String[] args)
    {
        TestRunner.run(SQLFormatterTest.class);
    }

    //--------------------------------------------------------------------------
    // Caps Mode Unit Tests 
    //--------------------------------------------------------------------------

    /**
     * Tests major caps mode formatting.
     */
    public void testFormatMajorCapsMode()  throws Exception
    {
        logger_.info("Running testFormatMajorCapsMode...");
        
        String formatterMode = "major";
        
        // Lowercase
        String[] expectedLower = 
            new String[] {"select", "from", "where", "Joe"};
        
        assertCapsMode(
            formatterMode, CapsMode.LOWERCASE, SQL_ALL, expectedLower);
 
        // Uppercase
        String[] expectedUpper = 
            new String[] {"SELECT", "FROM", "WHERE", "Joe"};
        
        assertCapsMode(
            formatterMode, CapsMode.UPPERCASE, SQL_ALL, expectedUpper);

        // Mixedcase
        String[] expectedPreserve = 
            new String[] {"sElEcT", "fRoM", "WhERe", "Joe"};
        
        assertCapsMode(
            formatterMode, 
            CapsMode.PRESERVE, 
            new String[] {SQL_MIXED}, 
            expectedPreserve);
        
        assertCapsMode(
            formatterMode, 
            CapsMode.PRESERVE, 
            new String[] {SQL_LOWER}, 
            expectedLower);

        assertCapsMode(
            formatterMode, 
            CapsMode.PRESERVE, 
            new String[] {SQL_UPPER}, 
            expectedUpper);
    }

    
    /**
     * Tests minor caps mode formatting.
     */
    public void testFormatMinorCapsMode() throws Exception
    {
        logger_.info("Running testFormatMinorCapsMode...");
        
        String formatterMode = "minor";
        
        // Lowercase
        String[] expectedLower  = new String[] {"like", "Joe"};
        assertCapsMode(
            formatterMode, CapsMode.LOWERCASE, SQL_ALL, expectedLower);
 
        // Uppercase
        String[] expectedUpper  = new String[] {"LIKE", "Joe"};
        assertCapsMode(
            formatterMode, CapsMode.UPPERCASE, SQL_ALL, expectedUpper);

        // Mixedcase
        String[] expectedPreserve  = new String[] {"liKE", "Joe"};
        
        assertCapsMode(
            formatterMode, 
            CapsMode.PRESERVE, 
            new String[] {SQL_MIXED}, 
            expectedPreserve);
        
        assertCapsMode(
            formatterMode, 
            CapsMode.PRESERVE, 
            new String[] {SQL_LOWER}, 
            expectedLower);

        assertCapsMode(
            formatterMode, 
            CapsMode.PRESERVE, 
            new String[] {SQL_UPPER}, 
            expectedUpper);
    }


    /**
     * Tests names caps mode formatting.
     */
    public void testFormatNamesCapsMode() throws Exception
    {
        logger_.info("Running testFormatNamesCapsMode...");
        
        String formatterMode = "names";
        
        // Lowercase
        String[] expectedLower  = new String[] {"user", "name", "Joe"};
        assertCapsMode(
            formatterMode, CapsMode.LOWERCASE, SQL_ALL, expectedLower);
 
        // Uppercase
        String[] expectedUpper  = new String[] {"USER", "NAME", "Joe"};
        assertCapsMode(
            formatterMode, CapsMode.UPPERCASE, SQL_ALL, expectedUpper);

        // Mixedcase
        String[] expectedPreserve  = new String[] {"uSeR", "nAMe", "Joe"};
        
        assertCapsMode(
            formatterMode, 
            CapsMode.PRESERVE, 
            new String[] {SQL_MIXED}, 
            expectedPreserve);
        
        assertCapsMode(
            formatterMode, 
            CapsMode.PRESERVE, 
            new String[] {SQL_LOWER}, 
            expectedLower);

        assertCapsMode(
            formatterMode, 
            CapsMode.PRESERVE, 
            new String[] {SQL_UPPER}, 
            expectedUpper);
    }

    
    /**
     * Common pattern for verifying the caps mode formatting.
     * 
     * @param formatterMode Major, minor, or names.
     * @param capsMode Caps mode to verify.
     * @param sqlStatements SQL statements to use as input.
     * @param expectedKeywords Expected case of keywords in the result.
     */
    protected void assertCapsMode(
        String formatterMode,
        CapsMode capsMode, 
        String[] sqlStatements, 
        String[] expectedKeywords) throws Exception
    {
        for (int i = 0; i < sqlStatements.length; i++)
        {
            SQLFormatter sf = new SQLFormatter();
            
            if (formatterMode.equals("major"))
                sf.setMajorCapsMode(capsMode);
            else if (formatterMode.equals("minor"))
                sf.setMinorCapsMode(capsMode);
            else if (formatterMode.equals("names"))
                sf.setNamesCapsMode(capsMode);
            else
                throw new IllegalArgumentException("Invalid formatterMode");
            
            String result = sf.format(sqlStatements[i]);
            logger_.info(StringUtil.banner(result));
            
            for (int j = 0; j < expectedKeywords.length; j++)
                assertTrue(result.indexOf(expectedKeywords[j]) >= 0);
        }
    }

    //--------------------------------------------------------------------------
    // Simple Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests formatting of a simple select stmt.
     */
    public void testFormatSQL1() throws Exception
    {
        logger_.info("Running testFormatSQL1...");
        
        String s = formatter_.format("select name, age, height from user");
        logger_.info(StringUtil.addBars(s));
    }

    
    /**
     * Tests formatting of a simple select stmt.
     */
    public void testFormatSQL1_1() throws Exception
    {
        logger_.info("Running testFormatSQL1_1...");
        String s = formatter_.format("SELECT * FROM USER");
        logger_.info(StringUtil.addBars(s));
    }
    
    
    /**
     * Tests formatting of a simple sql statement with criteria.
     */
    public void testFormatSQL2() throws Exception
    {
        logger_.info("Running testFormatSQL2...");
        
        String s = formatter_.format(
            "select one, two, three from user " +
            "where name like 'A%' and id = 34533 group by lastName");
        
        logger_.info(StringUtil.addBars(s));
    }

    
    /**
     * Tests formatting of a create table stmt.
     */
    public void testFormatSQL3() throws Exception
    {
        logger_.info("Running testFormatSQL3...");
        
        String s = formatter_.format(
            "        CREATE TABLE SAPCateg.SAPCategoryType("
            + "                id CHAR(8) NOT NULL,"
            + "                sequence CHAR(8) NOT NULL,"
            + "                version CHAR(8) NOT NULL,"
            + "                domain VARCHAR(50),"
            + "                PRIMARY KEY(id))"
            + "        in DATA01 index in INX01;");
        
        logger_.info(StringUtil.addBars(s));
    }
    
    
    /**
     * Tests formatting of a large create table stmt.
     */
    public void testFormatSQL4() throws Exception
    {
        logger_.info("Running testFormatSQL_4...");
        
        String s = formatter_.format(
            "CREATE TABLE Location (id CHAR(8) NOT NULL, sequence CHAR(8)"
            + " NOT NULL, version CHAR(8) NOT NULL, capability VARCHAR(16),"
            + " sequence CHAR(8) NOT NULL, version CHAR(8) NOT NULL, capabi"
            + "lity VARCHAR(16), applyPwswTiny CHAR(1), locationNumber VARC"
            + "HAR(32), localCompanyName VARCHAR(100), localCompanyNamePron"
            + " VARCHAR(100), companyName VARCHAR(100), dbaName VARCHAR(100"
            + "), contactAddress_addrLineOne VARCHAR(60), contactAddress_ad"
            + "drLineTwo VARCHAR(60), contactAddress_addrLineThree VARCHAR("
            + "60), contactAddress_addrLineFour VARCHAR(60), contactAddress"
            + "_city VARCHAR(35), contactAddress_country CHAR(2), contactAd"
            + "dress_country_L VARCHAR(35), contactAddress_state CHAR(6), c"
            + "ontactAddress_state_L VARCHAR(35), contactAddress_zip VARCHA"
            + "R(16), hasReturnedMail CHAR(1), voiceOne_ctryCode VARCHAR(10"
            + "0), voiceOne_number VARCHAR(32), faxOne_ctryCode VARCHAR(100"
            + "), faxOne_number VARCHAR(32), internetInfo_locationEmail VAR"
            + "CHAR(100), internetInfo_locationURL VARCHAR(150), parentID V"
            + "ARCHAR(100), ibmCustNum VARCHAR(10), federalTaxID VARCHAR(50"
            + "), corporationNumber VARCHAR(8), ppaID VARCHAR(10), numberLo"
            + "cations INTEGER, yearStarted INTEGER, geo CHAR(2), geo_L VAR"
            + "CHAR(35), geoRegion CHAR(6), geoRegion_L VARCHAR(35), numEmp"
            + "loyees INTEGER, bpdbID VARCHAR(26), cmrAction CHAR(6), cmrAc"
            + "tion_L VARCHAR(35), cmrRetry CHAR(1), cmr_customerNumber VAR"
            + "CHAR(10), cmr_date DATE, cmr_existing CHAR(1), cmr_firstName"
            + " VARCHAR(100), cmr_lastName VARCHAR(100), cmr_phone VARCHAR("
            + "100), cmr_email VARCHAR(100), cmr_contactNumber VARCHAR(10),"
            + " cmr_deleted CHAR(1), cmr_deniedParty CHAR(1), cmr_badAddres"
            + "s CHAR(1), cmr_duplicate CHAR(1), cmr_duplicateNumber VARCHA"
            + "R(10), cmr_duplicateName VARCHAR(35), cmr_success CHAR(1), c"
            + "mr_message VARCHAR(35), profileUpdateUser VARCHAR(40), profi"
            + "leUpdateTimestamp DATE, dplCheck CHAR(6), dplCheck_L VARCHAR"
            + "(35), supplierLists CHAR(6), supplierLists_L VARCHAR(35), ru"
            + "ssianFederation CHAR(6), russianFederation_L VARCHAR(35), PR"
            + "IMARY KEY(id));");
                
        logger_.info(StringUtil.addBars(s));
    }
    
    //--------------------------------------------------------------------------
    // IPreferenced Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests saving preferences.
     */
    public void testSavePrefs() throws Exception
    {
        SQLFormatter sf = new SQLFormatter();
        
        Element prefs = new Element("root");
        sf.savePrefs(prefs);
        logger_.info(StringUtil.banner(prefs.toXML()));
    }

    
    /**
     * Tests restoring preferences.
     */
    public void testApplyPrefs() throws Exception
    {
        SQLFormatter expected = new SQLFormatter();
        expected.setIndent(9);
        expected.setDebug(true);
        expected.setNewLineBeforeAnd(false);
        expected.setMajorCapsMode(CapsMode.LOWERCASE);
        expected.setMinorCapsMode(CapsMode.UPPERCASE);
        expected.setNamesCapsMode(CapsMode.LOWERCASE);
        
        Element prefs = new Element("root");
        expected.savePrefs(prefs);
        
        SQLFormatter actual = new SQLFormatter();
        actual.applyPrefs(prefs);
        
        assertEquals(expected.getIndent(), actual.getIndent());
        assertEquals(expected.isDebug(), actual.isDebug());
        assertEquals(expected.isNewLineBeforeAnd(), actual.isNewLineBeforeAnd());
        assertEquals(expected.getMajorCapsMode(), actual.getMajorCapsMode());
        assertEquals(expected.getMinorCapsMode(), actual.getMinorCapsMode());
        assertEquals(expected.getNamesCapsMode(), actual.getNamesCapsMode());
        
        logger_.info(StringUtil.banner(prefs.toXML()));
    }
}