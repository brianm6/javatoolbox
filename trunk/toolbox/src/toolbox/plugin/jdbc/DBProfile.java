package toolbox.jdbc;

import java.io.IOException;
import java.io.StringReader;

import toolbox.util.xml.XMLNode;
import toolbox.util.xml.XMLParser;

/**
 * Database connection profile for JDBC
 */
public class DBProfile
{
    /**
     * XML element name for a DBProfile
     */
    private static final String ELEMENT_PROFILE = "DBProfile";
    
    /**
     * XML attribute name for the profile name
     */
    private static final String ATTR_PROFILE_NAME = "profilename";
    
    /**
     * XML attribute name for the JDBC driver 
     */
    private static final String ATTR_DRIVER = "driver";
    
    /**
     * XML attribute name for the JDBC url 
     */
    private static final String ATTR_URL = "url";
    
    /**
     * XML attribute name for the JDBC user 
     */
    private static final String ATTR_USERNAME = "username";
    
    /**
     * XML attribute name for the JDBC authentication password (clear text)
     */
    private static final String ATTR_PASSWORD = "password";
    
    /**
     * Profile name is a user friendly term used to uniquely identify a profile 
     */
    private String profileName_;
    
    /**
     * JDBC driver class name. Must be a FQCN!
     */
    private String driver_;
    
    /**
     * JDBC access url
     */
    private String url_;
    
    /**
     * Username for database access
     */
    private String username_;
    
    /**
     * Password to authenticate the user with
     */
    private String password_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a DBProfile from XML
     *
     * @param  xml  String containing a valid XML persistence of DBProfile
     * @throws IOException on I/O error 
     */
    public DBProfile(String xml) throws IOException
    {
        XMLNode profile = new XMLParser().parseXML(new StringReader(xml));
        setProfileName(profile.getAttr(ATTR_PROFILE_NAME));
        setDriver(profile.getAttr(ATTR_DRIVER));
        setUrl(profile.getAttr(ATTR_URL));
        setUsername(profile.getAttr(ATTR_USERNAME));
        setPassword(profile.getAttr(ATTR_PASSWORD));   
    }

    /**
     * Creates a DBProfile
     *
     * @param  profileName Friendly name of the profile
     * @param  driver      JDBC driver class
     * @param  url         JDBC access url
     * @param  username    Username
     * @param  password    Password in clear text       
     */
    public DBProfile(
        String  profileName, 
        String driver, 
        String url, 
        String username,
        String password)
    {
        profileName_  = profileName;
        driver_   = driver;
        url_      = url;
        username_ = username;
        password_ = password;
    }
    
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------

    /**
     * Returns an XML representation of the data contained in this profile.
     * 
     * @return  XML string
     */
    public String toXML()
    {
        return toDOM().toString();
    }

    /**
     * Returns a DOM representation of the data contained in this profile.
     * 
     * @return  DOM tree
     */    
    public XMLNode toDOM()
    {
        // Tail element
        XMLNode profile = new XMLNode(ELEMENT_PROFILE);
        profile.addAttr(ATTR_PROFILE_NAME, profileName_);
        profile.addAttr(ATTR_DRIVER, driver_);
        profile.addAttr(ATTR_URL, url_);
        profile.addAttr(ATTR_USERNAME, username_);
        profile.addAttr(ATTR_PASSWORD, password_);
        
        return profile;
    }

    //--------------------------------------------------------------------------
    // Accessors/Mutators
    //--------------------------------------------------------------------------
        
    /**
     * @return
     */
    public String getProfileName()
    {
        return profileName_;
    }

    /**
     * @return
     */
    public String getDriver()
    {
        return driver_;
    }

    /**
     * @return
     */
    public String getPassword()
    {
        return password_;
    }

    /**
     * @return
     */
    public String getUrl()
    {
        return url_;
    }

    /**
     * @return
     */
    public String getUsername()
    {
        return username_;
    }

    /**
     * @param string
     */
    public void setProfileName(String string)
    {
        profileName_ = string;
    }

    /**
     * @param string
     */
    public void setDriver(String string)
    {
        driver_ = string;
    }

    /**
     * @param string
     */
    public void setPassword(String string)
    {
        password_ = string;
    }

    /**
     * @param string
     */
    public void setUrl(String string)
    {
        url_ = string;
    }

    /**
     * @param string
     */
    public void setUsername(String string)
    {
        username_ = string;
    }
    
    //--------------------------------------------------------------------------
    // Overrides java.lang.Object
    //--------------------------------------------------------------------------

    /**
     * Returns the name of the profile for the string representation. This is
     * what is used by default to render the text value of this profile when
     * added to a JComboBox.
     * 
     * @param  Profile name
     */ 
    public String toString()
    {
        return getProfileName();
    }
}