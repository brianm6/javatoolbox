package toolbox.util.db.test;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.StringUtil;
import toolbox.util.db.SQLFormatter;

/**
 * Unit test for SQLFormatter.
 */
public class SQLFormatterTest extends TestCase
{
    private static final Logger logger_ =
        Logger.getLogger(SQLFormatterTest.class);

    /**
     * Shared formatter use by unit tests.
     */
    SQLFormatter formatter_ = new SQLFormatter();
    
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
    // Unit Tests 
    //--------------------------------------------------------------------------

    /**
     * Tests formatting of some simple sql statements.
     */
    public void testFormatSQL()
    {
        logger_.info("Running testFormatSQL...");
        
        String s = formatter_.format("select * from user");
        logger_.info(StringUtil.addBars(s));
        
        s = formatter_.format("select one, two, three from user where name like 'A%' and id = 34533 group by lastName");
        logger_.info(StringUtil.addBars(s));
        
        s = formatter_.format("CREATE TABLE Location (id CHAR(8) NOT NULL, sequence CHAR(8) NOT NULL, version CHAR(8) NOT NULL, capability VARCHAR(16), sequence CHAR(8) NOT NULL, version CHAR(8) NOT NULL, capability VARCHAR(16), applyPwswTiny CHAR(1), locationNumber VARCHAR(32), localCompanyName VARCHAR(100), localCompanyNamePron VARCHAR(100), companyName VARCHAR(100), dbaName VARCHAR(100), contactAddress_addrLineOne VARCHAR(60), contactAddress_addrLineTwo VARCHAR(60), contactAddress_addrLineThree VARCHAR(60), contactAddress_addrLineFour VARCHAR(60), contactAddress_city VARCHAR(35), contactAddress_country CHAR(2), contactAddress_country_L VARCHAR(35), contactAddress_state CHAR(6), contactAddress_state_L VARCHAR(35), contactAddress_zip VARCHAR(16), hasReturnedMail CHAR(1), voiceOne_ctryCode VARCHAR(100), voiceOne_number VARCHAR(32), faxOne_ctryCode VARCHAR(100), faxOne_number VARCHAR(32), internetInfo_locationEmail VARCHAR(100), internetInfo_locationURL VARCHAR(150), parentID VARCHAR(100), ibmCustNum VARCHAR(10), federalTaxID VARCHAR(50), corporationNumber VARCHAR(8), ppaID VARCHAR(10), numberLocations INTEGER, yearStarted INTEGER, geo CHAR(2), geo_L VARCHAR(35), geoRegion CHAR(6), geoRegion_L VARCHAR(35), numEmployees INTEGER, bpdbID VARCHAR(26), cmrAction CHAR(6), cmrAction_L VARCHAR(35), cmrRetry CHAR(1), cmr_customerNumber VARCHAR(10), cmr_date DATE, cmr_existing CHAR(1), cmr_firstName VARCHAR(100), cmr_lastName VARCHAR(100), cmr_phone VARCHAR(100), cmr_email VARCHAR(100), cmr_contactNumber VARCHAR(10), cmr_deleted CHAR(1), cmr_deniedParty CHAR(1), cmr_badAddress CHAR(1), cmr_duplicate CHAR(1), cmr_duplicateNumber VARCHAR(10), cmr_duplicateName VARCHAR(35), cmr_success CHAR(1), cmr_message VARCHAR(35), profileUpdateUser VARCHAR(40), profileUpdateTimestamp DATE, dplCheck CHAR(6), dplCheck_L VARCHAR(35), supplierLists CHAR(6), supplierLists_L VARCHAR(35), russianFederation CHAR(6), russianFederation_L VARCHAR(35), PRIMARY KEY(id));");
        logger_.info(StringUtil.addBars(s));
        
        s = formatter_.format(
        "        CREATE TABLE SAPCateg.SAPCategoryType("
        + "                id CHAR(8) NOT NULL,"
        + "                sequence CHAR(8) NOT NULL,"
        + "                version CHAR(8) NOT NULL,"
        + "                domain VARCHAR(50),"
        + "                PRIMARY KEY(id))"
        + "        in DATA01 index in INX01;");
        
        logger_.info(StringUtil.addBars(s));
    }
}