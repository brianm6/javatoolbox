package toolbox.jdbc;

import java.io.IOException;
import java.io.StringReader;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Element;
import nu.xom.ParseException;

import toolbox.util.XOMUtil;

/**
 * Database connection profile for single JDBC connection.
 */
public class DBProfile
{
    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------
    
    /**
     * XML element name for a DBProfile.
     */
    private static final String ELEMENT_PROFILE = "DBProfile";
    
    /**
     * XML attribute name for the profile name.
     */
    private static final String ATTR_PROFILE_NAME = "profilename";

    /**
     * XML attribute name for the JDBC driver jar file. This field is optional
     * if the jar file is already on the classpath. 
     */
    private static final String ATTR_JARFILE = "jarfile";

    /**
     * XML attribute name for the JDBC driver. 
     */
    private static final String ATTR_DRIVER = "driver";
    
    /**
     * XML attribute name for the JDBC url. 
     */
    private static final String ATTR_URL = "url";
    
    /**
     * XML attribute name for the JDBC user.
     */
    private static final String ATTR_USERNAME = "username";
    
    /**
     * XML attribute name for the JDBC authentication password (clear text).
     */
    private static final String ATTR_PASSWORD = "password";
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Profile name is a user friendly term used to uniquely identify a profile. 
     */
    private String profileName_;
    
    /**
     * JDBC driver jar file name.
     */
    private String jarFile_;
    
    /**
     * JDBC driver class name. Must be a FQCN!
     */
    private String driver_;
    
    /**
     * JDBC access url.
     */
    private String url_;
    
    /**
     * Username for database access.
     */
    private String username_;
    
    /**
     * Password to authenticate the user with.
     */
    private String password_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a DBProfile from XML.
     *
     * @param xml String containing a valid XML persistence of DBProfile
     * @throws IOException on I/O error
     * @throws ParseException on XML parsing error 
     */
    public DBProfile(String xml) throws ParseException, IOException
    {
        Element profile = 
            new Builder().build(new StringReader(xml)).getRootElement();
        
        setProfileName(profile.getAttributeValue(ATTR_PROFILE_NAME));
        setJarFile(XOMUtil.getStringAttribute(profile,ATTR_JARFILE,""));
        setDriver(profile.getAttributeValue(ATTR_DRIVER));
        setUrl(profile.getAttributeValue(ATTR_URL));
        setUsername(profile.getAttributeValue(ATTR_USERNAME));
        setPassword(profile.getAttributeValue(ATTR_PASSWORD));
    }

    
    /**
     * Creates a DBProfile.
     *
     * @param profileName Friendly name of the profile
     * @param jarFile JDBC driver jar file
     * @param driver JDBC driver class
     * @param url JDBC access url
     * @param username Username
     * @param password Password in clear text       
     */
    public DBProfile(
        String profileName,
        String jarFile,
        String driver, 
        String url, 
        String username,
        String password)
    {
        profileName_ = profileName;
        jarFile_ = jarFile;
        driver_ = driver;
        url_ = url;
        username_ = username;
        password_ = password;
    }
    
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------

    /**
     * Returns an XML representation of the data contained in this profile.
     * 
     * @return XML string
     */
    public String toXML()
    {
        return toDOM().toXML();
    }

    
    /**
     * Returns a DOM representation of the data contained in this profile.
     * 
     * @return DOM tree
     */    
    public Element toDOM()
    {
        Element profile = new Element(ELEMENT_PROFILE);
        profile.addAttribute(new Attribute(ATTR_PROFILE_NAME, profileName_));
        profile.addAttribute(new Attribute(ATTR_JARFILE, jarFile_));
        profile.addAttribute(new Attribute(ATTR_DRIVER, driver_));
        profile.addAttribute(new Attribute(ATTR_URL, url_));
        profile.addAttribute(new Attribute(ATTR_USERNAME, username_));
        profile.addAttribute(new Attribute(ATTR_PASSWORD, password_));
        return profile;
    }

    //--------------------------------------------------------------------------
    // Accessors/Mutators
    //--------------------------------------------------------------------------
        
    /**
     * Returns the name of the database profile.
     * 
     * @return Profile name
     */
    public String getProfileName()
    {
        return profileName_;
    }

    
    /**
     * Returns the JDBC driver.
     * 
     * @return JDBC driver
     */
    public String getDriver()
    {
        return driver_;
    }

    
    /**
     * Returns the path and name of the jar file containing the JDBC driver
     * 
     * @return String
     */
    public String getJarFile()
    {
        return jarFile_;
    }
    
    
    /**
     * Returns the JDBC password.
     * 
     * @return JDBC password
     */
    public String getPassword()
    {
        return password_;
    }

    
    /**
     * Returns the JDBC URL.
     * 
     * @return JDBC URL
     */
    public String getUrl()
    {
        return url_;
    }

    
    /**
     * Returns the JDBC username.
     * 
     * @return JDBC username
     */
    public String getUsername()
    {
        return username_;
    }

    
    /**
     * Sets the name of the database profile.
     * 
     * @param string  Profile name
     */
    public void setProfileName(String string)
    {
        profileName_ = string;
    }

    
    /**
     * Sets the JDBC driver. Must be a FQCN.
     * 
     * @param string JDBC driver
     */
    public void setDriver(String string)
    {
        driver_ = string;
    }

    
    /**
     * Sets the JDBC password.
     * 
     * @param string  JDBC password in clear text
     */
    public void setPassword(String string)
    {
        password_ = string;
    }

    
    /**
     * Sets the JDBC URL.
     * 
     * @param string JDBC URL
     */
    public void setUrl(String string)
    {
        url_ = string;
    }

    
    /**
     * Sets the JDBC username.
     * 
     * @param string JDBC username
     */
    public void setUsername(String string)
    {
        username_ = string;
    }

    
    /**
     * Sets the name of the jar file containing the JDBC driver
     * 
     * @param jarFile Path and name of jdbc driver jar file.
     */
    public void setJarFile(String jarFile)
    {
        jarFile_ = jarFile;
    }
    
    //--------------------------------------------------------------------------
    // Overrides java.lang.Object
    //--------------------------------------------------------------------------

    /**
     * Returns the name of the profile for the string representation. This is
     * what is used by default to render the text value of this profile when
     * added to a JComboBox.
     * 
     * @return Profile name
     */ 
    public String toString()
    {
        return getProfileName();
    }
}