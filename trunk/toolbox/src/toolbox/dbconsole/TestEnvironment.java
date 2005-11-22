package toolbox.dbconsole;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import toolbox.util.JDBCSession;
import toolbox.util.service.Nameable;

/**
 * TestEnvironment contains a given test environment configuration and database
 * information.
 */
public class TestEnvironment implements Nameable {
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * Name that uniquely identifies this environment.
     */
    private String name;

    /**
     * Printstream
     */
    private PrintStream ps;
    
    /**
     * List of TestDatabases.
     */
    private List databases;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a TestEnvironmant.
     *
     * @param environmentName Friendly name of the environment.
     */
    public TestEnvironment(String environmentName) {
       setName(environmentName);
       databases = new ArrayList();
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------

    /**
     * Validates the test console can talk to the databases.
     */
    public void validate() {

        ps.println("Validating connection...");

        for (int i = 0; i < getDatabases().length; i++) {
            
            TestDatabase db = getDatabases()[i];
        
            try {
                validate(db);
                ps.println("Connected successfully to db " + db.getName());
            }
            catch (Exception e) {
                ps.println("Failed validation to db " + db.getName());
                e.printStackTrace(ps);
            }
        }
    }

    // -------------------------------------------------------------------------
    // Private 
    // -------------------------------------------------------------------------
    
    /**
     * Validates connection to a db.
     *
     * @param db Database.
     * @throws Exception on error.
     */
    private void validate(TestDatabase db) throws Exception {
        
        // Drop existing connection if one exists...
        
        try {
            disconnect(db);
        }
        catch (IllegalArgumentException iae) {
        }
        
        connect(db);
    }
    
    
    /**
     * Connects to a db.
     *
     * @param db Database.
     * @throws Exception
     */
    private void connect(TestDatabase db) throws Exception {

        JDBCSession.init(
            db.getName(),
            db.getDriver(),
            db.getUrl(),
            db.getUsername(),
            db.getPassword());
    }


    /**
     * Disconnects db.
     *
     * @throws Exception on error.
     */
    private void disconnect(TestDatabase db) throws Exception {
        JDBCSession.shutdown(db.getName());
    }

    // -------------------------------------------------------------------------
    // Nameable Interface
    // -------------------------------------------------------------------------
    
    /*
     * @see toolbox.util.service.Nameable#getName()
     */
    public String getName() {
        return this.name;
    }

    
    /*
     * @see toolbox.util.service.Nameable#setName(java.lang.String)
     */
    public void setName(String name) {
        this.name = name;
    }
    
    //--------------------------------------------------------------------------
    // Accessors/Mutators
    //--------------------------------------------------------------------------

    /**
     * Sets the printstream.
     *
     * @param ps PrintStream to set.
     */
    public void setPrintStream(PrintStream ps) {
        this.ps = ps;
    }

    
    /**
     * Returns an array of all the databases associated with this test
     * environment.
     *
     * @return TestDatabase[]
     */
    public TestDatabase[] getDatabases() {
        return (TestDatabase[]) databases.toArray(new TestDatabase[0]);
    }

    public void addDatabase(TestDatabase db) {
        databases.add(db);
    }

    public void removeDatabase(TestDatabase db) {
        databases.remove(db);
    }
    
    /**
     * Returns the database with the given name or null if the database does not
     * exist.
     *
     * @param name Name of the database.
     * @return TestDatabase
     */
    public TestDatabase getDatabase(String name) {

        TestDatabase[] dbs = getDatabases();

        for (int i = 0; i < dbs.length; i++) {
            if (dbs[i].getName().equals(name))
                return dbs[i];
        }

        return null;
    }

    //--------------------------------------------------------------------------
    // Overrides java.lang.Object
    //--------------------------------------------------------------------------

    /**
     * Returns the name of this test environment.
     *
     * @return String
     */
    public String toString() {
        return getName();
    }
}