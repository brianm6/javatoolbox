package toolbox.plugin.jdbc;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import nu.xom.Element;

import org.apache.commons.collections.MapUtils;
import org.apache.log4j.Logger;

import toolbox.util.DateTimeUtil;
import toolbox.util.JDBCSession;
import toolbox.util.JDBCUtil;
import toolbox.util.MemoryWatcher;
import toolbox.util.PreferencedUtil;
import toolbox.util.RandomUtil;
import toolbox.util.StringUtil;
import toolbox.util.XOMUtil;
import toolbox.util.service.ServiceException;
import toolbox.util.service.ServiceState;
import toolbox.util.service.ServiceTransition;
import toolbox.util.service.ServiceUtil;
import toolbox.util.service.Startable;
import toolbox.util.statemachine.StateMachine;
import toolbox.workspace.IPreferenced;
import toolbox.workspace.PreferencedException;

/**
 * This is a sample implementation of the Transaction Processing Performance
 * Council Benchmark B coded in Java and ANSI SQL2. This version is using one
 * connection per thread to parallellize server operations. Lifted from HSQLDB
 * with minor mods.
 */
public class DBBenchmark implements Startable, IPreferenced
{
    private static final Logger logger_ = Logger.getLogger(DBBenchmark.class);
    
    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------
    
    public static final int TELLER = 0;
    public static final int BRANCH = 1;
    public static final int ACCOUNT = 2;
    
    /**
     * Number formatter used during report generation.
     */
    private static final NumberFormat FMT = DecimalFormat.getNumberInstance();

    //--------------------------------------------------------------------------
    // IPreferenced Constants
    //--------------------------------------------------------------------------
    
    /**
     * XML node for dbbenchmark preferences.
     */
    private static final String NODE_DBBENCHMARK = "DBBenchmark";

    /**
     * Javabean properties that editable for this object. These properties
     * are persisted via the IPreferenced interface and edited by the 
     * DBBenchmarkView UI component.
     */
    public static final String[] SAVED_PROPS = new String[] {
        "verbose",
        "numClients",
        "numTxPerClient"
    };
    
    //--------------------------------------------------------------------------
    // Static
    //--------------------------------------------------------------------------
    
    // tpc bm b scaling rules
    
    /**
     * The tps scaling factor: here it is 1. 
     */
    private static int tps_ = 1;      
    
    /**
     * Number of branches in 1 tps db.       
     */
    private static int numBranches_ = 1;
    
    /** 
     * Number of tellers in  1 tps db.       
     */
    private static int numTellers_ = 10;
    
    /**
     * Number of accounts in 1 tps db.       
     */
    private static int numAccounts_ = 100000;
    
    /**
     * Table extension.
     */
    private static String tableExtension_ = "";
    
    /**
     * Create extension.
     */
    private static String createExtension_ = "";
    
    /**
     * Shutdown command.
     */
    private static String shutdownCommand_ = "";
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * Use transactions.
     */
    private boolean transactions_ = true;

    /**
     * Use prepared statements.
     */
    private boolean preparedStmt_ = false;

    /**
     * Default number of transactions per client.
     */
    private int numTxPerClient_ = 10;

    /**
     * Verbose flag.
     */
    private boolean verbose_ = false;

    /**
     * Number of clients (thread).
     */
    private int numClients_ = 10;
    
    /**
     * Number of failed transactions.
     */
    private int failedTx_ = 0;
    
    /**
     * Transaction count.
     */
    private int txCount_ = 0;
    
    /**
     * Start time.
     */
    private long startTime_ = 0;
    
    /**
     * Tracks memory usage while the test is running.
     */
    private MemoryWatcher memoryWatcher_;
    
    /**
     * Reports on the results are sent here.
     */
    private PrintWriter writer_;

    /**
     * Reference to the parent plugin.
     */
    private QueryPlugin plugin_;
    
    /**
     * Flag to initialize the database (create tables and populate data).
     */
    private boolean initDB_;
    
    /**
     * Flag to shutdown the database after the benchmarks have completed.
     */
    private boolean shutdownDB_;

