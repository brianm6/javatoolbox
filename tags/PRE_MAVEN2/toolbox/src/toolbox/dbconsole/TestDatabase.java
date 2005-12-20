package toolbox.dbconsole;

/**
 * Connection information to a test database.
 */
public class TestDatabase {

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * Name that uniquely identifies this test database.
     */
    private String name;

    /**
     * JDBC driver class name. Must be a FQCN!
     */
    private String driver;

    /**
     * JDBC access url.
     */
    private String url;

    /**
     * Username for database access.
     */
    private String username;

    /**
     * Password to authenticate the user with.
     */
    private String password;

    /**
     * Name of the admin user associated with this database. This field is
     * used by the copydb command to query databae metainfo that is not
     * returned when using a regular user account.
     */
    private String adminUser;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a TestDatabase.
     *
     * @param name Friendly name of the db.
     * @param driver JDBC driver class.
     * @param url JDBC access url.
     * @param username Username.
     * @param password Password in clear text.
     * @param adminUser Administrator user id.
     */
    public TestDatabase(
        String name,
        String driver,
        String url,
        String username,
        String password,
        String adminUser) {

        setName(name);
        setDriver(driver);
        setUrl(url);
        setUsername(username);
        setPassword(password);
        setAdminUser(adminUser);
    }

    //--------------------------------------------------------------------------
    // Accessors/Mutators
    //--------------------------------------------------------------------------

    /**
     * Returns the name of this test database.
     *
     * @return String
     */
    public String getName() {
        return name;
    }


    /**
     * Returns the fully qualified class name of the JDBC driver.
     *
     * @return String
     */
    public String getDriver() {
        return driver;
    }


    /**
     * Returns the JDBC password.
     *
     * @return String
     */
    public String getPassword() {
        return password;
    }


    /**
     * Returns the JDBC connection URL.
     *
     * @return String
     */
    public String getUrl() {
        return url;
    }


    /**
     * Returns the JDBC username.
     *
     * @return String
     */
    public String getUsername() {
        return username;
    }


    /**
     * Sets the name of this database.
     *
     * @param string Database name.
     */
    public void setName(String string) {
        name = string;
    }


    /**
     * Sets the JDBC driver. Must be a FQCN.
     *
     * @param string JDBC driver.
     */
    public void setDriver(String string) {
        driver = string;
    }


    /**
     * Sets the JDBC password.
     *
     * @param string JDBC password in clear text.
     */
    public void setPassword(String string) {
        password = string;
    }


    /**
     * Sets the JDBC URL.
     *
     * @param string JDBC URL.
     */
    public void setUrl(String string) {
        url = string;
    }


    /**
     * Sets the JDBC username.
     *
     * @param string JDBC username.
     */
    public void setUsername(String string) {
        username = string;
    }


    /**
     * Returns the name of the database admin user id.
     *
     * @return String
     */
    public String getAdminUser() {
        return adminUser;
    }


    /**
     * Sets the name of the database admin user id.
     *
     * @param string Admin user id.
     */
    public void setAdminUser(String string) {
        adminUser = string;
    }

    //--------------------------------------------------------------------------
    // Overrides java.lang.Object
    //--------------------------------------------------------------------------

    /**
     * Returns the name of this database.
     *
     * @return String
     */
    public String toString() {
        return getName();
    }
}