package toolbox.jdbc;

import java.io.IOException;
import java.io.StringReader;

import toolbox.util.xml.XMLNode;
import toolbox.util.xml.XMLParser;


class DBProfile
{
    private static final String ELEMENT_PROFILE = "DBProfile";
    private static final String ATTR_DB         = "database";
    private static final String ATTR_DRIVER     = "driver";
    private static final String ATTR_URL        = "url";
    private static final String ATTR_USERNAME   = "username";
    private static final String ATTR_PASSWORD   = "password";
    
    private String database_;
    private String driver_;
    private String url_;
    private String username_;
    private String password_;
    
    DBProfile(String xml) throws IOException
    {
        XMLNode profile = new XMLParser().parseXML(new StringReader(xml));
        
        setDatabase(profile.getAttr(ATTR_DB));
        setDriver(profile.getAttr(ATTR_DRIVER));
        setUrl(profile.getAttr(ATTR_URL));
        setUsername(profile.getAttr(ATTR_USERNAME));
        setPassword(profile.getAttr(ATTR_PASSWORD));   
    }
    
    DBProfile(
        String database, 
        String driver, 
        String url, 
        String username,
        String password)
    {
        database_ = database;
        driver_   = driver;
        url_      = url;
        username_ = username;
        password_ = password;
    }
    
    /**
     * @return
     */
    public String getDatabase()
    {
        return database_;
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
    public void setDatabase(String string)
    {
        database_ = string;
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
    
    public String toString()
    {
        return getDatabase();
    }
    
    public String toXML()
    {
        return toDOM().toString();
    }
    
    public XMLNode toDOM()
    {
        // Tail element
        XMLNode profile = new XMLNode(ELEMENT_PROFILE);
        profile.addAttr(ATTR_DB, database_);
        profile.addAttr(ATTR_DRIVER, driver_);
        profile.addAttr(ATTR_URL, url_);
        profile.addAttr(ATTR_USERNAME, username_);
        profile.addAttr(ATTR_PASSWORD, password_);
        
        return profile;
    }
}