    /**
     * State machine for this benchmarks lifecycle.
     */
    private StateMachine machine_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a DBBenchmark.
     * 
     * @param plugin Reference to the query plugin.
     */
    public DBBenchmark(QueryPlugin plugin)
    {
        this(plugin, true);
    }
    
    
    /**
     * Creates a DBBenchmark and sends output to System.out by default.
     *
     * @param plugin Reference to the query plugin.
     * @param initDB True to init the tables, false otherwise.
     */
    public DBBenchmark(QueryPlugin plugin, boolean initDB)
    {
        this(plugin, 
             initDB, 
             new PrintWriter(new OutputStreamWriter(System.out), true));
    }   

    
    /**
     * Creates a DBBenchmark.
     *
     * @param plugin Reference to the query plugin. 
     * @param initDB True to init the tables, false otherwise.
     * @param writer Destination of benchmark output.
     */
    public DBBenchmark(QueryPlugin plugin, boolean initDB, PrintWriter writer)
    {
        plugin_ = plugin;
        initDB_ = initDB;
        writer_ = writer;
        machine_ = ServiceUtil.createStateMachine(this);
    }

    //--------------------------------------------------------------------------
    // Startable Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.service.Startable#start()
     */
    public void start() throws ServiceException
    {
        machine_.checkTransition(ServiceTransition.START);
        
        try
        {
            if (initDB_)
                initDB();

            writer_.println("* Starting Benchmark Run *");

            // Run benchmarks for each combination of 
            // (transaction, prepared statements)
            
            runBenchmark(false, false);
            runBenchmark(true, false);
            runBenchmark(false, true);
            runBenchmark(true, true);
            machine_.transition(ServiceTransition.START);
        }
        catch (Exception ex)
        {
            writer_.println(ex.getMessage());
            ex.printStackTrace(writer_);
        }
        finally
        {
            if (shutdownDB_)
                shutdownDB();
        }
    }
    

    /**
     * @see toolbox.util.service.Startable#stop()
     */
    public void stop() throws ServiceException
    {
        machine_.checkTransition(ServiceTransition.STOP);
        
        try
        {
            JDBCSession.shutdown(plugin_.getCurrentProfile().toString());
            machine_.transition(ServiceTransition.STOP);
        }
        catch (SQLException e)
        {
            throw new ServiceException(e);
        }
    }
    
    
    /**
     * @see toolbox.util.service.Startable#isRunning()
     */
    public boolean isRunning()
    {
        return getState() == ServiceState.RUNNING;
    }
    
    //--------------------------------------------------------------------------
    // Service Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.service.Service#getState()
     */
    public ServiceState getState()
    {
        return (ServiceState) machine_.getState();
    }
    
    //--------------------------------------------------------------------------
    // IPreferenced Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.workspace.IPreferenced#applyPrefs(nu.xom.Element)
     */
    public void applyPrefs(Element prefs) throws PreferencedException
    {
        Element root = 
            XOMUtil.getFirstChildElement(
                prefs, 
                NODE_DBBENCHMARK,
                new Element(NODE_DBBENCHMARK));
     
        PreferencedUtil.readPreferences(this, root, SAVED_PROPS);
    }


    /**
     * @see toolbox.workspace.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs) throws PreferencedException
    {
        Element root = new Element(NODE_DBBENCHMARK);
        PreferencedUtil.writePreferences(this, root, SAVED_PROPS);
        XOMUtil.insertOrReplace(prefs, root);
    }
    
    
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Returns the numClients.
     * 
     * @return int
     */
    public int getNumClients()
    {
        return numClients_;
    }


    /**
     * Sets the numClients.
     * 
     * @param numClients The numClients to set.
     */
    public void setNumClients(int numClients)
    {
        numClients_ = numClients;
    }


    /**
     * Returns the numTxPerClient.
     * 
     * @return int
     */
    public int getNumTxPerClient()
    {
        return numTxPerClient_;
    }


    /**
     * Sets the numTxPerClient.
     * 
     * @param numTxPerClient The numTxPerClient to set.
     */
    public void setNumTxPerClient(int numTxPerClient)
    {
        numTxPerClient_ = numTxPerClient;
    }


    /**
     * Returns the verbose.
     * 
     * @return boolean
     */
    public boolean isVerbose()
    {
        return verbose_;
    }


    /**
     * Sets the verbose.
     * 
     * @param verbose The verbose to set.
     */
    public void setVerbose(boolean verbose)
    {
        verbose_ = verbose;
    }

    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------

