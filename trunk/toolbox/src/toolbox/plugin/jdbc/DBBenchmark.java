package toolbox.plugin.jdbc;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.Vector;

import org.apache.log4j.Logger;

/**
 * This is a sample implementation of the Transaction Processing Performance
 * Council Benchmark B coded in Java and ANSI SQL2. This version is using one
 * connection per thread to parallellize server operations.
 */
class DBBenchmark 
{
    private static final Logger logger_ = Logger.getLogger(DBBenchmark.class);
    
    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------
    
    public final static int TELLER              = 0;
    public final static int BRANCH              = 1;
    public final static int ACCOUNT             = 2;
    
    //--------------------------------------------------------------------------
    // Static
    //--------------------------------------------------------------------------
    
    // tpc bm b scaling rules
    public static int tps_         = 1;      /* the tps scaling factor: here it is 1 */
    public static int numBranches_ = 1;      /* number of branches in 1 tps db       */
    public static int numTellers_  = 10;     /* number of tellers in  1 tps db       */
    public static int numAccounts_ = 100000; /* number of accounts in 1 tps db       */
    public static int numHistory_  = 864000; /* number of history recs in 1 tps db   */
    
    static boolean          transactions_    = true;
    static boolean          preparedStmt_    = false;
    static String           tableExtension_  = "";
    static String           createExtension_ = "";
    static String           shutdownCommand_ = "";
    static PrintStream      tabFile_         = null;
    static boolean          verbose_         = false;
    static int              numClients_      = 10;
    static int              numTxPerClient_  = 10;
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    int                     failedTx_        = 0;
    int                     txCount_         = 0;
    long                    startTime_       = 0;
    MemoryWatcherThread     memoryWatcher_;
    private PrintWriter writer_;

    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    /**
     *  main program,    creates a 1-tps database:  i.e. 1 branch, 10 tellers,...
     *                    runs one TPC BM B transaction
     * example command line:
     * 
     * @param args -driver org.hsqldb.jdbcDriver -url jdbc:hsqldb:/hsql/test33 -user sa -clients 20
     */
    public static void main(String[] args) {

        //DriverManager.setLogWriter(new PrintWriter(new OutputStreamWriter(System.out)));
        
        String  driverName         = "";
        String  dbUrl              = "";
        String  dbUser             = "";
        String  dbPassword         = "";
        boolean initializeDataset = false;

        for (int i = 0; i < args.length; i++)
        {
            if (args[i].equals("-clients"))
            {
                if (i + 1 < args.length)
                {
                    i++;
                    numClients_ = Integer.parseInt(args[i]);
                }
            }
            else if (args[i].equals("-driver"))
            {
                if (i + 1 < args.length)
                {
                    i++;
                    driverName = args[i];

                    if (driverName.equals(
                        "org.enhydra.instantdb.jdbc.idbDriver"))
                        shutdownCommand_ = "SHUTDOWN";

                    if (driverName.equals(
                        "com.borland.datastore.jdbc.DataStoreDriver"))
                    {
                    }

                    if (driverName.equals("com.mckoi.JDBCDriver"))
                        shutdownCommand_ = "SHUTDOWN";

                    if (driverName.equals("org.hsqldb.jdbcDriver"))
                    {
                        tableExtension_ = "CREATE CACHED TABLE ";
                        shutdownCommand_ = "SHUTDOWN COMPACT";
                    }
                }
            }
            else if (args[i].equals("-url"))
            {
                if (i + 1 < args.length)
                {
                    i++;
                    dbUrl = args[i];
                }
            }
            else if (args[i].equals("-user"))
            {
                if (i + 1 < args.length)
                {
                    i++;
                    dbUser = args[i];
                }
            }
            else if (args[i].equals("-tabfile"))
            {
                if (i + 1 < args.length)
                {
                    i++;

                    try
                    {
                        FileOutputStream File = new FileOutputStream(args[i]);
                        tabFile_ = new PrintStream(File);
                    }
                    catch (Exception e)
                    {
                        tabFile_ = null;
                    }
                }
            }
            else if (args[i].equals("-password"))
            {
                if (i + 1 < args.length)
                {
                    i++;
                    dbPassword = args[i];
                }
            }
            else if (args[i].equals("-tpc"))
            {
                if (i + 1 < args.length)
                {
                    i++;
                    numTxPerClient_ = Integer.parseInt(args[i]);
                }
            }
            else if (args[i].equals("-init"))
                initializeDataset = true;
            else if (args[i].equals("-tps"))
            {
                if (i + 1 < args.length)
                {
                    i++;
                    tps_ = Integer.parseInt(args[i]);
                }
            }
            else if (args[i].equals("-v"))
                verbose_ = true;
        }

        if (driverName.length() == 0 || dbUrl.length() == 0) {
            System.out.println(
                "usage: java DBBenchmark -driver [driver_class_name] -url [url_to_db] -user [username] -password [password] [-v] [-init] [-tpc n] [-clients]");
            System.out.println();
            System.out.println("-v          verbose error messages");
            System.out.println("-init       initialize the tables");
            System.out.println("-tpc        transactions per client");
            System.out.println("-clients    number of simultaneous clients");
            System.exit(-1);
        }

        System.out.println(
            "*********************************************************");
        System.out.println(
            "* DBBenchmark v1.1                                        *");
        System.out.println(
            "*********************************************************");
        System.out.println();
        System.out.println("Driver: " + driverName);
        System.out.println("URL:" + dbUrl);
        System.out.println();
        System.out.println("Scale factor value: " + tps_);
        System.out.println("Number of clients: " + numClients_);
        System.out.println("Number of transactions per client: "
            + numTxPerClient_);
        System.out.println();

        try
        {
            Class.forName(driverName);

            DBBenchmark bench = 
                new DBBenchmark(dbUrl, dbUser, dbPassword, initializeDataset);
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a DBBenchmark and sends output to System.out be default.
     * 
     * @param url
     * @param user
     * @param password
     * @param init
     */
    public DBBenchmark(String url, String user, String password, boolean init)
    {
        this(url, user, password, init, 
            new PrintWriter(new OutputStreamWriter(System.out)));
    }   
    
    /**
     * Creates a DBBenchmark.
     * 
     * @param url
     * @param user
     * @param password
     * @param init
     * @param writer Destination of benchmark output.
     */
    public DBBenchmark(
        String url, 
        String user, 
        String password, 
        boolean init,
        PrintWriter writer)
    {
        writer_ = writer;
        Vector vc = new Vector(); // Vector Client
        Thread client = null;
        Enumeration e = null;

        try
        {
            if (init)
            {
                writer_.println("Start: " + new java.util.Date());
                writer_.print("Initializing dataset...");
                createDatabase(url, user, password);
                writer_.println("done.\n");
                writer_.println("Complete: " + new java.util.Date());
                writer_.flush();
            }

            writer_.println("* Starting Benchmark Run *");
            memoryWatcher_ = new MemoryWatcherThread();
            memoryWatcher_.start();

            transactions_ = false;
            preparedStmt_ = false;
            startTime_ = System.currentTimeMillis();

            for (int i = 0; i < numClients_; i++)
            {
                client = new ClientThread(numTxPerClient_, url, user, password);
                client.start();
                vc.addElement(client);
            }
            
            // Barrier to complete this test session
            
            e = vc.elements();

            while (e.hasMoreElements())
            {
                client = (Thread) e.nextElement();
                client.join();
            }

            vc.removeAllElements();
            reportDone();

            transactions_ = true;
            preparedStmt_ = false;
            startTime_ = System.currentTimeMillis();

            for (int i = 0; i < numClients_; i++)
            {
                client = new ClientThread(numTxPerClient_, url, user, password);
                client.start();
                vc.addElement(client);
            }

            // Barrier to complete this test session
            
            e = vc.elements();

            while (e.hasMoreElements())
            {
                client = (Thread) e.nextElement();
                client.join();
            }

            vc.removeAllElements();
            reportDone();

            transactions_ = false;
            preparedStmt_ = true;
            startTime_ = System.currentTimeMillis();

            for (int i = 0; i < numClients_; i++)
            {
                client = new ClientThread(numTxPerClient_, url, user, password);
                client.start();
                vc.addElement(client);
            }

            // Barrier to complete this test session
            
            e = vc.elements();

            while (e.hasMoreElements())
            {
                client = (Thread) e.nextElement();
                client.join();
            }

            vc.removeAllElements();
            reportDone();

            transactions_ = true;
            preparedStmt_ = true;
            startTime_ = System.currentTimeMillis();

            for (int i = 0; i < numClients_; i++)
            {
                client = new ClientThread(numTxPerClient_, url, user, password);
                client.start();
                vc.addElement(client);
            }

            // Barrier to complete this test session
            
            e = vc.elements();

            while (e.hasMoreElements())
            {
                client = (Thread) e.nextElement();
                client.join();
            }

            vc.removeAllElements();
            reportDone();
        }
        catch (Exception ex)
        {
            writer_.println(ex.getMessage());
            ex.printStackTrace();
        }
        finally
        {
            memoryWatcher_.end();

            try
            {
                memoryWatcher_.join();

                if (shutdownCommand_.length() > 0)
                {
                    Connection conn = connect(url, user, password);
                    Statement stmt = conn.createStatement();
                    stmt.execute(shutdownCommand_);
                    stmt.close();
                    connectClose(conn);
                }

                if (tabFile_ != null)
                    tabFile_.close();
            }
            catch (Exception ex2)
            {
            }

            //System.exit(0);
            
            writer_.flush();
        }
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Report done. 
     */
    public void reportDone()
    {
        long endTime = System.currentTimeMillis();
        double completionTime = ((double) endTime - (double) startTime_) / 1000;

        if (tabFile_ != null)
            tabFile_.print(
                tps_ + ";" + numClients_ + ";" + numTxPerClient_ + ";");

        //writer_.println("\n* Benchmark Report *");
        //writer_.print("* Featuring ");

        writer_.println();
        writer_.print("Benchmark: ");
        
        if (preparedStmt_)
        {
            writer_.print("Prepared Statements ");

            if (tabFile_ != null)
                tabFile_.print("<prepared statements>;");
        }
        else
        {
            writer_.print("Direct Queries ");

            if (tabFile_ != null)
                tabFile_.print("<direct queries>;");
        }

        if (transactions_)
        {
            writer_.print("+ Transactions");

            if (tabFile_ != null)
                tabFile_.print("<transactions>;");
        }
        else
        {
            writer_.print("+ Auto-commit");

            if (tabFile_ != null)
                tabFile_.print("<auto-commit>;");
        }

        writer_.println("\n================================================");
        
        writer_.println(
            //"Time for " + txCount_ + " transactions: " +
            "Time        " +
            DecimalFormat.getNumberInstance().format(completionTime) + 
            " sec(s)");

        writer_.println(
            "Passed      " +
            (txCount_ - failedTx_) + "/" + txCount_);

        double rate = (txCount_ - failedTx_) / completionTime;
        
        writer_.println(
            "Throughput  " + 
            DecimalFormat.getNumberInstance().format(rate) + 
            " transactions/sec.");
        
        writer_.println(
            "Min memory  " + 
            DecimalFormat.getIntegerInstance().format(memoryWatcher_.min) +
            " KB");
        
        writer_.println(
            "Max memory  " + 
            DecimalFormat.getIntegerInstance().format(memoryWatcher_.max) +
            " KB");
        
        //    + " / " + memoryWatcher_.min + " kb");
        

        if (tabFile_ != null)
            tabFile_.print(memoryWatcher_.max + ";" + memoryWatcher_.min + ";"
                + failedTx_ + ";" + rate + "\n");

        txCount_ = 0;
        failedTx_ = 0;

        memoryWatcher_.reset();
        
        writer_.flush();
    }

    
    /**
     * Bump up tx count.
     */
    public synchronized void incrementTransactionCount()
    {
        txCount_++;
    }


    /**
     * Bump up failed tx count.
     */
    public synchronized void incrementFailedTransactionCount()
    {
        failedTx_++;
    }

    //--------------------------------------------------------------------------
    // Package Protected
    //--------------------------------------------------------------------------
    
    /**
     * Creates the test database.
     * 
     * @param url
     * @param user
     * @param password
     * @throws Exception
     */
    void createDatabase(String url, String user, String password)
        throws Exception
    {
        Connection conn = connect(url, user, password);
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

            stmt.close();

            if (accountsnb == (numAccounts_ * tps_))
            {
                writer_.println("Tables already initialized");
                connectClose(conn);
                return;
            }
        }
        catch (Exception ex)
        {
        }

        writer_.println("Dropping old tables if they exist...");

        try
        {
            Statement stmt = conn.createStatement();
            String query;

            query = "DROP TABLE history";
            stmt.execute(query);
            stmt.clearWarnings();

            query = "DROP TABLE accounts";
            stmt.execute(query);
            stmt.clearWarnings();

            query = "DROP TABLE tellers";
            stmt.execute(query);
            stmt.clearWarnings();

            query = "DROP TABLE branches";
            stmt.execute(query);
            stmt.clearWarnings();

            if (transactions_)
                conn.commit();

            stmt.close();
        }
        catch (Exception ex2)
        {
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
            query += "filler      CHAR(88))"; /* pad to 100 bytes */

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
            query += "filler      CHAR(84))"; /* pad to 100 bytes */

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
            query += "filler      CHAR(84))"; /* pad to 100 bytes */

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
            query += "filler      CHAR(22))"; /* pad to 50 bytes */

            if (createExtension_.length() > 0)
                query += createExtension_;

            stmt.execute(query);
            stmt.clearWarnings();

            if (transactions_)
                conn.commit();

            stmt.close();
        }
        catch (Exception ex2)
        {
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
             * prime database using TPC BM B scaling rules. * Note that for each
             * branch and teller: * branch_id = teller_id / ntellers * branch_id =
             * account_id / naccounts
             */
            PreparedStatement pstmt = null;

            preparedStmt_ = true;

            if (preparedStmt_)
            {
                try
                {
                    query = "INSERT INTO branches(Bid,Bbalance) VALUES (?,0)";
                    pstmt = conn.prepareStatement(query);
                    writer_.println("Using prepared statements");
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
                    query = "INSERT INTO branches(Bid,Bbalance) VALUES (" + i
                        + ",0)";

                    stmt.executeUpdate(query);
                }

                if ((i % 100 == 0) && (transactions_))
                    conn.commit();
            }

            if (preparedStmt_)
                pstmt.close();

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
                pstmt.close();

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
                pstmt.close();

            if (transactions_)
                conn.commit();

            writer_.println(
                "\t" + (numAccounts_ * tps_) + "\t records inserted");
            
            stmt.close();
        }
        catch (Exception ex)
        {
            writer_.println(ex.getMessage());
            ex.printStackTrace();
        }

        connectClose(conn);
    }

    
    /**
     * 
     * @param lo
     * @param hi
     * @return
     */
    public static int getRandomInt(int lo, int hi)
    {
        // TODO: Replace with randomutils
        
        int ret = 0;

        ret = (int) (Math.random() * (hi - lo + 1));
        ret += lo;

        return ret;
    }

    
    /**
     * 
     * @param type
     * @return
     */
    public static int getRandomID(int type)
    {
        int min;
        int max;
        int num;

        max = min = 0;
        num = numAccounts_;

        switch (type)
        {

            case TELLER:
                min += numBranches_;
                num = numTellers_;

            /* FALLTHROUGH */
            case BRANCH:
                if (type == BRANCH)
                {
                    num = numBranches_;
                }

                min += numAccounts_;

            /* FALLTHROUGH */
            case ACCOUNT:
                max = min + num - 1;
        }

        return (getRandomInt(min, max));
    }

    
    /**
     * Gets a connection.
     *  
     * @param url
     * @param user
     * @param password
     * @return
     */
    public static Connection connect(String url, String user, String password)
    {
        Connection conn = null;
        
        try
        {
            conn = DriverManager.getConnection(url, user, password);
        }
        catch (Exception ex)
        {
            logger_.error(ex);
        }

        return conn;
    }

    
    /**
     * Closes a connection.
     * 
     * @param c
     */
    public static void connectClose(Connection c)
    {
        if (c == null)
            return;

        try
        {
            c.close();
        }
        catch (Exception ex)
        {
            logger_.error(ex);
        }
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
         * @param numOfTx
         * @param url
         * @param user
         * @param password
         */
        public ClientThread(
            int numOfTx, 
            String url, 
            String user,
            String password)
        {
            numTx_ = numOfTx;
            conn_ = connect(url, user, password);

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
                ex.printStackTrace();
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
                int account = DBBenchmark.getRandomID(ACCOUNT);
                int branch = DBBenchmark.getRandomID(BRANCH);
                int teller = DBBenchmark.getRandomID(TELLER);
                int delta = DBBenchmark.getRandomInt(0, 1000);

                doOne(branch, teller, account, delta);
                incrementTransactionCount();
            }

            if (preparedStmt_)
            {
                try
                {
                    if (pstmt1_ != null)
                        pstmt1_.close();

                    if (pstmt2_ != null)
                        pstmt2_.close();

                    if (pstmt3_ != null)
                        pstmt3_.close();

                    if (pstmt4_ != null)
                        pstmt4_.close();

                    if (pstmt5_ != null)
                        pstmt5_.close();
                }
                catch (Exception ex)
                {
                    writer_.println(ex.getMessage());
                    ex.printStackTrace();
                }
            }

            connectClose(conn_);
            conn_ = null;
        }

        //----------------------------------------------------------------------
        // Protected
        //----------------------------------------------------------------------
        
        /**
         * Executes a single TPC BM B transaction.
         *
         * @param bid B id
         * @param tid t id
         * @param aid A id
         * @param delta Delta 
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

                    int res = stmt.executeUpdate(query);

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
                    stmt.close();
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
                    ex.printStackTrace();
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
    
    //--------------------------------------------------------------------------
    // MemoryWatcherThread
    //--------------------------------------------------------------------------
    
    class MemoryWatcherThread extends Thread
    {
        long min = 0;
        long max = 0;
        boolean keep_running = true;


        public MemoryWatcherThread()
        {
            this.reset();
            keep_running = true;
        }


        public void reset()
        {
            System.gc();
            long currentFree = Runtime.getRuntime().freeMemory();
            long currentAlloc = Runtime.getRuntime().totalMemory();
            min = max = (currentAlloc - currentFree);
        }


        public void end()
        {
            keep_running = false;
        }


        public void run()
        {
            while (keep_running)
            {
                long currentFree = Runtime.getRuntime().freeMemory();
                long currentAlloc = Runtime.getRuntime().totalMemory();
                long used = currentAlloc - currentFree;

                if (used < min)
                    min = used;

                if (used > max)
                    max = used;

                try
                {
                    sleep(100);
                }
                catch (InterruptedException E)
                {
                }
            }
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
