package toolbox.plugin.jdbc;

import java.io.Serializable;

import nu.xom.Attribute;
import nu.xom.Element;

import org.apache.commons.codec.binary.Base64;

import toolbox.util.PreferencedUtil;
import toolbox.util.XOMUtil;
import toolbox.workspace.IPreferenced;

/**
 * DBProfile contains all necessary attributes to locate, authenticate, and 
 * converse with a database via JDBC.
 */
public class DBProfile implements IPreferenced, Serializable
{
    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------
    
    /**
     * XML element name for a DBProfile.
     */
    private static final String NODE_DBPROFILE = "DBProfile";
    
    //--------------------------------------------------------------------------
    // Javabean Property Names
    //--------------------------------------------------------------------------
    
    /**
     * Property name for the profile name.
     */
    private static final String PROP_PROFILE_NAME = "profileName";

    /**
     * Property name for the JDBC driver jar file. This field is optional
     * if the jar file is already on the classpath. 
     */
    private static final String PROP_JARFILE = "jarFile";

    /**
     * Property name for the JDBC driver. 
     */
    private static final String PROP_DRIVER = "driver";
    
    /**
     * Property name for the JDBC url. 
     */
    private static final String PROP_URL = "url";
    
    /**
     * Property name for the JDBC user.
     */
    private static final String PROP_USERNAME = "username";
    
    /**
     * Property name for the JDBC authentication password (clear text).
     */
    private static final String PROP_PASSWORD = "password";

    /**
     * Properties handled by IPreferenced interface.
     */
    private static final String[] SAVED_PROPS = {
        PROP_PROFILE_NAME,
        PROP_JARFILE,
        PROP_DRIVER,
        PROP_URL,
        PROP_USERNAME,
        // PROP_PASSWORD,  must be encoded prior to save
    };
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Profile name is a user friendly term used to uniquely identify a profile.
     */
    private String profileName_;
    
    /**
     * Absolute path of the JDBC driver's jar file. This is optional if the jar 
     * file is already on the classpath.
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
     * Creates an empty DBProfile.
     */
    public DBProfile() 
    {
    }
    
    
    /**
     * Creates a fully populated DBProfile.
     *
     * @param profileName Friendly name of the profile.
     * @param jarFile JDBC driver jar file.
     * @param driver JDBC driver class.
     * @param url JDBC access url.
     * @param username Username.
     * @param password Password in clear text.       
     */
    public DBProfile(
        String profileName,
        String jarFile,
        String driver, 
        String url, 
        String username,
        String password)
    {
        setProfileName(profileName);
        setJarFile(jarFile);
        setDriver(driver);
        setUrl(url);
        setUsername(username);
        setPassword(password);
    }

    //--------------------------------------------------------------------------
    // IPreferenced Interface
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.workspace.IPreferenced#applyPrefs(nu.xom.Element)
     */
    public void applyPrefs(Element prefs) throws Exception
    {
        Element root = XOMUtil.getFirstChildElement(
            prefs, NODE_DBPROFILE, new Element(NODE_DBPROFILE));
        
        PreferencedUtil.readPreferences(this, root, SAVED_PROPS);
        String scrambled = XOMUtil.getStringAttribute(root, PROP_PASSWORD, "");
        setPassword(new String(Base64.decodeBase64(scrambled.getBytes())));
    }


    /**
     * @see toolbox.workspace.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs) throws Exception
    {
        Element root = new Element(NODE_DBPROFILE);
        PreferencedUtil.writePreferences(this, root, SAVED_PROPS);

        // Password has to be done explicitly because of encoding
        root.addAttribute(new Attribute(PROP_PASSWORD, 
            new String(Base64.encodeBase64(password_.getBytes()))));
        
        XOMUtil.insertOrReplace(prefs, root);
    }
    
    //--------------------------------------------------------------------------
    // Accessors/Mutators
    //--------------------------------------------------------------------------
        
    /**
     * Returns the name of the database profile.
     * 
     * @return Profile name.
     */
    public String getProfileName()
    {
        return profileName_;
    }

    
    /**
     * Returns the JDBC driver.
     * 
     * @return JDBC driver.
     */
    public String getDriver()
    {
        return driver_;
    }

    
    /**
     * Returns the path and name of the jar file containing the JDBC driver.
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
     * @return JDBC password.
     */
    public String getPassword()
    {
        return password_;
    }

    
    /**
     * Returns the JDBC URL.
     * 
     * @return JDBC URL.
     */
    public String getUrl()
    {
        return url_;
    }

    
    /**
     * Returns the JDBC username.
     * 
     * @return JDBC username.
     */
    public String getUsername()
    {
        return username_;
    }

    
    /**
     * Sets the name of the database profile.
     * 
     * @param string Profile name.
     */
    public void setProfileName(String string)
    {
        profileName_ = string;
    }

    
    /**
     * Sets the JDBC driver. Must be a FQCN.
     * 
     * @param string JDBC driver.
     */
    public void setDriver(String string)
    {
        driver_ = string;
    }

    
    /**
     * Sets the JDBC password.
     * 
     * @param string JDBC password in clear text.
     */
    public void setPassword(String string)
    {
        password_ = string;
    }

    
    /**
     * Sets the JDBC URL.
     * 
     * @param string JDBC URL.
     */
    public void setUrl(String string)
    {
        url_ = string;
    }

    
    /**
     * Sets the JDBC username.
     * 
     * @param string JDBC username.
     */
    public void setUsername(String string)
    {
        username_ = string;
    }

    
    /**
     * Sets the name of the jar file containing the JDBC driver.
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
     * @return Profile name.
     */ 
    public String toString()
    {
        return getProfileName();
    }
}