    /**
     * Initializes the database by creating the necessary tables and data to
     * run the benchmark.
     * 
     * @throws Exception on error.
     */
    protected void initDB() throws Exception
    {
        writer_.println(
            "DB init started = " + DateTimeUtil.formatToSecond(new Date()));
        
        writer_.print("Initializing dataset...");
        
        createDatabase();
        
        writer_.println("Done.\n");
        
        writer_.println(
            "DB init complete = " + DateTimeUtil.formatToSecond(new Date()));
    }

    
    /**
     * Runs the benchmark.
     * 
     * @param useTransactions Use transactions in the benchmark.
     * @param usePreparedStatements Use prepared statements in the benchmark.
     * @throws InterruptedException on interruption.
     * @throws ServiceException on service error.
     * @throws SQLException on db error.
     */
    protected void runBenchmark(
        boolean useTransactions, 
        boolean usePreparedStatements) 
        throws InterruptedException, ServiceException, SQLException
    {
        memoryWatcher_ = new MemoryWatcher();
        memoryWatcher_.initialize(MapUtils.EMPTY_MAP);
        memoryWatcher_.start();
        
        try
        {
            transactions_ = useTransactions;
            preparedStmt_ = usePreparedStatements;
            Thread client;
            
            List clients = new ArrayList();            
            startTime_ = System.currentTimeMillis();
    
            for (int i = 0; i < numClients_; i++)
            {
                client = new ClientThread(numTxPerClient_);
                client.start();
                clients.add(client);
            }
            
            // Barrier to complete this test session
            
            Iterator it = clients.iterator();
    
            while (it.hasNext())
            {
                client = (Thread) it.next();
                client.join();
            }
    
            clients.clear();
        }
        finally 
        {
            memoryWatcher_.stop();
            memoryWatcher_.destroy();
        }
        
        generateReport();
        memoryWatcher_ = null;
    }

    
    /**
     * Generates a report containing statistics from the last executed 
     * benchmark. 
     *
     * @throws ServiceException on error. 
     */
    protected void generateReport() throws ServiceException
    {
        long endTime = System.currentTimeMillis();
        double completionTime = ((double) endTime - (double) startTime_) / 1000;

        writer_.println();
        writer_.print("Benchmark: ");
        
        if (preparedStmt_)
        {
            writer_.print("Prepared Statements ");
        }
        else
        {
            writer_.print("Direct Queries ");
        }

        if (transactions_)
        {
            writer_.print("+ Transactions");
        }
        else
        {
            writer_.print("+ Auto-commit");
        }

        writer_.println("\n" + StringUtil.BR);
        
        writer_.println(
            "Time        " + FMT.format(completionTime) + " sec(s)");

        writer_.println(
            "Passed      " +
            (txCount_ - failedTx_) + "/" + txCount_);

        double rate = (txCount_ - failedTx_) / completionTime;
        
        writer_.println(
            "Throughput  " + FMT.format(rate) + " transactions/sec.");
        
        writer_.println(
            "Min memory  " + FMT.format(memoryWatcher_.getMin()) +  " KB");
        
        writer_.println(
            "Max memory  " + FMT.format(memoryWatcher_.getMax()) + " KB");

        txCount_ = 0;
        failedTx_ = 0;
    }

    
    /**
     * Bump up the transaction count.
     */
    protected synchronized void incrementTransactionCount()
    {
        txCount_++;
    }


    /**
     * Bump up failed transaction count.
     */
    protected synchronized void incrementFailedTransactionCount()
    {
        failedTx_++;
    }

    
    /**
     * Creates the test database.
     * 
     * @throws Exception on error.
     */
    protected void createDatabase() throws Exception
    {
        Connection conn = connect();
        writer_.println(conn.getMetaData().getDatabaseProductName());
        transactions_ = true;

        if (transactions_)
        {
            try
            {
                conn.setAutoCommit(false);
                writer_.println("[In transaction mode]");
            }
            catch (SQLException se)
            {
                transactions_ = false;
            }
        }
        
        try
        {
            int accountsnb = 0;
            Statement stmt = conn.createStatement();
            String query;

            query = "SELECT count(*) FROM accounts";
            ResultSet rs = stmt.executeQuery(query);
            stmt.clearWarnings();

            while (rs.next())
                accountsnb = rs.getInt(1);

            if (transactions_)
                conn.commit();

            JDBCUtil.close(stmt);

            if (accountsnb == (numAccounts_ * tps_))
            {
                writer_.println("Tables already initialized");
                JDBCUtil.releaseConnection(conn);
                return;
            }
        }
        catch (Exception ex)
        {
            logger_.error(ex);
        }

        writer_.println("Dropping old tables if they exist...");
        
        try
        {
            Statement stmt = conn.createStatement();
            String query;

            query = "DROP TABLE history if exists";
            stmt.execute(query);
            stmt.clearWarnings();

            query = "DROP TABLE accounts if exists";
            stmt.execute(query);
            stmt.clearWarnings();

            query = "DROP TABLE tellers if exists";
            stmt.execute(query);
            stmt.clearWarnings();

            query = "DROP TABLE branches if exists";
            stmt.execute(query);
            stmt.clearWarnings();

            if (transactions_)
                conn.commit();

            JDBCUtil.close(stmt);
        }
        catch (Exception ex)
        {
            logger_.error(ex);
        }

        writer_.println("Creating tables...");
        
        try
        {
            Statement stmt = conn.createStatement();
            String query;

            if (tableExtension_.length() > 0)
                query = tableExtension_ + " branches (";
            else
                query = "CREATE TABLE branches (";

            query += "Bid         INTEGER NOT NULL PRIMARY KEY, ";
            query += "Bbalance    INTEGER,";
            query += "filler      CHAR(88))"; // pad to 100 bytes

            if (createExtension_.length() > 0)
                query += createExtension_;

            stmt.execute(query);
            stmt.clearWarnings();

            if (tableExtension_.length() > 0)
                query = tableExtension_ + " tellers (";
            else
                query = "CREATE TABLE tellers (";

            query += "Tid         INTEGER NOT NULL PRIMARY KEY,";
            query += "Bid         INTEGER,";
            query += "Tbalance    INTEGER,";
            query += "filler      CHAR(84))"; // pad to 100 bytes 

            if (createExtension_.length() > 0)
                query += createExtension_;

            stmt.execute(query);
            stmt.clearWarnings();

            if (tableExtension_.length() > 0)
                query = tableExtension_ + " accounts (";
            else
                query = "CREATE TABLE accounts (";

            query += "Aid         INTEGER NOT NULL PRIMARY KEY, ";
            query += "Bid         INTEGER, ";
            query += "Abalance    INTEGER, ";
            query += "filler      CHAR(84))"; // pad to 100 bytes 

            if (createExtension_.length() > 0)
                query += createExtension_;

            stmt.execute(query);
            stmt.clearWarnings();

            if (tableExtension_.length() > 0)
                query = tableExtension_ + " history (";
            else
                query = "CREATE TABLE history (";

            query += "Tid         INTEGER, ";
            query += "Bid         INTEGER, ";
            query += "Aid         INTEGER, ";
            query += "delta       INTEGER, ";
            query += "tstime        TIMESTAMP, ";
            query += "filler      CHAR(22))"; // pad to 50 bytes 

            if (createExtension_.length() > 0)
                query += createExtension_;

            stmt.execute(query);
            stmt.clearWarnings();

            if (transactions_)
                conn.commit();

            JDBCUtil.close(stmt);
        }
        catch (Exception ex2)
        {
            logger_.error(ex2);
        }

        writer_.println("Deleting table contents in case Drop didn't work...");
        
        try
        {
            Statement stmt = conn.createStatement();
            String query;

            query = "DELETE FROM history";
            stmt.execute(query);
            stmt.clearWarnings();

            query = "DELETE FROM accounts";
            stmt.execute(query);
            stmt.clearWarnings();

            query = "DELETE FROM tellers";
            stmt.execute(query);
            stmt.clearWarnings();

            query = "DELETE FROM branches";
            stmt.execute(query);
            stmt.clearWarnings();

            if (transactions_)
                conn.commit();

            /*
             * prime database using TPC BM B scaling rules. Note that for each
             * branch and teller: 
             * branch_id = teller_id/ntellers 
             * branch_id = account_id/naccounts
             */
            PreparedStatement pstmt = null;

            preparedStmt_ = true;

            if (preparedStmt_)
            {
                try
                {
                    query = "INSERT INTO branches(Bid,Bbalance) VALUES (?,0)";
                    pstmt = conn.prepareStatement(query);
                    writer_.println("[Using prepared statements]");
                }
                catch (SQLException se)
                {
                    pstmt = null;
                    preparedStmt_ = false;
                }
            }

            writer_.println("Populating branches table...");
            
            for (int i = 0; i < numBranches_ * tps_; i++)
            {
                if (preparedStmt_)
                {
                    pstmt.setInt(1, i);
                    pstmt.executeUpdate();
                    pstmt.clearWarnings();
                }
                else
                {
                    query = "INSERT INTO branches (Bid,Bbalance) " +
                            "VALUES (" + i + ",0)";

                    stmt.executeUpdate(query);
                }

                if ((i % 100 == 0) && (transactions_))
                    conn.commit();
            }

            if (preparedStmt_)
                JDBCUtil.close(stmt);

            if (transactions_)
                conn.commit();

            if (preparedStmt_)
            {
                query = "INSERT INTO tellers(Tid,Bid,Tbalance) VALUES (?,?,0)";
                pstmt = conn.prepareStatement(query);
            }

            writer_.println("Populating tellers table...");
            
            for (int i = 0; i < numTellers_ * tps_; i++)
            {
                if (preparedStmt_)
                {
                    pstmt.setInt(1, i);
                    pstmt.setInt(2, i / numTellers_);
                    pstmt.executeUpdate();
                    pstmt.clearWarnings();
                }
                else
                {
                    query = "INSERT INTO tellers(Tid,Bid,Tbalance) VALUES ("
                        + i + "," + i / numTellers_ + ",0)";

                    stmt.executeUpdate(query);
                }

                if ((i % 100 == 0) && (transactions_))
                    conn.commit();
            }

            if (preparedStmt_)
                JDBCUtil.close(pstmt);

            if (transactions_)
                conn.commit();

            if (preparedStmt_)
            {
                query = "INSERT INTO accounts(Aid,Bid,Abalance) VALUES (?,?,0)";
                pstmt = conn.prepareStatement(query);
            }

            writer_.println("Populating accounts table...");
            
            for (int i = 0; i < numAccounts_ * tps_; i++)
            {
                if (preparedStmt_)
                {
                    pstmt.setInt(1, i);
                    pstmt.setInt(2, i / numAccounts_);
                    pstmt.executeUpdate();
                    pstmt.clearWarnings();
                }
                else
                {
                    query = "INSERT INTO accounts(Aid,Bid,Abalance) VALUES ("
                        + i + "," + i / numAccounts_ + ",0)";

                    stmt.executeUpdate(query);
                }

                if ((i % 10000 == 0) && (transactions_))
                    conn.commit();

                if ((i > 0) && ((i % 10000) == 0))
                    writer_.println("\t" + i + "\t records inserted");
            }

            if (preparedStmt_)
                JDBCUtil.close(pstmt);

            if (transactions_)
                conn.commit();

            writer_.println(
                "\t" + (numAccounts_ * tps_) + "\t records inserted");
            
            JDBCUtil.close(stmt);
        }
        catch (Exception ex)
        {
            writer_.println(ex.getMessage());
            ex.printStackTrace(writer_);
        }

        JDBCUtil.releaseConnection(conn);
    }

    
    /**
     * Shuts down the database.
     */
    protected void shutdownDB()
    {
        try
        {
            if (shutdownCommand_.length() > 0)
            {
                Connection conn = connect();
                Statement stmt = conn.createStatement();
                stmt.execute(shutdownCommand_);
                JDBCUtil.close(stmt);
                JDBCUtil.releaseConnection(conn);
            }
            else
                writer_.println("No shutdown command specified.");
        }
        catch (Exception ex2)
        {
            logger_.error(ex2);
        }
    }

    
    /**
     * Returns a random ID.
     * 
     * @param type ACCOUNT, TELLER, or BRANCH
     * @return int
     */
    protected int getRandomID(int type)
    {
        int min = 0;
        int max = 0;
        int num = numAccounts_;

        switch (type)
        {
            case TELLER:
                min += numBranches_;
                num = numTellers_;

            // FALLTHROUGH
            case BRANCH:
                if (type == BRANCH)
                {
                    num = numBranches_;
                }

                min += numAccounts_;

            // FALLTHROUGH
            case ACCOUNT:
                max = min + num - 1;
        }

        return RandomUtil.nextInt(min, max);
    }

    
    /**
     * Gets a database connection.
     * 
     * @return Connection
     * @throws SQLException on db error.
     */
    protected Connection connect() throws SQLException
    {
        String name = plugin_.getCurrentProfile().toString();
        
        try
        {
            JDBCSession.getSession(name);
        }
        catch (IllegalArgumentException iae)
        {
            try
            {
                JDBCSession.init(
                    name, 
                    plugin_.getCurrentProfile().getDriver(), 
                    plugin_.getCurrentProfile().getUrl(), 
                    plugin_.getCurrentProfile().getUsername(), 
                    plugin_.getCurrentProfile().getPassword());
            }
            catch (Exception e)
            {
                logger_.error(e);
            }
        }
    
        return JDBCSession.getConnection(name);
    }
    
    //--------------------------------------------------------------------------
    // ClientThread
    //--------------------------------------------------------------------------
    
    class ClientThread extends Thread
    {
        //----------------------------------------------------------------------
        // Fields
        //----------------------------------------------------------------------
        
        private int numTx_ = 0;
        private Connection conn_;
        private PreparedStatement pstmt1_ = null;
        private PreparedStatement pstmt2_ = null;
        private PreparedStatement pstmt3_ = null;
        private PreparedStatement pstmt4_ = null;
        private PreparedStatement pstmt5_ = null;

        //----------------------------------------------------------------------
        // Constructors
        //----------------------------------------------------------------------
        
        /**
         * Creates a ClientThread.
         * 
         * @param numOfTx Number of transactions.
         * @throws SQLException on connection error.
         */
        public ClientThread(int numOfTx) throws SQLException
        {
            numTx_ = numOfTx;
            conn_ = connect();

            if (conn_ == null)
                return;

            try
            {
                if (transactions_)
                    conn_.setAutoCommit(false);

                if (preparedStmt_)
                {
                    String Query;

                    Query = "UPDATE accounts ";
                    Query += "SET     Abalance = Abalance + ? ";
                    Query += "WHERE   Aid = ?";
                    pstmt1_ = conn_.prepareStatement(Query);
                    
                    Query = "SELECT Abalance ";
                    Query += "FROM   accounts ";
                    Query += "WHERE  Aid = ?";
                    pstmt2_ = conn_.prepareStatement(Query);
                    
                    Query = "UPDATE tellers ";
                    Query += "SET    Tbalance = Tbalance + ? ";
                    Query += "WHERE  Tid = ?";
                    pstmt3_ = conn_.prepareStatement(Query);
                    
                    Query = "UPDATE branches ";
                    Query += "SET    Bbalance = Bbalance + ? ";
                    Query += "WHERE  Bid = ?";
                    pstmt4_ = conn_.prepareStatement(Query);
                    
                    Query = "INSERT INTO history(Tid, Bid, Aid, delta) ";
                    Query += "VALUES (?,?,?,?)";
                    pstmt5_ = conn_.prepareStatement(Query);
                }
            }
            catch (Exception ex)
            {
                writer_.println(ex.getMessage());
                ex.printStackTrace(writer_);
            }
        }

        //----------------------------------------------------------------------
        // Runnable Interface
        //----------------------------------------------------------------------
        
        /**
         * @see java.lang.Runnable#run()
         */
        public void run()
        {
            while (numTx_-- > 0)
            {
                int account = getRandomID(ACCOUNT);
                int branch = getRandomID(BRANCH);
                int teller = getRandomID(TELLER);
                int delta = RandomUtil.nextInt(0, 1000);

                doOne(branch, teller, account, delta);
                incrementTransactionCount();
            }

            if (preparedStmt_)
            {
                try
                {
                    JDBCUtil.close(pstmt1_);
                    JDBCUtil.close(pstmt2_);
                    JDBCUtil.close(pstmt3_);
                    JDBCUtil.close(pstmt4_);
                    JDBCUtil.close(pstmt5_);
                }
                catch (Exception ex)
                {
                    writer_.println(ex.getMessage());
                    ex.printStackTrace(writer_);
                }
            }

            JDBCUtil.releaseConnection(conn_);
        }

        //----------------------------------------------------------------------
        // Protected
        //----------------------------------------------------------------------
        
        /**
         * Executes a single TPC BM B transaction.
         *
         * @param bid B id.
         * @param tid t id.
         * @param aid A id.
         * @param delta Delta. 
         * @return int
         */
        protected int doOne(int bid, int tid, int aid, int delta)
        {
            int balance = 0;

            if (conn_ == null)
            {
                incrementFailedTransactionCount();
                return 0;
            }

            try
            {
                if (preparedStmt_)
                {
                    pstmt1_.setInt(1, delta);
                    pstmt1_.setInt(2, aid);
                    pstmt1_.executeUpdate();
                    pstmt1_.clearWarnings();
                    pstmt2_.setInt(1, aid);

                    ResultSet rs = pstmt2_.executeQuery();

                    pstmt2_.clearWarnings();

                    while (rs.next())
                        balance = rs.getInt(1);

                    pstmt3_.setInt(1, delta);
                    pstmt3_.setInt(2, tid);
                    pstmt3_.executeUpdate();
                    pstmt3_.clearWarnings();
                    pstmt4_.setInt(1, delta);
                    pstmt4_.setInt(2, bid);
                    pstmt4_.executeUpdate();
                    pstmt4_.clearWarnings();
                    pstmt5_.setInt(1, tid);
                    pstmt5_.setInt(2, bid);
                    pstmt5_.setInt(3, aid);
                    pstmt5_.setInt(4, delta);
                    pstmt5_.executeUpdate();
                    pstmt5_.clearWarnings();
                }
                else
                {
                    Statement stmt = conn_.createStatement();
                    String query = "UPDATE accounts ";

                    query += "SET     Abalance = Abalance + " + delta + " ";
                    query += "WHERE   Aid = " + aid;

                    stmt.executeUpdate(query);
                    stmt.clearWarnings();

                    query = "SELECT Abalance ";
                    query += "FROM   accounts ";
                    query += "WHERE  Aid = " + aid;

                    ResultSet rs = stmt.executeQuery(query);

                    stmt.clearWarnings();

                    while (rs.next())
                        balance = rs.getInt(1);

                    query = "UPDATE tellers ";
                    query += "SET    Tbalance = Tbalance + " + delta + " ";
                    query += "WHERE  Tid = " + tid;

                    stmt.executeUpdate(query);
                    stmt.clearWarnings();

                    query = "UPDATE branches ";
                    query += "SET    Bbalance = Bbalance + " + delta + " ";
                    query += "WHERE  Bid = " + bid;

                    stmt.executeUpdate(query);
                    stmt.clearWarnings();

                    query = "INSERT INTO history(Tid, Bid, Aid, delta) ";
                    query += "VALUES (";
                    query += tid + ",";
                    query += bid + ",";
                    query += aid + ",";
                    query += delta + ")";

                    stmt.executeUpdate(query);
                    stmt.clearWarnings();
                    JDBCUtil.close(stmt); 
                }

                if (transactions_)
                    conn_.commit();

                return balance;
            }
            catch (Exception ex)
            {
                if (verbose_)
                {
                    writer_.println("Transaction failed: " + ex.getMessage());
                    ex.printStackTrace(writer_);
                }

                incrementFailedTransactionCount();

                if (transactions_)
                {
                    try
                    {
                        conn_.rollback();
                    }
                    catch (SQLException E1)
                    {
                    }
                }
            }

            return 0;
        }
    }
    
}

/* Copyright (c) 2001-2002, The HSQL Development Group
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of the HSQL Development Group nor the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL HSQL DEVELOPMENT GROUP, HSQLDB.ORG, 
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */



/**
 * Creates a 1-tps database (1 branch, 10 tellers) and runs one TPC BM B 
 * transaction. Example command line:
 * <pre>
 * java DBBench -driver org.hsqldb.jdbcDriver -url jdbc:hsqldb:/hsql/test33 -user sa -clients 20
 * </pre>
 * 
 * @param args See above. 
 */
//public static void main(String[] args) 
//{
//    //DriverManager.setLogWriter(new PrintWriter(new OutputStreamWriter(System.out)));
//    
//    String  driver   = "";
//    String  url      = "";
//    String  user     = "";
//    String  password = "";
//    boolean initDB   = false;
//
//    for (int i = 0; i < args.length; i++)
//    {
//        if (args[i].equals("-clients"))
//        {
//            if (i + 1 < args.length)
//            {
//                i++;
//                numClients_ = Integer.parseInt(args[i]);
//            }
//        }
//        else if (args[i].equals("-driver"))
//        {
//            if (i + 1 < args.length)
//            {
//                i++;
//                driver = args[i];
//
//                if (driver.equals(
//                    "org.enhydra.instantdb.jdbc.idbDriver"))
//                    shutdownCommand_ = "SHUTDOWN";
//
//                if (driver.equals(
//                    "com.borland.datastore.jdbc.DataStoreDriver"))
//                {
//                }
//
//                if (driver.equals("com.mckoi.JDBCDriver"))
//                    shutdownCommand_ = "SHUTDOWN";
//
//                if (driver.equals("org.hsqldb.jdbcDriver"))
//                {
//                    tableExtension_ = "CREATE CACHED TABLE ";
//                    shutdownCommand_ = "SHUTDOWN COMPACT";
//                }
//            }
//        }
//        else if (args[i].equals("-url"))
//        {
//            if (i + 1 < args.length)
//            {
//                i++;
//                url = args[i];
//            }
//        }
//        else if (args[i].equals("-user"))
//        {
//            if (i + 1 < args.length)
//            {
//                i++;
//                user = args[i];
//            }
//        }
//        else if (args[i].equals("-tabfile"))
//        {
//            if (i + 1 < args.length)
//            {
//                i++;
//
//                try
//                {
//                    tabFile_ = new PrintStream(
//                        new FileOutputStream(args[i]));
//                }
//                catch (Exception e)
//                {
//                    tabFile_ = new PrintStream(new NullOutputStream());
//                }
//            }
//        }
//        else if (args[i].equals("-password"))
//        {
//            if (i + 1 < args.length)
//            {
//                i++;
//                password = args[i];
//            }
//        }
//        else if (args[i].equals("-tpc"))
//        {
//            if (i + 1 < args.length)
//            {
//                i++;
//                numTxPerClient_ = Integer.parseInt(args[i]);
//            }
//        }
//        else if (args[i].equals("-init"))
//            initDB = true;
//        else if (args[i].equals("-tps"))
//        {
//            if (i + 1 < args.length)
//            {
//                i++;
//                tps_ = Integer.parseInt(args[i]);
//            }
//        }
//        else if (args[i].equals("-v"))
//            verbose_ = true;
//    }
//
//    if (StringUtils.isBlank(driver) || 
//        StringUtils.isBlank(url)) 
//    {
//        System.out.println(
//            "Usage: java DBBenchmark -driver [driver_class_name] " +
//            "-url [url_to_db] -user [username] -password [password] [-v] " +
//            "[-init] [-tpc n] [-clients]");
//        
//        System.out.println();
//        System.out.println("-v       Verbose error messages");
//        System.out.println("-init    Initialize the tables");
//        System.out.println("-tpc     Transactions per client");
//        System.out.println("-clients Number of simultaneous clients");
//        System.exit(-1);
//    }
//
//    System.out.println(
//        "*********************************************************");
//    System.out.println(
//        "* DBBenchmark v1.1                                        *");
//    System.out.println(
//        "*********************************************************");
//    System.out.println();
//    System.out.println("Driver      : " + driver);
//    System.out.println("URL         : " + url);
//    System.out.println();
//    System.out.println("Scale factor: " + tps_);
//    System.out.println("# Clients   : " + numClients_);
//    System.out.println("# Txn/Client: " + numTxPerClient_);
//    System.out.println();
//
//    try
//    {
//        Class.forName(driver);
//
//        DBBenchmark bench = 
//            new DBBenchmark(url, user, password, initDB);
//    }
//    catch (Exception ex)
//    {
//        System.out.println(ex.getMessage());
//        ex.printStackTrace();
//    }
//}

